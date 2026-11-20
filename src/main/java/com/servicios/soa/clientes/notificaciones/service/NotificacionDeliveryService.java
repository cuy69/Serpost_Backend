package com.servicios.soa.clientes.notificaciones.service;

import com.servicios.soa.clientes.config.SerpostProperties;
import com.servicios.soa.clientes.model.Cliente;
import com.servicios.soa.clientes.notificaciones.model.Notificacion;
import com.servicios.soa.clientes.notificaciones.model.NotificacionIntento;
import com.servicios.soa.clientes.notificaciones.repository.NotificacionIntentoRepository;
import com.servicios.soa.clientes.notificaciones.repository.NotificacionRepository;
import com.servicios.soa.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class NotificacionDeliveryService {
    private final NotificacionRepository notificacionRepository;
    private final NotificacionIntentoRepository intentoRepository;
    private final ClienteRepository clienteRepository;
    private final NotificacionDeliverySender sender;
    private final int maximoIntentos;

    public NotificacionDeliveryService(NotificacionRepository notificacionRepository,
                                       NotificacionIntentoRepository intentoRepository,
                                       ClienteRepository clienteRepository,
                                       NotificacionDeliverySender sender,
                                       SerpostProperties properties) {
        int maximoIntentos = properties.notificaciones().reintentos().maximo();
        if (maximoIntentos < 1) {
            throw new IllegalArgumentException("serpost.notificaciones.reintentos.maximo debe ser mayor que cero");
        }
        this.notificacionRepository = notificacionRepository;
        this.intentoRepository = intentoRepository;
        this.clienteRepository = clienteRepository;
        this.sender = sender;
        this.maximoIntentos = maximoIntentos;
    }

    @Transactional
    public void procesar(UUID notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId).orElse(null);
        if (notificacion == null || !"pendiente".equals(notificacion.getEstado())) {
            return;
        }

        int numeroIntento = notificacion.getNumeroIntentos() + 1;
        String estado;
        String detalle;
        try {
            Cliente cliente = clienteRepository.findById(notificacion.getClienteId())
                    .orElseThrow(ProveedorNotificacionNoDisponibleException::new);
            String destino = switch (notificacion.getCanal()) {
                case "correo" -> cliente.getCorreo();
                case "sms", "mensajeria" -> cliente.getTelefono();
                default -> throw new IllegalStateException("Canal de notificación no soportado");
            };
            if (destino == null || destino.isBlank()) {
                throw new ProveedorNotificacionNoDisponibleException();
            }
            sender.enviar(notificacion.getCanal(), destino, notificacion.getMensaje());
            estado = "enviada";
            detalle = "Entrega aceptada por el adaptador de canal";
        } catch (ProveedorNotificacionNoDisponibleException exception) {
            estado = numeroIntento >= maximoIntentos ? "fallida" : "pendiente";
            detalle = "Proveedor de canal no disponible";
        }

        notificacion.registrarIntento(estado);
        intentoRepository.save(new NotificacionIntento(
                notificacionId,
                numeroIntento,
                Instant.now(),
                estado,
                detalle
        ));
    }
}
