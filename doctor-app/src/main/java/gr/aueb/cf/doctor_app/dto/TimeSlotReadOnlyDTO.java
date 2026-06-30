package gr.aueb.cf.doctor_app.dto;

import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotReadOnlyDTO(

    Long id,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    TimeSlotStatus status

) {

}
