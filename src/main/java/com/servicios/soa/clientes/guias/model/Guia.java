package com.servicios.soa.clientes.guias.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "guias", uniqueConstraints = {
        @UniqueConstraint(name = "uk_guia_envio", columnNames = "id_envio"),
        @UniqueConstraint(name = "uk_guia_codigo", columnNames = "codigo_guia")
})
public class Guia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_guia", nullable = false, updatable = false)
    private UUID idGuia;

    @Column(name = "id_envio", nullable = false, updatable = false)
    private UUID idEnvio;

    @Column(name = "id_cobro", nullable = false, updatable = false)
    private UUID idCobro;

    @Column(name = "codigo_guia", nullable = false, length = 18, updatable = false)
    private String codigoGuia;

    @Column(name = "url_seguimiento", nullable = false, length = 500, updatable = false)
    private String urlSeguimiento;

    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private Instant fechaEmision;

    protected Guia() {
    }

    public Guia(UUID idEnvio, UUID idCobro, String codigoGuia, String urlSeguimiento, Instant fechaEmision) {
        this.idEnvio = idEnvio;
        this.idCobro = idCobro;
        this.codigoGuia = codigoGuia;
        this.urlSeguimiento = urlSeguimiento;
        this.fechaEmision = fechaEmision;
    }

    public UUID idEnvio() { return idEnvio; }
    public UUID idCobro() { return idCobro; }
    public String codigoGuia() { return codigoGuia; }
    public String urlSeguimiento() { return urlSeguimiento; }
    public Instant fechaEmision() { return fechaEmision; }
}
