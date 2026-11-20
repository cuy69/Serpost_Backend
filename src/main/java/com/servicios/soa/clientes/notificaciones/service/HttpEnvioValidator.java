package com.servicios.soa.clientes.notificaciones.service;

import com.servicios.soa.clientes.config.SerpostProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Component
public class HttpEnvioValidator implements EnvioValidator {
    private static final Duration TIMEOUT = Duration.ofMillis(700);

    private final String baseUrl;
    private final HttpClient httpClient;

    public HttpEnvioValidator(SerpostProperties properties) {
        String baseUrl = properties.envios().baseUrl();
        this.baseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;
        this.httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @Override
    public void validar(UUID envioId) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(baseUrl + "/envios/" + envioId))
                .timeout(TIMEOUT)
                .GET()
                .build();
        try {
            int status = httpClient.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            if (status == 404) {
                throw new EnvioNoEncontradoException();
            }
            if (status < 200 || status >= 300) {
                throw new ServicioEnvioNoDisponibleException();
            }
        } catch (IOException exception) {
            throw new ServicioEnvioNoDisponibleException();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ServicioEnvioNoDisponibleException();
        }
    }
}
