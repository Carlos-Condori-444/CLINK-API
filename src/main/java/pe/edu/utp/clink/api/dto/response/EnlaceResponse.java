package pe.edu.utp.clink.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnlaceResponse {
    private Long id;
    private String urlOriginal;
    private String urlCorta;
    private String codigo;
    private String alias;
    private String nombrePersonal;
    private String fuente;
    private Integer clicsTotal;
    private Integer clicsMovil;
    private Integer clicsDesktop;
    private LocalDateTime fechaExpiracion;
    private LocalDateTime fechaCreacion;
    private String estado;
}