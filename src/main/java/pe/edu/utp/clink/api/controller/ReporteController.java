package pe.edu.utp.clink.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.clink.api.security.UsuarioDetails;
import pe.edu.utp.clink.api.service.ReporteService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Exportación de reportes a Excel (solo plan Premium)")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/excel")
    @Operation(summary = "Exportar enlaces a Excel",
            description = "Genera un archivo XLSX con los enlaces del usuario y sus métricas. Requiere plan Premium.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<byte[]> exportarExcel(
            @AuthenticationPrincipal UsuarioDetails userDetails) throws IOException {
        byte[] archivo = reporteService.exportarExcel(userDetails.getUsuario().getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enlaces.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(archivo);
    }
}