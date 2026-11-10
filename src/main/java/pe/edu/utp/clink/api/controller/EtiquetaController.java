package pe.edu.utp.clink.api.controller;

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
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    @PostMapping
    public ResponseEntity<EtiquetaResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody EtiquetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(etiquetaService.crear(userDetails.getUsuario().getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<EtiquetaResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(etiquetaService.listar(userDetails.getUsuario().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        etiquetaService.eliminar(userDetails.getUsuario().getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/enlace/{enlaceId}/etiqueta/{etiquetaId}")
    public ResponseEntity<Void> asignar(
            @PathVariable Long enlaceId,
            @PathVariable Long etiquetaId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        etiquetaService.asignarAEnlace(userDetails.getUsuario().getId(), enlaceId, etiquetaId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/enlace/{enlaceId}/etiqueta/{etiquetaId}")
    public ResponseEntity<Void> quitar(
            @PathVariable Long enlaceId,
            @PathVariable Long etiquetaId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        etiquetaService.quitarDeEnlace(userDetails.getUsuario().getId(), enlaceId, etiquetaId);
        return ResponseEntity.noContent().build();
    }
}