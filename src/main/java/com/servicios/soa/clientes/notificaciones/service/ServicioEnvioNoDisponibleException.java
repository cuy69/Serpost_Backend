package com.servicios.soa.clientes.notificaciones.service;

public class ServicioEnvioNoDisponibleException extends RuntimeException {
    public ServicioEnvioNoDisponibleException() {
        super("El servicio de envíos no está disponible");
    }
}
