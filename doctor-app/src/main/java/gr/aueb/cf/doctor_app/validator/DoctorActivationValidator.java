package gr.aueb.cf.doctor_app.validator;

import gr.aueb.cf.doctor_app.dto.DoctorActivationDTO;
import gr.aueb.cf.doctor_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorActivationValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return DoctorActivationDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DoctorActivationDTO activationDTO = (DoctorActivationDTO) target;

        if (activationDTO.userInsertDTO() !=null
        && userRepository.existsByUsername(activationDTO.userInsertDTO().username())){
            errors.rejectValue("userInsertDTO.username", "user.username.exists",
                    "Username already exists");
        }
    }
}
