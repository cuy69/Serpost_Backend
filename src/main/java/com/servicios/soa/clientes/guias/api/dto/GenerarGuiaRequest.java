package com.servicios.soa.clientes.guias.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GenerarGuiaRequest(@NotNull UUID id_envio, @NotNull UUID id_cobro) {
}
