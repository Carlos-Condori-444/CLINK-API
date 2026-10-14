package pe.edu.utp.clink.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "planes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "limite_recursos", nullable = false)
    private Integer limiteRecursos;

    @Column(name = "permite_qr_propio", nullable = false)
    private Boolean permiteQrPropio;

    @Column(name = "permite_etiquetas", nullable = false)
    private Boolean permiteEtiquetas;

    @Column(name = "permite_exportar", nullable = false)
    private Boolean permiteExportar;

    @Column(name = "permite_desactivar", nullable = false)
    private Boolean permiteDesactivar;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 255)
    private String descripcion;
}