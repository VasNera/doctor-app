package gr.aueb.cf.doctor_app.model;


import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "timeslots")
public class TimeSlot extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeSlotStatus timeSlotStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(mappedBy ="timeSlot", fetch = FetchType.LAZY)
    private Appointment appointment;

    public void addDoctor(Doctor doctor){
        this.doctor = doctor;
        doctor.getTimeSlots().add(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TimeSlot timeSlot)) return false;
        return id != null && id.equals(timeSlot.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
