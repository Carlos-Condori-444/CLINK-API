package pe.edu.utp.clink.api.service;

import pe.edu.utp.clink.api.dto.request.CambiarPlanRequest;
import pe.edu.utp.clink.api.dto.response.UsuarioResponse;

import java.util.List;

public interface AdminService {
    List<UsuarioResponse> listarUsuarios();
    UsuarioResponse cambiarPlan(Long adminId, Long usuarioId, CambiarPlanRequest request);
    UsuarioResponse cambiarEstado(Long adminId, Long usuarioId);
}