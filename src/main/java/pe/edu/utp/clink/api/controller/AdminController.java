package pe.edu.utp.clink.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Administración", description = "Gestión de usuarios de prueba (solo rol ADMIN)")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios",
            description = "Devuelve todos los usuarios registrados con su plan y consumo.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @PutMapping("/usuarios/{id}/plan")
    @Operation(summary = "Cambiar plan de un usuario",
            description = "Permite asignar un nuevo plan (Free o Premium). Registra auditoría.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UsuarioResponse> cambiarPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @Valid @RequestBody CambiarPlanRequest request) {
        return ResponseEntity.ok(
                adminService.cambiarPlan(userDetails.getUsuario().getId(), id, request));
    }

    @PutMapping("/usuarios/{id}/estado")
    @Operation(summary = "Activar o desactivar una cuenta",
            description = "Alterna el estado activo/inactivo del usuario. Registra auditoría.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        return ResponseEntity.ok(
                adminService.cambiarEstado(userDetails.getUsuario().getId(), id));
    }
}