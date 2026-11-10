package pe.edu.utp.clink.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Enlaces", description = "Gestión de enlaces acortados")
public class EnlaceController {

    private final EnlaceService enlaceService;

    @PostMapping("/api/v1/enlaces")
    @Operation(summary = "Crear un enlace acortado",
            description = "Requiere autenticación. Valida el límite del plan y la unicidad del alias.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<EnlaceResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody EnlaceRequest request) {
        Long usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enlaceService.crear(usuarioId, request));
    }

    @GetMapping("/api/v1/enlaces")
    @Operation(summary = "Listar enlaces del usuario",
            description = "Devuelve solo los enlaces activos (no eliminados) del usuario autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<EnlaceResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(enlaceService.listar(usuarioId));
    }

    @GetMapping("/api/v1/enlaces/{id}")
    @Operation(summary = "Obtener un enlace por ID",
            description = "Solo el propietario puede consultarlo.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<EnlaceResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(enlaceService.obtener(usuarioId, id));
    }

    @DeleteMapping("/api/v1/enlaces/{id}")
    @Operation(summary = "Eliminar un enlace (soft delete)",
            description = "Marca el enlace como ELIMINADO. No borra la fila físicamente.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        enlaceService.eliminar(usuarioId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/r/{codigo}")
    @Operation(summary = "Redirección pública",
            description = "Endpoint público. Registra el clic y redirige a la URL original con HTTP 302.",
            security = {})
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