package pe.edu.utp.clink.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.dto.request.EtiquetaRequest;
import pe.edu.utp.clink.api.dto.response.EtiquetaResponse;
import pe.edu.utp.clink.api.security.UsuarioDetails;
import pe.edu.utp.clink.api.service.EtiquetaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/etiquetas")
@RequiredArgsConstructor
@Tag(name = "Etiquetas", description = "Gestión de etiquetas (solo plan Premium)")
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    @PostMapping
    @Operation(summary = "Crear etiqueta",
            description = "Crea una nueva etiqueta. Requiere plan Premium.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<EtiquetaResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody EtiquetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(etiquetaService.crear(userDetails.getUsuario().getId(), request));
    }

    @GetMapping
    @Operation(summary = "Listar etiquetas del usuario",
            description = "Devuelve todas las etiquetas creadas por el usuario autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<EtiquetaResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(etiquetaService.listar(userDetails.getUsuario().getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar etiqueta",
            description = "Elimina una etiqueta propia. Solo el propietario puede hacerlo.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        etiquetaService.eliminar(userDetails.getUsuario().getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/enlace/{enlaceId}/etiqueta/{etiquetaId}")
    @Operation(summary = "Asignar etiqueta a un enlace",
            description = "Vincula una etiqueta existente a un enlace del mismo usuario.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> asignar(
            @PathVariable Long enlaceId,
            @PathVariable Long etiquetaId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        etiquetaService.asignarAEnlace(userDetails.getUsuario().getId(), enlaceId, etiquetaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/enlace/{enlaceId}/etiqueta/{etiquetaId}")
    @Operation(summary = "Quitar etiqueta de un enlace",
            description = "Desvincula una etiqueta de un enlace del mismo usuario.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> quitar(
            @PathVariable Long enlaceId,
            @PathVariable Long etiquetaId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        etiquetaService.quitarDeEnlace(userDetails.getUsuario().getId(), enlaceId, etiquetaId);
        return ResponseEntity.noContent().build();
    }
}