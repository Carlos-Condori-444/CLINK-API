package pe.edu.utp.clink.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.clink.api.entity.Click;

import java.util.List;

public interface ClickRepository extends JpaRepository<Click, Long> {

    List<Click> findByEnlaceId(Long enlaceId);

    @Query("SELECT c.dispositivo, COUNT(c) FROM Click c WHERE c.enlace.id = :enlaceId GROUP BY c.dispositivo")
    List<Object[]> contarPorDispositivo(@Param("enlaceId") Long enlaceId);

    @Query("SELECT c.referido, COUNT(c) FROM Click c WHERE c.enlace.id = :enlaceId GROUP BY c.referido")
    List<Object[]> contarPorFuente(@Param("enlaceId") Long enlaceId);
}