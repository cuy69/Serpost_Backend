package com.servicios.soa.clientes.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "serpost")
public record SerpostProperties(
        @NotNull @Valid Envios envios,
        @NotNull @Valid Notificaciones notificaciones
) {
    public record Envios(@NotBlank String baseUrl) {
    }

    public record Notificaciones(
            @Positive long intervaloProcesamientoMs,
            @NotNull @Valid Reintentos reintentos
    ) {
    }

    public record Reintentos(@Positive int maximo) {
    }
}
