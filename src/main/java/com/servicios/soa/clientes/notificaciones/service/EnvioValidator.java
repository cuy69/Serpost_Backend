package com.servicios.soa.clientes.notificaciones.service;

import java.util.UUID;

public interface EnvioValidator {
    void validar(UUID envioId);
}
