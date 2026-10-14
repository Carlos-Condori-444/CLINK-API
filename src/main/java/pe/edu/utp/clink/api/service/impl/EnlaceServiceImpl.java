package pe.edu.utp.clink.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.clink.api.dto.request.EnlaceRequest;
import pe.edu.utp.clink.api.dto.response.EnlaceResponse;
import pe.edu.utp.clink.api.entity.Enlace;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.repository.UsuarioRepository;
import pe.edu.utp.clink.api.service.ClickService;
import pe.edu.utp.clink.api.service.EnlaceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnlaceServiceImpl implements EnlaceService {

    private static final String URL_BASE = "https://clink.pe/";
    private static final long MESES_EXPIRACION_FREE = 2;

    private final EnlaceRepository enlaceRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClickService clickService;

    @Override
    @Transactional
    public EnlaceResponse crear(Long usuarioId, EnlaceRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar límite del plan
        long usados = enlaceRepository.countByUsuarioIdAndEstadoNot(usuarioId, "ELIMINADO");
        if (usados >= usuario.getPlan().getLimiteRecursos()) {
            throw new BusinessException("Has alcanzado el límite de " +
                    usuario.getPlan().getLimiteRecursos() + " recursos de tu plan");
        }

        // Validar alias
        if (request.getAlias() != null && !request.getAlias().isBlank()) {
            if (enlaceRepository.existsByAlias(request.getAlias())) {
                throw new BusinessException("El alias ya está en uso");
            }
        }

        String codigo = generarCodigoUnico();
        String aliasFinal = (request.getAlias() != null && !request.getAlias().isBlank())
                ? request.getAlias()
                : codigo;

        Enlace enlace = Enlace.builder()
                .urlOriginal(request.getUrlOriginal())
                .urlCorta(URL_BASE + aliasFinal)
                .codigo(codigo)
                .alias(request.getAlias())
                .nombrePersonal(request.getNombrePersonal())
                .fuente(request.getFuente())
                .estado("ACTIVO")
                .fechaExpiracion(LocalDateTime.now().plusMonths(MESES_EXPIRACION_FREE))
                .usuario(usuario)
                .build();

        Enlace guardado = enlaceRepository.save(enlace);
        return mapToResponse(guardado);
    }

    @Override
    public List<EnlaceResponse> listar(Long usuarioId) {
        return enlaceRepository.listarActivosPorUsuario(usuarioId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public EnlaceResponse obtener(Long usuarioId, Long enlaceId) {
        Enlace enlace = enlaceRepository.findById(enlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));
        if (!enlace.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre este enlace");
        }
        return mapToResponse(enlace);
    }

    @Override
    @Transactional
    public void eliminar(Long usuarioId, Long enlaceId) {
        Enlace enlace = enlaceRepository.findById(enlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));
        if (!enlace.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre este enlace");
        }
        enlace.setEstado("ELIMINADO");
        enlaceRepository.save(enlace);
    }

    @Override
    @Transactional
    public String redirigir(String codigoOAlias, String ip, String userAgent, String referido) {
        // Buscar por alias primero, si no, por código
        Enlace enlace = enlaceRepository.findByAlias(codigoOAlias)
                .orElseGet(() -> enlaceRepository.findByCodigo(codigoOAlias)
                        .orElseThrow(() -> new ResourceNotFoundException("Código no encontrado")));

        if ("ELIMINADO".equals(enlace.getEstado())) {
            throw new BusinessException("El enlace fue eliminado");
        }
        if ("EXPIRADO".equals(enlace.getEstado()) ||
                (enlace.getFechaExpiracion() != null && enlace.getFechaExpiracion().isBefore(LocalDateTime.now()))) {
            throw new BusinessException("El enlace ha expirado");
        }
        if (!enlace.estaActivo()) {
            throw new BusinessException("El enlace está inactivo");
        }

        String dispositivo = detectarDispositivo(userAgent);
        String navegador = detectarNavegador(userAgent);

        clickService.registrarClick(enlace.getId(), ip, dispositivo, navegador, referido, userAgent);

        return enlace.getUrlOriginal();
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 6);
        } while (enlaceRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }

    private String detectarDispositivo(String userAgent) {
        if (userAgent == null) return "OTRO";
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "MOVIL";
        } else if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux")) {
            return "DESKTOP";
        }
        return "OTRO";
    }

    private String detectarNavegador(String userAgent) {
        if (userAgent == null) return "Desconocido";
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg")) return "Edge";
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari")) return "Safari";
        return "Desconocido";
    }

    private EnlaceResponse mapToResponse(Enlace e) {
        return EnlaceResponse.builder()
                .id(e.getId())
                .urlOriginal(e.getUrlOriginal())
                .urlCorta(e.getUrlCorta())
                .codigo(e.getCodigo())
                .alias(e.getAlias())
                .nombrePersonal(e.getNombrePersonal())
                .fuente(e.getFuente())
                .clicsTotal(e.getClicsTotal())
                .clicsMovil(e.getClicsMovil())
                .clicsDesktop(e.getClicsDesktop())
                .fechaExpiracion(e.getFechaExpiracion())
                .fechaCreacion(e.getFechaCreacion())
                .estado(e.getEstado())
                .build();
    }
}