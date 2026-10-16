package com.gestionpolizas.service;

import com.gestionpolizas.dto.RiesgoRequestDTO;
import com.gestionpolizas.exception.BusinessRuleException;
import com.gestionpolizas.model.*;
import com.gestionpolizas.repository.RiesgoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiesgoServiceTest {

    @Mock
    private RiesgoRepository riesgoRepository;

    @Mock
    private PolizaService polizaService;

    @Mock
    private CoreIntegrationService coreIntegrationService;

    @InjectMocks
    private RiesgoService riesgoService;

    @Test
    void agregarRiesgo_aPolizaIndividual_lanzaBusinessRuleException() {
        Poliza poliza = new Poliza();
        poliza.setId(1L);
        poliza.setTipo(TipoPoliza.INDIVIDUAL);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setRiesgos(new ArrayList<>());

        when(polizaService.obtenerPolizaOFallar(1L)).thenReturn(poliza);

        RiesgoRequestDTO request = new RiesgoRequestDTO(
                "Calle 1 # 2-3",
                "Arrendatario Test",
                "Beneficiario Test"
        );

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> riesgoService.agregarRiesgo(1L, request));

        assertTrue(ex.getMessage().contains("COLECTIVA"));
        verify(riesgoRepository, never()).save(any());
    }

    @Test
    void agregarRiesgo_aPolizaCancelada_lanzaBusinessRuleException() {
        Poliza poliza = new Poliza();
        poliza.setId(2L);
        poliza.setTipo(TipoPoliza.COLECTIVA);
        poliza.setEstado(EstadoPoliza.CANCELADA);
        poliza.setRiesgos(new ArrayList<>());

        when(polizaService.obtenerPolizaOFallar(2L)).thenReturn(poliza);

        RiesgoRequestDTO request = new RiesgoRequestDTO(
                "Calle 5 # 10-20",
                "Arrendatario Test",
                "Beneficiario Test"
        );

        assertThrows(BusinessRuleException.class,
                () -> riesgoService.agregarRiesgo(2L, request));

        verify(riesgoRepository, never()).save(any());
    }
}
