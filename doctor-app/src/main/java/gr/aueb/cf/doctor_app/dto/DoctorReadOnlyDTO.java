package gr.aueb.cf.doctor_app.dto;

import gr.aueb.cf.doctor_app.model.enums.Specialty;


import java.util.UUID;

public record DoctorReadOnlyDTO(

        UUID uuid,
        String firstname,
        String lastname,
        String email,
        Specialty specialty

) {
}
