package pe.edu.utp.clink.api.service;

import pe.edu.utp.clink.api.dto.request.EnlaceRequest;
import pe.edu.utp.clink.api.dto.response.EnlaceResponse;

import java.util.List;

public interface EnlaceService {
    EnlaceResponse crear(Long usuarioId, EnlaceRequest request);
    List<EnlaceResponse> listar(Long usuarioId);
    EnlaceResponse obtener(Long usuarioId, Long enlaceId);
    void eliminar(Long usuarioId, Long enlaceId);
    String redirigir(String codigo, String ip, String userAgent, String referido);
}