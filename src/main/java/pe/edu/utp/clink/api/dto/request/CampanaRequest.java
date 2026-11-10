package pe.edu.utp.clink.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampanaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}