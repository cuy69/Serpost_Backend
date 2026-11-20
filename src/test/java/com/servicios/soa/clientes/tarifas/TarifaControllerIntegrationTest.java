package com.servicios.soa.clientes.tarifas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static java.util.Objects.requireNonNull;

@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example",
        "serpost.tarifas.reglas[0].linea-negocio=Encomienda",
        "serpost.tarifas.reglas[0].origen=Chimbote",
        "serpost.tarifas.reglas[0].destino=Lima",
        "serpost.tarifas.reglas[0].tipo-servicio=Estándar",
        "serpost.tarifas.reglas[0].peso-min-kg=0.1",
        "serpost.tarifas.reglas[0].peso-max-kg=5.0",
        "serpost.tarifas.reglas[0].tarifa-base=1.50",
        "serpost.tarifas.reglas[0].recargos=0.50",
        "serpost.tarifas.reglas[0].tasa-igv=18",
        "serpost.tarifas.reglas[0].moneda=PEN"
})
@AutoConfigureMockMvc
class TarifaControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void calculaTarifaConReglaAplicable() throws Exception {
        mockMvc.perform(post("/tarifas/calculos")
                        .with(requireNonNull(jwt().authorities(new SimpleGrantedAuthority("SCOPE_atencion_cliente"))))
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
                .andExpect(jsonPath("$.tarifa_base").value(1.50))
                .andExpect(jsonPath("$.recargos").value(0.50))
                .andExpect(jsonPath("$.igv").value(0.36))
                .andExpect(jsonPath("$.tarifa_total").value(2.36))
                .andExpect(jsonPath("$.moneda").value("PEN"));
    }

    @Test
    void devuelveNotFoundSiNoHayReglaAplicable() throws Exception {
        mockMvc.perform(post("/tarifas/calculos")
                        .with(requireNonNull(jwt().authorities(new SimpleGrantedAuthority("SCOPE_atencion_cliente"))))
                        .contentType(requireNonNull(APPLICATION_JSON))
                        .content("""
                                {
                                  "linea_negocio": "Personal",
                                  "peso_kg": 2.5,
                                  "origen": "Chimbote",
                                  "destino": "Lima",
                                  "tipo_servicio": "Estándar"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaPesoNoPositivo() throws Exception {
        mockMvc.perform(post("/tarifas/calculos")
                        .with(requireNonNull(jwt().authorities(new SimpleGrantedAuthority("SCOPE_atencion_cliente"))))
                        .contentType(requireNonNull(APPLICATION_JSON))
                        .content("""
                                {
                                  "linea_negocio": "Encomienda",
                                  "peso_kg": 0,
                                  "origen": "Chimbote",
                                  "destino": "Lima",
                                  "tipo_servicio": "Estándar"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazaCamposObligatoriosAusentes() throws Exception {
        mockMvc.perform(post("/tarifas/calculos")
                        .with(requireNonNull(jwt().authorities(new SimpleGrantedAuthority("SCOPE_atencion_cliente"))))
                        .contentType(requireNonNull(APPLICATION_JSON))
                        .content("""
                                {
                                  "linea_negocio": "Encomienda",
                                  "peso_kg": 2.5,
                                  "origen": "Chimbote",
                                  "destino": "Lima"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.tipo_servicio").exists());
    }

    @Test
    void restringeAccesoAlRolDeAtencionAlCliente() throws Exception {
        mockMvc.perform(post("/tarifas/calculos")
                        .with(requireNonNull(jwt()))
                        .contentType(requireNonNull(APPLICATION_JSON))
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
