package com.servicios.soa.clientes.envios.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.servicios.soa.clientes.envios.model.Envio;

import java.math.BigDecimal;
import java.util.UUID;

public record EnvioResponse(
        @JsonProperty("id_envio") UUID idEnvio,
        @JsonProperty("id_remitente") String idRemitente,
        @JsonProperty("id_destinatario") String idDestinatario,
        @JsonProperty("peso_kg") BigDecimal pesoKg,
        String contenido,
        @JsonProperty("linea_negocio") String lineaNegocio,
        String origen,
        String destino,
        String estado,
        String servicio,
        String zona,
        @JsonProperty("ida_vuelta") Boolean idaVuelta,
        @JsonProperty("tarifa_total") BigDecimal tarifaTotal) {
    public static EnvioResponse from(Envio envio) {
        return new EnvioResponse(envio.getIdEnvio(), envio.getIdRemitente(), envio.getIdDestinatario(),
                envio.getPesoKg(), envio.getContenido(), envio.getLineaNegocio(), envio.getOrigen(),
                envio.getDestino(), envio.getEstado(), envio.getServicio(), envio.getZona(),
                envio.getIdaVuelta(), envio.getTarifaTotal());
    }
}
