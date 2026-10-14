package pe.edu.utp.clink.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.utp.clink.api.dto.request.LoginRequest;
import pe.edu.utp.clink.api.dto.request.RegistroRequest;
import pe.edu.utp.clink.api.dto.response.AuthResponse;
import pe.edu.utp.clink.api.dto.response.UsuarioResponse;
import pe.edu.utp.clink.api.entity.Plan;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.repository.PlanRepository;
import pe.edu.utp.clink.api.repository.UsuarioRepository;
import pe.edu.utp.clink.api.util.JwtUtil;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - AuthServiceImpl")
class AuthServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PlanRepository planRepository;
    @Mock private EnlaceRepository enlaceRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private Plan planFree;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        planFree = Plan.builder()
                .id(1L)
                .nombre("Plan Gratuito")
                .limiteRecursos(10)
                .permiteQrPropio(false)
                .permiteEtiquetas(false)
                .permiteExportar(false)
                .permiteDesactivar(false)
                .precio(BigDecimal.ZERO)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Carlos Condori")
                .email("carlos@clink.pe")
                .passwordHash("$2a$10$hashencriptado")
                .rol("CLIENTE")
                .activo(true)
                .plan(planFree)
                .build();
    }

    // ============================================================
    // REGISTRO
    // ============================================================

    @Test
    @DisplayName("registrar: usuario nuevo retorna UsuarioResponse con plan gratuito")
    void registrar_usuarioNuevo_retornaUsuarioResponse() {
        RegistroRequest request = new RegistroRequest(
                "Carlos Condori", "carlos@clink.pe", "123456");

        when(usuarioRepository.existsByEmail("carlos@clink.pe")).thenReturn(false);
        when(planRepository.findByNombre("Plan Gratuito")).thenReturn(Optional.of(planFree));
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$hashencriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(enlaceRepository.countByUsuarioIdAndEstadoNot(1L, "ELIMINADO")).thenReturn(0L);

        UsuarioResponse response = authService.registrar(request);

        assertNotNull(response);
        assertEquals("carlos@clink.pe", response.getEmail());
        assertEquals("Plan Gratuito", response.getPlanNombre());
        assertEquals(10, response.getLimiteRecursos().intValue());
        assertEquals(0L, response.getRecursosUsados().longValue());

        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("registrar: email duplicado lanza BusinessException")
    void registrar_emailDuplicado_lanzaBusinessException() {
        RegistroRequest request = new RegistroRequest(
                "Carlos Condori", "carlos@clink.pe", "123456");

        when(usuarioRepository.existsByEmail("carlos@clink.pe")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.registrar(request));

        assertEquals("El email ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrar: plan gratuito no existe lanza ResourceNotFoundException")
    void registrar_sinPlanGratuito_lanzaResourceNotFoundException() {
        RegistroRequest request = new RegistroRequest(
                "Carlos Condori", "carlos@clink.pe", "123456");

        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(planRepository.findByNombre("Plan Gratuito")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.registrar(request));

        verify(usuarioRepository, never()).save(any());
    }

    // ============================================================
    // LOGIN
    // ============================================================

    @Test
    @DisplayName("login: credenciales válidas retorna token JWT")
    void login_credencialesValidas_retornaToken() {
        LoginRequest request = new LoginRequest("carlos@clink.pe", "123456");

        when(usuarioRepository.findByEmail("carlos@clink.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", usuario.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generarToken("carlos@clink.pe", "CLIENTE")).thenReturn("jwt-token-fake");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-fake", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals("carlos@clink.pe", response.getEmail());
        assertEquals("CLIENTE", response.getRol());

        verify(jwtUtil).generarToken("carlos@clink.pe", "CLIENTE");
    }

    @Test
    @DisplayName("login: email no existe lanza BusinessException")
    void login_emailNoExiste_lanzaBusinessException() {
        LoginRequest request = new LoginRequest("noexiste@clink.pe", "123456");

        when(usuarioRepository.findByEmail("noexiste@clink.pe")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(request));

        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(jwtUtil, never()).generarToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login: contraseña incorrecta lanza BusinessException")
    void login_passwordIncorrecta_lanzaBusinessException() {
        LoginRequest request = new LoginRequest("carlos@clink.pe", "malapassword");

        when(usuarioRepository.findByEmail("carlos@clink.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("malapassword", usuario.getPasswordHash())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(request));

        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(jwtUtil, never()).generarToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login: cuenta inactiva lanza BusinessException")
    void login_cuentaInactiva_lanzaBusinessException() {
        usuario.setActivo(false);
        LoginRequest request = new LoginRequest("carlos@clink.pe", "123456");

        when(usuarioRepository.findByEmail("carlos@clink.pe")).thenReturn(Optional.of(usuario));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(request));

        assertEquals("La cuenta está inactiva", ex.getMessage());
    }

    // ============================================================
    // PERFIL
    // ============================================================

    @Test
    @DisplayName("obtenerPerfil: usuario existe retorna sus datos")
    void obtenerPerfil_usuarioExiste_retornaDatos() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enlaceRepository.countByUsuarioIdAndEstadoNot(1L, "ELIMINADO")).thenReturn(3L);

        UsuarioResponse response = authService.obtenerPerfil(1L);

        assertNotNull(response);
        assertEquals("Carlos Condori", response.getNombre());
        assertEquals("carlos@clink.pe", response.getEmail());
        assertEquals(3L, response.getRecursosUsados().longValue());
    }

    @Test
    @DisplayName("obtenerPerfil: usuario no existe lanza ResourceNotFoundException")
    void obtenerPerfil_usuarioNoExiste_lanzaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.obtenerPerfil(99L));
    }
}