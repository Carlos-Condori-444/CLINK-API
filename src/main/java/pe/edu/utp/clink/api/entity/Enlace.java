package pe.edu.utp.clink.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "enlaces")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_original", nullable = false, columnDefinition = "TEXT")
    private String urlOriginal;

    @Column(name = "url_corta", nullable = false, length = 100)
    private String urlCorta;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(unique = true, length = 100)
    private String alias;

    @Column(name = "nombre_personal", length = 100)
    private String nombrePersonal;

    @Column(length = 100)
    private String fuente;

    @Column(name = "clics_total", nullable = false)
    private Integer clicsTotal;

    @Column(name = "clics_movil", nullable = false)
    private Integer clicsMovil;

    @Column(name = "clics_desktop", nullable = false)
    private Integer clicsDesktop;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false, length = 20)
    private String estado;  // ACTIVO, INACTIVO, EXPIRADO, ELIMINADO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campana_id")
    private Campana campana;

    @OneToMany(mappedBy = "enlace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Click> clicks;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (clicsTotal == null) clicsTotal = 0;
        if (clicsMovil == null) clicsMovil = 0;
        if (clicsDesktop == null) clicsDesktop = 0;
        if (estado == null) estado = "ACTIVO";
    }

    public boolean estaActivo() {
        return "ACTIVO".equals(estado);
    }
}