package com.servicios.soa.clientes.notificaciones.service;

public class EnvioNoEncontradoException extends RuntimeException {
    public EnvioNoEncontradoException() {
        super("No se encontró el envío relacionado");
    }
}
