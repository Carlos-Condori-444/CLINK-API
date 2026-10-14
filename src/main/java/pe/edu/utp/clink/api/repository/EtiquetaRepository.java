package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.clink.api.entity.Etiqueta;

import java.util.List;
import java.util.Optional;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    List<Etiqueta> findByUsuarioId(Long usuarioId);
    Optional<Etiqueta> findByUsuarioIdAndNombre(Long usuarioId, String nombre);
    boolean existsByUsuarioIdAndNombre(Long usuarioId, String nombre);
}