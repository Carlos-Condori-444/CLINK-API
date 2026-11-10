package pe.edu.utp.clink.api.controller;

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
public class ClickController {

    private final ClickService clickService;
    private final EnlaceService enlaceService;

    @GetMapping("/enlace/{enlaceId}/dispositivo")
    public ResponseEntity<Map<String, Long>> estadisticasPorDispositivo(
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        enlaceService.obtener(usuarioId, enlaceId);
        return ResponseEntity.ok(clickService.estadisticasPorDispositivo(enlaceId));
    }

    @GetMapping("/enlace/{enlaceId}/fuente")
    public ResponseEntity<Map<String, Long>> estadisticasPorFuente(
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        Long usuarioId = userDetails.getUsuario().getId();
        enlaceService.obtener(usuarioId, enlaceId);
        return ResponseEntity.ok(clickService.estadisticasPorFuente(enlaceId));
    }
}