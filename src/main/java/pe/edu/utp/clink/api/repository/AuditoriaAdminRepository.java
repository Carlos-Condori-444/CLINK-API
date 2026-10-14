package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.clink.api.entity.AuditoriaAdmin;

import java.util.List;

public interface AuditoriaAdminRepository extends JpaRepository<AuditoriaAdmin, Long> {
    List<AuditoriaAdmin> findByAdminId(Long adminId);
}