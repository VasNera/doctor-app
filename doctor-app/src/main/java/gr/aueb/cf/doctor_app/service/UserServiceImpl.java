package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.doctor_app.dto.UserInsertDTO;
import gr.aueb.cf.doctor_app.model.Role;
import gr.aueb.cf.doctor_app.model.User;
import gr.aueb.cf.doctor_app.repository.RoleRepository;
import gr.aueb.cf.doctor_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User createUser(UserInsertDTO userInsertDTO, String roleName) throws EntityAlreadyExistsException {
        try {
            if (userRepository.existsByUsername(userInsertDTO.username()))
                throw new EntityAlreadyExistsException("User", "User with username" + userInsertDTO.username()
                        + "already exists");

            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalStateException("Role" + roleName + "not found"));


            User user = new User(userInsertDTO.username(), passwordEncoder.encode(userInsertDTO.password()));
            user.setRole(role);

            log.info("Created User ={} with role ={}", userInsertDTO.username(), roleName);
            return user;

        } catch (EntityAlreadyExistsException e) {
            log.error("User with username={} is already register", userInsertDTO.username());
            throw e;
        }
    }
}
