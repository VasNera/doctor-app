package gr.aueb.cf.doctor_app.repository;

import gr.aueb.cf.doctor_app.model.Capability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapabilityRepository extends JpaRepository <Capability, Long> {
}
