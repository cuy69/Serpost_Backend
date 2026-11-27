# GestionarCliente

Backend REST en Java 25 y Spring Boot con PostgreSQL 18.

## Requisitos

- Docker Desktop con Docker Compose

No necesitas instalar Java ni Maven para ejecutar el entorno local; Docker construye y ejecuta la aplicación.

## Configuración y ejecución

Desde la raíz del proyecto, crea tu archivo de configuración local y levanta la aplicación y PostgreSQL:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

La primera construcción descarga dependencias y puede tardar unos minutos. La API queda en `http://localhost:8081`, así no choca con otros proyectos que usen el puerto `8080`. Compose activa el perfil `local`, pensado solo para desarrollo; modifica `.env` si necesitas ajustar la configuración. Para cambiar el puerto del host, define `BACKEND_PORT` en `.env`.

Fuera del perfil local, la API valida los JWT Bearer usando los metadatos/JWK del issuer y requiere autenticación. En producción, configura un issuer real y publica el servicio únicamente detrás de HTTPS.

El backend usa PostgreSQL 18 para clientes, envíos, reglas de contenido, cobros, guías y notificaciones.

## PostgreSQL 18

La URL, el usuario y la contraseña de la aplicación se pueden cambiar con `DATABASE_URL`, `DATABASE_USERNAME` y `DATABASE_PASSWORD`. Compose admite `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` y `POSTGRES_PORT`. El volumen `postgres_data` conserva los datos al reiniciar el contenedor; `docker compose down -v` lo elimina. Flyway aplica migraciones versionadas al arrancar y Hibernate valida el esquema.

Define `SERPOST_DB_PASSWORD` en `.env`; Compose la usa para inicializar PostgreSQL y para conectar el backend. El valor se aplica al inicializar un volumen vacío; cambiarlo después no cambia la contraseña guardada en una base existente.

Para detener los servicios usa `docker compose down`; los datos persisten en `postgres_data`. `docker compose down -v` también elimina la base local.

Los archivos JSON heredados ya no son leídos ni escritos por el backend. Sus datos no se importan automáticamente a PostgreSQL.

## API

Base URL: `http://localhost:8081`

Fuera del perfil local, envía `Authorization: Bearer <access_token>` en cada solicitud. El campo `tipo_documento` admite `DNI`, `RUC`, `CE` o `pasaporte`; `rol` admite `remitente` o `destinatario`.

### Registrar cliente

`POST /clientes` — responde `201 Created`.

```json
{
  "tipo_documento": "DNI",
  "numero_documento": "12345678",
  "nombres": "Ana Pérez",
  "direccion": "Calle Principal 123",
  "telefono": "555123456",
  "correo": "ana@example.com",
  "rol": "remitente"
}
```

Respuesta:

```json
{
  "id_cliente": "uuid",
  "estado": "registrado"
}
```

### Consultar cliente

`GET /clientes/{id_cliente}` — responde `200 OK` con los datos del cliente.

### Actualizar cliente

`PUT /clientes/{id_cliente}` — requiere los mismos campos que el registro y responde `200 OK`.

```json
{
  "id_cliente": "uuid",
  "estado": "actualizado"
}
```

## Errores

- `400 Bad Request`: formato o validación de datos incorrectos.
- `401 Unauthorized`: falta un token Bearer válido.
- `404 Not Found`: el identificador de cliente no existe.
- `409 Conflict`: ya existe un cliente con el mismo tipo y número de documento.
- `503 Service Unavailable`: indisponibilidad de la aplicación o sus dependencias.

Los errores de la API contienen `timestamp`, `status`, `error`, `mensaje` y `campos` (con errores de validación por campo).

## Registrar y consultar envíos

`POST /envios` guarda el envío en PostgreSQL y `GET /envios/{id_envio}` lo consulta. El registro requiere peso positivo, contenido, línea de negocio, origen y destino. Las reglas iniciales de contenido prohibido se insertan con la migración Flyway y la comparación ignora mayúsculas y tildes; una coincidencia devuelve `422`. Un duplicado devuelve `409`.

## Calcular tarifa de envío

`POST /tarifas/calculos` (versión `1.0.0`) calcula el desglose de una tarifa con el tarifario configurado. Requiere un JWT Bearer válido con el scope `atencion_cliente` o la autoridad `ROLE_ATENCION_CLIENTE`; como el resto de la API, debe exponerse mediante HTTPS en producción.

```json
{
  "linea_negocio": "Encomienda",
  "peso_kg": 2.5,
  "origen": "Chimbote",
  "destino": "Lima",
  "tipo_servicio": "Estándar"
}
```

La respuesta contiene `tarifa_base`, `recargos`, `igv`, `tarifa_total` y `moneda`. El catálogo inicia vacío intencionalmente; una solicitud sin una regla aplicable devuelve `404` y no se asignan tarifas de ejemplo.

Para habilitar tarifas, configura `serpost.tarifas.reglas` en `application.yml` o en la configuración externa de Spring Boot. Cada regla requiere `linea-negocio`, `origen`, `destino`, `tipo-servicio`, `peso-min-kg`, `peso-max-kg`, `tarifa-base`, `recargos`, `tasa-igv` y `moneda`. Los límites de peso son inclusivos; base y recargos son importes monetarios, e IGV es un porcentaje aplicado a su suma. La moneda debe indicarse explícitamente en cada regla. Si más de una regla coincide con una solicitud, el tarifario está ambiguo y debe corregirse.

