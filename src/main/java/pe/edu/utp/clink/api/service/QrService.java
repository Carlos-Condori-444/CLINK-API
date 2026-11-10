package pe.edu.utp.clink.api.service;

import pe.edu.utp.clink.api.dto.request.QrRequest;
import pe.edu.utp.clink.api.dto.response.QrResponse;

import java.util.List;

public interface QrService {
    QrResponse crear(Long usuarioId, QrRequest request);
    List<QrResponse> listar(Long usuarioId);
    QrResponse obtener(Long usuarioId, Long qrId);
    byte[] descargarImagen(Long usuarioId, Long qrId);
    String redirigir(String codigo);
}