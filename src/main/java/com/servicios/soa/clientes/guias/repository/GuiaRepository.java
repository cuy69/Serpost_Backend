package com.servicios.soa.clientes.guias.repository;

import com.servicios.soa.clientes.guias.model.Guia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GuiaRepository extends JpaRepository<Guia, UUID> {
    boolean existsByIdEnvio(UUID idEnvio);
    Optional<Guia> findByCodigoGuia(String codigoGuia);
}
