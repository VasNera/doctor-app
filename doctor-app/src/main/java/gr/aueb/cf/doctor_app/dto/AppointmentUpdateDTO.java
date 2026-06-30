package gr.aueb.cf.doctor_app.dto;

import jakarta.validation.constraints.NotNull;

public record AppointmentUpdateDTO(

        String reason,

        @NotNull(message = "{timeSlot.notNull}")
        Long timeSlotId
) {
}
