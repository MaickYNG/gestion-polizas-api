package com.gestionpolizas.dto;

import com.gestionpolizas.model.EstadoRiesgo;
import com.gestionpolizas.model.Riesgo;

public record RiesgoResponseDTO(
        Long id,
        String direccionInmueble,
        String arrendatarioAsegurado,
        String arrendadorBeneficiario,
        EstadoRiesgo estado,
        Long polizaId
) {
    public static RiesgoResponseDTO from(Riesgo riesgo) {
        return new RiesgoResponseDTO(
                riesgo.getId(),
                riesgo.getDireccionInmueble(),
                riesgo.getArrendatarioAsegurado(),
                riesgo.getArrendadorBeneficiario(),
                riesgo.getEstado(),
                riesgo.getPoliza().getId()
        );
    }
}
