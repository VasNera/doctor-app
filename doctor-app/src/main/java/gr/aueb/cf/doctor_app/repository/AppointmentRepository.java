package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.Appointment;
import gr.aueb.cf.doctor_app.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByUuidAndDeletedFalse(UUID uuid);

    Page<Appointment> findAllByPatientId(Long patientId, Pageable pageable);

    Page<Appointment> findAllByPatientIdAndAppointmentStatus
            (Long patientId, AppointmentStatus status, Pageable pageable);

    Page<Appointment> findAllByTimeSlotDoctorId(Long doctorId, Pageable pageable);

    Page<Appointment> findAllByTimeSlotDoctorIdAndAppointmentStatus
            (Long doctorId, AppointmentStatus status, Pageable pageable);

    boolean existsByTimeSlotId(Long timeSlotId);

    boolean existsByPatientIdAndTimeSlotId(Long patientId, Long timeSlotId);


}
