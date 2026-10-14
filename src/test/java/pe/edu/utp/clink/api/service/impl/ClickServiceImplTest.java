package pe.edu.utp.clink.api.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.clink.api.entity.Click;
import pe.edu.utp.clink.api.entity.Enlace;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.ClickRepository;
import pe.edu.utp.clink.api.repository.EnlaceRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ClickServiceImpl")
class ClickServiceImplTest {

    @Mock private ClickRepository clickRepository;
    @Mock private EnlaceRepository enlaceRepository;

    @InjectMocks
    private ClickServiceImpl clickService;

    private Enlace enlace;

    @BeforeEach
    void setUp() {
        enlace = Enlace.builder()
                .id(1L)
                .urlOriginal("https://google.com")
                .clicsTotal(0).clicsMovil(0).clicsDesktop(0)
                .estado("ACTIVO")
                .build();
    }

    // ============================================================
    // REGISTRAR CLICK
    // ============================================================

    @Test
    @DisplayName("registrarClick: dispositivo MOVIL incrementa clicsMovil")
    void registrarClick_dispositivoMovil_incrementaMovil() {
        when(enlaceRepository.findById(1L)).thenReturn(Optional.of(enlace));
        when(clickRepository.save(any(Click.class))).thenAnswer(inv -> inv.getArgument(0));
        when(enlaceRepository.save(any(Enlace.class))).thenReturn(enlace);

        clickService.registrarClick(1L, "127.0.0.1", "MOVIL", "Chrome", "facebook", "agent");

        assertEquals(1, enlace.getClicsTotal().intValue());
        assertEquals(1, enlace.getClicsMovil().intValue());
        assertEquals(0, enlace.getClicsDesktop().intValue());

        verify(clickRepository).save(any(Click.class));
        verify(enlaceRepository).save(enlace);
    }

    @Test
    @DisplayName("registrarClick: dispositivo DESKTOP incrementa clicsDesktop")
    void registrarClick_dispositivoDesktop_incrementaDesktop() {
        when(enlaceRepository.findById(1L)).thenReturn(Optional.of(enlace));
        when(clickRepository.save(any(Click.class))).thenAnswer(inv -> inv.getArgument(0));
        when(enlaceRepository.save(any(Enlace.class))).thenReturn(enlace);

        clickService.registrarClick(1L, "127.0.0.1", "DESKTOP", "Firefox", null, "agent");

        assertEquals(1, enlace.getClicsTotal().intValue());
        assertEquals(0, enlace.getClicsMovil().intValue());
        assertEquals(1, enlace.getClicsDesktop().intValue());
    }

    @Test
    @DisplayName("registrarClick: dispositivo OTRO solo incrementa total")
    void registrarClick_dispositivoOtro_soloIncrementaTotal() {
        when(enlaceRepository.findById(1L)).thenReturn(Optional.of(enlace));
        when(clickRepository.save(any(Click.class))).thenAnswer(inv -> inv.getArgument(0));
        when(enlaceRepository.save(any(Enlace.class))).thenReturn(enlace);

        clickService.registrarClick(1L, "127.0.0.1", "OTRO", "Bot", null, "agent");

        assertEquals(1, enlace.getClicsTotal().intValue());
        assertEquals(0, enlace.getClicsMovil().intValue());
        assertEquals(0, enlace.getClicsDesktop().intValue());
    }

    @Test
    @DisplayName("registrarClick: enlace no existe lanza ResourceNotFoundException")
    void registrarClick_enlaceNoExiste_lanzaResourceNotFoundException() {
        when(enlaceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clickService.registrarClick(99L, "127.0.0.1", "MOVIL",
                        "Chrome", null, "agent"));

        verify(clickRepository, never()).save(any());
    }

    // ============================================================
    // ESTADÍSTICAS
    // ============================================================

    @Test
    @DisplayName("estadisticasPorDispositivo: agrupa correctamente")
    void estadisticasPorDispositivo_agrupaCorrectamente() {
        List<Object[]> resultado = List.of(
                new Object[]{"MOVIL", 15L},
                new Object[]{"DESKTOP", 8L}
        );
        when(clickRepository.contarPorDispositivo(1L)).thenReturn(resultado);

        Map<String, Long> mapa = clickService.estadisticasPorDispositivo(1L);

        assertEquals(2, mapa.size());
        assertEquals(15L, mapa.get("MOVIL").longValue());
        assertEquals(8L, mapa.get("DESKTOP").longValue());
    }

    @Test
    @DisplayName("estadisticasPorFuente: agrupa correctamente")
    void estadisticasPorFuente_agrupaCorrectamente() {
        List<Object[]> resultado = List.of(
                new Object[]{"facebook", 20L},
                new Object[]{"instagram", 12L}
        );
        when(clickRepository.contarPorFuente(1L)).thenReturn(resultado);

        Map<String, Long> mapa = clickService.estadisticasPorFuente(1L);

        assertEquals(2, mapa.size());
        assertEquals(20L, mapa.get("facebook").longValue());
        assertEquals(12L, mapa.get("instagram").longValue());
    }
}