package pe.edu.utp.clink.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.clink.api.entity.Click;
import pe.edu.utp.clink.api.entity.Enlace;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.ClickRepository;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.service.ClickService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClickServiceImpl implements ClickService {

    private final ClickRepository clickRepository;
    private final EnlaceRepository enlaceRepository;

    @Override
    @Transactional
    public void registrarClick(Long enlaceId, String ip, String dispositivo,
                               String navegador, String referido, String userAgent) {
        Enlace enlace = enlaceRepository.findById(enlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));

        Click click = Click.builder()
                .enlace(enlace)
                .ip(ip)
                .dispositivo(dispositivo)
                .navegador(navegador)
                .referido(referido)
                .userAgent(userAgent)
                .build();

        clickRepository.save(click);

        // Actualizar contadores (el trigger de PostgreSQL también lo hará,
        // pero dejamos esto como respaldo si no se usa trigger)
        enlace.setClicsTotal(enlace.getClicsTotal() + 1);
        if ("MOVIL".equals(dispositivo)) {
            enlace.setClicsMovil(enlace.getClicsMovil() + 1);
        } else if ("DESKTOP".equals(dispositivo)) {
            enlace.setClicsDesktop(enlace.getClicsDesktop() + 1);
        }
        enlaceRepository.save(enlace);
    }

    @Override
    public Map<String, Long> estadisticasPorDispositivo(Long enlaceId) {
        List<Object[]> resultado = clickRepository.contarPorDispositivo(enlaceId);
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            mapa.put((String) fila[0], (Long) fila[1]);
        }
        return mapa;
    }

    @Override
    public Map<String, Long> estadisticasPorFuente(Long enlaceId) {
        List<Object[]> resultado = clickRepository.contarPorFuente(enlaceId);
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (Object[] fila : resultado) {
            mapa.put((String) fila[0], (Long) fila[1]);
        }
        return mapa;
    }
}