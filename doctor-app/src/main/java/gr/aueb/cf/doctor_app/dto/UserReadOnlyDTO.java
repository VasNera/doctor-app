package gr.aueb.cf.doctor_app.dto;

import java.util.UUID;

public record UserReadOnlyDTO(

        UUID uuid,
        String username,
        String role

) {
}
