package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUuidAndDeletedFalse(UUID uuid);

    Optional<Doctor> findByEmailAndDeletedFalse(String email);

    Optional<Doctor> findByLicenceNumberAndDeletedFalse(String licenceNumber);

    Page<Doctor> findAllByDeletedFalse(Pageable pageable);

    Page<Doctor> findAllByDeletedTrue(Pageable pageable);

    Optional<Doctor> findByActivationToken(String token);

    Optional<Doctor> findByUserUsername(String username);

    boolean existsByUuid(UUID uuid);

    boolean existsByEmail(String email);

    boolean existsByLicenceNumber(String licenceNumber);
}
