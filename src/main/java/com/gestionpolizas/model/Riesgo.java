package com.gestionpolizas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "riesgos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String direccionInmueble;

    @Column(nullable = false)
    private String arrendatarioAsegurado;

    @Column(nullable = false)
    private String arrendadorBeneficiario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRiesgo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    @JsonIgnore
    private Poliza poliza;
}
