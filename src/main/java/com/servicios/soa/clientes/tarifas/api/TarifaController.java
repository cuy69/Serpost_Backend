package com.servicios.soa.clientes.tarifas.api;

import com.servicios.soa.clientes.tarifas.service.TarifaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tarifas")
public class TarifaController {
    private final TarifaService service;

    public TarifaController(TarifaService service) {
        this.service = service;
    }

    @PostMapping("/calculos")
    public CalcularTarifaResponse calcular(@Valid @RequestBody CalcularTarifaRequest request) {
        return service.calcular(request);
    }
}
