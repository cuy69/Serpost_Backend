package com.servicios.soa.clientes.cobros.repository;

import com.servicios.soa.clientes.cobros.model.Cobro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CobroRepository extends JpaRepository<Cobro, UUID> {
    boolean existsByIdEnvioAndReferenciaOperacion(UUID idEnvio, String referenciaOperacion);
}
