package pe.edu.utp.clink.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.dto.request.QrRequest;
import pe.edu.utp.clink.api.dto.response.QrResponse;
import pe.edu.utp.clink.api.security.UsuarioDetails;
import pe.edu.utp.clink.api.service.QrService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "QR", description = "Generación y descarga de códigos QR")
public class QrController {

    private final QrService qrService;

    @PostMapping("/api/v1/qr")
    @Operation(summary = "Crear código QR",
            description = """
                       Crea un QR de dos tipos:
                       - INDEPENDIENTE: para una URL externa. Consume cupo del plan.
                       - ENLACE: para un enlace propio. Solo Premium.
                       """,
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<QrResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody QrRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrService.crear(userDetails.getUsuario().getId(), request));
    }

    @GetMapping("/api/v1/qr")
    @Operation(summary = "Listar QRs del usuario",
            description = "Devuelve todos los QRs creados por el usuario autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<QrResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(qrService.listar(userDetails.getUsuario().getId()));
    }

    @GetMapping("/api/v1/qr/{id}")
    @Operation(summary = "Obtener QR por ID",
            description = "Devuelve los datos del QR incluyendo la imagen en base64.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<QrResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(qrService.obtener(userDetails.getUsuario().getId(), id));
    }

    @GetMapping("/api/v1/qr/{id}/descargar")
    @Operation(summary = "Descargar imagen PNG del QR",
            description = "Devuelve el archivo PNG listo para descargar.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<byte[]> descargar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        byte[] imagen = qrService.descargarImagen(userDetails.getUsuario().getId(), id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(imagen);
    }

    @GetMapping("/qr/{codigo}")
    @Operation(summary = "Redirección pública de QR",
            description = "Endpoint público. Registra el clic y redirige a la URL de destino.",
            security = {})
    public ResponseEntity<Void> redirigir(@PathVariable String codigo) {
        String url = qrService.redirigir(codigo);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}