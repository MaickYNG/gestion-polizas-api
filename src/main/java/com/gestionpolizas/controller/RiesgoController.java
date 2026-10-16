package com.gestionpolizas.controller;

import com.gestionpolizas.dto.RiesgoResponseDTO;
import com.gestionpolizas.service.RiesgoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {

    private final RiesgoService riesgoService;

    public RiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<RiesgoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.cancelarRiesgo(id));
    }
}
