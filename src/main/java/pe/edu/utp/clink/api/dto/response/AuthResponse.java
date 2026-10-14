package pe.edu.utp.clink.api.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tipo;
    private Long usuarioId;
    private String nombre;
    private String email;
    private String rol;
}