package pe.edu.utp.clink.api.service;

import pe.edu.utp.clink.api.dto.request.CampanaRequest;
import pe.edu.utp.clink.api.dto.response.CampanaResponse;

import java.util.List;

public interface CampanaService {
    CampanaResponse crear(Long usuarioId, CampanaRequest request);
    List<CampanaResponse> listar(Long usuarioId);
    CampanaResponse obtener(Long usuarioId, Long campanaId);
    void eliminar(Long usuarioId, Long campanaId);
    void asignarEnlace(Long usuarioId, Long campanaId, Long enlaceId);
    void quitarEnlace(Long usuarioId, Long campanaId, Long enlaceId);
}