package gr.aueb.cf.doctor_app.validator;

import gr.aueb.cf.doctor_app.dto.PatientInsertDTO;
import gr.aueb.cf.doctor_app.repository.PatientRepository;
import gr.aueb.cf.doctor_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
@Slf4j
public class PatientInsertValidator implements Validator {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return PatientInsertDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PatientInsertDTO patientInsertDTO = (PatientInsertDTO) target;

        if (patientRepository.existsByAmka(patientInsertDTO.amka())){
            errors.rejectValue("amka","patient.amka.exists","amka already exists");
        }
        if (patientRepository.existsByEmail(patientInsertDTO.email())){
            errors.rejectValue("email", "patient.email.exists","email already exists");
        }
        if (patientInsertDTO.userInsertDTO() != null && userRepository.existsByUsername(patientInsertDTO.userInsertDTO().username())) {
            errors.rejectValue("userInsertDTO.username", "user.username.exists", "Username already exists");
        }

    }
}
