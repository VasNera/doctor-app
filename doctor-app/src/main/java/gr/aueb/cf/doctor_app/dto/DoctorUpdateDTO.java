package gr.aueb.cf.doctor_app.dto;

import gr.aueb.cf.doctor_app.model.enums.Specialty;
import jakarta.validation.constraints.*;

public record DoctorUpdateDTO(

        @NotBlank(message = "{firstname.notBlank}")
        @Size(min = 2, max = 30, message = "{firstname.size}")
        String firstname,

        @NotBlank(message = "{lastname.notBlank}")
        @Size(min = 2, max = 30, message = "{lastname.size}")
        String lastname,

        @NotBlank(message = "{email.notBlank}")
        @Email(message = "{email.invalid}")
        String email,

        @NotBlank(message = "{licence.notBlank}")
        @Pattern(regexp ="^DOC-\\d{4}-\\d{5}$", message = "{licence.pattern.invalid}")
        String licenceNumber,

        @NotNull(message = "{specialty.notNull}")
        Specialty specialty
) {
}
