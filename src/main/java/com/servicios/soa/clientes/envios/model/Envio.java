package com.servicios.soa.clientes.envios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "envios", uniqueConstraints = @UniqueConstraint(name = "uk_envio_datos",
        columnNames = {"id_remitente", "id_destinatario", "peso_kg", "contenido", "linea_negocio", "origen", "destino"}))
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_envio", nullable = false, updatable = false)
    private UUID idEnvio;
    @Column(name = "id_remitente", length = 50)
    private String idRemitente;
    @Column(name = "id_destinatario", length = 50)
    private String idDestinatario;
    @Column(name = "peso_kg", precision = 10, scale = 3)
    private BigDecimal pesoKg;
    @Column(length = 500)
    private String contenido;
    @Column(name = "linea_negocio", length = 80)
    private String lineaNegocio;
    @Column(length = 150)
    private String origen;
    @Column(length = 150)
    private String destino;
    @Column(nullable = false, length = 30)
    private String estado = "REGISTRADO";
    @Column(length = 30)
    private String servicio;
    @Column(length = 30)
    private String zona;
    @Column(name = "ida_vuelta")
    private Boolean idaVuelta;
    @Column(name = "tarifa_total", precision = 12, scale = 2)
    private BigDecimal tarifaTotal;
    @Column(name = "simulacion_log", length = 1000)
    private String simulacionLog;

    protected Envio() { }

    public Envio(String idRemitente, String idDestinatario, BigDecimal pesoKg, String contenido,
                 String lineaNegocio, String origen, String destino) {
        this.idRemitente = idRemitente;
        this.idDestinatario = idDestinatario;
        this.pesoKg = pesoKg;
        this.contenido = contenido;
        this.lineaNegocio = lineaNegocio;
        this.origen = origen;
        this.destino = destino;
    }

    public UUID getIdEnvio() { return idEnvio; }
    public String getIdRemitente() { return idRemitente; }
    public String getIdDestinatario() { return idDestinatario; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public String getContenido() { return contenido; }
    public String getLineaNegocio() { return lineaNegocio; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getEstado() { return estado; }
    public String getServicio() { return servicio; }
    public String getZona() { return zona; }
    public Boolean getIdaVuelta() { return idaVuelta; }
    public BigDecimal getTarifaTotal() { return tarifaTotal; }
}
