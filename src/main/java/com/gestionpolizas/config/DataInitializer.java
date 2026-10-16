package com.gestionpolizas.config;

import com.gestionpolizas.model.*;
import com.gestionpolizas.repository.PolizaRepository;
import com.gestionpolizas.repository.RiesgoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;

    public DataInitializer(PolizaRepository polizaRepository, RiesgoRepository riesgoRepository) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Poliza poliza1 = polizaRepository.save(Poliza.builder()
                .numeroPoliza("POL-2024-001")
                .tipo(TipoPoliza.INDIVIDUAL)
                .estado(EstadoPoliza.ACTIVA)
                .tomador("Carlos Mendoza")
                .asegurado("Carlos Mendoza")
                .beneficiario("Laura Mendoza")
                .canonMensual(new BigDecimal("850000.00"))
                .mesesVigencia(12)
                .prima(new BigDecimal("10200000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2024, 12, 31))
                .build());

        riesgoRepository.save(Riesgo.builder()
                .direccionInmueble("Calle 72 # 45-10, Bogotá")
                .arrendatarioAsegurado("Carlos Mendoza")
                .arrendadorBeneficiario("Laura Mendoza")
                .estado(EstadoRiesgo.ACTIVO)
                .poliza(poliza1)
                .build());

        Poliza poliza2 = polizaRepository.save(Poliza.builder()
                .numeroPoliza("POL-2024-002")
                .tipo(TipoPoliza.COLECTIVA)
                .estado(EstadoPoliza.ACTIVA)
                .tomador("Constructora Andina S.A.S")
                .asegurado("Constructora Andina S.A.S")
                .beneficiario("Constructora Andina S.A.S")
                .canonMensual(new BigDecimal("2400000.00"))
                .mesesVigencia(24)
                .prima(new BigDecimal("57600000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2025, 12, 31))
                .build());

        riesgoRepository.saveAll(List.of(
                Riesgo.builder()
                        .direccionInmueble("Carrera 15 # 93-47, Bogotá")
                        .arrendatarioAsegurado("Pedro Vargas")
                        .arrendadorBeneficiario("Constructora Andina S.A.S")
                        .estado(EstadoRiesgo.ACTIVO)
                        .poliza(poliza2)
                        .build(),
                Riesgo.builder()
                        .direccionInmueble("Avenida El Dorado # 68-85, Bogotá")
                        .arrendatarioAsegurado("Sofía Restrepo")
                        .arrendadorBeneficiario("Constructora Andina S.A.S")
                        .estado(EstadoRiesgo.ACTIVO)
                        .poliza(poliza2)
                        .build()
        ));

        polizaRepository.save(Poliza.builder()
                .numeroPoliza("POL-2024-003")
                .tipo(TipoPoliza.INDIVIDUAL)
                .estado(EstadoPoliza.CANCELADA)
                .tomador("María Torres")
                .asegurado("María Torres")
                .beneficiario("Juan Torres")
                .canonMensual(new BigDecimal("650000.00"))
                .mesesVigencia(6)
                .prima(new BigDecimal("3900000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2024, 6, 30))
                .build());

        log.info("Datos iniciales cargados: 3 pólizas, 3 riesgos.");
    }
}
