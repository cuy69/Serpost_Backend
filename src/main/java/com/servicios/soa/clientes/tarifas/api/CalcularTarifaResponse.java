package com.servicios.soa.clientes.tarifas.api;

import java.math.BigDecimal;

public record CalcularTarifaResponse(
        BigDecimal tarifa_base,
        BigDecimal recargos,
        BigDecimal igv,
        BigDecimal tarifa_total,
        String moneda
) {
}
