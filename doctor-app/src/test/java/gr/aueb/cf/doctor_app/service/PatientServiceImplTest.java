package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.PatientInsertDTO;
import gr.aueb.cf.doctor_app.dto.PatientReadOnlyDTO;
import gr.aueb.cf.doctor_app.dto.UserInsertDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Patient;
import gr.aueb.cf.doctor_app.model.User;
import gr.aueb.cf.doctor_app.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private Mapper mapper;

    @Mock
    private IUserService userService;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    void registerPatient_uniqueAmkaAndEmail_registersPatient()throws Exception{

        PatientInsertDTO insertDTO = new PatientInsertDTO(
                "Fanis",
                "Pitsilos",
                "pitsilos@example.com",
                "12345678901",
                "6947653211",
                new UserInsertDTO("pitsilos",
                        "Pitsilos1@"));


        Patient patient = new Patient();
        User user = new User();

        PatientReadOnlyDTO expected = new PatientReadOnlyDTO(
                UUID.randomUUID(),
                "Fanis",
                "Pitsilos",
                "pitsilos@example.com",
                "6947653211");

        when(patientRepository.existsByAmka("12345678901")).thenReturn(false);
        when(patientRepository.existsByEmail("pitsilos@example.com")).thenReturn(false);
        when(mapper.mapToPatientEntity(insertDTO)).thenReturn(patient);
        when(userService.createUser(insertDTO.userInsertDTO(), "PATIENT")).thenReturn(user);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(mapper.mapToPatientReadOnlyDTO(patient)).thenReturn(expected);

        PatientReadOnlyDTO result = patientService.registerPatient(insertDTO);

        assertSame(expected,result);
        assertNotNull(patient.getUser());

    }

    @Test
    void registerPatient_duplicateAmka_throwsEntityAlreadyExists()throws Exception{
        PatientInsertDTO insertDTO = new PatientInsertDTO(
                "Fanis",
                "Pitsilos",
                "pitsilos@example.com",
                "12345678901",
                "6947653211",
                new UserInsertDTO(
                        "pitsilos",
                        "Pitsilos1@"));
        when(patientRepository.existsByAmka("12345678901")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,()-> patientService.registerPatient(insertDTO));

        verify(userService,never()).createUser(any(),any());


    }

    @Test
    void registerPatient_duplicateEmail_throwsEntityAlreadyExists()throws Exception{
        PatientInsertDTO insertDTO = new PatientInsertDTO(
                "Fanis",
                "Pitsilos",
                "pitsilos@example.com",
                "12345678901",
                "6947653211",
                new UserInsertDTO(
                        "pitsilos",
                        "Pitsilos1@"));
        when(patientRepository.existsByAmka("12345678901")).thenReturn(false);
        when(patientRepository.existsByEmail("pitsilos@example.com")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> patientService.registerPatient(insertDTO));

        verify(userService, never()).createUser(any(), any());

    }

}