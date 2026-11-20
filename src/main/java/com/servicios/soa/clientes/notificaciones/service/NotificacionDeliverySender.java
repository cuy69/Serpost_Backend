package com.servicios.soa.clientes.notificaciones.service;

public interface NotificacionDeliverySender {
    boolean disponible();

    void enviar(String canal, String destino, String mensaje);
}
