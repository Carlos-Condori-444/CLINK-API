package pe.edu.utp.clink.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPlanRequest {

    @NotNull(message = "El planId es obligatorio")
    private Long planId;
}