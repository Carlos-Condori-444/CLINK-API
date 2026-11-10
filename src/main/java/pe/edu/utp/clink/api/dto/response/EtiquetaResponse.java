package pe.edu.utp.clink.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtiquetaResponse {
    private Long id;
    private String nombre;
    private String color;
    private LocalDateTime fechaCreacion;
}