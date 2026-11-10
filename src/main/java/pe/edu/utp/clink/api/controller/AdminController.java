package pe.edu.utp.clink.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.dto.request.CambiarPlanRequest;
import pe.edu.utp.clink.api.dto.response.UsuarioResponse;
import pe.edu.utp.clink.api.security.UsuarioDetails;
import pe.edu.utp.clink.api.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PutMapping("/usuarios/{id}/plan")
    public ResponseEntity<UsuarioResponse> cambiarPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody CambiarPlanRequest request) {
        return ResponseEntity.ok(
                adminService.cambiarPlan(userDetails.getUsuario().getId(), id, request));
    }

    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(
                adminService.cambiarEstado(userDetails.getUsuario().getId(), id));
    }
}