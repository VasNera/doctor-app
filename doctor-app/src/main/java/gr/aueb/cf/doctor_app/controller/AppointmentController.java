package gr.aueb.cf.doctor_app.controller;


import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.TimeSlotNotAvailableException;
import gr.aueb.cf.doctor_app.core.exceptions.ValidationException;
import gr.aueb.cf.doctor_app.dto.AppointmentInsertDTO;
import gr.aueb.cf.doctor_app.dto.AppointmentReadOnlyDTO;
import gr.aueb.cf.doctor_app.dto.ErrorResponseDTO;
import gr.aueb.cf.doctor_app.dto.ValidationErrorResponseDTO;
import gr.aueb.cf.doctor_app.model.enums.AppointmentStatus;
import gr.aueb.cf.doctor_app.service.IAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "Bearer Authentication")
public class AppointmentController {

    private final IAppointmentService appointmentService;


    @Operation(
            summary = "Book an appointment",
            description = "Books an available timeslot."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201" , description = "Appointment booked",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentReadOnlyDTO.class))),
            @ApiResponse(
                    responseCode = "409" , description = "Timeslot is not available",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404" , description = "Patient or timeslot not found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400" , description = "Validation Error",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponseDTO.class)))
    })

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

    @Operation(
            summary = "View patient's appointments",
            description = "Returns the logged-in patient's appointments, optionally filtered by status. Paginated."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200" , description = "Appointments returned",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "404" , description = "Patient not found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)))

    })

    @GetMapping("/patient")
    public ResponseEntity<Page<AppointmentReadOnlyDTO>> getPatientAppointments(
            @RequestParam(required = false)
            AppointmentStatus appointmentStatus,
            Pageable pageable,
            Authentication authentication) throws EntityNotFoundException{

        return ResponseEntity.ok(appointmentService.getPatientsAppointments(
                authentication.getName(),appointmentStatus, pageable));
    }

    @Operation(
            summary = "View doctor's appointments",
            description = "Returns the logged-in doctor's scheduled appointments, optionally filtered by status." +
                    " Paginated."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200" , description = "Appointment returned",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "404" , description = "Doctor not found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    @GetMapping("/doctor")
    public ResponseEntity<Page<AppointmentReadOnlyDTO>> getDoctorAppointments(
            @RequestParam(required = false)
            AppointmentStatus appointmentStatus,
            Pageable pageable,
            Authentication authentication) throws EntityNotFoundException{
        return ResponseEntity.ok(appointmentService.getDoctorsAppointments(
                authentication.getName(),appointmentStatus,pageable));
    }

    @Operation(
            summary = "Patient cancel an appointment",
            description = "Patients cancel their own appointment and frees the timeslot."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200" , description = "Appointment cancelled",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AppointmentReadOnlyDTO.class))),
            @ApiResponse(
                    responseCode = "404" , description = "Appointment not found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400" , description = "Appointment cannot cancelled",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    @PatchMapping("/{uuid}/cancel")
    public ResponseEntity<AppointmentReadOnlyDTO> cancelAppointment(
            @PathVariable UUID uuid,
            Authentication authentication) throws EntityNotFoundException, InvalidArgumentException
    {
        AppointmentReadOnlyDTO response = appointmentService.cancelAppointment(uuid, authentication.getName());
        return ResponseEntity.ok(response);

    }
}
