package com.servicios.soa.clientes.guias.service;

public class GuiaNotFoundException extends RuntimeException {
    public GuiaNotFoundException() {
        super("No se encontró la guía solicitada.");
    }
}
