package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.TimeSlotNotAvailableException;
import gr.aueb.cf.doctor_app.dto.AppointmentInsertDTO;
import gr.aueb.cf.doctor_app.dto.AppointmentReadOnlyDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Appointment;
import gr.aueb.cf.doctor_app.model.Patient;
import gr.aueb.cf.doctor_app.model.TimeSlot;
import gr.aueb.cf.doctor_app.model.enums.AppointmentStatus;
import gr.aueb.cf.doctor_app.model.enums.TimeSlotStatus;
import gr.aueb.cf.doctor_app.repository.AppointmentRepository;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import gr.aueb.cf.doctor_app.repository.PatientRepository;
import gr.aueb.cf.doctor_app.repository.TimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void bookAppointment_availableSlot_createsAppointmentAndBookSlot()throws Exception {

        AppointmentInsertDTO dto = new AppointmentInsertDTO("checkup", 1L);

        Patient patient = new Patient();
        patient.setId(1L);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setTimeSlotStatus(TimeSlotStatus.AVAILABLE);

        Appointment appointment = new Appointment();
        AppointmentReadOnlyDTO expected = new AppointmentReadOnlyDTO(
                UUID.randomUUID(),
                "checkup",
                null,
                null,
                null,
                AppointmentStatus.PENDING,
                null,
                null,
                null
        );

        when(patientRepository.findByUserUsername("vaspatient")).thenReturn(Optional.of(patient));
        when(timeSlotRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(timeSlot));
        when(mapper.mapToAppointmentEntity(dto,timeSlot,patient)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(mapper.mapToAppointmentReadOnlyDTO(appointment)).thenReturn(expected);

        AppointmentReadOnlyDTO result = appointmentService.bookAppointment(dto, "vaspatient");

        assertSame(expected,result);
        assertEquals(TimeSlotStatus.BOOKED,timeSlot.getTimeSlotStatus());
        verify(appointmentRepository).save(appointment);

    }

    @Test
    void bookAppointment_slotAlreadyBooked_throws() {

        AppointmentInsertDTO dto = new AppointmentInsertDTO("checkup", 1L);

        Patient patient = new Patient();
        patient.setId(1L);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setTimeSlotStatus(TimeSlotStatus.BOOKED); // ο "δεύτερος ασθενής" βρίσκει πιασμένο slot

        when(patientRepository.findByUserUsername("vaspatient")).thenReturn(Optional.of(patient));
        when(timeSlotRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(timeSlot));

        assertThrows(TimeSlotNotAvailableException.class,
                () -> appointmentService.bookAppointment(dto, "vaspatient"));

        verify(appointmentRepository, never()).save(any()); // τίποτα δεν σώθηκε
    }

    @Test
    void bookAppointment_patientNotFound_throws() {

        AppointmentInsertDTO dto = new AppointmentInsertDTO("checkup", 1L);

        when(patientRepository.findByUserUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> appointmentService.bookAppointment(dto, "ghost"));

        verify(timeSlotRepository, never()).findByIdForUpdate(any()); // δεν έφτασε καν στο slot
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_pendingAppointment_cancelsAndFreesSlot() throws Exception {

        UUID uuid = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(1L);

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setTimeSlotStatus(TimeSlotStatus.BOOKED);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);        // ο καλών ΕΙΝΑΙ ο ιδιοκτήτης
        appointment.setTimeSlot(timeSlot);
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);

        AppointmentReadOnlyDTO expected = new AppointmentReadOnlyDTO(
                uuid, null, null, null, null,
                AppointmentStatus.CANCELLED, null, null, null);

        when(patientRepository.findByUserUsername("vaspatient")).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByUuidAndDeletedFalse(uuid)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(mapper.mapToAppointmentReadOnlyDTO(appointment)).thenReturn(expected);

        AppointmentReadOnlyDTO result = appointmentService.cancelAppointment(uuid, "vaspatient");

        assertSame(expected, result);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getAppointmentStatus()); // ακυρώθηκε
        assertEquals(TimeSlotStatus.AVAILABLE, timeSlot.getTimeSlotStatus());          // ⭐ το slot ελευθερώθηκε
    }

    @Test
    void cancelAppointment_alreadyCancelled_throws() {

        UUID uuid = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(1L);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED); // ήδη ακυρωμένο

        when(patientRepository.findByUserUsername("vaspatient")).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByUuidAndDeletedFalse(uuid)).thenReturn(Optional.of(appointment));

        assertThrows(InvalidArgumentException.class,
                () -> appointmentService.cancelAppointment(uuid, "vaspatient"));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancelAppointment_notOwner_throwsNotFound() {

        UUID uuid = UUID.randomUUID();

        Patient caller = new Patient();
        caller.setId(1L);                       // αυτός που ζητάει την ακύρωση...

        Patient owner = new Patient();
        owner.setId(2L);                        // ...ΔΕΝ είναι ο ιδιοκτήτης

        Appointment appointment = new Appointment();
        appointment.setPatient(owner);
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);

        when(patientRepository.findByUserUsername("vaspatient")).thenReturn(Optional.of(caller));
        when(appointmentRepository.findByUuidAndDeletedFalse(uuid)).thenReturn(Optional.of(appointment));

        // 404 (όχι 403): δεν αποκαλύπτουμε ότι το ραντεβού υπάρχει
        assertThrows(EntityNotFoundException.class,
                () -> appointmentService.cancelAppointment(uuid, "vaspatient"));

        verify(appointmentRepository, never()).save(any());
    }

}