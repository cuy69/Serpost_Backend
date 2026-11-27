package com.servicios.soa.clientes.envios.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EnvioRequest(
        @JsonProperty("id_remitente") @Size(max = 50) String idRemitente,
        @JsonProperty("id_destinatario") @Size(max = 50) String idDestinatario,
        @JsonProperty("peso_kg") @NotNull @DecimalMin(value = "0.001") BigDecimal pesoKg,
        @JsonProperty("contenido") @NotBlank @Size(max = 500) String contenido,
        @JsonProperty("linea_negocio") @NotBlank @Size(max = 80) String lineaNegocio,
        @JsonProperty("origen") @NotBlank @Size(max = 150) String origen,
        @JsonProperty("destino") @NotBlank @Size(max = 150) String destino) {
}
