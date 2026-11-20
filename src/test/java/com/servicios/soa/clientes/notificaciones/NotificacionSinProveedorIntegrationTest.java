package com.servicios.soa.clientes.notificaciones;

import com.servicios.soa.clientes.model.Cliente;
import com.servicios.soa.clientes.notificaciones.repository.NotificacionIntentoRepository;
import com.servicios.soa.clientes.notificaciones.repository.NotificacionRepository;
import com.servicios.soa.clientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example")
@AutoConfigureMockMvc
class NotificacionSinProveedorIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private NotificacionIntentoRepository intentoRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private UUID clienteId;

    @BeforeEach
    void prepararCliente() {
        intentoRepository.deleteAll();
        notificacionRepository.deleteAll();
        clienteRepository.deleteAll();
        Cliente cliente = clienteRepository.saveAndFlush(new Cliente(
                "DNI", "12345678", "Ana Perez", "Av. Brasil 123",
                "912345678", "ana@example.com", "remitente"
        ));
        clienteId = cliente.getIdCliente();
    }

    @Test
    void devuelve503YDejaLaNotificacionPendienteParaReintento() throws Exception {
        String response = mockMvc.perform(post("/notificaciones")
                        .with(requireNonNull(jwt()))
                        .contentType(requireNonNull(APPLICATION_JSON))
                        .content("""
                                {
                                  "cliente_id": "%s",
                                  "tipo": "correccion_datos",
                                  "canal": "correo",
                                  "campos_invalidos": ["correo"]
                                }
                                """.formatted(clienteId)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.estado").value("pendiente"))
                .andReturn().getResponse().getContentAsString();
        String notificacionId = com.jayway.jsonpath.JsonPath.read(response, "$.notificacion_id");

        String estado = "pendiente";
        for (int intento = 0; intento < 30 && "pendiente".equals(estado); intento++) {
            Thread.sleep(100);
            String actual = mockMvc.perform(get("/notificaciones/{id}", notificacionId)
                            .with(requireNonNull(jwt())))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            estado = com.jayway.jsonpath.JsonPath.read(actual, "$.estado");
        }

        org.junit.jupiter.api.Assertions.assertEquals("fallida", estado);
    }
}
