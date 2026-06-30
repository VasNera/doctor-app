package gr.aueb.cf.doctor_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(

        @NotBlank(message = "{username.notBlank}")
        @Size(min = 2, max = 30, message = "{username.size}")
        String username,

        @NotBlank(message = "{password.notBlank}")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])^.{8,}$",
                message = "{password.pattern.invalid}")
        String password
) {
}
