package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.clink.api.entity.Enlace;

import java.util.List;
import java.util.Optional;

public interface EnlaceRepository extends JpaRepository<Enlace, Long> {

    Optional<Enlace> findByCodigo(String codigo);
    Optional<Enlace> findByAlias(String alias);
    boolean existsByAlias(String alias);

    List<Enlace> findByUsuarioIdAndEstadoNot(Long usuarioId, String estado);

    long countByUsuarioIdAndEstadoNot(Long usuarioId, String estado);

    @Query("SELECT e FROM Enlace e WHERE e.usuario.id = :usuarioId AND e.estado <> 'ELIMINADO' ORDER BY e.fechaCreacion DESC")
    List<Enlace> listarActivosPorUsuario(@Param("usuarioId") Long usuarioId);
}