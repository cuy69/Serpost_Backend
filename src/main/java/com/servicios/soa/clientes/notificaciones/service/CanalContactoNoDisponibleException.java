package com.servicios.soa.clientes.notificaciones.service;

public class CanalContactoNoDisponibleException extends RuntimeException {
    public CanalContactoNoDisponibleException(String canal) {
        super("El cliente no tiene datos de contacto para el canal " + canal);
    }
}
