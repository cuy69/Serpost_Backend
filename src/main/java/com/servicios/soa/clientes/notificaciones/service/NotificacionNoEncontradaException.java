package com.servicios.soa.clientes.notificaciones.service;

public class NotificacionNoEncontradaException extends RuntimeException {
    public NotificacionNoEncontradaException() {
        super("No se encontró la notificación solicitada");
    }
}
