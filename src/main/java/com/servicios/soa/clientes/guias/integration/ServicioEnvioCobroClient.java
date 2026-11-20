package com.servicios.soa.clientes.guias.integration;

import com.servicios.soa.clientes.guias.service.GuiaServiceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

@Component
public class ServicioEnvioCobroClient {
    private final RestClient envios;
    private final RestClient cobros;

    public ServicioEnvioCobroClient(
            @Value("${services.envios.base-url:http://localhost:3001}") String enviosBaseUrl,
            @Value("${services.cobros.base-url:http://127.0.0.1:8080}") String cobrosBaseUrl) {
        this.envios = crearCliente(enviosBaseUrl);
        this.cobros = crearCliente(cobrosBaseUrl);
    }

    private RestClient crearCliente(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory())
                .requestInterceptor(this::forwardBearerToken)
                .build();
    }

    public void verificarEnvioExiste(UUID idEnvio) {
        ejecutarConsulta("Envíos", () -> envios.get().uri("/envios/{id}", idEnvio).retrieve().toBodilessEntity());
    }

    public String consultarEstadoCobro(UUID idCobro) {
        CobroResponse response = ejecutarConsulta("Cobros", () -> cobros.get()
                .uri("/cobros/{id}", idCobro)
                .retrieve()
                .body(CobroResponse.class));
        if (response == null || response.estado_pago() == null) {
            throw new GuiaServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de Cobro devolvió una respuesta incompleta.");
        }
        return response.estado_pago();
    }

    private <T> T ejecutarConsulta(String servicio, Consulta<T> consulta) {
        try {
            return consulta.ejecutar();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new GuiaServiceException(HttpStatus.NOT_FOUND,
                        "No se encontró el envío o el cobro indicado.");
            }
            throw new GuiaServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de " + servicio + " respondió con un error.");
        } catch (ResourceAccessException exception) {
            throw new GuiaServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servicio de " + servicio
                            + ". Verifica su URL y que esté ejecutándose.");
        }
    }

    private org.springframework.http.client.ClientHttpResponse forwardBearerToken(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws java.io.IOException {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest incoming = attributes.getRequest();
            String authorization = incoming.getHeader("Authorization");
            if (authorization != null && !authorization.isBlank()) {
                request.getHeaders().set("Authorization", authorization);
            }
        }
        return execution.execute(request, body);
    }

    private JdkClientHttpRequestFactory requestFactory() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(3));
        return factory;
    }

    @FunctionalInterface
    private interface Consulta<T> {
        T ejecutar();
    }

    private record CobroResponse(String estado_pago) {
    }
}
