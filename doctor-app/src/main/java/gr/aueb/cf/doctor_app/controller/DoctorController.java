package gr.aueb.cf.doctor_app.controller;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.ValidationException;
import gr.aueb.cf.doctor_app.dto.DoctorActivationDTO;
import gr.aueb.cf.doctor_app.dto.DoctorInsertDTO;
import gr.aueb.cf.doctor_app.dto.DoctorReadOnlyDTO;
import gr.aueb.cf.doctor_app.service.IDoctorService;
import gr.aueb.cf.doctor_app.validator.DoctorActivationValidator;
import gr.aueb.cf.doctor_app.validator.DoctorInsertValidator;
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
