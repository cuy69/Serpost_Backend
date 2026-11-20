package com.servicios.soa.clientes.guias.api;

import com.servicios.soa.clientes.guias.api.dto.GenerarGuiaRequest;
import com.servicios.soa.clientes.guias.api.dto.GuiaDetailsResponse;
import com.servicios.soa.clientes.guias.api.dto.GuiaResponse;
import com.servicios.soa.clientes.guias.service.GuiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guias")
public class GuiaController {
    private final GuiaService service;

    public GuiaController(GuiaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuiaResponse generar(@Valid @RequestBody GenerarGuiaRequest request) {
        return service.generar(request);
    }

    @GetMapping("/{codigo_guia}")
    public GuiaDetailsResponse consultar(@PathVariable("codigo_guia") String codigoGuia) {
        return service.consultar(codigoGuia);
    }
}
