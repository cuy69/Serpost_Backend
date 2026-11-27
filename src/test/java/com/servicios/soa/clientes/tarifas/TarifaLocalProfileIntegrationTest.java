package com.servicios.soa.clientes.tarifas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static java.util.Objects.requireNonNull;

@SpringBootTest(properties = "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example")
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
class TarifaLocalProfileIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void permiteCalcularTarifaLocalSinToken() throws Exception {
        mockMvc.perform(post("/tarifas/calculos")
                        .contentType(requireNonNull(APPLICATION_JSON))
                        .content("""
                                {
                                  "linea_negocio": "Encomienda",
                                  "peso_kg": 2.5,
                                  "origen": "Chimbote",
                                  "destino": "Lima",
                                  "tipo_servicio": "Estándar"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarifa_total").value(14.16));
    }
}
