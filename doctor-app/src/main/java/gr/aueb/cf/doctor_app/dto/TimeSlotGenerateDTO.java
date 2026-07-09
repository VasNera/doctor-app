package gr.aueb.cf.doctor_app.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TimeSlotGenerateDTO(


        @NotNull(message = "{fromDate.notNull}")
        @FutureOrPresent(message = "{fromDate.futureOrPresent}")
        LocalDate fromDate,

        @NotNull(message = "{toDate.notNull}")
        LocalDate toDate

) {
}
