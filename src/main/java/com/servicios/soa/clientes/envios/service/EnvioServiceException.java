package com.servicios.soa.clientes.envios.service;

import org.springframework.http.HttpStatus;

public class EnvioServiceException extends RuntimeException {
    private final HttpStatus status;

    public EnvioServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
