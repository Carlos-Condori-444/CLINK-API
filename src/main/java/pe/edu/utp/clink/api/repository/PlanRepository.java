package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.clink.api.entity.Plan;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByNombre(String nombre);
}