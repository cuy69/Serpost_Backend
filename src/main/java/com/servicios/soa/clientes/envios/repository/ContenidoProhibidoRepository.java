package com.servicios.soa.clientes.envios.repository;

import com.servicios.soa.clientes.envios.model.ContenidoProhibido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenidoProhibidoRepository extends JpaRepository<ContenidoProhibido, Long> {
    List<ContenidoProhibido> findAllByActivoTrue();
}
