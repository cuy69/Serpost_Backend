package com.servicios.soa.clientes.notificaciones.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @Column(name = "notificacion_id", nullable = false, updatable = false)
    private UUID notificacionId;

    @Column(name = "cliente_id", nullable = false, updatable = false)
    private UUID clienteId;

    @Column(name = "envio_id", updatable = false)
    private UUID envioId;

    @Column(nullable = false, length = 40, updatable = false)
    private String tipo;

    @Column(nullable = false, length = 30, updatable = false)
    private String canal;

    @Column(nullable = false, length = 255, updatable = false)
    private String mensaje;

    @Column(name = "campos_invalidos", length = 1638, updatable = false)
    private String camposInvalidos;

    @Column(length = 200, updatable = false)
    private String motivo;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private Instant fechaEnvio;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "numero_intentos", nullable = false)
    private int numeroIntentos;

    protected Notificacion() {
    }

    public Notificacion(UUID notificacionId, UUID clienteId, UUID envioId, String tipo,
                        String canal, String mensaje, String camposInvalidos, String motivo,
                        Instant fechaEnvio) {
        this.notificacionId = notificacionId;
        this.clienteId = clienteId;
        this.envioId = envioId;
        this.tipo = tipo;
        this.canal = canal;
        this.mensaje = mensaje;
        this.camposInvalidos = camposInvalidos;
        this.motivo = motivo;
        this.fechaEnvio = fechaEnvio;
        this.estado = "pendiente";
    }

    public UUID getNotificacionId() {
        return notificacionId;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public String getCanal() {
        return canal;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public Instant getFechaEnvio() {
        return fechaEnvio;
    }

    public int getNumeroIntentos() {
        return numeroIntentos;
    }

    public void registrarIntento(String estado) {
        numeroIntentos++;
        this.estado = estado;
    }
}
