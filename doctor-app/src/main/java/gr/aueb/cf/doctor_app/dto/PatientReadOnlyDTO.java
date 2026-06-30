package gr.aueb.cf.doctor_app.dto;

import java.util.UUID;

public record PatientReadOnlyDTO(

        UUID uuid,
        String firstname,
        String lastname,
        String email,
        String phoneNumber

) {
}
