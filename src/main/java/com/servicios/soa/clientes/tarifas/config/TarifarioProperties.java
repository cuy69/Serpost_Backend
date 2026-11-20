package com.servicios.soa.clientes.tarifas.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "serpost.tarifas")
public record TarifarioProperties(@NotNull List<@Valid Regla> reglas) {
    public TarifarioProperties {
        reglas = reglas == null ? List.of() : List.copyOf(reglas);
    }

    public record Regla(
            @NotBlank String lineaNegocio,
            @NotBlank String origen,
            @NotBlank String destino,
            @NotBlank String tipoServicio,
            @NotNull @PositiveOrZero BigDecimal pesoMinKg,
            @NotNull @Positive BigDecimal pesoMaxKg,
            @NotNull @PositiveOrZero BigDecimal tarifaBase,
            @NotNull @PositiveOrZero BigDecimal recargos,
            @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal tasaIgv,
            @NotBlank @Pattern(regexp = "[A-Z]{3}") String moneda
    ) {
        @AssertTrue(message = "peso-max-kg debe ser mayor que peso-min-kg")
        public boolean isRangoPesoValido() {
            return pesoMinKg != null && pesoMaxKg != null && pesoMaxKg.compareTo(pesoMinKg) > 0;
        }
    }
}
