package com.servicios.soa.clientes.config;

import com.servicios.soa.clientes.tarifas.config.TarifarioProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({TarifarioProperties.class, SerpostProperties.class})
public class TarifarioConfiguration {
}
