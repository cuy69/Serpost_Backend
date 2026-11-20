package com.servicios.soa.clientes.notificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notificacion_intentos")
public class NotificacionIntento {
    @Id
    @Column(name = "intento_id", nullable = false, updatable = false)
    private UUID intentoId;

    @Column(name = "notificacion_id", nullable = false, updatable = false)
    private UUID notificacionId;

    @Column(name = "numero_intento", nullable = false, updatable = false)
    private int numeroIntento;

    @Column(name = "fecha_intento", nullable = false, updatable = false)
    private Instant fechaIntento;

    @Column(nullable = false, length = 30, updatable = false)
    private String resultado;

    @Column(length = 200, updatable = false)
    private String detalle;

    protected NotificacionIntento() {
    }

    public NotificacionIntento(UUID notificacionId, int numeroIntento, Instant fechaIntento,
                               String resultado, String detalle) {
        this.intentoId = UUID.randomUUID();
        this.notificacionId = notificacionId;
        this.numeroIntento = numeroIntento;
        this.fechaIntento = fechaIntento;
        this.resultado = resultado;
        this.detalle = detalle;
    }
}
