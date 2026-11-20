package com.servicios.soa.clientes.guias.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record Guia(@JsonProperty("id_envio") UUID idEnvio,
                   @JsonProperty("id_cobro") UUID idCobro,
                   @JsonProperty("codigo_guia") String codigoGuia,
                   @JsonProperty("url_seguimiento") String urlSeguimiento,
                   @JsonProperty("fecha_emision") Instant fechaEmision) {
}
