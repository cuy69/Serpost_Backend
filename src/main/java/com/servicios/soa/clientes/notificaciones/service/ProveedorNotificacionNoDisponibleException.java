package com.servicios.soa.clientes.notificaciones.service;

public class ProveedorNotificacionNoDisponibleException extends RuntimeException {
    public ProveedorNotificacionNoDisponibleException() {
        super("Proveedor de notificaciones no configurado");
    }
}
