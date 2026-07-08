package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.UserInsertDTO;
import gr.aueb.cf.doctor_app.model.User;


import java.util.UUID;

public interface IUserService {

    User createUser(UserInsertDTO userInsertDTO, String roleName) throws EntityAlreadyExistsException;
}
