package com.servicios.soa.clientes.notificaciones.service;

import com.servicios.soa.clientes.envios.repository.EnvioRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DatabaseEnvioValidator implements EnvioValidator {
    private final EnvioRepository envios;

    public DatabaseEnvioValidator(EnvioRepository envios) {
        this.envios = envios;
    }

    @Override
    public void validar(UUID envioId) {
        if (!envios.existsById(envioId)) {
            throw new EnvioNoEncontradoException();
        }
    }
}
