package com.servicios.soa.clientes.tarifas.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CalcularTarifaRequest(
        @NotBlank @Pattern(regexp = "(?i)Personal|Empresarial|Encomienda|Filatelia")
        String linea_negocio,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal peso_kg,
        @NotBlank String origen,
        @NotBlank String destino,
        @NotBlank String tipo_servicio
) {
    public CalcularTarifaRequest {
        linea_negocio = trim(linea_negocio);
        origen = trim(origen);
        destino = trim(destino);
        tipo_servicio = trim(tipo_servicio);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
