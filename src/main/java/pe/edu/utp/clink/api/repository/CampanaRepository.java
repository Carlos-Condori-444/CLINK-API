package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.clink.api.entity.Campana;

import java.util.List;

public interface CampanaRepository extends JpaRepository<Campana, Long> {
    List<Campana> findByUsuarioId(Long usuarioId);
}