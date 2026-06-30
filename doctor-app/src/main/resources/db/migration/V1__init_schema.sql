CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)

) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE capabilities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),

    CONSTRAINT pk_capabilities PRIMARY KEY (id),
    CONSTRAINT uk_capabilities_name UNIQUE (name),

    INDEX ix_capabilities_name (name)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


  CREATE TABLE roles_capabilities (
      role_id BIGINT NOT NULL,
      capability_id BIGINT NOT NULL,

      CONSTRAINT pk_roles_capabilities
          PRIMARY KEY (role_id, capability_id),

      CONSTRAINT fk_roles_capabilities_role
          FOREIGN KEY (role_id)
          REFERENCES roles(id)
          ON DELETE CASCADE,

      CONSTRAINT fk_roles_capabilities_capability
          FOREIGN KEY (capability_id)
          REFERENCES capabilities(id)
          ON DELETE CASCADE,

      INDEX ix_roles_capabilities_capability_id (capability_id)
  ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    uuid BINARY(16) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,

    created_at DATETIME NOT NULL,
        updated_at DATETIME NOT NULL,
        deleted TINYINT(1) NOT NULL DEFAULT 0,
        deleted_at DATETIME NULL,

        CONSTRAINT pk_users PRIMARY KEY (id),

        CONSTRAINT uk_users_uuid UNIQUE (uuid),
        CONSTRAINT uk_users_username UNIQUE (username),

        CONSTRAINT fk_users_role
            FOREIGN KEY (role_id)
            REFERENCES roles(id)
            ON DELETE RESTRICT,

        INDEX ix_users_username (username),
        INDEX ix_users_role_id (role_id),
        INDEX ix_users_deleted (deleted),
        INDEX ix_users_deleted_at (deleted_at)
    ) ENGINE=InnoDB
      DEFAULT CHARSET=utf8mb4
      COLLATE=utf8mb4_0900_ai_ci;

      CREATE TABLE doctors (
          id BIGINT NOT NULL AUTO_INCREMENT,

          firstname VARCHAR(255),
          lastname VARCHAR(255),

          uuid BINARY(16) NOT NULL,
          email VARCHAR(255) NOT NULL,
          phone_number VARCHAR(255) NOT NULL,
          licence_number VARCHAR(255) NOT NULL,
          specialty VARCHAR(255),

          user_id BIGINT UNIQUE,

          created_at DATETIME NOT NULL,
          updated_at DATETIME NOT NULL,
          deleted TINYINT(1) NOT NULL DEFAULT 0,
          deleted_at DATETIME NULL,

          CONSTRAINT pk_doctors PRIMARY KEY (id),

          CONSTRAINT uk_doctors_uuid UNIQUE (uuid),
          CONSTRAINT uk_doctors_email UNIQUE (email),
          CONSTRAINT uk_doctors_licence_number UNIQUE (licence_number),

          CONSTRAINT fk_doctors_user
              FOREIGN KEY (user_id)
              REFERENCES users(id)
              ON DELETE RESTRICT,

          INDEX ix_doctors_user_id (user_id),
          INDEX ix_doctors_email (email),
          INDEX ix_doctors_licence_number (licence_number),
          INDEX ix_doctors_deleted (deleted),
          INDEX ix_doctors_deleted_at (deleted_at)
      ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_0900_ai_ci;

        CREATE TABLE patients
        (
            id BIGINT NOT NULL AUTO_INCREMENT,
            firstname VARCHAR(30) NOT NULL,
            lastname VARCHAR(30) NOT NULL,

            uuid BINARY(16) NOT NULL,

            email VARCHAR(255) NOT NULL,
            phone_number VARCHAR(20) NOT NULL,
            amka VARCHAR(11) NOT NULL,

            user_id BIGINT NOT NULL,

             created_at DATETIME NOT NULL,
             updated_at DATETIME NOT NULL,
             deleted TINYINT(1) NOT NULL DEFAULT 0,
             deleted_at DATETIME NULL,

            CONSTRAINT pk_patients
                PRIMARY KEY (id),

            CONSTRAINT uk_patients_uuid
                UNIQUE (uuid),

            CONSTRAINT uk_patients_email
                UNIQUE (email),

            CONSTRAINT uk_patients_amka
                UNIQUE (amka),

            CONSTRAINT uk_patients_user
                UNIQUE (user_id),

            CONSTRAINT fk_patients_user
                FOREIGN KEY (user_id)
                REFERENCES users(id)

        ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_0900_ai_ci;

        CREATE TABLE timeslots
        (
            id BIGINT NOT NULL AUTO_INCREMENT,

            date DATE NOT NULL,

            start_time TIME NOT NULL,

            end_time TIME NOT NULL,

            time_slots_status VARCHAR(30) NOT NULL,

            doctor_id BIGINT NOT NULL,

             created_at DATETIME NOT NULL,
             updated_at DATETIME NOT NULL,
             deleted TINYINT(1) NOT NULL DEFAULT 0,
             deleted_at DATETIME NULL,

            CONSTRAINT pk_timeslots
                PRIMARY KEY (id),

            CONSTRAINT fk_timeslots_doctor
                FOREIGN KEY (doctor_id)
                REFERENCES doctors(id),

            CONSTRAINT uk_timeslots_doctor_date_start
                UNIQUE (doctor_id, date, start_time)

        ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_0900_ai_ci;


        CREATE TABLE appointments
        (
            id BIGINT NOT NULL AUTO_INCREMENT,

            uuid BINARY(16) NOT NULL,

            reason VARCHAR(500),

            appointment_status VARCHAR(30) NOT NULL,

            patient_id BIGINT NOT NULL,

            timeslot_id BIGINT NOT NULL,

             created_at DATETIME NOT NULL,
             updated_at DATETIME NOT NULL,
             deleted TINYINT(1) NOT NULL DEFAULT 0,
             deleted_at DATETIME NULL,

            CONSTRAINT pk_appointments
                PRIMARY KEY (id),

            CONSTRAINT uk_appointments_uuid
                UNIQUE (uuid),

            CONSTRAINT uk_appointments_timeslot
                UNIQUE (timeslot_id),

            CONSTRAINT fk_appointments_patient
                FOREIGN KEY (patient_id)
                REFERENCES patients(id),

            CONSTRAINT fk_appointments_timeslot
                FOREIGN KEY (timeslot_id)
                REFERENCES timeslots(id)

        ) ENGINE=InnoDB
        DEFAULT CHARSET=utf8mb4
        COLLATE=utf8mb4_0900_ai_ci;