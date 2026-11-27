package com.servicios.soa.clientes.envios.service;

import com.servicios.soa.clientes.envios.api.EnvioRequest;
import com.servicios.soa.clientes.envios.api.EnvioResponse;
import com.servicios.soa.clientes.envios.model.ContenidoProhibido;
import com.servicios.soa.clientes.envios.model.Envio;
import com.servicios.soa.clientes.envios.repository.ContenidoProhibidoRepository;
import com.servicios.soa.clientes.envios.repository.EnvioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Service
public class EnvioService {
    private final EnvioRepository envios;
    private final ContenidoProhibidoRepository reglas;

    public EnvioService(EnvioRepository envios, ContenidoProhibidoRepository reglas) {
        this.envios = envios;
        this.reglas = reglas;
    }

    public EnvioResponse registrar(EnvioRequest request) {
        String contenidoNormalizado = normalizar(request.contenido());
        for (ContenidoProhibido regla : reglas.findAllByActivoTrue()) {
            if (contenidoNormalizado.contains(normalizar(regla.getPalabraClave()))) {
                throw new EnvioServiceException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "Contenido prohibido: " + regla.getCategoria() + " (" + regla.getPalabraClave() + ").");
            }
        }
        if (envios.existsByIdRemitenteAndIdDestinatarioAndPesoKgAndContenidoAndLineaNegocioAndOrigenAndDestino(
                request.idRemitente(), request.idDestinatario(), request.pesoKg(), request.contenido(),
                request.lineaNegocio(), request.origen(), request.destino())) {
            throw new EnvioServiceException(HttpStatus.CONFLICT, "Ya existe un envío con los mismos datos.");
        }
        try {
            Envio envio = envios.saveAndFlush(new Envio(request.idRemitente(), request.idDestinatario(), request.pesoKg(),
                    request.contenido(), request.lineaNegocio(), request.origen(), request.destino()));
            return EnvioResponse.from(envio);
        } catch (DataIntegrityViolationException exception) {
            throw new EnvioServiceException(HttpStatus.CONFLICT, "Ya existe un envío con los mismos datos.");
        }
    }

    public java.util.List<EnvioResponse> listar() {
        return envios.findAll().stream().map(EnvioResponse::from).toList();
    }

    public EnvioResponse consultar(UUID id) {
        return envios.findById(id).map(EnvioResponse::from)
                .orElseThrow(() -> new EnvioServiceException(HttpStatus.NOT_FOUND, "Envío no encontrado."));
    }

    private String normalizar(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
    }
}
