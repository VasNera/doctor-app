package gr.aueb.cf.doctor_app.controller;


import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.TimeSlotNotAvailableException;
import gr.aueb.cf.doctor_app.core.exceptions.ValidationException;
import gr.aueb.cf.doctor_app.dto.AppointmentInsertDTO;
import gr.aueb.cf.doctor_app.dto.AppointmentReadOnlyDTO;
import gr.aueb.cf.doctor_app.model.enums.AppointmentStatus;
import gr.aueb.cf.doctor_app.service.IAppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentReadOnlyDTO> bookAppointment(
            @Valid
            @RequestBody
            AppointmentInsertDTO appointmentInsertDTO,
            BindingResult bindingResult,
            Authentication authentication)
            throws EntityNotFoundException,
            TimeSlotNotAvailableException,
            ValidationException{

        if (bindingResult.hasErrors()) {
            throw new ValidationException("Appointment", "Validation failed", bindingResult);
        }
        AppointmentReadOnlyDTO response = appointmentService.bookAppointment
                (appointmentInsertDTO, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/patient")
    public ResponseEntity<Page<AppointmentReadOnlyDTO>> getPatientAppointments(
            @RequestParam(required = false)
            AppointmentStatus appointmentStatus,
            Pageable pageable,
            Authentication authentication) throws EntityNotFoundException{

        return ResponseEntity.ok(appointmentService.getPatientsAppointments(
                authentication.getName(),appointmentStatus, pageable));
    }

    @GetMapping("/doctor")
    public ResponseEntity<Page<AppointmentReadOnlyDTO>> getDoctorAppointments(
            @RequestParam(required = false)
            AppointmentStatus appointmentStatus,
            Pageable pageable,
            Authentication authentication) throws EntityNotFoundException{
        return ResponseEntity.ok(appointmentService.getDoctorsAppointments(
                authentication.getName(),appointmentStatus,pageable));
    }


    @PatchMapping("/{uuid}/cancel")
    public ResponseEntity<AppointmentReadOnlyDTO> cancelAppointment(
            @PathVariable UUID uuid,
            Authentication authentication) throws EntityNotFoundException, InvalidArgumentException
    {
        AppointmentReadOnlyDTO response = appointmentService.cancelAppointment(uuid, authentication.getName());
        return ResponseEntity.ok(response);

    }
}
