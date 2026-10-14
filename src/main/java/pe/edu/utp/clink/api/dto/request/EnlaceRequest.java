package pe.edu.utp.clink.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnlaceRequest {

    @NotBlank(message = "La URL original es obligatoria")
    private String urlOriginal;

    @Size(max = 100)
    private String alias;

    @Size(max = 100)
    private String nombrePersonal;

    @Size(max = 100)
    private String fuente;
}