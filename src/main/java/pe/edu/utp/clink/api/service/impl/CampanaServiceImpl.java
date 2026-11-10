package pe.edu.utp.clink.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.clink.api.dto.request.CampanaRequest;
import pe.edu.utp.clink.api.dto.response.CampanaResponse;
import pe.edu.utp.clink.api.entity.Campana;
import pe.edu.utp.clink.api.entity.Enlace;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.CampanaRepository;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.repository.UsuarioRepository;
import pe.edu.utp.clink.api.service.CampanaService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampanaServiceImpl implements CampanaService {

    private final CampanaRepository campanaRepository;
    private final EnlaceRepository enlaceRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CampanaResponse crear(Long usuarioId, CampanaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getPlan().getPermiteEtiquetas())) {
            throw new BusinessException("Las campañas son exclusivas del plan Premium");
        }

        if (request.getFechaInicio() != null && request.getFechaFin() != null
                && request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la de inicio");
        }

        Campana campana = Campana.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .activo(true)
                .usuario(usuario)
                .build();

        return mapToResponse(campanaRepository.save(campana));
    }

    @Override
    public List<CampanaResponse> listar(Long usuarioId) {
        return campanaRepository.findByUsuarioId(usuarioId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public CampanaResponse obtener(Long usuarioId, Long campanaId) {
        Campana campana = campanaRepository.findById(campanaId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaña no encontrada"));
        if (!campana.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre esta campaña");
        }
        return mapToResponse(campana);
    }

    @Override
    @Transactional
    public void eliminar(Long usuarioId, Long campanaId) {
        Campana campana = campanaRepository.findById(campanaId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaña no encontrada"));
        if (!campana.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre esta campaña");
        }
        campanaRepository.delete(campana);
    }

    @Override
    @Transactional
    public void asignarEnlace(Long usuarioId, Long campanaId, Long enlaceId) {
        Campana campana = campanaRepository.findById(campanaId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaña no encontrada"));
        Enlace enlace = enlaceRepository.findById(enlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));

        if (!campana.getUsuario().getId().equals(usuarioId)
                || !enlace.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre estos recursos");
        }

        enlace.setCampana(campana);
        enlaceRepository.save(enlace);
    }

    @Override
    @Transactional
    public void quitarEnlace(Long usuarioId, Long campanaId, Long enlaceId) {
        Enlace enlace = enlaceRepository.findById(enlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));
        if (!enlace.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre este enlace");
        }
        enlace.setCampana(null);
        enlaceRepository.save(enlace);
    }

    private CampanaResponse mapToResponse(Campana c) {
        return CampanaResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .fechaInicio(c.getFechaInicio())
                .fechaFin(c.getFechaFin())
                .activo(c.getActivo())
                .build();
    }
}