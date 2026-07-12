package gr.aueb.cf.doctor_app.controller;

import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.ValidationException;
import gr.aueb.cf.doctor_app.dto.ErrorResponseDTO;
import gr.aueb.cf.doctor_app.dto.TimeSlotGenerateDTO;
import gr.aueb.cf.doctor_app.dto.TimeSlotReadOnlyDTO;

import gr.aueb.cf.doctor_app.dto.ValidationErrorResponseDTO;
import gr.aueb.cf.doctor_app.service.ITimeSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/timeslots")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class TimeSlotController {

    private final ITimeSlotService timeSlotService;


    @Operation(
            summary = "Generate available timeslots",
            description = "Doctors generate available timeslots from Monday to Friday."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201" , description = "Timeslots generated",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TimeSlotReadOnlyDTO.class)))),
            @ApiResponse(
                    responseCode = "400" , description = "Invalid date range or validation error",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404" , description = "Doctor not found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    @PostMapping("/generate")
    public ResponseEntity<List<TimeSlotReadOnlyDTO>> generateTimeSlots(
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
        return new ResponseEntity<>(response,HttpStatus.CREATED);

    }

    @Operation(
            summary = "View a doctor's available timeslots",
            description = "Returns available timeslots for a doctor, paginated."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Available timeslots returned",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "404", description = "Doctor not found",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    @GetMapping("/available")
    public ResponseEntity<Page<TimeSlotReadOnlyDTO>> getAvailableTimeSlots(
            @RequestParam UUID doctorUuid,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) throws EntityNotFoundException {
        return ResponseEntity.ok(timeSlotService.getAvailableTimeSlots(doctorUuid, date, pageable));
    }
}