Para probar tarifas y notificaciones localmente sin un proveedor OAuth, inicia con el perfil `local` usando `mvn spring-boot:run "-Dspring-boot.run.profiles=local"`. Este perfil permite registrar clientes, calcular tarifas y crear/consultar notificaciones sin token, y limita el servidor a `127.0.0.1`; las demás rutas siguen requiriendo autenticación. No actives este perfil en producción. En Postman selecciona **No Auth** para esas pruebas.

- `400 Bad Request`: campos obligatorios ausentes, línea de negocio no admitida o peso no positivo.
- `401 Unauthorized`: falta un token Bearer válido.
- `403 Forbidden`: el token no tiene el scope o rol requerido.
- `404 Not Found`: no existe una regla para la línea, peso, origen, destino y tipo de servicio solicitados.

## Enviar y consultar notificaciones

- `POST /notificaciones` acepta avisos y devuelve `202 Accepted` con `notificacion_id`, `estado` y `fecha_envio`. El estado inicial es `pendiente`; consulta `GET /notificaciones/{notificacion_id}` para ver el resultado del procesamiento asíncrono.
- Los tipos admitidos son `correccion_datos`, `restriccion_contenido` y `problema_pago`; los canales son `correo`, `sms` y `mensajeria`. Correo usa el email del cliente; SMS y mensajería usan su teléfono.
- `campos_invalidos` es obligatorio para correcciones. Para restricciones y problemas de pago son obligatorios `envio_id` y `motivo`. Cuando se omite `mensaje`, se genera una plantilla en español.
- Si se proporciona `envio_id`, se valida directamente contra los envíos guardados en PostgreSQL.
- En perfil `local`, el adaptador simula la entrega y no envía correo, SMS ni mensajes reales. Fuera de ese perfil no se ha configurado un proveedor: `POST` devuelve `503` con el ID creado y estado `pendiente`, y las notificaciones se reintentan hasta el límite `serpost.notificaciones.reintentos.maximo` (por defecto 3). Consulta el ID para revisar el estado; los intentos se auditan en la base de datos.
- El contrato actual registra los campos inválidos y los intentos, pero no incluye valores anteriores/nuevos de los datos corregidos; para auditar versiones de datos (RNF-04.2) se debe ampliar la solicitud con esas versiones.
- El perfil local permite `POST /clientes` para crear el cliente de prueba. Usa el `id_cliente` de la respuesta en la notificación. Todas estas excepciones de seguridad local solo aplican al perfil `local`.



## Generar y consultar guías

El servicio Java también expone `POST /guias` y `GET /guias/{codigo_guia}`. Para emitir una guía, consulta el envío en `GET /envios/{id_envio}` y el cobro en `GET /cobros/{id_cobro}`; solo la guarda si Cobro responde `estado_pago: "aprobado"`. La guía queda asociada de forma única al envío.

Envíos, cobros y guías consultan PostgreSQL dentro de la misma aplicación. En Compose, la URL de seguimiento apunta a `http://localhost:8081/guias`.

Solicitud para Postman (`POST http://localhost:8081/guias`, con `Authorization: Bearer <access_token>`):

```json
{
  "id_envio": "8d4f9a2c-0e3b-4f61-9c72-5a1b3d7e8f90",
  "id_cobro": "a1957e60-826e-4f4c-9a52-53ec0f9a8b21"
}
```

La respuesta `201 Created` incluye `codigo_guia`, `url_seguimiento` y `fecha_emision`. Usa el código devuelto en `GET http://localhost:8081/guias/{codigo_guia}` para consultar la guía. Si un servicio dependiente no está disponible, responde `503`; un pago no aprobado responde `422`.

Las guías se guardan en la tabla `guias` de PostgreSQL.

### Prueba local sin OAuth

Para pruebas locales de Postman puedes iniciar Spring Boot con el perfil `local`, que permite solicitudes sin Bearer token. Actívalo solo en tu entorno local con `$env:SPRING_PROFILES_ACTIVE = "local"` antes de iniciar la aplicación. Sin ese perfil, las rutas siguen exigiendo JWT. No uses el perfil local en producción.

## Registrar y consultar cobros

El servicio Java expone `POST /cobros` para registrar pagos y `GET /cobros/{id_cobro}` para consultarlos. Los registros se guardan en la tabla `cobros` de PostgreSQL junto con monto, moneda, medio, referencia, estado y fecha.

En modo local, usa **No Auth** en Postman. Para registrar un pago, envía `id_envio`, `monto` positivo, moneda de tres letras, `medio_pago` (`efectivo`, `tarjeta` o `transferencia`) y `referencia_operacion` para pagos digitales. El servicio devuelve `id_cobro`, `estado_pago` y `fecha_hora`. Una referencia igual a `RECHAZADO` simula un rechazo para probar el `422`; el proyecto no está conectado a una pasarela bancaria real.

Guías consulta Cobros desde la misma base de datos. En producción, `POST /cobros` requiere un JWT con `SCOPE_cajero`, `ROLE_CAJERO`, `SCOPE_servicio_autorizado` o `ROLE_SERVICIO_AUTORIZADO`.
