package pe.edu.utp.clink.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.dto.request.EnlaceRequest;
import pe.edu.utp.clink.api.dto.response.EnlaceResponse;
import pe.edu.utp.clink.api.security.UsuarioDetails;
import pe.edu.utp.clink.api.service.EnlaceService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EnlaceController {

    private final EnlaceService enlaceService;

    // ===== PRIVADOS (requieren JWT) =====

    @PostMapping("/api/v1/enlaces")
    public ResponseEntity<EnlaceResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody EnlaceRequest request) {
        Long usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enlaceService.crear(usuarioId, request));
    }

    @GetMapping("/api/v1/enlaces")
    public ResponseEntity<List<EnlaceResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(enlaceService.listar(usuarioId));
    }

    @GetMapping("/api/v1/enlaces/{id}")
    public ResponseEntity<EnlaceResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(enlaceService.obtener(usuarioId, id));
    }

    @DeleteMapping("/api/v1/enlaces/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        enlaceService.eliminar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    // ===== PÚBLICO (redirección) =====

    @GetMapping("/r/{codigo}")
    public ResponseEntity<Void> redirigir(
            @PathVariable String codigo,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referido = request.getHeader("Referer");
        String urlOriginal = enlaceService.redirigir(codigo, ip, userAgent, referido);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlOriginal))
                .build();
    }
}