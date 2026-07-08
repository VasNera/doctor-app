package gr.aueb.cf.doctor_app.service;


import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.PatientInsertDTO;
import gr.aueb.cf.doctor_app.dto.PatientReadOnlyDTO;

public interface IPatientService {

    PatientReadOnlyDTO registerPatient(PatientInsertDTO insertDTO)
            throws EntityAlreadyExistsException;

}
