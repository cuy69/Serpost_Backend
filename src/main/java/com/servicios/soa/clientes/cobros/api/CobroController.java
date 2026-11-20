package com.servicios.soa.clientes.cobros.api;

import com.servicios.soa.clientes.cobros.service.CobroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/cobros")
public class CobroController {
    private final CobroService service;

    public CobroController(CobroService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CobroResponse registrar(@Valid @RequestBody CobroRequest request) {
        return service.registrar(request);
    }

    @GetMapping("/{id_cobro}")
    public CobroResponse consultar(@PathVariable("id_cobro") UUID idCobro) {
        return service.consultar(idCobro);
    }
}
