package gr.aueb.cf.doctor_app.dto;


import jakarta.validation.constraints.NotNull;

public record AppointmentInsertDTO(


        String reason,

        @NotNull(message = "{timeSlot.notNull}")
        Long timeSlotId


) {
}
