package gr.aueb.cf.doctor_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record DoctorActivationDTO(


        @NotBlank(message = "{licence.notBlank}")
        @Pattern(regexp ="^DOC-\\d{4}-\\d{5}$", message = "{licence.pattern.invalid}")
        String licenceNumber,

         @Valid
         @NotNull
         UserInsertDTO userInsertDTO

) {
}
