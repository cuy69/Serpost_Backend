package com.servicios.soa.clientes.notificaciones.api;

import com.servicios.soa.clientes.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NotificacionResponse> enviar(@Valid @RequestBody NotificacionRequest request) {
        NotificacionResponse response = service.enviar(request);
        ResponseEntity.BodyBuilder respuesta = service.proveedorConfigurado()
                ? ResponseEntity.accepted()
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE);
        return respuesta
                .location(URI.create("/notificaciones/" + response.notificacion_id()))
                .body(response);
    }

    @GetMapping("/{notificacion_id}")
    public NotificacionResponse consultar(@PathVariable("notificacion_id") UUID notificacionId) {
        return service.consultar(notificacionId);
    }
}
