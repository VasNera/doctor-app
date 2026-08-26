# Doctor App — Backend

REST API for a small clinic, built with Spring Boot: patients register and book appointments on doctors' time slots, doctors manage their schedule, and an admin manages the doctors.

Frontend (React SPA) lives in its own repository: [VasNera/frontend](https://github.com/VasNera/frontend).

## Features

- **Stateless JWT authentication** with role- and capability-based authorization (`ADMIN`, `DOCTOR`, `PATIENT`)
- **Patient self-registration** (account created together with the patient, password BCrypt-hashed)
- **Doctor onboarding flow**: admin registers the doctor → the doctor receives an activation email with a one-time token (48h expiry) → the doctor picks their own username & password. The admin never handles credentials.
- **Time-slot generation**: doctors generate their own availability (Mon–Fri, 09:00–17:00, 30-minute slots) for a date range — idempotent, weekends skipped, range capped
- **Appointment booking protected against double-booking** with pessimistic database locking
- **Appointment lifecycle**: PENDING → CANCELLED (cancelling frees the slot); ownership enforced
- **Pagination & filtering** on all listings (Spring Data `Pageable`, nested-property sorting)
- **Localized error messages** (EL/EN) via `Accept-Language`
- **OpenAPI / Swagger UI** documentation with JWT support
- Soft delete, audit columns (`created_at`, `updated_at`), UUID external identifiers

## Tech stack

| Concern | Choice |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 3.5 (Web, Data JPA, Security, Validation, Mail) |
| Database | MySQL 8, schema managed by **Flyway** (`ddl-auto=validate`) |
| Auth | Spring Security + JWT (jjwt), BCrypt (strength 12) |
| Email | JavaMailSender (Mailtrap sandbox in development) |
| API docs | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, Spring MockMvc |
| Build | Gradle |

## API overview

All endpoints are prefixed with `/api/v1`.

| Method & path | Access | Purpose |
|---|---|---|
| `POST /auth/authenticate` | public | login → JWT |
| `POST /patients` | public | patient self-registration |
| `POST /doctors` | `CREATE_DOCTOR` (admin) | register doctor + send activation email |
| `POST /doctors/activate` | public (token) | doctor sets credentials, account activated |
| `GET /doctors` | `VIEW_DOCTORS` (admin, patient) | paginated doctors list |
| `POST /timeslots/generate` | `CREATE_TIMESLOT` (doctor) | generate own availability for a range |
| `GET /timeslots/available?doctorUuid=&date=` | authenticated | available slots of a doctor, paged |
| `POST /appointments` | `CREATE_APPOINTMENT` (patient) | book a slot |
| `GET /appointments/patient` | `VIEW_APPOINTMENTS` | own bookings (patient) |
| `GET /appointments/doctor` | `VIEW_APPOINTMENTS` | own schedule (doctor) |
| `PATCH /appointments/{uuid}/cancel` | `UPDATE_APPOINTMENT` (owner) | cancel, slot becomes available again |

Interactive documentation: **`http://localhost:8080/swagger-ui.html`** (authorize with a Bearer token to try secured endpoints).

## Getting started

Prerequisites: **Java 21**, **MySQL 8**.

**1. Database** (defaults — all overridable via environment variables):

```sql
CREATE DATABASE doctorappdb;
CREATE USER 'doctorappuser'@'localhost' IDENTIFIED BY '<your password>';
GRANT ALL PRIVILEGES ON doctorappdb.* TO 'doctorappuser'@'localhost';
```

**2. Secrets** — create `doctor-app/src/main/resources/application-secret.properties` (git-ignored):

```properties
MYSQL_PASSWORD=<your db password>
JWT_SECRET_KEY=<Base64-encoded key, at least 256 bits>
MAILTRAP_USERNAME=<mailtrap sandbox user>
MAILTRAP_PASSWORD=<mailtrap sandbox password>
```

**3. Run:**

```bash
cd doctor-app
./gradlew bootRun        # Windows: gradlew.bat bootRun
```

On startup Flyway applies the migrations and a bootstrap admin is seeded (`admin` / `Ad12345!` — development only). Activation emails land in the Mailtrap sandbox; the activation link points to the frontend (`http://localhost:5173/activate?token=...`).

**4. Tests:**

```bash
./gradlew test
```

## Architecture

Classic layered architecture — controllers stay thin, business rules live in services, persistence behind Spring Data repositories:

```
gr.aueb.cf.doctor_app
├── controller/        REST endpoints (validation wiring + delegation, OpenAPI annotations)
├── service/           IXxxService interfaces + implementations (@Transactional business rules)
├── repository/        Spring Data JPA repositories
├── model/             entities (extend AbstractEntity: id, uuid, audit, soft-delete) + enums
├── dto/               request/response records (insert / read-only per resource)
├── mapper/            DTO ↔ entity mapping
├── validator/         Spring Validators (uniqueness / cross-field → field-level errors)
├── authentication/    JwtService, CustomUserDetailService
├── security/          SecurityConfig, JwtAuthenticationFilter, custom 401/403 handlers
├── core/              GlobalExceptionHandler, typed exceptions, error DTOs
└── runner/            DataInitializer (idempotent admin seed)
```

**Error contract.** Every error returns a typed body: `{code, message}` (`ErrorResponseDTO`) or, for validation, `{..., errors: {field: message}}` (`ValidationErrorResponseDTO`). Messages are resolved from `messages.properties` per `Accept-Language`, and the field map is what the frontend binds 1:1 onto its forms.

## Design decisions

### Double-booking prevention (pessimistic locking)

Two patients booking the last free slot at the same moment is the core race condition of the domain. It is handled with **defense in depth**:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT t FROM TimeSlot t WHERE t.id = :id")
Optional<TimeSlot> findByIdForUpdate(@Param("id") Long id);
```

1. The booking transaction locks the slot row (`SELECT … FOR UPDATE`) — a concurrent request **waits**.
2. Inside the lock, the status is checked: not `AVAILABLE` → `409 Conflict`.
3. A unique constraint on `appointments.timeslot_id` stands as the database's last line of defense.

Pessimistic (rather than optimistic `@Version`) locking was chosen deliberately: the conflict is the exact scenario being guarded, the transaction is very short, and the loser gets a clean 409 instead of a retry loop.

### Doctor activation via one-time token

Doctor accounts are created **without** a user: the admin enters professional details only, and the system emails a random UUID token (unique, 48h expiry). The doctor sets their own credentials on activation, after which the token is cleared. A doctor without a linked user simply cannot log in — no extra "active" flag needed. (The medical licence number was rejected as the activation secret: it is public information and would allow account hijacking.)

### Capabilities, not just roles

Authorization checks use fine-grained capabilities (`CREATE_DOCTOR`, `CREATE_TIMESLOT`, `CREATE_APPOINTMENT`, …) assigned to roles in the database and enforced with `@PreAuthorize("hasAuthority(...)")` at the service layer. Adding a permission is a data change, not a code change.

### Flyway owns the schema

`ddl-auto=validate`: Hibernate never touches the schema, it only verifies that entities match it. Applied migrations are immutable — every change is a new versioned script:

| Migration | Purpose |
|---|---|
| `V1` | initial schema (8 tables) |
| `V2` | seed roles & capabilities |
| `V3` | column rename (`time_slots_status` → `time_slot_status`) |
| `V4` | doctor activation token + expiry |
| `V5` | `CREATE_TIMESLOT` capability for doctors |

### Not leaking information

External identifiers are **UUIDs**, never sequential ids (no enumeration/IDOR). Ownership violations return **404, not 403** — a patient probing someone else's appointment cannot even learn that it exists.

### Other choices

- `spring.jpa.open-in-view=false` — no lazy loading from the web layer; fetching is explicit (`@EntityGraph` where needed).
- Timestamps are stored as UTC `Instant`s.
- The activation email is sent inside the creation transaction, so a mail failure rolls back the doctor — creation is atomic.

## Testing

**Service layer** — unit tests with **JUnit 5 + Mockito** (no Spring context, no database), covering the business rules of all four services (booking, doctor onboarding, slot generation, registration). The centrepiece is `AppointmentServiceImpl`, where correctness matters most:

- booking an available slot creates the appointment and marks the slot `BOOKED`
- booking an already-booked slot throws (`409`) and **saves nothing**
- unknown user → not found, the slot is never touched
- cancelling sets `CANCELLED` **and frees the slot**
- cancelling twice is rejected
- cancelling someone else's appointment → 404 (existence hidden)

**Web layer** — controller tests with **Spring MockMvc** (`standaloneSetup`), asserting HTTP status, JSON body (`jsonPath`), and exception→status mapping — including that the response never leaks a password.

Run everything with `./gradlew test`.

## Related

- Frontend: [VasNera/frontend](https://github.com/VasNera/frontend) — React 19 + TypeScript, TanStack Router/Query, react-hook-form + zod, i18n EL/EN.
