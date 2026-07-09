package gr.aueb.cf.doctor_app.service;


import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.TimeSlotNotAvailableException;
import gr.aueb.cf.doctor_app.dto.AppointmentInsertDTO;
import gr.aueb.cf.doctor_app.dto.AppointmentReadOnlyDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Appointment;
import gr.aueb.cf.doctor_app.model.Doctor;
import gr.aueb.cf.doctor_app.model.Patient;
import gr.aueb.cf.doctor_app.model.TimeSlot;
import gr.aueb.cf.doctor_app.model.enums.AppointmentStatus;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import gr.aueb.cf.doctor_app.repository.AppointmentRepository;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import gr.aueb.cf.doctor_app.repository.PatientRepository;
import gr.aueb.cf.doctor_app.repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements IAppointmentService {


    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final Mapper mapper;


    @Override
    @PreAuthorize("hasAuthority('CREATE_APPOINTMENT')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, TimeSlotNotAvailableException.class})
    public AppointmentReadOnlyDTO bookAppointment(AppointmentInsertDTO appointmentInsertDTO, String username)
            throws EntityNotFoundException, TimeSlotNotAvailableException {

        try{
            Patient patient = patientRepository.findByUserUsername(username)
                    .orElseThrow(()-> new EntityNotFoundException("PATIENT",
                            "Patient with username: " + username + "not found"));

            TimeSlot timeSlot = timeSlotRepository.findByIdForUpdate(appointmentInsertDTO.timeSlotId())
                    .orElseThrow(()-> new EntityNotFoundException("TIMESLOT",
                            "Timeslot not found with id: " + appointmentInsertDTO.timeSlotId()));

            if (timeSlot.getTimeSlotStatus() != TimeSlotStatus.AVAILABLE)
                throw new TimeSlotNotAvailableException("TIMESLOT", "Timeslot is not available");

            Appointment appointment = mapper.mapToAppointmentEntity(appointmentInsertDTO, timeSlot, patient);
            timeSlot.setTimeSlotStatus(TimeSlotStatus.BOOKED);

            Appointment saved = appointmentRepository.save(appointment);
                log.info("Appointment is booked, uuid={}, for user={}", saved.getUuid(), username);

                return mapper.mapToAppointmentReadOnlyDTO(saved);


        }catch (EntityNotFoundException | TimeSlotNotAvailableException e){
            log.error("Appointment booking failed: {}" , e.getMessage());
            throw e;
        }

    }


    @Override
    @PreAuthorize("hasAuthority('VIEW_APPOINTMENTS')")
    @Transactional(readOnly = true)
    public Page<AppointmentReadOnlyDTO> getPatientsAppointments(
            String username, AppointmentStatus appointmentStatus, Pageable pageable)
            throws EntityNotFoundException {
        Patient patient = patientRepository.findByUserUsername(username)
                .orElseThrow(()->
                        new EntityNotFoundException
                                ("PATIENT","Patient with username: "+ username +" not found"));
        Page<Appointment> appointments = (appointmentStatus !=null)
                ? appointmentRepository.findAllByPatientIdAndAppointmentStatus(patient.getId(),appointmentStatus,pageable)
                : appointmentRepository.findAllByPatientId(patient.getId(), pageable);
        return appointments.map(mapper::mapToAppointmentReadOnlyDTO);
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_APPOINTMENTS')")
    @Transactional(readOnly = true)
    public Page<AppointmentReadOnlyDTO> getDoctorsAppointments(
            String username, AppointmentStatus appointmentStatus, Pageable pageable)
            throws EntityNotFoundException {

        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(()-> new EntityNotFoundException
                        ("DOCTOR", "Doctor with username: "+ username +" not found"));
        Page<Appointment> appointments = (appointmentStatus !=null)
                ? appointmentRepository.findAllByTimeSlotDoctorIdAndAppointmentStatus(doctor.getId(),
                appointmentStatus, pageable)
                : appointmentRepository.findAllByTimeSlotDoctorId(doctor.getId(), pageable);
        return appointments.map(mapper::mapToAppointmentReadOnlyDTO);

    }

    @Override
    @PreAuthorize("hasAuthority('CANCEL_APPOINTMENT')")
    @Transactional(rollbackFor = {EntityNotFoundException.class, InvalidArgumentException.class})
    public AppointmentReadOnlyDTO cancelAppointment(UUID uuid, String username)
            throws EntityNotFoundException, InvalidArgumentException {
        try{
            Patient patient = patientRepository.findByUserUsername(username)
                    .orElseThrow(()-> new EntityNotFoundException(
                            "PATIENT", "Patient with username: "+ username +" not found"));

            Appointment appointment = appointmentRepository.findByUuidAndDeletedFalse(uuid)
                    .orElseThrow(()-> new EntityNotFoundException(
                            "APPOINTMENT", "Appointment with uuid: "+ uuid +"not found"));

            if (!appointment.getPatient().getId().equals(patient.getId()))
                throw new EntityNotFoundException("APPOINTMENT", "Appointment with uuid: "+ uuid +"not found");

            if (appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED
            || appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED)
                throw new InvalidArgumentException("APPOINTMENT", "Appointment cannot be cancelled");

            appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
            appointment.getTimeSlot().setTimeSlotStatus(TimeSlotStatus.AVAILABLE);

            Appointment saved = appointmentRepository.save(appointment);
            log.info("Appointment cancelled. Uuid={} by User={} " , saved.getUuid(), username);
            return mapper.mapToAppointmentReadOnlyDTO(saved);


        }catch (EntityNotFoundException | InvalidArgumentException e){
            log.error("Appointment Cancellation failed: {} " , e.getMessage());
            throw e;
        }
    }
}
