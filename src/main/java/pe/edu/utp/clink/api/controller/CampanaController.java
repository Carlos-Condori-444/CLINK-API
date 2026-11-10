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
import pe.edu.utp.clink.api.dto.request.CampanaRequest;
import pe.edu.utp.clink.api.dto.response.CampanaResponse;
import pe.edu.utp.clink.api.security.UsuarioDetails;
import pe.edu.utp.clink.api.service.CampanaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campanas")
@RequiredArgsConstructor
@Tag(name = "Campañas", description = "Gestión de campañas de marketing (solo plan Premium)")
public class CampanaController {

    private final CampanaService campanaService;

    @PostMapping
    @Operation(summary = "Crear campaña",
            description = "Crea una nueva campaña con rango de fechas. Requiere plan Premium.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CampanaResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody CampanaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campanaService.crear(userDetails.getUsuario().getId(), request));
    }

    @GetMapping
    @Operation(summary = "Listar campañas del usuario",
            description = "Devuelve todas las campañas creadas por el usuario autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CampanaResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(campanaService.listar(userDetails.getUsuario().getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener campaña por ID",
            description = "Solo el propietario puede consultarla.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CampanaResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(campanaService.obtener(userDetails.getUsuario().getId(), id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar campaña",
            description = "Elimina una campaña propia. Los enlaces asociados quedan sin campaña.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        campanaService.eliminar(userDetails.getUsuario().getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{campanaId}/enlace/{enlaceId}")
    @Operation(summary = "Asignar enlace a una campaña",
            description = "Vincula un enlace existente a una campaña del mismo usuario.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> asignarEnlace(
            @PathVariable Long campanaId,
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        campanaService.asignarEnlace(userDetails.getUsuario().getId(), campanaId, enlaceId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{campanaId}/enlace/{enlaceId}")
    @Operation(summary = "Quitar enlace de una campaña",
            description = "Desvincula un enlace de su campaña actual.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> quitarEnlace(
            @PathVariable Long campanaId,
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        campanaService.quitarEnlace(userDetails.getUsuario().getId(), campanaId, enlaceId);
        return ResponseEntity.noContent().build();
    }
}