package com.servicios.soa.clientes.cobros.api;

import java.time.Instant;
import java.util.UUID;

public record CobroResponse(UUID id_cobro, String estado_pago, Instant fecha_hora) {
}
