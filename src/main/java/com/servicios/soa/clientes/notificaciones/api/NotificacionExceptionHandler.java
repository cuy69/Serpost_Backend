package com.servicios.soa.clientes.notificaciones.api;

import com.servicios.soa.clientes.api.dto.ApiError;
import com.servicios.soa.clientes.notificaciones.service.CanalContactoNoDisponibleException;
import com.servicios.soa.clientes.notificaciones.service.EnvioNoEncontradoException;
import com.servicios.soa.clientes.notificaciones.service.NotificacionNoEncontradaException;
import com.servicios.soa.clientes.notificaciones.service.SolicitudNotificacionInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class NotificacionExceptionHandler {

    @ExceptionHandler({NotificacionNoEncontradaException.class, EnvioNoEncontradoException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CanalContactoNoDisponibleException.class)
    public ResponseEntity<ApiError> handleContactUnavailable(CanalContactoNoDisponibleException exception) {
        return response(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage());
    }

    @ExceptionHandler(SolicitudNotificacionInvalidaException.class)
    public ResponseEntity<ApiError> handleUnavailable(RuntimeException exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, Map.of());
        return ResponseEntity.status(status).body(body);
    }
}
