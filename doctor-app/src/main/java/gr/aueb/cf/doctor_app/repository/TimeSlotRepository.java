package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.TimeSlot;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {


    Optional<TimeSlot> findByIdAndDoctorId(Long id, Long doctorId);

    Page<TimeSlot> findAllByDoctorIdAndDate(Long doctorId, LocalDate date, Pageable pageable);

    Page<TimeSlot> findAllByDoctorIdAndDateAndTimeSlotStatus
            (Long doctorId, LocalDate date, TimeSlotStatus status,Pageable pageable);

    Page<TimeSlot> findAllByDoctorIdAndTimeSlotStatus
            (Long doctorId, TimeSlotStatus status, Pageable pageable);

    boolean existsByIdAndDoctorIdAndTimeSlotStatus(Long id, Long doctorId, TimeSlotStatus status);


}
