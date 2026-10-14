package pe.edu.utp.clink.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EnlaceEtiquetaId implements Serializable {

    @Column(name = "enlace_id")
    private Long enlaceId;

    @Column(name = "etiqueta_id")
    private Long etiquetaId;
}