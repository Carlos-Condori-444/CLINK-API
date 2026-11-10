package pe.edu.utp.clink.api.service;

import java.io.IOException;

public interface ReporteService {
    byte[] exportarExcel(Long usuarioId) throws IOException;
}