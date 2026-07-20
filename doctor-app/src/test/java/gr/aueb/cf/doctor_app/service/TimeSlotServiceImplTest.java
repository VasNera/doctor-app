package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.dto.TimeSlotGenerateDTO;
import gr.aueb.cf.doctor_app.dto.TimeSlotReadOnlyDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Doctor;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import gr.aueb.cf.doctor_app.repository.TimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TimeSlotServiceImplTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private TimeSlotServiceImpl timeSlotService;


    @Test
    void generateTimeSlots_validSingleWeekday_creates16Slots()throws Exception{

        LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        TimeSlotGenerateDTO generateDTO = new TimeSlotGenerateDTO(monday,monday);

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        TimeSlotReadOnlyDTO slotDTO = new TimeSlotReadOnlyDTO(
                1L,
                monday,
                LocalTime.of(9,0),
                LocalTime.of(9,30),
                TimeSlotStatus.AVAILABLE);
        when(doctorRepository.findByUserUsername("drnikos")).thenReturn(Optional.of(doctor));
        when(timeSlotRepository.existsByDoctorIdAndDateAndStartTime(anyLong(), any(), any()))
                .thenReturn(false);
        when(timeSlotRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.mapToTimeSlotReadOnlyDTO(any())).thenReturn(slotDTO);

        List<TimeSlotReadOnlyDTO> result = timeSlotService.generateTimeSlots(generateDTO,"drnikos");

        assertEquals(16,result.size());
        verify(timeSlotRepository).saveAll(anyList());

    }

    @Test
    void generateTimeSlots_toDateBeforeFromDate_throwsInvalidArgument(){

        LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        TimeSlotGenerateDTO generateDTO = new TimeSlotGenerateDTO(monday,monday.minusDays(1));

        assertThrows(InvalidArgumentException.class,
                ()-> timeSlotService.generateTimeSlots(generateDTO,"drnikos"));
    }

    @Test
    void generateTimeSlots_pastDate_throwsInvalidArgument(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        TimeSlotGenerateDTO generateDTO = new TimeSlotGenerateDTO(yesterday, yesterday.plusDays(1));

        assertThrows(InvalidArgumentException.class,
                () -> timeSlotService.generateTimeSlots(generateDTO, "drnikos"));
    }

    @Test
    void generateTimeSlots_rangeTooLarge_throwsInvalidArgument(){
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        TimeSlotGenerateDTO generateDTO = new TimeSlotGenerateDTO(monday, monday.plusDays(61));

        assertThrows(InvalidArgumentException.class,
                () -> timeSlotService.generateTimeSlots(generateDTO, "drnikos"));

    }

    @Test
    void generateTimeSlots_doctorNotFound_throwsNotFound(){
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        TimeSlotGenerateDTO generateDTO = new TimeSlotGenerateDTO(monday, monday);

        when(doctorRepository.findByUserUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                ()-> timeSlotService.generateTimeSlots(generateDTO,"ghost"));

    }
}