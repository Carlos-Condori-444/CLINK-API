package pe.edu.utp.clink.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.clink.api.dto.request.EtiquetaRequest;
import pe.edu.utp.clink.api.dto.response.EtiquetaResponse;
import pe.edu.utp.clink.api.entity.*;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.*;
import pe.edu.utp.clink.api.service.EtiquetaService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtiquetaServiceImpl implements EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final EnlaceEtiquetaRepository enlaceEtiquetaRepository;
    private final EnlaceRepository enlaceRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public EtiquetaResponse crear(Long usuarioId, EtiquetaRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Regla: solo Premium puede usar etiquetas
        if (!Boolean.TRUE.equals(usuario.getPlan().getPermiteEtiquetas())) {
            throw new BusinessException("Las etiquetas son exclusivas del plan Premium");
        }

        if (etiquetaRepository.existsByUsuarioIdAndNombre(usuarioId, request.getNombre())) {
            throw new BusinessException("Ya existe una etiqueta con ese nombre");
        }

        Etiqueta etiqueta = Etiqueta.builder()
                .nombre(request.getNombre())
                .color(request.getColor())
                .usuario(usuario)
                .build();

        return mapToResponse(etiquetaRepository.save(etiqueta));
    }

    @Override
    public List<EtiquetaResponse> listar(Long usuarioId) {
        return etiquetaRepository.findByUsuarioId(usuarioId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminar(Long usuarioId, Long etiquetaId) {
        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));

        if (!etiqueta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre esta etiqueta");
        }
        etiquetaRepository.delete(etiqueta);
    }

    @Override
    @Transactional
    public void asignarAEnlace(Long usuarioId, Long enlaceId, Long etiquetaId) {
        Enlace enlace = enlaceRepository.findById(enlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));

        Etiqueta etiqueta = etiquetaRepository.findById(etiquetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));

        if (!enlace.getUsuario().getId().equals(usuarioId)
                || !etiqueta.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre estos recursos");
        }

        EnlaceEtiquetaId id = new EnlaceEtiquetaId(enlaceId, etiquetaId);
        if (enlaceEtiquetaRepository.existsById(id)) {
            throw new BusinessException("La etiqueta ya está asignada a este enlace");
        }

        enlaceEtiquetaRepository.save(EnlaceEtiqueta.builder()
                .id(id).enlace(enlace).etiqueta(etiqueta).build());
    }

    @Override
    @Transactional
    public void quitarDeEnlace(Long usuarioId, Long enlaceId, Long etiquetaId) {
        Enlace enlace = enlaceRepository.findById(enlaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));

        if (!enlace.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre este enlace");
        }

        EnlaceEtiquetaId id = new EnlaceEtiquetaId(enlaceId, etiquetaId);
        if (!enlaceEtiquetaRepository.existsById(id)) {
            throw new BusinessException("La etiqueta no está asignada a este enlace");
        }
        enlaceEtiquetaRepository.deleteById(id);
    }

    private EtiquetaResponse mapToResponse(Etiqueta e) {
        return EtiquetaResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .color(e.getColor())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }
}