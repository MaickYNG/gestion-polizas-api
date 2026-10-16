package com.gestionpolizas.controller;

import com.gestionpolizas.dto.CoreEventoRequestDTO;
import com.gestionpolizas.service.CoreIntegrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private final CoreIntegrationService coreIntegrationService;

    public CoreMockController(CoreIntegrationService coreIntegrationService) {
        this.coreIntegrationService = coreIntegrationService;
    }

    @PostMapping("/evento")
    public ResponseEntity<Map<String, String>> registrarEvento(
            @Valid @RequestBody CoreEventoRequestDTO request) {
        coreIntegrationService.notificarActualizacion(request.evento(), request.polizaId());
        return ResponseEntity.ok(Map.of("estado", "ENVIADO", "evento", request.evento(),
                "polizaId", String.valueOf(request.polizaId())));
    }
}
