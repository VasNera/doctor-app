package gr.aueb.cf.doctor_app.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDTO(

        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
