package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.dto.DoctorActivationDTO;
import gr.aueb.cf.doctor_app.dto.DoctorInsertDTO;
import gr.aueb.cf.doctor_app.dto.DoctorReadOnlyDTO;
import gr.aueb.cf.doctor_app.dto.UserInsertDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Doctor;
import gr.aueb.cf.doctor_app.model.User;
import gr.aueb.cf.doctor_app.model.enums.Specialty;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private Mapper mapper;

    @Mock
    private IEmailService emailService;

    @Mock
    private IUserService userService;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void createDoctor_ValidDoctor_createsDoctor()throws Exception{

        DoctorInsertDTO dto = new DoctorInsertDTO(
                "Katerina",
                "Dalaka",
                "dalaka@example.com",
                "6948642817",
                "DOC-2025-12345",
                Specialty.NEUROLOGY
        );

        Doctor doctor = new Doctor();
        doctor.setEmail(dto.email());
        doctor.setLicenceNumber(dto.licenceNumber());

        DoctorReadOnlyDTO expected = new DoctorReadOnlyDTO(
                UUID.randomUUID(),
                dto.firstname(),
                dto.lastname(),
                dto.email(),
                dto.specialty()
        );

        when(doctorRepository.existsByEmail(dto.email())).thenReturn(false);
        when(doctorRepository.existsByLicenceNumber(dto.licenceNumber())).thenReturn(false);

        when(mapper.mapToDoctorEntity(dto)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(mapper.mapToDoctorReadOnlyDTO(doctor)).thenReturn(expected);

        DoctorReadOnlyDTO result = doctorService.createDoctor(dto);

        assertSame(expected, result);
        assertNotNull(doctor.getActivationToken());
        assertNotNull(doctor.getActivationTokenExpiresAt());

        verify(doctorRepository).save(any(Doctor.class));
        verify(emailService).sendActivationEmail(eq(doctor.getEmail()),
                anyString());

    }

    @Test
    void createDoctor_duplicateEmail_throwsEntityAlreadyExists(){

        DoctorInsertDTO dto = new DoctorInsertDTO(
                "Katerina",
                "Dalaka",
                "dalaka@example.com",
                "6948642817",
                "DOC-2025-12345",
                Specialty.NEUROLOGY
        );


        when(doctorRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> doctorService.createDoctor(dto));

        verify(emailService,never()).sendActivationEmail(any(),any());

    }

    @Test
    void createDoctor_duplicateLicenceNumber_throwsEntityAlreadyExists(){

        DoctorInsertDTO dto = new DoctorInsertDTO(
                "Katerina",
                "Dalaka",
                "dalaka@example.com",
                "6948642817",
                "DOC-2025-12345",
                Specialty.NEUROLOGY
        );

        when(doctorRepository.existsByEmail(dto.email())).thenReturn(false);
        when(doctorRepository.existsByLicenceNumber(dto.licenceNumber())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> doctorService.createDoctor(dto));

        verify(emailService,never()).sendActivationEmail(any(),any());

    }

    @Test
    void activateDoctor_validToken_createsUserAndDeleteToken()throws Exception{

        UserInsertDTO userDto = new UserInsertDTO("dalakito","Dalakito!2");
        DoctorActivationDTO activationDTO = new DoctorActivationDTO("valid-token", userDto);

        Doctor doctor = new Doctor();
        doctor.setActivationToken("valid-token");
        doctor.setActivationTokenExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));

        User user = new User();

        DoctorReadOnlyDTO expected = new DoctorReadOnlyDTO(
                UUID.randomUUID(),
                "Katerina",
                "Dalaka",
                "dalaka@example.com",
                Specialty.NEUROLOGY
        );

        when(doctorRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(doctor));
        when(userService.createUser(userDto,"DOCTOR")).thenReturn(user);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(mapper.mapToDoctorReadOnlyDTO(doctor)).thenReturn(expected);

        DoctorReadOnlyDTO result = doctorService.activateDoctor(activationDTO);

        assertSame(expected,result);
        assertNull(doctor.getActivationToken());
        assertNull(doctor.getActivationTokenExpiresAt());
        assertNotNull(doctor.getUser());

    }

    @Test
    void activateDoctor_invalidToken_throwsNotFound()throws Exception{

        UserInsertDTO userDTO = new UserInsertDTO("dalakito", "Dalaka1!");
        DoctorActivationDTO activationDTO = new DoctorActivationDTO("bad-token", userDTO);

        when(doctorRepository.findByActivationToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,()->doctorService.activateDoctor(activationDTO));

        verify(userService, never()).createUser(any(),any());

    }

    @Test
    void activateDoctor_alreadyActivated_throwInvalidArgument()throws Exception{
        UserInsertDTO userDTO = new UserInsertDTO("dalakito", "Dalaka1!");
        DoctorActivationDTO activationDTO = new DoctorActivationDTO("valid-token", userDTO);

        Doctor doctor = new Doctor();
        doctor.setUser(new User());

        when(doctorRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(doctor));

        assertThrows(InvalidArgumentException.class,()->doctorService.activateDoctor(activationDTO));
        verify(userService, never()).createUser(any(),any());

    }




}