package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.dto.DoctorActivationDTO;
import gr.aueb.cf.doctor_app.dto.DoctorInsertDTO;
import gr.aueb.cf.doctor_app.dto.DoctorReadOnlyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDoctorService {

    DoctorReadOnlyDTO createDoctor(DoctorInsertDTO doctorInsertDTO)
            throws EntityAlreadyExistsException;

    DoctorReadOnlyDTO activateDoctor(DoctorActivationDTO activationDTO)
        throws EntityAlreadyExistsException, InvalidArgumentException, EntityNotFoundException;

    Page<DoctorReadOnlyDTO> getDoctors(Pageable pageable);
}
