package com.servicios.soa.clientes.tarifas.service;

import com.servicios.soa.clientes.tarifas.api.CalcularTarifaRequest;
import com.servicios.soa.clientes.tarifas.api.CalcularTarifaResponse;
import com.servicios.soa.clientes.tarifas.config.TarifarioProperties;
import com.servicios.soa.clientes.tarifas.config.TarifarioProperties.Regla;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TarifaService {
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int ESCALA_MONETARIA = 2;

    private final TarifarioProperties tarifario;

    public TarifaService(TarifarioProperties tarifario) {
        this.tarifario = tarifario;
    }

    public CalcularTarifaResponse calcular(CalcularTarifaRequest request) {
        List<Regla> coincidencias = tarifario.reglas().stream()
                .filter(regla -> coincide(regla, request))
                .toList();

        if (coincidencias.isEmpty()) {
            throw new TarifaNoEncontradaException();
        }
        if (coincidencias.size() > 1) {
            throw new IllegalStateException("El tarifario contiene reglas coincidentes ambiguas");
        }

        Regla regla = coincidencias.get(0);
        BigDecimal base = regla.tarifaBase().setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
        BigDecimal recargos = regla.recargos().setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
        BigDecimal igv = base.add(recargos)
                .multiply(regla.tasaIgv())
                .divide(CIEN, ESCALA_MONETARIA, RoundingMode.HALF_UP);
        BigDecimal total = base.add(recargos).add(igv).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

        return new CalcularTarifaResponse(base, recargos, igv, total, regla.moneda());
    }

    private boolean coincide(Regla regla, CalcularTarifaRequest request) {
        return regla.lineaNegocio().equalsIgnoreCase(request.linea_negocio())
                && regla.origen().equalsIgnoreCase(request.origen())
                && regla.destino().equalsIgnoreCase(request.destino())
                && regla.tipoServicio().equalsIgnoreCase(request.tipo_servicio())
                && request.peso_kg().compareTo(regla.pesoMinKg()) >= 0
                && request.peso_kg().compareTo(regla.pesoMaxKg()) <= 0;
    }
}
