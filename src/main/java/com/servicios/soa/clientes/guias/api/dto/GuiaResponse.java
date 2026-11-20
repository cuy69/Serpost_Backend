package com.servicios.soa.clientes.guias.api.dto;

import java.time.Instant;

public record GuiaResponse(String codigo_guia, String url_seguimiento, Instant fecha_emision) {
}
