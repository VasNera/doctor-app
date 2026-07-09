package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.TimeSlot;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {


    Optional<TimeSlot> findByIdAndDoctorId(Long id, Long doctorId);

    Page<TimeSlot> findAllByDoctorIdAndDate(Long doctorId, LocalDate date, Pageable pageable);

    Page<TimeSlot> findAllByDoctorIdAndDateAndTimeSlotStatus
            (Long doctorId, LocalDate date, TimeSlotStatus status,Pageable pageable);

    Page<TimeSlot> findAllByDoctorIdAndTimeSlotStatus
            (Long doctorId, TimeSlotStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TimeSlot t WHERE t.id = :id")
    Optional<TimeSlot> findByIdForUpdate(@Param("id")Long id);

    boolean existsByIdAndDoctorIdAndTimeSlotStatus(Long id, Long doctorId, TimeSlotStatus status);

    boolean existsByDoctorIdAndDateAndStartTime(Long doctorId, LocalDate date, LocalTime startTime);




}
