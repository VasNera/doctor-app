package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByOrderByNameAsc();
}
