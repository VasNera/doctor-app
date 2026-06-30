package gr.aueb.cf.doctor_app.dto;

import jakarta.validation.constraints.*;

public record PatientUpdateDTO(

        @NotBlank(message = "{firstname.notBlank}")
        @Size(min = 2, max = 30, message = "{firstname.size}")
        String firstname,

        @NotBlank(message = "{lastname.notBlank}")
        @Size(min = 2, max = 30, message = "{lastname.size}")
        String lastname,

        @NotBlank(message = "{email.notBlank}")
        @Email(message = "{email.invalid}")
        String email,

        @NotBlank(message = "{amka.notBlank}")
        @Pattern(regexp = "^\\d{11}$", message = "{amka.pattern.invalid}")
        String amka,

        @NotNull(message = "{phoneNumber.notNull}")
        String phoneNumber
) {
}
