package pe.edu.utp.clink.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.clink.api.dto.request.CambiarPlanRequest;
import pe.edu.utp.clink.api.dto.response.UsuarioResponse;
import pe.edu.utp.clink.api.entity.AuditoriaAdmin;
import pe.edu.utp.clink.api.entity.Plan;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.*;
import pe.edu.utp.clink.api.service.AdminService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final EnlaceRepository enlaceRepository;
    private final AuditoriaAdminRepository auditoriaAdminRepository;

    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarPlan(Long adminId, Long usuarioId, CambiarPlanRequest request) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        String detalle = "Plan cambiado de " + usuario.getPlan().getNombre()
                + " a " + plan.getNombre();

        usuario.setPlan(plan);
        usuarioRepository.save(usuario);

        registrarAuditoria(admin, usuario, "CAMBIO_PLAN", detalle);

        return mapToResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstado(Long adminId, Long usuarioId) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setActivo(!usuario.getActivo());
        usuarioRepository.save(usuario);

        String detalle = "Usuario " + (usuario.getActivo() ? "activado" : "desactivado");
        registrarAuditoria(admin, usuario, "CAMBIO_ESTADO", detalle);

        return mapToResponse(usuario);
    }

    private void registrarAuditoria(Usuario admin, Usuario afectado, String accion, String detalle) {
        auditoriaAdminRepository.save(AuditoriaAdmin.builder()
                .admin(admin)
                .usuarioAfectado(afectado)
                .accion(accion)
                .detalle(detalle)
                .build());
    }

    private UsuarioResponse mapToResponse(Usuario u) {
        long usados = enlaceRepository.countByUsuarioIdAndEstadoNot(u.getId(), "ELIMINADO");
        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rol(u.getRol())
                .activo(u.getActivo())
                .planNombre(u.getPlan().getNombre())
                .limiteRecursos(u.getPlan().getLimiteRecursos())
                .recursosUsados(usados)
                .build();
    }
}