package gr.aueb.cf.doctor_app.validator;

import gr.aueb.cf.doctor_app.dto.DoctorInsertDTO;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorInsertValidator implements Validator {

    private final DoctorRepository doctorRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return DoctorInsertDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DoctorInsertDTO doctorInsertDTO = (DoctorInsertDTO) target;

        if (doctorRepository.existsByEmail(doctorInsertDTO.email())) {
            errors.rejectValue("email", "doctor.email.exists", "email already exists");
        }

        if (doctorRepository.existsByLicenceNumber(doctorInsertDTO.licenceNumber())){
            errors.rejectValue("licenceNumber", "doctor.licenceNumber.exists",
                    "Licence Number already exists");
        }
    }
}
