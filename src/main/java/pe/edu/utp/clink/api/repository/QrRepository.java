package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.clink.api.entity.Qr;

import java.util.List;
import java.util.Optional;

public interface QrRepository extends JpaRepository<Qr, Long> {
    Optional<Qr> findByCodigo(String codigo);
    List<Qr> findByUsuarioId(Long usuarioId);
    long countByUsuarioIdAndActivoTrue(Long usuarioId);
}