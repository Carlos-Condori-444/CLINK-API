package pe.edu.utp.clink.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String tipo;  // INDEPENDIENTE o ENLACE

    @Column(name = "url_destino", nullable = false, columnDefinition = "TEXT")
    private String urlDestino;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "imagen_ruta", length = 255)
    private String imagenRuta;

    @Column(name = "clics_total", nullable = false)
    private Integer clicsTotal;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enlace_id")
    private Enlace enlace;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (clicsTotal == null) clicsTotal = 0;
        if (activo == null) activo = true;
    }
}