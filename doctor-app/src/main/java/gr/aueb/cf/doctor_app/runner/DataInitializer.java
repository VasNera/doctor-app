package gr.aueb.cf.doctor_app.runner;

import gr.aueb.cf.doctor_app.model.Role;
import gr.aueb.cf.doctor_app.model.User;
import gr.aueb.cf.doctor_app.repository.RoleRepository;
import gr.aueb.cf.doctor_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")){
            return;
        }

       Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        User admin = new User("admin", passwordEncoder.encode("Ad12345!"));
        admin.setRole(adminRole);

        userRepository.save(admin);
        log.info("Admin seeded");

    }
}
