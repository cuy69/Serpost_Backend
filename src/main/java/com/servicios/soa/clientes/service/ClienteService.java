package com.servicios.soa.clientes.service;

import com.servicios.soa.clientes.api.dto.ClienteRequest;
import com.servicios.soa.clientes.api.dto.ClienteResponse;
import com.servicios.soa.clientes.model.Cliente;
import com.servicios.soa.clientes.repository.ClienteRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse registrar(ClienteRequest request) {
        if (repository.existsByTipoDocumentoAndNumeroDocumento(
                request.tipo_documento(), request.numero_documento())) {
            throw new ClienteDuplicadoException();
        }

        Cliente cliente = new Cliente(
                request.tipo_documento(), request.numero_documento(), request.nombres(),
                request.direccion(), request.telefono(), request.correo(), request.rol()
        );
        try {
            Cliente guardado = repository.saveAndFlush(cliente);
            return new ClienteResponse(guardado.getIdCliente(), "registrado");
        } catch (DataIntegrityViolationException exception) {
            throw new ClienteDuplicadoException();
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<Cliente> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente consultar(@NonNull UUID idCliente) {
        return repository.findById(idCliente).orElseThrow(ClienteNotFoundException::new);
    }

    @Transactional
    public ClienteResponse actualizar(UUID idCliente, ClienteRequest request) {
        Cliente cliente = consultar(idCliente);
        if (repository.existsByTipoDocumentoAndNumeroDocumentoAndIdClienteNot(
                request.tipo_documento(), request.numero_documento(), idCliente)) {
            throw new ClienteDuplicadoException();
        }

        cliente.setTipoDocumento(request.tipo_documento());
        cliente.setNumeroDocumento(request.numero_documento());
        cliente.setNombres(request.nombres());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setCorreo(request.correo());
        cliente.setRol(request.rol());

        try {
            repository.saveAndFlush(cliente);
            return new ClienteResponse(cliente.getIdCliente(), "actualizado");
        } catch (DataIntegrityViolationException exception) {
            throw new ClienteDuplicadoException();
        }
    }

    @Transactional
    public void eliminar(UUID idCliente) {
        repository.delete(consultar(idCliente));
    }
}
