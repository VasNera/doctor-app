package gr.aueb.cf.doctor_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aueb.cf.doctor_app.core.GlobalExceptionHandler;
import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.PatientInsertDTO;
import gr.aueb.cf.doctor_app.dto.PatientReadOnlyDTO;
import gr.aueb.cf.doctor_app.dto.UserInsertDTO;
import gr.aueb.cf.doctor_app.service.IPatientService;
import gr.aueb.cf.doctor_app.validator.PatientInsertValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private IPatientService patientService;

    @Mock
    private PatientInsertValidator patientInsertValidator;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private final PatientInsertDTO validDto = new PatientInsertDTO(
            "Vasilis", "Neratzis", "vas@example.com", "12345678901", "6900000000",
            new UserInsertDTO("vaspatient", "Pat123!@"));

    @BeforeEach
    void setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(patientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerPatient_validBody_returns201WithBody() throws Exception {
        PatientReadOnlyDTO response = new PatientReadOnlyDTO(
                UUID.randomUUID(), "Vasilis", "Neratzis", "vas@example.com", "6900000000");
        when(patientService.registerPatient(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Vasilis"))
                .andExpect(jsonPath("$.email").value("vas@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist()); // no password leak
    }

    @Test
    void registerPatient_duplicate_returns409() throws Exception {
        when(patientService.registerPatient(any()))
                .thenThrow(new EntityAlreadyExistsException("PATIENT", "Patient already exists"));

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isConflict());
    }
}
