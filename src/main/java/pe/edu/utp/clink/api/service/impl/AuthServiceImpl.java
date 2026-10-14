package pe.edu.utp.clink.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.clink.api.dto.request.LoginRequest;
import pe.edu.utp.clink.api.dto.request.RegistroRequest;
import pe.edu.utp.clink.api.dto.response.AuthResponse;
import pe.edu.utp.clink.api.dto.response.UsuarioResponse;
import pe.edu.utp.clink.api.entity.Plan;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.repository.PlanRepository;
import pe.edu.utp.clink.api.repository.UsuarioRepository;
import pe.edu.utp.clink.api.service.AuthService;
import pe.edu.utp.clink.api.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final EnlaceRepository enlaceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }

        Plan planFree = planRepository.findByNombre("Plan Gratuito")
                .orElseThrow(() -> new ResourceNotFoundException("Plan gratuito no encontrado"));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .rol("CLIENTE")
                .activo(true)
                .plan(planFree)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return mapToResponse(guardado);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (!usuario.getActivo()) {
            throw new BusinessException("La cuenta está inactiva");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new BusinessException("Credenciales inválidas");
        }

        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());

        return AuthResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }

    @Override
    public UsuarioResponse obtenerPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        long usados = enlaceRepository.countByUsuarioIdAndEstadoNot(usuario.getId(), "ELIMINADO");
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .planNombre(usuario.getPlan().getNombre())
                .limiteRecursos(usuario.getPlan().getLimiteRecursos())
                .recursosUsados(usados)
                .build();
    }
}