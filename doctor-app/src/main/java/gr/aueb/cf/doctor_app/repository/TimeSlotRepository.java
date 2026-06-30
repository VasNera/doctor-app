package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.TimeSlot;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {


    Optional<TimeSlot> findByIdAndDoctorId(Long id, Long doctorId);

    Page<TimeSlot> findAllByDoctorIdAndDate(Long doctorId, LocalDate date, Pageable pageable);

    Page<TimeSlot> findAllByDoctorIdAndDateAndTimeSlotsStatus
            (Long doctorId, LocalDate date, TimeSlotsStatus status,Pageable pageable);

    Page<TimeSlot> findAllByDoctorIdAndTimeSlotsStatus
            (Long doctorId, TimeSlotsStatus status, Pageable pageable);

    boolean existsByIdAndDoctorIdAndTimeSlotsStatus(Long id, Long doctorId,TimeSlotsStatus status);


}
