package com.servicios.soa.clientes.envios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contenidos_prohibidos")
public class ContenidoProhibido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String categoria;
    @Column(name = "palabra_clave", nullable = false, length = 150)
    private String palabraClave;
    @Column(nullable = false)
    private boolean activo;

    protected ContenidoProhibido() { }
    public String getCategoria() { return categoria; }
    public String getPalabraClave() { return palabraClave; }
}
