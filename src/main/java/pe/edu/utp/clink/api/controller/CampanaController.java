package pe.edu.utp.clink.api.controller;

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
public class CampanaController {

    private final CampanaService campanaService;

    @PostMapping
    public ResponseEntity<CampanaResponse> crear(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody CampanaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campanaService.crear(userDetails.getUsuario().getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<CampanaResponse>> listar(
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(campanaService.listar(userDetails.getUsuario().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampanaResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(campanaService.obtener(userDetails.getUsuario().getId(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        campanaService.eliminar(userDetails.getUsuario().getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{campanaId}/enlace/{enlaceId}")
    public ResponseEntity<Void> asignarEnlace(
            @PathVariable Long campanaId,
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        campanaService.asignarEnlace(userDetails.getUsuario().getId(), campanaId, enlaceId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{campanaId}/enlace/{enlaceId}")
    public ResponseEntity<Void> quitarEnlace(
            @PathVariable Long campanaId,
            @PathVariable Long enlaceId,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        campanaService.quitarEnlace(userDetails.getUsuario().getId(), campanaId, enlaceId);
        return ResponseEntity.noContent().build();
    }
}