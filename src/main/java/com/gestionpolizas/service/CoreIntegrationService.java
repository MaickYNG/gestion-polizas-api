package com.gestionpolizas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CoreIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(CoreIntegrationService.class);

    public void notificarActualizacion(String evento, Long polizaId) {
        log.info("Enviando evento '{}' al CORE para polizaId={}", evento, polizaId);
    }
}
