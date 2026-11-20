package com.servicios.soa.clientes.notificaciones.service;

public class SolicitudNotificacionInvalidaException extends RuntimeException {
    public SolicitudNotificacionInvalidaException() {
        super("El mensaje generado supera el máximo de 255 caracteres");
    }
}
