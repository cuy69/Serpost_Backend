package com.servicios.soa.clientes.envios.repository;

import com.servicios.soa.clientes.envios.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.UUID;

public interface EnvioRepository extends JpaRepository<Envio, UUID> {
    boolean existsByIdRemitenteAndIdDestinatarioAndPesoKgAndContenidoAndLineaNegocioAndOrigenAndDestino(
            String idRemitente, String idDestinatario, BigDecimal pesoKg, String contenido,
            String lineaNegocio, String origen, String destino);
}
