package gr.aueb.cf.doctor_app.mapper;

import gr.aueb.cf.doctor_app.dto.*;
import gr.aueb.cf.doctor_app.model.*;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public User mapToUserEntity(UserInsertDTO insertDTO) {
        return new User(insertDTO.username(), insertDTO.password());
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user){
        return new UserReadOnlyDTO(user.getUuid(), user.getUsername(), user.getRole().getName());
    }

    public Doctor mapToDoctorEntity(DoctorInsertDTO insertDTO){
        Doctor doctor = new Doctor();
                doctor.setFirstname(insertDTO.firstname());
                doctor.setLastname(insertDTO.lastname());
                doctor.setEmail(insertDTO.email());
                doctor.setPhoneNumber(insertDTO.phoneNumber());
                doctor.setLicenceNumber(insertDTO.licenceNumber());
                doctor.setSpecialty(insertDTO.specialty());


        return doctor;
    }

    public DoctorReadOnlyDTO mapToDoctorReadOnlyDTO(Doctor doctor){
        return new DoctorReadOnlyDTO(
                doctor.getUuid(),
                doctor.getFirstname(),
                doctor.getLastname(),
                doctor.getEmail(),
                doctor.getSpecialty());
    }

    public Patient mapToPatientEntity(PatientInsertDTO insertDTO){
        Patient patient = new Patient();
        patient.setFirstname(insertDTO.firstname());
        patient.setLastname(insertDTO.lastname());
        patient.setEmail(insertDTO.email());
        patient.setPhoneNumber(insertDTO.phoneNumber());
        patient.setAmka(insertDTO.amka());

       return patient;
    }

    public PatientReadOnlyDTO mapToPatientReadOnlyDTO(Patient patient){
        return new PatientReadOnlyDTO(
                patient.getUuid(),
                patient.getFirstname(),
                patient.getLastname(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );
    }

    public Appointment mapToAppointmentEntity(AppointmentInsertDTO insertDTO,
                                              TimeSlot timeSlot,
                                              Patient patient){
        Appointment appointment = new Appointment();
        appointment.setReason(insertDTO.reason());
        appointment.setTimeSlot(timeSlot);
        patient.addAppointment(appointment);
        return appointment;

    }


    public AppointmentReadOnlyDTO mapToAppointmentReadOnlyDTO(Appointment appointment){
        TimeSlot timeSlot = appointment.getTimeSlot();
        Doctor doctor = timeSlot.getDoctor();

        return new AppointmentReadOnlyDTO(
                appointment.getUuid(),
                appointment.getReason(),
                timeSlot.getDate(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                appointment.getAppointmentStatus(),
                doctor.getFirstname(),
                doctor.getLastname(),
                doctor.getSpecialty()
        );

    }

    public TimeSlotReadOnlyDTO mapToTimeSlotReadOnlyDTO(TimeSlot timeslot){
        return new TimeSlotReadOnlyDTO(
                timeslot.getId(),
                timeslot.getDate(),
                timeslot.getStartTime(),
                timeslot.getEndTime(),
                timeslot.getTimeSlotStatus()

        );

    }

}
