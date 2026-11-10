package pe.edu.utp.clink.api.service;

import pe.edu.utp.clink.api.dto.request.EtiquetaRequest;
import pe.edu.utp.clink.api.dto.response.EtiquetaResponse;

import java.util.List;

public interface EtiquetaService {
    EtiquetaResponse crear(Long usuarioId, EtiquetaRequest request);
    List<EtiquetaResponse> listar(Long usuarioId);
    void eliminar(Long usuarioId, Long etiquetaId);
    void asignarAEnlace(Long usuarioId, Long enlaceId, Long etiquetaId);
    void quitarDeEnlace(Long usuarioId, Long enlaceId, Long etiquetaId);
}