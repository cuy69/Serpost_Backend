package com.servicios.soa.clientes.notificaciones.repository;

import com.servicios.soa.clientes.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {
    List<Notificacion> findTop20ByEstadoOrderByFechaEnvioAsc(String estado);
}
