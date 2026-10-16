package com.gestionpolizas.service;

import com.gestionpolizas.config.AppProperties;
import com.gestionpolizas.dto.PolizaResponseDTO;
import com.gestionpolizas.exception.BusinessRuleException;
import com.gestionpolizas.model.*;
import com.gestionpolizas.repository.PolizaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolizaServiceTest {

    @Mock
    private PolizaRepository polizaRepository;

    @Mock
    private CoreIntegrationService coreIntegrationService;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private PolizaService polizaService;

    @Test
    void renovarPoliza_conEstadoCancelada_lanzaBusinessRuleException() {
        Poliza poliza = new Poliza();
        poliza.setId(1L);
        poliza.setEstado(EstadoPoliza.CANCELADA);

        when(polizaRepository.findById(1L)).thenReturn(Optional.of(poliza));

        assertThrows(BusinessRuleException.class,
                () -> polizaService.renovarPoliza(1L, null));

        verify(polizaRepository, never()).save(any());
    }

    @Test
    void cancelarPoliza_cancelaEnCascadaTodosLosRiesgos() {
        Riesgo r1 = new Riesgo();
        r1.setId(1L);
        r1.setEstado(EstadoRiesgo.ACTIVO);
        r1.setDireccionInmueble("Calle 72 # 45-10, Bogotá");
        r1.setArrendatarioAsegurado("Carlos Mendoza");
        r1.setArrendadorBeneficiario("Laura Mendoza");

        Riesgo r2 = new Riesgo();
        r2.setId(2L);
        r2.setEstado(EstadoRiesgo.ACTIVO);
        r2.setDireccionInmueble("Carrera 15 # 93-47, Bogotá");
        r2.setArrendatarioAsegurado("Pedro Vargas");
        r2.setArrendadorBeneficiario("Constructora Andina");

        Poliza poliza = construirPolizaActiva(TipoPoliza.COLECTIVA, new ArrayList<>(List.of(r1, r2)));
        r1.setPoliza(poliza);
        r2.setPoliza(poliza);

        when(polizaRepository.findById(1L)).thenReturn(Optional.of(poliza));
        when(polizaRepository.save(any())).thenReturn(poliza);

        PolizaResponseDTO resultado = polizaService.cancelarPoliza(1L);

        assertEquals(EstadoPoliza.CANCELADA, poliza.getEstado());
        assertTrue(poliza.getRiesgos().stream()
                .allMatch(r -> r.getEstado() == EstadoRiesgo.CANCELADO),
                "Todos los riesgos deben quedar en estado CANCELADO");
        verify(coreIntegrationService).notificarActualizacion(eq("ACTUALIZACION"), eq(1L));
    }

    @Test
    void renovarPoliza_conIpcExplicito_recalculaCanonYPrimaCorrectamente() {
        Poliza poliza = construirPolizaActiva(TipoPoliza.INDIVIDUAL, new ArrayList<>());
        poliza.setCanonMensual(new BigDecimal("1000000.00"));
        poliza.setMesesVigencia(12);

        when(polizaRepository.findById(1L)).thenReturn(Optional.of(poliza));
        when(polizaRepository.save(any())).thenReturn(poliza);

        polizaService.renovarPoliza(1L, new BigDecimal("10"));

        assertEquals(new BigDecimal("1100000.00"), poliza.getCanonMensual());
        assertEquals(new BigDecimal("13200000.00"), poliza.getPrima());
        assertEquals(EstadoPoliza.RENOVADA, poliza.getEstado());
    }

    private Poliza construirPolizaActiva(TipoPoliza tipo, List<Riesgo> riesgos) {
        Poliza poliza = new Poliza();
        poliza.setId(1L);
        poliza.setNumeroPoliza("POL-TEST-001");
        poliza.setTipo(tipo);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setTomador("Tomador Prueba");
        poliza.setAsegurado("Asegurado Prueba");
        poliza.setBeneficiario("Beneficiario Prueba");
        poliza.setCanonMensual(new BigDecimal("1000000.00"));
        poliza.setMesesVigencia(12);
        poliza.setPrima(new BigDecimal("12000000.00"));
        poliza.setFechaInicio(LocalDate.of(2024, 1, 1));
        poliza.setFechaFin(LocalDate.of(2024, 12, 31));
        poliza.setRiesgos(riesgos);
        return poliza;
    }
}
