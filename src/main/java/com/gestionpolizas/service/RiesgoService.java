package com.gestionpolizas.service;

import com.gestionpolizas.dto.RiesgoRequestDTO;
import com.gestionpolizas.dto.RiesgoResponseDTO;
import com.gestionpolizas.exception.BusinessRuleException;
import com.gestionpolizas.exception.ResourceNotFoundException;
import com.gestionpolizas.model.*;
import com.gestionpolizas.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final PolizaService polizaService;
    private final CoreIntegrationService coreIntegrationService;

    public RiesgoService(RiesgoRepository riesgoRepository,
                         PolizaService polizaService,
                         CoreIntegrationService coreIntegrationService) {
        this.riesgoRepository = riesgoRepository;
        this.polizaService = polizaService;
        this.coreIntegrationService = coreIntegrationService;
    }

    @Transactional(readOnly = true)
    public List<RiesgoResponseDTO> listarPorPoliza(Long polizaId) {
        polizaService.obtenerPolizaOFallar(polizaId);
        return riesgoRepository.findByPolizaId(polizaId).stream()
                .map(RiesgoResponseDTO::from)
                .toList();
    }

    public RiesgoResponseDTO agregarRiesgo(Long polizaId, RiesgoRequestDTO request) {
        Poliza poliza = polizaService.obtenerPolizaOFallar(polizaId);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessRuleException("No se puede agregar un riesgo a una póliza en estado CANCELADA.");
        }

        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessRuleException(
                    "Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA mediante este endpoint.");
        }

        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL && poliza.getRiesgos().size() >= 1) {
            throw new BusinessRuleException("Una póliza de tipo INDIVIDUAL solo puede tener máximo 1 riesgo.");
        }

        Riesgo riesgo = Riesgo.builder()
                .direccionInmueble(request.direccionInmueble())
                .arrendatarioAsegurado(request.arrendatarioAsegurado())
                .arrendadorBeneficiario(request.arrendadorBeneficiario())
                .estado(EstadoRiesgo.ACTIVO)
                .poliza(poliza)
                .build();

        Riesgo guardado = riesgoRepository.save(riesgo);
        coreIntegrationService.notificarActualizacion("ACTUALIZACION", polizaId);

        return RiesgoResponseDTO.from(guardado);
    }

    public RiesgoResponseDTO cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado con id: " + riesgoId));

        riesgo.setEstado(EstadoRiesgo.CANCELADO);

        Riesgo actualizado = riesgoRepository.save(riesgo);
        coreIntegrationService.notificarActualizacion("ACTUALIZACION", riesgo.getPoliza().getId());

        return RiesgoResponseDTO.from(actualizado);
    }
}
