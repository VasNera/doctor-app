package gr.aueb.cf.doctor_app.controller;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.ValidationException;
import gr.aueb.cf.doctor_app.dto.*;
import gr.aueb.cf.doctor_app.service.IDoctorService;
import gr.aueb.cf.doctor_app.validator.DoctorActivationValidator;
import gr.aueb.cf.doctor_app.validator.DoctorInsertValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final IDoctorService doctorService;
    private final DoctorInsertValidator doctorInsertValidator;
    private final DoctorActivationValidator doctorActivationValidator;

    @Operation(
            summary = "Create a new doctor",
            description = "Admin create a new doctor and send an activation email with a token."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201" , description = "Doctor Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorReadOnlyDTO.class))),
            @ApiResponse(
                    responseCode = "409" , description = "Doctor already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400" , description = "Validation Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class))),

    })

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<DoctorReadOnlyDTO> createDoctor(
            @Valid
            @RequestBody
            DoctorInsertDTO doctorInsertDTO,
            BindingResult bindingResult)throws EntityAlreadyExistsException, ValidationException {
        doctorInsertValidator.validate(doctorInsertDTO,bindingResult);
        if (bindingResult.hasErrors()){
            throw new ValidationException("Doctor", "Validation failed", bindingResult);

        }
        DoctorReadOnlyDTO response =doctorService.createDoctor(doctorInsertDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    @Operation(
            summary = "Activate a doctor account",
            description = "Uses the activation token from the email to set username & password."
    )

    @ApiResponses({
        @ApiResponse(
                responseCode = "200" , description = "Doctor is activated",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = DoctorReadOnlyDTO.class))),
            @ApiResponse(
                    responseCode = "404" , description = "Invalid activation token",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400" , description = "Token is expired or validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "409" , description = "Username already exists",
                    content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    @PostMapping("/activate")
    public ResponseEntity<DoctorReadOnlyDTO> activateDoctor(
            @Valid
            @RequestBody
            DoctorActivationDTO doctorActivationDTO,
            BindingResult bindingResult)
            throws EntityAlreadyExistsException,
            InvalidArgumentException,
            EntityNotFoundException,
            ValidationException{
        doctorActivationValidator.validate(doctorActivationDTO, bindingResult);
        if (bindingResult.hasErrors()){
            throw new ValidationException("DoctorActivation", "Activation failed", bindingResult);
        }
        DoctorReadOnlyDTO response = doctorService.activateDoctor(doctorActivationDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
