package com.servicios.soa.clientes.notificaciones.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public record NotificacionRequest(
        @NotNull UUID cliente_id,
        UUID envio_id,
        @NotNull @Pattern(regexp = "correccion_datos|restriccion_contenido|problema_pago")
        String tipo,
        @NotNull @Pattern(regexp = "correo|sms|mensajeria")
        String canal,
        @Size(max = 255) String mensaje,
        @Size(max = 20) List<@jakarta.validation.constraints.NotBlank @Size(max = 80) String> campos_invalidos,
        @Size(max = 200) String motivo
) {
    public NotificacionRequest {
        tipo = normalize(tipo);
        canal = normalize(canal);
        mensaje = trim(mensaje);
        motivo = trim(motivo);
        campos_invalidos = campos_invalidos == null
                ? null
                : Collections.unmodifiableList(new ArrayList<>(campos_invalidos));
    }

    @AssertTrue(message = "campos_invalidos es obligatorio para correcciones; envio_id y motivo son obligatorios para restricciones y problemas de pago")
    public boolean isDatosCondicionalesValidos() {
        if ("correccion_datos".equals(tipo)) {
            return campos_invalidos != null && !campos_invalidos.isEmpty()
                    && campos_invalidos.stream().allMatch(campo -> campo != null && !campo.isBlank());
        }
        if ("restriccion_contenido".equals(tipo) || "problema_pago".equals(tipo)) {
            return envio_id != null && motivo != null && !motivo.isBlank();
        }
        return true;
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
