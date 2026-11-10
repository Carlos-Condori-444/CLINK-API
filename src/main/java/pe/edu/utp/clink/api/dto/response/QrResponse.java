package pe.edu.utp.clink.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrResponse {
    private Long id;
    private String tipo;
    private String urlDestino;
    private String codigo;
    private String imagenBase64;   // imagen embebida
    private Integer clicsTotal;
    private LocalDateTime fechaCreacion;
    private Boolean activo;
}