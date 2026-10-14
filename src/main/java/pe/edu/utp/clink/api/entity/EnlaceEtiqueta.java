package pe.edu.utp.clink.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enlace_etiqueta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnlaceEtiqueta {

    @EmbeddedId
    private EnlaceEtiquetaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("enlaceId")
    @JoinColumn(name = "enlace_id")
    private Enlace enlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("etiquetaId")
    @JoinColumn(name = "etiqueta_id")
    private Etiqueta etiqueta;
}