package com.gestionpolizas.dto;

import jakarta.validation.constraints.NotBlank;

public record RiesgoRequestDTO(
        @NotBlank(message = "La dirección del inmueble es obligatoria")
        String direccionInmueble,

        @NotBlank(message = "El arrendatario asegurado es obligatorio")
        String arrendatarioAsegurado,

        @NotBlank(message = "El arrendador beneficiario es obligatorio")
        String arrendadorBeneficiario
) {}
