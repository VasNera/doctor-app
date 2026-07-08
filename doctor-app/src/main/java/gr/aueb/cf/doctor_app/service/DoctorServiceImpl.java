package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.DoctorInsertDTO;
import gr.aueb.cf.doctor_app.dto.DoctorReadOnlyDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Doctor;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final Mapper mapper;


    @Override
    @PreAuthorize("hasAuthority('CREATE_DOCTOR')")
    @Transactional(rollbackFor = EntityAlreadyExistsException.class)
    public DoctorReadOnlyDTO createDoctor(DoctorInsertDTO doctorInsertDTO) throws EntityAlreadyExistsException {
        try {
            if (doctorRepository.existsByEmail(doctorInsertDTO.email()))
                throw new EntityAlreadyExistsException("DOCTOR", "Doctor with email: "
                        + doctorInsertDTO.email() + "already exists");

            if (doctorRepository.existsByLicenceNumber(doctorInsertDTO.licenceNumber()))
                throw new EntityAlreadyExistsException("DOCTOR", "Doctor with Licence Number: "
                        + doctorInsertDTO.licenceNumber() + "already exists");

            Doctor doctor = mapper.mapToDoctorEntity(doctorInsertDTO);

            Doctor saved = doctorRepository.save(doctor);

            log.info("Doctor with licenceNumber={} created", doctorInsertDTO.licenceNumber());
            return mapper.mapToDoctorReadOnlyDTO(saved);


        } catch (EntityAlreadyExistsException e) {
            log.error("Doctor creation failed: {}", e.getMessage());
            throw e;
        }
    }
}