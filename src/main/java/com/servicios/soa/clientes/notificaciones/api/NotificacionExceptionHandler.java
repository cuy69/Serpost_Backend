package com.servicios.soa.clientes.notificaciones.api;

import com.servicios.soa.clientes.api.dto.ApiError;
import com.servicios.soa.clientes.notificaciones.service.CanalContactoNoDisponibleException;
import com.servicios.soa.clientes.notificaciones.service.EnvioNoEncontradoException;
import com.servicios.soa.clientes.notificaciones.service.NotificacionNoEncontradaException;
import com.servicios.soa.clientes.notificaciones.service.ServicioEnvioNoDisponibleException;
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

    @ExceptionHandler({ServicioEnvioNoDisponibleException.class, SolicitudNotificacionInvalidaException.class})
    public ResponseEntity<ApiError> handleUnavailable(RuntimeException exception) {
        HttpStatus status = exception instanceof SolicitudNotificacionInvalidaException
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.SERVICE_UNAVAILABLE;
        return response(status, exception.getMessage());
    }

    private ResponseEntity<ApiError> response(HttpStatus status, String message) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, Map.of());
        return ResponseEntity.status(status).body(body);
    }
}
