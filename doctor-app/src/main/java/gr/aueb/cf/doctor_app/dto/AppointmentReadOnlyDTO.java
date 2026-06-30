package gr.aueb.cf.doctor_app.dto;

import gr.aueb.cf.doctor_app.model.enums.AppointmentStatus;
import gr.aueb.cf.doctor_app.model.enums.Specialty;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentReadOnlyDTO(

        UUID uuid,
        String reason,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status,
        String doctorFirstname,
        String doctorLastname,
        Specialty specialty
) {
}
