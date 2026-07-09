package gr.aueb.cf.doctor_app.controller;

import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.ValidationException;
import gr.aueb.cf.doctor_app.dto.TimeSlotGenerateDTO;
import gr.aueb.cf.doctor_app.dto.TimeSlotReadOnlyDTO;

import gr.aueb.cf.doctor_app.service.ITimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final ITimeSlotService timeSlotService;

    @PostMapping("/generate")
    public ResponseEntity<TimeSlotReadOnlyDTO> generateTimeSlots(
            @Valid
            @RequestBody
            TimeSlotGenerateDTO timeSlotGenerateDTO,
            BindingResult bindingResult,
            Authentication authentication) throws EntityNotFoundException,
            InvalidArgumentException,
            ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException("TimeSlot", "Validation failed", bindingResult);
        }

        List<TimeSlotReadOnlyDTO> response = timeSlotService.generateTimeSlots(timeSlotGenerateDTO,
                authentication.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping("/available")
    public ResponseEntity<Page<TimeSlotReadOnlyDTO>> getAvailableTimeSlots(
            @RequestParam Long doctorId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(timeSlotService.getAvailableTimeSlots(doctorId, date, pageable));
    }
}