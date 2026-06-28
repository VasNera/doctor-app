package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUuidAndDeletedFalse(UUID uuid);

    Optional<Patient> findByAmkaAndDeletedFalse(String amka);

    Page<Patient> findAllByDeletedFalse(Pageable pageable);

    Page<Patient> findAllByDeletedTrue(Pageable pageable);

    boolean existsByUuid(UUID uuid);

    boolean existsByAmka(String amka);
}
