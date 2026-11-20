package com.servicios.soa.clientes.notificaciones.service;

import com.servicios.soa.clientes.model.Cliente;
import com.servicios.soa.clientes.notificaciones.api.NotificacionRequest;
import com.servicios.soa.clientes.notificaciones.api.NotificacionResponse;
import com.servicios.soa.clientes.notificaciones.model.Notificacion;
import com.servicios.soa.clientes.notificaciones.repository.NotificacionRepository;
import com.servicios.soa.clientes.repository.ClienteRepository;
import com.servicios.soa.clientes.service.ClienteNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class NotificacionService {
    private final ClienteRepository clienteRepository;
    private final NotificacionRepository notificacionRepository;
    private final EnvioValidator envioValidator;
    private final NotificacionDeliverySender deliverySender;

    public NotificacionService(ClienteRepository clienteRepository,
                               NotificacionRepository notificacionRepository,
                               EnvioValidator envioValidator,
                               NotificacionDeliverySender deliverySender) {
        this.clienteRepository = clienteRepository;
        this.notificacionRepository = notificacionRepository;
        this.envioValidator = envioValidator;
        this.deliverySender = deliverySender;
    }

    @Transactional
    public NotificacionResponse enviar(NotificacionRequest request) {
        Cliente cliente = clienteRepository.findById(request.cliente_id())
                .orElseThrow(ClienteNotFoundException::new);
        validarContacto(cliente, request.canal());
        if (request.envio_id() != null) {
            envioValidator.validar(request.envio_id());
        }

        String mensaje = crearMensaje(request);
        if (mensaje.length() > 255) {
            throw new SolicitudNotificacionInvalidaException();
        }

        Instant fechaEnvio = Instant.now();
        Notificacion notificacion = new Notificacion(
                UUID.randomUUID(),
                request.cliente_id(),
                request.envio_id(),
                request.tipo(),
                request.canal(),
                mensaje,
                request.campos_invalidos() == null ? null : String.join(", ", request.campos_invalidos()),
                request.motivo(),
                fechaEnvio
        );
        Notificacion guardada = notificacionRepository.saveAndFlush(notificacion);
        return new NotificacionResponse(guardada.getNotificacionId(), guardada.getEstado(), fechaEnvio);
    }

    @Transactional(readOnly = true)
    public NotificacionResponse consultar(UUID notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(NotificacionNoEncontradaException::new);
        return new NotificacionResponse(
                notificacion.getNotificacionId(),
                notificacion.getEstado(),
                notificacion.getFechaEnvio()
        );
    }

    public boolean proveedorConfigurado() {
        return deliverySender.disponible();
    }

    private void validarContacto(Cliente cliente, String canal) {
        String contacto = switch (canal) {
            case "correo" -> cliente.getCorreo();
            case "sms", "mensajeria" -> cliente.getTelefono();
            default -> null;
        };
        if (contacto == null || contacto.isBlank()) {
            throw new CanalContactoNoDisponibleException(canal);
        }
    }

    private String crearMensaje(NotificacionRequest request) {
        if (request.mensaje() != null && !request.mensaje().isBlank()) {
            return request.mensaje();
        }
        return switch (request.tipo()) {
            case "correccion_datos" -> "Corrige estos campos: "
                    + String.join(", ", request.campos_invalidos())
                    + ". Actualiza los datos y vuelve a enviar.";
            case "restriccion_contenido" -> "Envío restringido: " + request.motivo() + ".";
            case "problema_pago" -> "Problema de pago: " + request.motivo()
                    + ". Corrige el pago e inténtalo nuevamente.";
            default -> throw new IllegalStateException("Tipo de notificación no soportado");
        };
    }
}
