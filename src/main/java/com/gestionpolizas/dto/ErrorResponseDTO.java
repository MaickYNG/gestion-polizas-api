package com.gestionpolizas.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
        int codigo,
        String mensaje,
        List<String> errores,
        LocalDateTime timestamp
) {
    public static ErrorResponseDTO of(int codigo, String mensaje) {
        return new ErrorResponseDTO(codigo, mensaje, List.of(), LocalDateTime.now());
    }

    public static ErrorResponseDTO of(int codigo, String mensaje, List<String> errores) {
        return new ErrorResponseDTO(codigo, mensaje, errores, LocalDateTime.now());
    }
}
