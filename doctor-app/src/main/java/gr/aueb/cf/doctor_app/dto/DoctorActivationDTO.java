package gr.aueb.cf.doctor_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record DoctorActivationDTO(


        @NotBlank(message = "{activation.token.notBlank}")
        String token,

         @Valid
         @NotNull
         UserInsertDTO userInsertDTO

) {
}
