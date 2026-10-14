package pe.edu.utp.clink.api.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private Boolean activo;
    private String planNombre;
    private Integer limiteRecursos;
    private Long recursosUsados;
}