package com.servicios.soa.clientes.notificaciones.service;

import com.servicios.soa.clientes.notificaciones.repository.NotificacionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacionPendingProcessor {
    private final NotificacionRepository notificacionRepository;
    private final NotificacionDeliveryService deliveryService;

    public NotificacionPendingProcessor(NotificacionRepository notificacionRepository,
                                        NotificacionDeliveryService deliveryService) {
        this.notificacionRepository = notificacionRepository;
        this.deliveryService = deliveryService;
    }

    @Scheduled(fixedDelayString = "${serpost.notificaciones.intervalo-procesamiento-ms:250}")
    public void procesarPendientes() {
        notificacionRepository.findTop20ByEstadoOrderByFechaEnvioAsc("pendiente")
                .forEach(notificacion -> deliveryService.procesar(notificacion.getNotificacionId()));
    }
}
