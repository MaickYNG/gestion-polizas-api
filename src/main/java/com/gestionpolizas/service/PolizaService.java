package com.gestionpolizas.service;

import com.gestionpolizas.config.AppProperties;
import com.gestionpolizas.dto.PolizaResponseDTO;
import com.gestionpolizas.exception.BusinessRuleException;
import com.gestionpolizas.exception.ResourceNotFoundException;
import com.gestionpolizas.model.EstadoPoliza;
import com.gestionpolizas.model.EstadoRiesgo;
import com.gestionpolizas.model.Poliza;
import com.gestionpolizas.model.TipoPoliza;
import com.gestionpolizas.repository.PolizaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final CoreIntegrationService coreIntegrationService;
    private final AppProperties appProperties;

    public PolizaService(PolizaRepository polizaRepository,
                         CoreIntegrationService coreIntegrationService,
                         AppProperties appProperties) {
        this.polizaRepository = polizaRepository;
        this.coreIntegrationService = coreIntegrationService;
        this.appProperties = appProperties;
    }

    @Transactional(readOnly = true)
    public List<PolizaResponseDTO> listarPolizas(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.filtrarPorTipoYEstado(tipo, estado).stream()
                .map(PolizaResponseDTO::from)
                .toList();
    }

    public PolizaResponseDTO renovarPoliza(Long id, BigDecimal porcentajeIpcSolicitado) {
        Poliza poliza = obtenerPolizaOFallar(id);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessRuleException("No se puede renovar una póliza en estado CANCELADA.");
        }

        BigDecimal porcentajeIpc = (porcentajeIpcSolicitado != null)
                ? porcentajeIpcSolicitado
                : appProperties.getIpc().getPorcentajeDefecto();

        BigDecimal factor = BigDecimal.ONE.add(
                porcentajeIpc.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
        );

        BigDecimal nuevoCanon = poliza.getCanonMensual()
                .multiply(factor)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal nuevaPrima = nuevoCanon
                .multiply(BigDecimal.valueOf(poliza.getMesesVigencia()))
                .setScale(2, RoundingMode.HALF_UP);

        poliza.setCanonMensual(nuevoCanon);
        poliza.setPrima(nuevaPrima);
        poliza.setEstado(EstadoPoliza.RENOVADA);

        Poliza actualizada = polizaRepository.save(poliza);
        coreIntegrationService.notificarActualizacion("ACTUALIZACION", actualizada.getId());

        return PolizaResponseDTO.from(actualizada);
    }

    public PolizaResponseDTO cancelarPoliza(Long id) {
        Poliza poliza = obtenerPolizaOFallar(id);

        poliza.setEstado(EstadoPoliza.CANCELADA);
        poliza.getRiesgos().forEach(riesgo -> riesgo.setEstado(EstadoRiesgo.CANCELADO));

        Poliza actualizada = polizaRepository.save(poliza);
        coreIntegrationService.notificarActualizacion("ACTUALIZACION", actualizada.getId());

        return PolizaResponseDTO.from(actualizada);
    }

    @Transactional(readOnly = true)
    public Poliza obtenerPolizaOFallar(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con id: " + id));
    }
}
