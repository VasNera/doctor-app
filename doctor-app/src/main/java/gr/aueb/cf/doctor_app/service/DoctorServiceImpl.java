package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.dto.DoctorActivationDTO;
import gr.aueb.cf.doctor_app.dto.DoctorInsertDTO;
import gr.aueb.cf.doctor_app.dto.DoctorReadOnlyDTO;
import gr.aueb.cf.doctor_app.mapper.Mapper;
import gr.aueb.cf.doctor_app.model.Doctor;
import gr.aueb.cf.doctor_app.model.User;
import gr.aueb.cf.doctor_app.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements IDoctorService {


    private final DoctorRepository doctorRepository;
    private final Mapper mapper;
    private final IEmailService emailService;
    private final IUserService userService;


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

            doctor.setActivationToken(UUID.randomUUID().toString());
            doctor.setActivationTokenExpiresAt(Instant.now().plus(48, ChronoUnit.HOURS));

            Doctor saved = doctorRepository.save(doctor);

            emailService.sendActivationEmail(saved.getEmail(), saved.getActivationToken());

            log.info("Doctor with licenceNumber={} created", doctorInsertDTO.licenceNumber());
            return mapper.mapToDoctorReadOnlyDTO(saved);


        } catch (EntityAlreadyExistsException e) {
            log.error("Doctor creation failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityAlreadyExistsException.class,
            InvalidArgumentException.class ,
            EntityNotFoundException.class})
    public DoctorReadOnlyDTO activateDoctor(DoctorActivationDTO activationDTO)
            throws EntityAlreadyExistsException, InvalidArgumentException, EntityNotFoundException {

        try {
            Doctor doctor = doctorRepository.findByActivationToken(activationDTO.token())
                    .orElseThrow(() -> new EntityNotFoundException("DOCTOR",
                            "Invalid activation token"));

            if (doctor.getUser()!=null) throw new InvalidArgumentException("DOCTOR",
                    "Doctor account is already activated");

            if (doctor.getActivationTokenExpiresAt() == null
                || doctor.getActivationTokenExpiresAt().isBefore(Instant.now()))
                throw new InvalidArgumentException("DOCTOR", "Activation token is expired");

            User user = userService.createUser(activationDTO.userInsertDTO(), "DOCTOR");
            doctor.addUser(user);

            doctor.setActivationToken(null);
            doctor.setActivationTokenExpiresAt(null);

            Doctor saved = doctorRepository.save(doctor);
            log.info("Doctor activated. Licence Number={}" , saved.getLicenceNumber());
            return mapper.mapToDoctorReadOnlyDTO(saved);

        }catch (EntityNotFoundException | InvalidArgumentException | EntityAlreadyExistsException e){
            log.error("Doctor activation failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_DOCTORS')")
    @Transactional(readOnly = true)
    public Page<DoctorReadOnlyDTO> getDoctors(Pageable pageable) {
        return doctorRepository.findAllByDeletedFalse(pageable)
                .map(mapper::mapToDoctorReadOnlyDTO);
    }
}