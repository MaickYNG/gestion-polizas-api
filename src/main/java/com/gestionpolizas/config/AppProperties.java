package com.gestionpolizas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Security security = new Security();
    private Ipc ipc = new Ipc();

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Ipc getIpc() {
        return ipc;
    }

    public void setIpc(Ipc ipc) {
        this.ipc = ipc;
    }

    public static class Security {
        private String apiKey;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }
    }

    public static class Ipc {
        private BigDecimal porcentajeDefecto;

        public BigDecimal getPorcentajeDefecto() {
            return porcentajeDefecto;
        }

        public void setPorcentajeDefecto(BigDecimal porcentajeDefecto) {
            this.porcentajeDefecto = porcentajeDefecto;
        }
    }
}
