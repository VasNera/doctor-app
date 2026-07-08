package gr.aueb.cf.doctor_app.model;


import gr.aueb.cf.doctor_app.model.enums.Specialty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "doctors")
public class Doctor extends AbstractEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    @Column(unique = true,nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name ="phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "licence_number", nullable = false, unique = true)
    private String licenceNumber;

    @Enumerated(EnumType.STRING)
    private Specialty specialty;

    @Column(name = "activation_token", unique = true)
    private String activationToken;

    @Column(name = "activation_token_expires_at", columnDefinition = "DATETIME")
    private Instant activationTokenExpiresAt;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", unique = true)
    private User user;


    @OneToMany(mappedBy = "doctor",fetch = FetchType.LAZY)
    private Set<TimeSlot> timeSlots = new HashSet<>();


    public void addTimeSlot(TimeSlot timeSlot){
        timeSlots.add(timeSlot);
        timeSlot.setDoctor(this);
    }

    public void removeTimeSlot(TimeSlot timeSlot){
        timeSlots.remove(timeSlot);
        timeSlot.setDoctor(null);

    }


    public void addUser(User user){
        this.user = user;
        user.setDoctor(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Doctor doctor)) return false;
        return Objects.equals(getUuid(), doctor.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUuid());
    }
}
