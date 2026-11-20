package com.servicios.soa.clientes.notificaciones.api;

import java.time.Instant;
import java.util.UUID;

public record NotificacionResponse(UUID notificacion_id, String estado, Instant fecha_envio) {
}
