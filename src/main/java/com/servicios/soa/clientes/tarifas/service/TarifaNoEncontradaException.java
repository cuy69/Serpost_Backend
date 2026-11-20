package com.servicios.soa.clientes.tarifas.service;

public class TarifaNoEncontradaException extends RuntimeException {
    public TarifaNoEncontradaException() {
        super("No existe una tarifa definida para los datos solicitados");
    }
}
