package gr.aueb.cf.doctor_app.service;


import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.PatientInsertDTO;
import gr.aueb.cf.doctor_app.dto.PatientReadOnlyDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Patient;
import gr.aueb.cf.doctor_app.model.User;
import gr.aueb.cf.doctor_app.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements IPatientService {

    private final PatientRepository patientRepository;
    private final IUserService userService;
    private final Mapper mapper;


    @Override
    @Transactional(rollbackFor = EntityAlreadyExistsException.class)
    public PatientReadOnlyDTO registerPatient(PatientInsertDTO patientInsertDTO) throws EntityAlreadyExistsException {
        try {
            if (patientRepository.existsByAmka(patientInsertDTO.amka())) throw
                    new EntityAlreadyExistsException("PATIENT", "Patient with amka: " + patientInsertDTO.amka() + "already exists");

            if (patientRepository.existsByEmail(patientInsertDTO.email())) throw
                    new EntityAlreadyExistsException("PATIENT", "Patient with email: " + patientInsertDTO.email() + "already exists");

            Patient patient = mapper.mapToPatientEntity(patientInsertDTO);

            User user = userService.createUser(patientInsertDTO.userInsertDTO(), "PATIENT");
            patient.addUser(user);
            Patient saved = patientRepository.save(patient);
            log.info("Patient registered, username={}", patientInsertDTO.userInsertDTO().username());
            return mapper.mapToPatientReadOnlyDTO(saved);

        } catch (EntityAlreadyExistsException e) {
            log.error("Patient registration failed :{}", e.getMessage());
            throw e;
        }
    }
}
