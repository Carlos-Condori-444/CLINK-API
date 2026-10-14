package pe.edu.utp.clink.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.dto.request.EnlaceRequest;
import pe.edu.utp.clink.api.dto.response.EnlaceResponse;
import pe.edu.utp.clink.api.service.EnlaceService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EnlaceController {

    private final EnlaceService enlaceService;

    @PostMapping("/api/v1/enlaces/usuario/{usuarioId}")
    public ResponseEntity<EnlaceResponse> crear(@PathVariable Long usuarioId,
                                                @Valid @RequestBody EnlaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enlaceService.crear(usuarioId, request));
    }

    @GetMapping("/api/v1/enlaces/usuario/{usuarioId}")
    public ResponseEntity<List<EnlaceResponse>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(enlaceService.listar(usuarioId));
    }

    @GetMapping("/api/v1/enlaces/{id}/usuario/{usuarioId}")
    public ResponseEntity<EnlaceResponse> obtener(@PathVariable Long id, @PathVariable Long usuarioId) {
        return ResponseEntity.ok(enlaceService.obtener(usuarioId, id));
    }

    @DeleteMapping("/api/v1/enlaces/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @PathVariable Long usuarioId) {
        enlaceService.eliminar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint público de redirección
    @GetMapping("/r/{codigo}")
    public ResponseEntity<Void> redirigir(@PathVariable String codigo, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referido = request.getHeader("Referer");
        String urlOriginal = enlaceService.redirigir(codigo, ip, userAgent, referido);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlOriginal))
                .build();
    }
}