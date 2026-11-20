package com.servicios.soa.clientes.cobros.service;

import org.springframework.http.HttpStatus;

public class CobroException extends RuntimeException {
    private final HttpStatus status;

    public CobroException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
