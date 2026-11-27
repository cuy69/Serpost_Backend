package com.servicios.soa.clientes.envios.api;

import com.servicios.soa.clientes.envios.service.EnvioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/envios")
public class EnvioController {
    private final EnvioService service;
    public EnvioController(EnvioService service) { this.service = service; }

    @GetMapping
    public List<EnvioResponse> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioResponse registrar(@Valid @RequestBody EnvioRequest request) {
        return service.registrar(request);
    }

    @GetMapping("/{id_envio}")
    public EnvioResponse consultar(@PathVariable("id_envio") UUID id) {
        return service.consultar(id);
    }
}
