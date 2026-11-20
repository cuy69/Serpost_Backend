package com.servicios.soa.clientes.guias.service;

import org.springframework.http.HttpStatus;

public class GuiaServiceException extends RuntimeException {
    private final HttpStatus status;

    public GuiaServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
