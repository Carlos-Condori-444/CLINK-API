package pe.edu.utp.clink.api.service;

import pe.edu.utp.clink.api.dto.request.LoginRequest;
import pe.edu.utp.clink.api.dto.request.RegistroRequest;
import pe.edu.utp.clink.api.dto.response.AuthResponse;
import pe.edu.utp.clink.api.dto.response.UsuarioResponse;

public interface AuthService {
    UsuarioResponse registrar(RegistroRequest request);
    AuthResponse login(LoginRequest request);
    UsuarioResponse obtenerPerfil(Long usuarioId);
}