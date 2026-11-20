package com.servicios.soa.clientes.cobros.service;

import com.servicios.soa.clientes.cobros.api.CobroRequest;
import com.servicios.soa.clientes.cobros.api.CobroResponse;
import com.servicios.soa.clientes.cobros.model.Cobro;
import com.servicios.soa.clientes.cobros.repository.CobroRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class CobroService {
    private static final String REFERENCIA_RECHAZADA_PARA_PRUEBAS = "RECHAZADO";

    private final CobroRepository repository;

    public CobroService(CobroRepository repository) {
        this.repository = repository;
    }

    public CobroResponse registrar(CobroRequest request) {
        if (repository.existsByIdEnvioAndReferenciaOperacion(
                request.id_envio(), request.referencia_operacion())) {
            throw new CobroException(HttpStatus.CONFLICT,
                    "Ya existe un cobro con el mismo envío y referencia de operación.");
        }

        String estadoPago = REFERENCIA_RECHAZADA_PARA_PRUEBAS.equalsIgnoreCase(request.referencia_operacion())
                ? "rechazado"
                : "aprobado";
        Cobro cobro = new Cobro(request.id_envio(), request.monto(), request.moneda(), request.medio_pago(),
                request.referencia_operacion(), estadoPago, Instant.now());

        try {
            Cobro guardado = repository.saveAndFlush(cobro);
            if ("rechazado".equals(guardado.getEstadoPago())) {
                throw new CobroException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "El pago fue rechazado por la pasarela o el banco.");
            }
            return response(guardado);
        } catch (DataIntegrityViolationException exception) {
            throw new CobroException(HttpStatus.CONFLICT,
                    "Ya existe un cobro con el mismo envío y referencia de operación.");
        }
    }

    public CobroResponse consultar(UUID idCobro) {
        Cobro cobro = repository.findById(idCobro)
                .orElseThrow(() -> new CobroException(HttpStatus.NOT_FOUND,
                        "No se encontró el cobro solicitado."));
        return response(cobro);
    }

    private CobroResponse response(Cobro cobro) {
        return new CobroResponse(cobro.getIdCobro(), cobro.getEstadoPago(), cobro.getFechaHora());
    }
}
