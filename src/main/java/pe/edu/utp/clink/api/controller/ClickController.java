package pe.edu.utp.clink.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.security.UsuarioDetails;
import pe.edu.utp.clink.api.service.ClickService;
import pe.edu.utp.clink.api.service.EnlaceService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/clicks")
@RequiredArgsConstructor
@Tag(name = "Clicks", description = "Estadísticas de clics por enlace")
public class ClickController {

    private final ClickService clickService;
    private final EnlaceService enlaceService;

    @GetMapping("/enlace/{enlaceId}/dispositivo")
    @Operation(summary = "Estadísticas por dispositivo",
            description = "Devuelve el conteo de clics agrupado por dispositivo (MOVIL, DESKTOP, OTRO).",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Long>> estadisticasPorDispositivo(
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        enlaceService.obtener(usuarioId, enlaceId);
        return ResponseEntity.ok(clickService.estadisticasPorDispositivo(enlaceId));
    }

    @GetMapping("/enlace/{enlaceId}/fuente")
    @Operation(summary = "Estadísticas por fuente de tráfico",
            description = "Devuelve el conteo de clics agrupado por fuente (facebook, instagram, etc.).",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, Long>> estadisticasPorFuente(
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        enlaceService.obtener(usuarioId, enlaceId);
        return ResponseEntity.ok(clickService.estadisticasPorFuente(enlaceId));
    }
}