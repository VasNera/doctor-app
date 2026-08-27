package gr.aueb.cf.doctor_app.runner;


import gr.aueb.cf.doctor_app.model.*;
import gr.aueb.cf.doctor_app.model.enums.Specialty;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import gr.aueb.cf.doctor_app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DemoDataInitializer implements CommandLineRunner {

    private static final String DOCTOR_EMAIL = "demo.doctor@doctorapp.gr";
    private static final String PATIENT_EMAIL = "demo.patient@doctorapp.gr";
    private static final LocalTime WORK_START = LocalTime.of(9,0);
    private static final LocalTime WORK_END = LocalTime.of(17,0);
    private static final int TIMESLOT_DURATION = 30;
    private static final int DAYS_AHEAD = 14;

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    @Transactional
    public void run(String... args) throws Exception {
      Doctor doctor = doctorRepository.findByEmailAndDeletedFalse(DOCTOR_EMAIL)
              .orElseGet(this::seedDoctor);

      if (!patientRepository.existsByEmail(PATIENT_EMAIL)){
          seedPatient();
        }

      seedTimeslots(doctor);

    }

    private Doctor seedDoctor(){
        Role doctorRole = roleRepository.findByName("DOCTOR")
                .orElseThrow(()-> new IllegalStateException("Doctor Role not found"));

        User user = new User("drdemo", passwordEncoder.encode("Doc12345!"));
        user.setRole(doctorRole);

        Doctor doctor = new Doctor();
        doctor.setFirstname("Nikos");
        doctor.setLastname("Papadopoulos");
        doctor.setEmail(DOCTOR_EMAIL);
        doctor.setPhoneNumber("2101234567");
        doctor.setLicenceNumber("DEMO-0001");
        doctor.setSpecialty(Specialty.CARDIOLOGY);
        doctor.addUser(user);

        Doctor saved = doctorRepository.save(doctor);
        log.info("Demo doctor seeded: Username: drdemo");
        return saved;

    }

    private void seedPatient(){
        Role patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(()-> new IllegalStateException("Patient role not found"));

        User user = new User("patientdemo", passwordEncoder.encode("Pat12345!"));
        user.setRole(patientRole);

        Patient patient = new Patient();
        patient.setFirstname("Maria");
        patient.setLastname("Georgiou");
        patient.setEmail(PATIENT_EMAIL);
        patient.setPhoneNumber("2109876543");
        patient.setAmka("99999999999");
        patient.addUser(user);

        patientRepository.save(patient);
        log.info("Demo patient seeded: Username: patientdemo");
    }

    private void seedTimeslots(Doctor doctor){
        List<TimeSlot> slots = new ArrayList<>();
        LocalDate today = LocalDate.now();

        today.datesUntil(today.plusDays(DAYS_AHEAD))
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY
                && date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .forEach(date ->{
                    for(LocalTime start = WORK_START; start.isBefore(WORK_END);
                    start = start.plusMinutes(TIMESLOT_DURATION)) {
                        if (timeSlotRepository.existsByDoctorIdAndDateAndStartTime(
                                doctor.getId(), date, start)) {
                            continue;
                        }
                        TimeSlot timeSlot = new TimeSlot();
                        timeSlot.setDate(date);
                        timeSlot.setStartTime(start);
                        timeSlot.setEndTime(start.plusMinutes(TIMESLOT_DURATION));
                        timeSlot.setTimeSlotStatus(TimeSlotStatus.AVAILABLE);
                        timeSlot.setDoctor(doctor);
                        slots.add(timeSlot);
                    }
                });

        if (!slots.isEmpty()){
            timeSlotRepository.saveAll(slots);
            log.info("Seeded {} demo timeslots ", slots.size());
        }


    }
}
