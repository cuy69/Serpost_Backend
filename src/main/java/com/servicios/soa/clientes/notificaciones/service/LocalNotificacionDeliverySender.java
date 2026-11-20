package com.servicios.soa.clientes.notificaciones.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalNotificacionDeliverySender implements NotificacionDeliverySender {
    @Override
    public boolean disponible() {
        return true;
    }

    @Override
    public void enviar(String canal, String destino, String mensaje) {
    }
}
