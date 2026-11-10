package pe.edu.utp.clink.api.controller;

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
public class QrController {

    private final QrService qrService;

    // ===== PRIVADOS =====

    @PostMapping("/api/v1/qr")
    public ResponseEntity<QrResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody QrRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrService.crear(userDetails.getUsuario().getId(), request));
    }

    @GetMapping("/api/v1/qr")
    public ResponseEntity<List<QrResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(qrService.listar(userDetails.getUsuario().getId()));
    }

    @GetMapping("/api/v1/qr/{id}")
    public ResponseEntity<QrResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(qrService.obtener(userDetails.getUsuario().getId(), id));
    }

    @GetMapping("/api/v1/qr/{id}/descargar")
    public ResponseEntity<byte[]> descargar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        byte[] imagen = qrService.descargarImagen(userDetails.getUsuario().getId(), id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(imagen);
    }

    // ===== PÚBLICO: redirección de QR =====

    @GetMapping("/qr/{codigo}")
    public ResponseEntity<Void> redirigir(@PathVariable String codigo) {
        String url = qrService.redirigir(codigo);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}