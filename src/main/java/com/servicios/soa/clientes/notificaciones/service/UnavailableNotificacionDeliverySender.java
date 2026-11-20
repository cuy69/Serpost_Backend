package com.servicios.soa.clientes.notificaciones.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public class UnavailableNotificacionDeliverySender implements NotificacionDeliverySender {
    @Override
    public boolean disponible() {
        return false;
    }

    @Override
    public void enviar(String canal, String destino, String mensaje) {
        throw new ProveedorNotificacionNoDisponibleException();
    }
}
