package com.gestionpolizas.controller;

import com.gestionpolizas.dto.PolizaResponseDTO;
import com.gestionpolizas.dto.RenovarPolizaRequestDTO;
import com.gestionpolizas.dto.RiesgoRequestDTO;
import com.gestionpolizas.dto.RiesgoResponseDTO;
import com.gestionpolizas.model.EstadoPoliza;
import com.gestionpolizas.model.TipoPoliza;
import com.gestionpolizas.service.PolizaService;
import com.gestionpolizas.service.RiesgoService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;
    private final RiesgoService riesgoService;

    public PolizaController(PolizaService polizaService, RiesgoService riesgoService) {
        this.polizaService = polizaService;
        this.riesgoService = riesgoService;
    }

    @GetMapping
    public ResponseEntity<List<PolizaResponseDTO>> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return ResponseEntity.ok(polizaService.listarPolizas(tipo, estado));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<PolizaResponseDTO> renovar(
            @PathVariable Long id,
            @RequestBody(required = false) RenovarPolizaRequestDTO request) {
        BigDecimal porcentajeIpc = (request != null) ? request.porcentajeIpc() : null;
        return ResponseEntity.ok(polizaService.renovarPoliza(id, porcentajeIpc));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PolizaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelarPoliza(id));
    }

    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<RiesgoResponseDTO>> listarRiesgos(@PathVariable Long id) {
        return ResponseEntity.ok(riesgoService.listarPorPoliza(id));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiesgoResponseDTO> agregarRiesgo(
            @PathVariable Long id,
            @Valid @RequestBody RiesgoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(riesgoService.agregarRiesgo(id, request));
    }
}
