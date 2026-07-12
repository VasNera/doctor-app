package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.dto.TimeSlotGenerateDTO;
import gr.aueb.cf.doctor_app.dto.TimeSlotReadOnlyDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Doctor;
import gr.aueb.cf.doctor_app.model.TimeSlot;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import gr.aueb.cf.doctor_app.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotServiceImpl implements ITimeSlotService{

    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;
    private final Mapper mapper;

    private static final LocalTime WORK_START = LocalTime.of(9,0);
    private static final LocalTime WORK_END = LocalTime.of(17,0);
    private static final long SLOT_DURATION_MINUTES = 30;
    private static final long MAX_RANGE_DAYS = 60;

    @PreAuthorize("hasAuthority('CREATE_TIMESLOT')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, InvalidArgumentException.class})
    @Override
    public List<TimeSlotReadOnlyDTO> generateTimeSlots(TimeSlotGenerateDTO generateDTO, String username)
            throws EntityNotFoundException, InvalidArgumentException {
        try {
            if (generateDTO.toDate().isBefore(generateDTO.fromDate()))
                throw new InvalidArgumentException("TIMESLOT", "End date cannot be before start date");

            if (generateDTO.fromDate().isBefore(LocalDate.now())) {
                throw new InvalidArgumentException(
                        "TIMESLOT",
                        "Start date cannot be in the past"
                );
            }

            if (generateDTO.fromDate().plusDays(MAX_RANGE_DAYS).isBefore(generateDTO.toDate()))
                throw new InvalidArgumentException("TIMESLOT",
                        "Date range too large (max " + MAX_RANGE_DAYS + " days)");

            Doctor doctor = doctorRepository.findByUserUsername(username)
                    .orElseThrow(()-> new EntityNotFoundException("DOCTOR", "Doctor not found for user: " + username));

                    List<TimeSlot> timeSlots = new ArrayList<>();
            generateDTO.fromDate().datesUntil(generateDTO.toDate().plusDays(1))
                    .filter(date ->{
                        DayOfWeek day = date.getDayOfWeek();
                        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
                    })
                    .forEach(date ->{
                        for (LocalTime start = WORK_START; start.isBefore (WORK_END);
                             start = start.plusMinutes(SLOT_DURATION_MINUTES)){
                            if (timeSlotRepository.existsByDoctorIdAndDateAndStartTime(doctor.getId(),date,start))
                                continue;
                            TimeSlot timeSlot = new TimeSlot();
                            timeSlot.setDate(date);
                            timeSlot.setStartTime(start);
                            timeSlot.setEndTime(start.plusMinutes(SLOT_DURATION_MINUTES));
                            timeSlot.setTimeSlotStatus(TimeSlotStatus.AVAILABLE);
                            timeSlot.setDoctor(doctor);
                            timeSlots.add(timeSlot);
                        }
                    });

            List<TimeSlot> saved = timeSlotRepository.saveAll(timeSlots);
            log.info("Generated {} timeslots for doctor with id={}" ,saved.size(), doctor.getId());
            return saved
                    .stream()
                    .map(mapper::mapToTimeSlotReadOnlyDTO)
                    .toList();


        }catch (EntityNotFoundException | InvalidArgumentException e){
            log.error("Timeslot generation failed: {}" , e.getMessage());
            throw e;
        }
    }


    @PreAuthorize("hasAuthority('VIEW_TIMESLOTS')")
    @Transactional(readOnly = true)
    @Override
    public Page<TimeSlotReadOnlyDTO> getAvailableTimeSlots(UUID doctorUuid, LocalDate date, Pageable pageable)
            throws EntityNotFoundException {

        Doctor doctor = doctorRepository.findByUuidAndDeletedFalse(doctorUuid)
                .orElseThrow(() -> new EntityNotFoundException("DOCTOR",
                        "Doctor not found with uuid: " + doctorUuid));

        Page<TimeSlot> timeSlots = (date != null) ? timeSlotRepository.findAllByDoctorIdAndDateAndTimeSlotStatus(
                doctor.getId(), date, TimeSlotStatus.AVAILABLE, pageable)
                : timeSlotRepository.findAllByDoctorIdAndTimeSlotStatus(doctor.getId(),TimeSlotStatus.AVAILABLE, pageable);
        return timeSlots.map(mapper::mapToTimeSlotReadOnlyDTO);
        }
    }

