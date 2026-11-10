package pe.edu.utp.clink.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.dto.request.LoginRequest;
import pe.edu.utp.clink.api.dto.request.RegistroRequest;
import pe.edu.utp.clink.api.dto.response.AuthResponse;
import pe.edu.utp.clink.api.dto.response.UsuarioResponse;
import pe.edu.utp.clink.api.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y perfil de usuario")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea una cuenta con plan gratuito (límite de 10 recursos).")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Devuelve un token JWT válido por 24 horas.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/perfil/{id}")
    @Operation(summary = "Obtener perfil de usuario",
            description = "Devuelve datos del usuario y consumo de su plan.")
    public ResponseEntity<UsuarioResponse> perfil(@PathVariable Long id) {
        return ResponseEntity.ok(authService.obtenerPerfil(id));
    }
}