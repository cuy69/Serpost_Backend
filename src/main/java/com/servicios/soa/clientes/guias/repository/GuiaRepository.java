package com.servicios.soa.clientes.guias.repository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.servicios.soa.clientes.guias.model.Guia;
import com.servicios.soa.clientes.guias.service.GuiaServiceException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GuiaRepository {
    private final ObjectMapper objectMapper;
    private final Path databasePath;
    private final List<Guia> guias = new ArrayList<>();

    public GuiaRepository(ObjectMapper objectMapper,
                          @Value("${services.guias.database-path:guias.json}") String databasePath) {
        this.objectMapper = objectMapper;
        this.databasePath = Path.of(databasePath).toAbsolutePath().normalize();
    }

    @PostConstruct
    void cargarGuias() {
        try {
            Files.createDirectories(databasePath.getParent());
            if (Files.notExists(databasePath)) {
                escribirArchivo(List.of());
                return;
            }

            JsonNode root = objectMapper.readTree(databasePath.toFile());
            JsonNode records = root == null ? null : root.get("guias");
            if (records != null && !records.isArray()) {
                throw new IOException("La propiedad 'guias' debe ser un arreglo.");
            }
            if (records != null) {
                List<Guia> cargadas = objectMapper.convertValue(records, new TypeReference<List<Guia>>() { });
                guias.addAll(cargadas);
            }
        } catch (IOException | IllegalArgumentException exception) {
            throw new IllegalStateException("No se pudo leer el archivo de guías: " + databasePath, exception);
        }
    }

    public synchronized boolean existsByIdEnvio(UUID idEnvio) {
        return guias.stream().anyMatch(guia -> guia.idEnvio().equals(idEnvio));
    }

    public synchronized Optional<Guia> findByCodigoGuia(String codigoGuia) {
        return guias.stream().filter(guia -> guia.codigoGuia().equals(codigoGuia)).findFirst();
    }

    public synchronized Guia save(Guia guia) {
        if (existsByIdEnvio(guia.idEnvio())
                || findByCodigoGuia(guia.codigoGuia()).isPresent()) {
            throw new DataIntegrityViolationException("El envío ya tiene una guía registrada.");
        }
        List<Guia> actualizadas = new ArrayList<>(guias);
        actualizadas.add(guia);
        escribirArchivo(actualizadas);
        guias.add(guia);
        return guia;
    }

    private void escribirArchivo(List<Guia> registros) {
        Path temporaryFile = null;
        try {
            Path parent = databasePath.getParent();
            temporaryFile = Files.createTempFile(parent, "guias-", ".tmp");
            Map<String, List<Guia>> contenido = new LinkedHashMap<>();
            contenido.put("guias", registros);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(temporaryFile.toFile(), contenido);
            try {
                Files.move(temporaryFile, databasePath,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, databasePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // La falla principal de escritura se informa al llamador.
                }
            }
            throw new GuiaServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo guardar la guía en el archivo JSON.");
        }
    }
}
