package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.DoctorInsertDTO;
import gr.aueb.cf.doctor_app.dto.DoctorReadOnlyDTO;

public interface IDoctorService {

    DoctorReadOnlyDTO createDoctor(DoctorInsertDTO doctorInsertDTO)
            throws EntityAlreadyExistsException;
}
