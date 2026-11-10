package pe.edu.utp.clink.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrRequest {

    // "INDEPENDIENTE" o "ENLACE"
    @NotBlank
    @Pattern(regexp = "INDEPENDIENTE|ENLACE", message = "Tipo inválido")
    private String tipo;

    // Obligatorio si tipo = INDEPENDIENTE
    private String urlDestino;

    // Obligatorio si tipo = ENLACE
    private Long enlaceId;
}