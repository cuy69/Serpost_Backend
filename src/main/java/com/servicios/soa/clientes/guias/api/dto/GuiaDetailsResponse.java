package com.servicios.soa.clientes.guias.api.dto;

import java.time.Instant;
import java.util.UUID;

public record GuiaDetailsResponse(String codigo_guia, UUID id_envio, UUID id_cobro,
                                  String url_seguimiento, Instant fecha_emision) {
}
