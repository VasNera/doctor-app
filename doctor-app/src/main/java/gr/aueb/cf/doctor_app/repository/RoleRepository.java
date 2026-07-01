package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByOrderByNameAsc();

    Optional<Role> findByName(String name);
}
