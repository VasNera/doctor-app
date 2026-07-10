package gr.aueb.cf.doctor_app.controller;


import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.core.exceptions.ValidationException;
import gr.aueb.cf.doctor_app.dto.ErrorResponseDTO;
import gr.aueb.cf.doctor_app.dto.PatientInsertDTO;
import gr.aueb.cf.doctor_app.dto.PatientReadOnlyDTO;
import gr.aueb.cf.doctor_app.dto.ValidationErrorResponseDTO;
import gr.aueb.cf.doctor_app.service.IPatientService;
import gr.aueb.cf.doctor_app.validator.PatientInsertValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final IPatientService patientService;
    private final PatientInsertValidator patientInsertValidator;

    @Operation(
            summary = "Register a patient",
            description = "Self-registers a patient together with a user account."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201" , description = "Patient registered",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientReadOnlyDTO.class))),
            @ApiResponse(
                    responseCode = "409" , description = "Patient already exists",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = "400" , description = "Validation Error",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponseDTO.class)))
    })

    @PostMapping
    public ResponseEntity<PatientReadOnlyDTO> registerPatient(
            @Valid
            @RequestBody
            PatientInsertDTO patientInsertDTO,
            BindingResult bindingResult) throws EntityAlreadyExistsException, ValidationException {

        patientInsertValidator.validate(patientInsertDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ValidationException("Patient", "Validation failed", bindingResult);

        }

        PatientReadOnlyDTO response = patientService.registerPatient(patientInsertDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

}
