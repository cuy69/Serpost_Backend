package com.servicios.soa.clientes.tarifas.api;

import com.servicios.soa.clientes.api.dto.ApiError;
import com.servicios.soa.clientes.tarifas.service.TarifaNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class TarifaExceptionHandler {

    @ExceptionHandler(TarifaNoEncontradaException.class)
    public ResponseEntity<ApiError> handleTarifaNoEncontrada(TarifaNoEncontradaException exception) {
        ApiError body = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                Map.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
