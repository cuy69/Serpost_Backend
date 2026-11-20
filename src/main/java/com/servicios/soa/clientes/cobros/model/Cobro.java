package com.servicios.soa.clientes.cobros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cobros", uniqueConstraints = @UniqueConstraint(
        name = "uk_cobro_envio_referencia", columnNames = {"id_envio", "referencia_operacion"}))
public class Cobro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cobro", nullable = false, updatable = false)
    private UUID idCobro;

    @Column(name = "id_envio", nullable = false, updatable = false)
    private UUID idEnvio;

    @Column(nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal monto;

    @Column(nullable = false, length = 3, updatable = false)
    private String moneda;

    @Column(name = "medio_pago", nullable = false, length = 20, updatable = false)
    private String medioPago;

    @Column(name = "referencia_operacion", length = 100, updatable = false)
    private String referenciaOperacion;

    @Column(name = "estado_pago", nullable = false, length = 20, updatable = false)
    private String estadoPago;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private Instant fechaHora;

    protected Cobro() {
    }

    public Cobro(UUID idEnvio, BigDecimal monto, String moneda, String medioPago,
                 String referenciaOperacion, String estadoPago, Instant fechaHora) {
        this.idEnvio = idEnvio;
        this.monto = monto;
        this.moneda = moneda;
        this.medioPago = medioPago;
        this.referenciaOperacion = referenciaOperacion;
        this.estadoPago = estadoPago;
        this.fechaHora = fechaHora;
    }

    public UUID getIdCobro() { return idCobro; }
    public UUID getIdEnvio() { return idEnvio; }
    public BigDecimal getMonto() { return monto; }
    public String getMoneda() { return moneda; }
    public String getMedioPago() { return medioPago; }
    public String getReferenciaOperacion() { return referenciaOperacion; }
    public String getEstadoPago() { return estadoPago; }
    public Instant getFechaHora() { return fechaHora; }
}
