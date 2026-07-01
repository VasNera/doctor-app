package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<User> findByUsername(String username);

    Page<User> findAllByDeletedFalse(Pageable pageable);

    Optional<User> findByUsernameAndDeletedFalse(String username);

    boolean existsByUsername(String username);
}
