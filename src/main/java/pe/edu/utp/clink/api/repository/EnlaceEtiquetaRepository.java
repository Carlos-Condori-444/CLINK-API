package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.clink.api.entity.EnlaceEtiqueta;
import pe.edu.utp.clink.api.entity.EnlaceEtiquetaId;

import java.util.List;

public interface EnlaceEtiquetaRepository extends JpaRepository<EnlaceEtiqueta, EnlaceEtiquetaId> {
    List<EnlaceEtiqueta> findByEnlaceId(Long enlaceId);
    List<EnlaceEtiqueta> findByEtiquetaId(Long etiquetaId);
}