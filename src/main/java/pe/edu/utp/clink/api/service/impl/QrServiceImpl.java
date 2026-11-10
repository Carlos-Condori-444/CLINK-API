package pe.edu.utp.clink.api.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.clink.api.dto.request.QrRequest;
import pe.edu.utp.clink.api.dto.response.QrResponse;
import pe.edu.utp.clink.api.entity.Enlace;
import pe.edu.utp.clink.api.entity.Qr;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.repository.QrRepository;
import pe.edu.utp.clink.api.repository.UsuarioRepository;
import pe.edu.utp.clink.api.service.QrService;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private static final String URL_BASE = "https://clink.pe/qr/";

    private final QrRepository qrRepository;
    private final EnlaceRepository enlaceRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public QrResponse crear(Long usuarioId, QrRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String urlDestino;
        Enlace enlace = null;

        if ("ENLACE".equals(request.getTipo())) {
            // Regla: solo Premium puede hacer QR de enlace propio
            if (!Boolean.TRUE.equals(usuario.getPlan().getPermiteQrPropio())) {
                throw new BusinessException("El QR de enlace propio es exclusivo del plan Premium");
            }
            if (request.getEnlaceId() == null) {
                throw new BusinessException("Debe indicar el enlaceId");
            }
            enlace = enlaceRepository.findById(request.getEnlaceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enlace no encontrado"));

            if (!enlace.getUsuario().getId().equals(usuarioId)) {
                throw new BusinessException("El enlace no te pertenece");
            }
            urlDestino = enlace.getUrlCorta();
        } else {
            // INDEPENDIENTE: consume cupo del plan
            if (request.getUrlDestino() == null || request.getUrlDestino().isBlank()) {
                throw new BusinessException("Debe indicar urlDestino");
            }

            long usados = qrRepository.countByUsuarioIdAndActivoTrue(usuarioId);
            long enlacesUsados = enlaceRepository.countByUsuarioIdAndEstadoNot(usuarioId, "ELIMINADO");
            long totalUsados = usados + enlacesUsados;

            if (totalUsados >= usuario.getPlan().getLimiteRecursos()) {
                throw new BusinessException("Has alcanzado el límite de " +
                        usuario.getPlan().getLimiteRecursos() + " recursos");
            }
            urlDestino = request.getUrlDestino();
        }

        String codigo = generarCodigoUnico();

        Qr qr = Qr.builder()
                .tipo(request.getTipo())
                .urlDestino(urlDestino)
                .codigo(codigo)
                .usuario(usuario)
                .enlace(enlace)
                .activo(true)
                .build();

        Qr guardado = qrRepository.save(qr);

        // Generar imagen QR en base64 (con la URL pública de redirección)
        String urlPublica = URL_BASE + codigo;
        String imagenBase64 = generarImagenQrBase64(urlPublica);

        return mapToResponse(guardado, imagenBase64);
    }

    @Override
    public List<QrResponse> listar(Long usuarioId) {
        return qrRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(q -> mapToResponse(q, generarImagenQrBase64(URL_BASE + q.getCodigo())))
                .collect(Collectors.toList());
    }

    @Override
    public QrResponse obtener(Long usuarioId, Long qrId) {
        Qr qr = qrRepository.findById(qrId)
                .orElseThrow(() -> new ResourceNotFoundException("QR no encontrado"));
        if (!qr.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre este QR");
        }
        return mapToResponse(qr, generarImagenQrBase64(URL_BASE + qr.getCodigo()));
    }

    @Override
    public byte[] descargarImagen(Long usuarioId, Long qrId) {
        Qr qr = qrRepository.findById(qrId)
                .orElseThrow(() -> new ResourceNotFoundException("QR no encontrado"));
        if (!qr.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tienes permiso sobre este QR");
        }
        return generarImagenQrBytes(URL_BASE + qr.getCodigo());
    }

    @Override
    @Transactional
    public String redirigir(String codigo) {
        Qr qr = qrRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("QR no encontrado"));

        if (!Boolean.TRUE.equals(qr.getActivo())) {
            throw new BusinessException("El QR está inactivo");
        }

        qr.setClicsTotal(qr.getClicsTotal() + 1);
        qrRepository.save(qr);

        return qr.getUrlDestino();
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 8);
        } while (qrRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }

    private byte[] generarImagenQrBytes(String contenido) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(contenido, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Error al generar QR: " + e.getMessage());
        }
    }

    private String generarImagenQrBase64(String contenido) {
        return Base64.getEncoder().encodeToString(generarImagenQrBytes(contenido));
    }

    private QrResponse mapToResponse(Qr q, String imagenBase64) {
        return QrResponse.builder()
                .id(q.getId())
                .tipo(q.getTipo())
                .urlDestino(q.getUrlDestino())
                .codigo(q.getCodigo())
                .imagenBase64(imagenBase64)
                .clicsTotal(q.getClicsTotal())
                .fechaCreacion(q.getFechaCreacion())
                .activo(q.getActivo())
                .build();
    }
}