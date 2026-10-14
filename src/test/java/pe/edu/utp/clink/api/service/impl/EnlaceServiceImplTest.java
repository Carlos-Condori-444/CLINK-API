package pe.edu.utp.clink.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.clink.api.dto.request.EnlaceRequest;
import pe.edu.utp.clink.api.dto.response.EnlaceResponse;
import pe.edu.utp.clink.api.entity.Enlace;
import pe.edu.utp.clink.api.entity.Plan;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.repository.UsuarioRepository;
import pe.edu.utp.clink.api.service.ClickService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - EnlaceServiceImpl")
class EnlaceServiceImplTest {

    @Mock private EnlaceRepository enlaceRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ClickService clickService;

    @InjectMocks
    private EnlaceServiceImpl enlaceService;

    private Usuario usuario;
    private Plan planFree;
    private EnlaceRequest enlaceRequest;
    private Enlace enlace;

    @BeforeEach
    void setUp() {
        planFree = Plan.builder()
                .id(1L).nombre("Plan Gratuito").limiteRecursos(10)
                .build();

        usuario = Usuario.builder()
                .id(1L).nombre("Carlos").email("carlos@clink.pe")
                .plan(planFree).activo(true).rol("CLIENTE")
                .build();

        enlaceRequest = new EnlaceRequest(
                "https://www.google.com",
                "mi-promo",
                "Campaña Google",
                "facebook"
        );

        enlace = Enlace.builder()
                .id(1L)
                .urlOriginal("https://www.google.com")
                .urlCorta("https://clink.pe/mi-promo")
                .codigo("abc123")
                .alias("mi-promo")
                .nombrePersonal("Campaña Google")
                .fuente("facebook")
                .clicsTotal(0).clicsMovil(0).clicsDesktop(0)
                .estado("ACTIVO")
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMonths(2))
                .usuario(usuario)
                .build();
    }

    // ============================================================
    // CREAR
    // ============================================================

    @Test
    @DisplayName("crear: dentro del límite crea enlace correctamente")
    void crear_dentroDelLimite_creaEnlace() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enlaceRepository.countByUsuarioIdAndEstadoNot(1L, "ELIMINADO")).thenReturn(5L);
        when(enlaceRepository.existsByAlias("mi-promo")).thenReturn(false);
        when(enlaceRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(enlaceRepository.save(any(Enlace.class))).thenReturn(enlace);

        EnlaceResponse response = enlaceService.crear(1L, enlaceRequest);

        assertNotNull(response);
        assertEquals("https://www.google.com", response.getUrlOriginal());
        assertEquals("mi-promo", response.getAlias());
        assertEquals("ACTIVO", response.getEstado());

        verify(enlaceRepository).save(any(Enlace.class));
    }

    @Test
    @DisplayName("crear: límite de 10 excedido lanza BusinessException")
    void crear_limiteExcedido_lanzaBusinessException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enlaceRepository.countByUsuarioIdAndEstadoNot(1L, "ELIMINADO")).thenReturn(10L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enlaceService.crear(1L, enlaceRequest));

        assertTrue(ex.getMessage().contains("límite de 10"));
        verify(enlaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: alias duplicado lanza BusinessException")
    void crear_aliasDuplicado_lanzaBusinessException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enlaceRepository.countByUsuarioIdAndEstadoNot(1L, "ELIMINADO")).thenReturn(5L);
        when(enlaceRepository.existsByAlias("mi-promo")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enlaceService.crear(1L, enlaceRequest));

        assertEquals("El alias ya está en uso", ex.getMessage());
        verify(enlaceRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear: usuario no existe lanza ResourceNotFoundException")
    void crear_usuarioNoExiste_lanzaResourceNotFoundException() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> enlaceService.crear(99L, enlaceRequest));

        verify(enlaceRepository, never()).save(any());
    }

    // ============================================================
    // LISTAR
    // ============================================================

    @Test
    @DisplayName("listar: retorna solo enlaces del usuario")
    void listar_retornaEnlacesDelUsuario() {
        when(enlaceRepository.listarActivosPorUsuario(1L)).thenReturn(List.of(enlace));

        List<EnlaceResponse> resultado = enlaceService.listar(1L);

        assertEquals(1, resultado.size());
        assertEquals("mi-promo", resultado.get(0).getAlias());
        verify(enlaceRepository).listarActivosPorUsuario(1L);
    }

    @Test
    @DisplayName("listar: usuario sin enlaces retorna lista vacía")
    void listar_sinEnlaces_retornaListaVacia() {
        when(enlaceRepository.listarActivosPorUsuario(1L)).thenReturn(List.of());

        List<EnlaceResponse> resultado = enlaceService.listar(1L);

        assertTrue(resultado.isEmpty());
    }

    // ============================================================
    // OBTENER
    // ============================================================

    @Test
    @DisplayName("obtener: enlace del propio usuario retorna datos")
    void obtener_enlacePropio_retornaDatos() {
        when(enlaceRepository.findById(1L)).thenReturn(Optional.of(enlace));

        EnlaceResponse response = enlaceService.obtener(1L, 1L);

        assertNotNull(response);
        assertEquals("mi-promo", response.getAlias());
    }

    @Test
    @DisplayName("obtener: enlace de otro usuario lanza BusinessException")
    void obtener_enlaceDeOtroUsuario_lanzaBusinessException() {
        when(enlaceRepository.findById(1L)).thenReturn(Optional.of(enlace));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enlaceService.obtener(99L, 1L));

        assertTrue(ex.getMessage().contains("permiso"));
    }

    @Test
    @DisplayName("obtener: enlace no existe lanza ResourceNotFoundException")
    void obtener_enlaceNoExiste_lanzaResourceNotFoundException() {
        when(enlaceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> enlaceService.obtener(1L, 99L));
    }

    // ============================================================
    // ELIMINAR (soft delete)
    // ============================================================

    @Test
    @DisplayName("eliminar: cambia estado a ELIMINADO")
    void eliminar_enlacePropio_cambiaEstado() {
        when(enlaceRepository.findById(1L)).thenReturn(Optional.of(enlace));
        when(enlaceRepository.save(any(Enlace.class))).thenReturn(enlace);

        enlaceService.eliminar(1L, 1L);

        assertEquals("ELIMINADO", enlace.getEstado());
        verify(enlaceRepository).save(enlace);
    }

    @Test
    @DisplayName("eliminar: enlace de otro usuario lanza BusinessException")
    void eliminar_enlaceDeOtroUsuario_lanzaBusinessException() {
        when(enlaceRepository.findById(1L)).thenReturn(Optional.of(enlace));

        assertThrows(BusinessException.class,
                () -> enlaceService.eliminar(99L, 1L));

        verify(enlaceRepository, never()).save(any());
    }

    // ============================================================
    // REDIRIGIR
    // ============================================================

    @Test
    @DisplayName("redirigir: código válido retorna URL original y registra click")
    void redirigir_codigoValido_retornaUrlOriginal() {
        when(enlaceRepository.findByCodigo("abc123")).thenReturn(Optional.of(enlace));

        String url = enlaceService.redirigir(
                "abc123", "127.0.0.1",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)",
                "https://facebook.com"
        );

        assertEquals("https://www.google.com", url);
        verify(clickService).registrarClick(eq(1L), eq("127.0.0.1"),
                eq("MOVIL"), anyString(), eq("https://facebook.com"), anyString());
    }

    @Test
    @DisplayName("redirigir: user-agent Windows detecta DESKTOP")
    void redirigir_userAgentDesktop_detectaDesktop() {
        when(enlaceRepository.findByCodigo("abc123")).thenReturn(Optional.of(enlace));

        enlaceService.redirigir("abc123", "127.0.0.1",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0", null);

        verify(clickService).registrarClick(eq(1L), anyString(),
                eq("DESKTOP"), eq("Chrome"), any(), anyString());
    }

    @Test
    @DisplayName("redirigir: código no existe lanza ResourceNotFoundException")
    void redirigir_codigoNoExiste_lanzaResourceNotFoundException() {
        when(enlaceRepository.findByCodigo("noexiste")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> enlaceService.redirigir("noexiste", "127.0.0.1", "agent", null));

        verify(clickService, never()).registrarClick(anyLong(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("redirigir: enlace eliminado lanza BusinessException")
    void redirigir_enlaceEliminado_lanzaBusinessException() {
        enlace.setEstado("ELIMINADO");
        when(enlaceRepository.findByCodigo("abc123")).thenReturn(Optional.of(enlace));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enlaceService.redirigir("abc123", "127.0.0.1", "agent", null));

        assertTrue(ex.getMessage().contains("eliminado"));
        verify(clickService, never()).registrarClick(anyLong(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("redirigir: enlace expirado lanza BusinessException")
    void redirigir_enlaceExpirado_lanzaBusinessException() {
        enlace.setFechaExpiracion(LocalDateTime.now().minusDays(1));
        when(enlaceRepository.findByCodigo("abc123")).thenReturn(Optional.of(enlace));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enlaceService.redirigir("abc123", "127.0.0.1", "agent", null));

        assertTrue(ex.getMessage().contains("expirado"));
    }

    @Test
    @DisplayName("redirigir: enlace inactivo lanza BusinessException")
    void redirigir_enlaceInactivo_lanzaBusinessException() {
        enlace.setEstado("INACTIVO");
        when(enlaceRepository.findByCodigo("abc123")).thenReturn(Optional.of(enlace));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enlaceService.redirigir("abc123", "127.0.0.1", "agent", null));

        assertTrue(ex.getMessage().contains("inactivo"));
    }
}