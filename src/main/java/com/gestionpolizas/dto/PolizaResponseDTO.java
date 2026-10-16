package com.gestionpolizas.dto;

import com.gestionpolizas.model.EstadoPoliza;
import com.gestionpolizas.model.Poliza;
import com.gestionpolizas.model.TipoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PolizaResponseDTO(
        Long id,
        String numeroPoliza,
        TipoPoliza tipo,
        EstadoPoliza estado,
        String tomador,
        String asegurado,
        String beneficiario,
        BigDecimal canonMensual,
        Integer mesesVigencia,
        BigDecimal prima,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        List<RiesgoResponseDTO> riesgos
) {
    public static PolizaResponseDTO from(Poliza poliza) {
        return new PolizaResponseDTO(
                poliza.getId(),
                poliza.getNumeroPoliza(),
                poliza.getTipo(),
                poliza.getEstado(),
                poliza.getTomador(),
                poliza.getAsegurado(),
                poliza.getBeneficiario(),
                poliza.getCanonMensual(),
                poliza.getMesesVigencia(),
                poliza.getPrima(),
                poliza.getFechaInicio(),
                poliza.getFechaFin(),
                poliza.getRiesgos().stream()
                        .map(RiesgoResponseDTO::from)
                        .toList()
        );
    }
}
