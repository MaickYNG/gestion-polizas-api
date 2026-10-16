package com.gestionpolizas.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroPoliza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado;

    @Column(nullable = false)
    private String tomador;

    @Column(nullable = false)
    private String asegurado;

    @Column(nullable = false)
    private String beneficiario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal canonMensual;

    @Column(nullable = false)
    private Integer mesesVigencia;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal prima;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Riesgo> riesgos = new ArrayList<>();
}
