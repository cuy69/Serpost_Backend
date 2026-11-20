package com.servicios.soa.clientes.cobros.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public record CobroRequest(
        @NotNull UUID id_envio,
        @NotNull @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 10, fraction = 2) BigDecimal monto,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "debe ser un código de moneda de 3 letras") String moneda,
        @NotBlank @Pattern(regexp = "efectivo|tarjeta|transferencia",
                message = "debe ser efectivo, tarjeta o transferencia") String medio_pago,
        @Size(max = 100) String referencia_operacion
) {
    public CobroRequest {
        if (moneda != null) {
            moneda = moneda.trim().toUpperCase(Locale.ROOT);
        }
        if (medio_pago != null) {
            medio_pago = medio_pago.trim().toLowerCase(Locale.ROOT);
        }
        if (referencia_operacion != null) {
            referencia_operacion = referencia_operacion.trim();
            if (referencia_operacion.isEmpty()) {
                referencia_operacion = null;
            }
        }
    }

    @AssertTrue(message = "referencia_operacion es obligatoria para tarjeta y transferencia")
    public boolean isReferenciaOperacionValida() {
        return medio_pago == null
                || (!medio_pago.equals("tarjeta") && !medio_pago.equals("transferencia"))
                || (referencia_operacion != null && !referencia_operacion.isBlank());
    }
}
