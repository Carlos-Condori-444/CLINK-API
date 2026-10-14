package pe.edu.utp.clink.api.service;

import pe.edu.utp.clink.api.entity.Click;

import java.util.Map;

public interface ClickService {
    void registrarClick(Long enlaceId, String ip, String dispositivo, String navegador, String referido, String userAgent);
    Map<String, Long> estadisticasPorDispositivo(Long enlaceId);
    Map<String, Long> estadisticasPorFuente(Long enlaceId);
}