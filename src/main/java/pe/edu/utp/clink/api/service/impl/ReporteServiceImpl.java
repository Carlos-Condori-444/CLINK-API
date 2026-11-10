package pe.edu.utp.clink.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pe.edu.utp.clink.api.entity.Enlace;
import pe.edu.utp.clink.api.entity.Usuario;
import pe.edu.utp.clink.api.exception.BusinessException;
import pe.edu.utp.clink.api.exception.ResourceNotFoundException;
import pe.edu.utp.clink.api.repository.EnlaceRepository;
import pe.edu.utp.clink.api.repository.UsuarioRepository;
import pe.edu.utp.clink.api.service.ReporteService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final UsuarioRepository usuarioRepository;
    private final EnlaceRepository enlaceRepository;

    @Override
    public byte[] exportarExcel(Long usuarioId) throws IOException {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getPlan().getPermiteExportar())) {
            throw new BusinessException("La exportación a Excel es exclusiva del plan Premium");
        }

        List<Enlace> enlaces = enlaceRepository.listarActivosPorUsuario(usuarioId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Enlaces");

            // Encabezados
            Row header = sheet.createRow(0);
            String[] columnas = {
                    "ID", "Alias", "URL Original", "URL Corta",
                    "Clics Total", "Clics Móvil", "Clics Desktop",
                    "Estado", "Fecha Creación"
            };
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Datos
            int rowNum = 1;
            for (Enlace e : enlaces) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getAlias() != null ? e.getAlias() : "");
                row.createCell(2).setCellValue(e.getUrlOriginal());
                row.createCell(3).setCellValue(e.getUrlCorta());
                row.createCell(4).setCellValue(e.getClicsTotal());
                row.createCell(5).setCellValue(e.getClicsMovil());
                row.createCell(6).setCellValue(e.getClicsDesktop());
                row.createCell(7).setCellValue(e.getEstado());
                row.createCell(8).setCellValue(e.getFechaCreacion().toString());
            }

            // Auto-ajustar columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}