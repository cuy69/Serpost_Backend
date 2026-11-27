package com.servicios.soa.clientes.guias.service;

import com.servicios.soa.clientes.guias.api.dto.GenerarGuiaRequest;
import com.servicios.soa.clientes.guias.api.dto.GuiaDetailsResponse;
import com.servicios.soa.clientes.guias.api.dto.GuiaResponse;
import com.servicios.soa.clientes.cobros.repository.CobroRepository;
import com.servicios.soa.clientes.envios.repository.EnvioRepository;
import com.servicios.soa.clientes.guias.model.Guia;
import com.servicios.soa.clientes.guias.repository.GuiaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class GuiaService {
    private final GuiaRepository repository;
    private final EnvioRepository envios;
    private final CobroRepository cobros;
    private final String trackingBaseUrl;

    public GuiaService(GuiaRepository repository, EnvioRepository envios, CobroRepository cobros,
                       @Value("${services.guias.tracking-base-url:http://localhost:8080/guias}") String trackingBaseUrl) {
        this.repository = repository;
        this.envios = envios;
        this.cobros = cobros;
        this.trackingBaseUrl = trackingBaseUrl.replaceAll("/+$", "");
    }

    public GuiaResponse generar(GenerarGuiaRequest request) {
        if (repository.existsByIdEnvio(request.id_envio())) {
            throw new GuiaServiceException(HttpStatus.CONFLICT,
                    "Ya existe una guía para este envío.");
        }
        if (!envios.existsById(request.id_envio())) {
            throw new GuiaServiceException(HttpStatus.NOT_FOUND, "No se encontró el envío indicado.");
        }
        String estadoPago = cobros.findById(request.id_cobro())
                .orElseThrow(() -> new GuiaServiceException(HttpStatus.NOT_FOUND,
                        "No se encontró el cobro indicado."))
                .getEstadoPago();
        if (!"aprobado".equalsIgnoreCase(estadoPago)) {
            throw new GuiaServiceException(HttpStatus.UNPROCESSABLE_CONTENT,
                    "No se puede emitir la guía porque el pago no está aprobado.");
        }

        String codigo = "SP" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 16).toUpperCase(Locale.ROOT);
        Instant fechaEmision = Instant.now();
        Guia guia = new Guia(request.id_envio(), request.id_cobro(), codigo,
                trackingBaseUrl + "/" + codigo, fechaEmision);
        try {
            Guia guardada = repository.save(guia);
            return new GuiaResponse(guardada.codigoGuia(), guardada.urlSeguimiento(),
                    guardada.fechaEmision());
        } catch (DataIntegrityViolationException exception) {
            throw new GuiaServiceException(HttpStatus.CONFLICT,
                    "Ya existe una guía para este envío.");
        }
    }

    public GuiaDetailsResponse consultar(String codigoGuia) {
        Guia guia = repository.findByCodigoGuia(codigoGuia)
                .orElseThrow(GuiaNotFoundException::new);
        return new GuiaDetailsResponse(guia.codigoGuia(), guia.idEnvio(), guia.idCobro(),
                guia.urlSeguimiento(), guia.fechaEmision());
    }
}
