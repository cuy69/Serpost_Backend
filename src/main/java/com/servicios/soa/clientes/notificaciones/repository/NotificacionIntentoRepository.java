package com.servicios.soa.clientes.notificaciones.repository;

import com.servicios.soa.clientes.notificaciones.model.NotificacionIntento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionIntentoRepository extends JpaRepository<NotificacionIntento, UUID> {
    List<NotificacionIntento> findByNotificacionIdOrderByNumeroIntentoAsc(UUID notificacionId);
}
