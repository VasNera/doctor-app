package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.dto.TimeSlotGenerateDTO;
import gr.aueb.cf.doctor_app.dto.TimeSlotReadOnlyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ITimeSlotService {

    List<TimeSlotReadOnlyDTO> generateTimeSlots(TimeSlotGenerateDTO generateDTO, String username)
        throws EntityNotFoundException, InvalidArgumentException;
    Page<TimeSlotReadOnlyDTO> getAvailableTimeSlots(Long doctorId, LocalDate date, Pageable pageable);
}
