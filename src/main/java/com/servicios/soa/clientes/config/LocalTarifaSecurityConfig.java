package com.servicios.soa.clientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@Profile("local")
public class LocalTarifaSecurityConfig {

    @Bean
    SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/tarifas/calculos").permitAll()
                        .requestMatchers(HttpMethod.POST, "/clientes").permitAll()
                        .requestMatchers(HttpMethod.POST, "/notificaciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/notificaciones/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cobros").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cobros/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/envios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/envios/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/guias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/guias/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
                .build();
    }
}
