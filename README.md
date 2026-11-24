# GestionarCliente

Servicio REST síncrono en Java 25 y Spring Boot para registrar, actualizar y consultar clientes.

## Requisitos

- Java 25 o superior
- Maven 3.9 o superior
- Un proveedor OAuth 2.0 / OpenID Connect que emita JWT

## Configuración y ejecución

Configura `OAUTH_ISSUER_URI` con el issuer de tu proveedor de identidad y ejecuta:

```powershell
$env:OAUTH_ISSUER_URI = "https://identidad.ejemplo.com/issuer"
mvn spring-boot:run
```

Si la variable no se define, la aplicación usa un valor de respaldo `https://issuer.example` para que pueda arrancar en entornos locales sin bloquear el proceso de desarrollo. En producción, reemplázalo por tu issuer real.

Para iniciar el API simulado que persiste en `db.json` y rechaza solicitudes duplicadas, ejecuta desde `src/main/java/com/servicios/soa/clientes/api`:

cd src\main\java\com\servicios\soa\clientes\api

```powershell
npm start
```

Los `POST` duplicados y las actualizaciones `PUT`/`PATCH` que crearían duplicados responden `409 Conflict`: en clientes se compara tipo y número de documento; en envíos, todos los datos excepto `id`. Usa este comando en lugar de `npx json-server db.json` para que se aplique la validación.

La API valida los JWT Bearer usando los metadatos/JWK del issuer. Todas las rutas requieren autenticación. En producción, publica el servicio únicamente detrás de HTTPS (por ejemplo, con un proxy inverso que termine TLS); no envíes tokens ni datos personales por HTTP sin cifrar.

La base H2 está en memoria para facilitar la ejecución local y se reinicia al detener la aplicación. Para persistencia de producción, configura una base de datos administrada y credenciales mediante variables de entorno.

## API

Base URL: `http://localhost:8080`

Envía el encabezado `Authorization: Bearer <access_token>` en cada solicitud. El campo `tipo_documento` admite `DNI`, `RUC`, `CE` o `pasaporte`; `rol` admite `remitente` o `destinatario`.

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

## RegistrarEnvío con almacenamiento JSON

La API local de Postman se ejecuta desde `src/main/java/com/servicios/soa/clientes/api` con `npm start` y escucha en `http://localhost:3001`.

- `POST /envios` valida UUIDs, peso positivo, contenido, línea de negocio, origen y destino; guarda el envío en `db.json` y responde `201` con `id_envio` y `estado`.
- `GET /envios/{id_envio}` devuelve los datos persistidos. Un ID mal formado responde `400` y uno inexistente `404`.
- Los campos inválidos se devuelven juntos con `400`. El contenido se compara sin distinguir mayúsculas ni tildes contra `contenidos_prohibidos` en `db.json`; una coincidencia activa responde `422` con categoría y palabra clave. Un fallo al escribir el archivo responde `503`.

La lista JSON inicial contiene las palabras clave de las seis categorías de contenido prohibido.

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
- Si se proporciona `envio_id`, se valida mediante `SERPOST_ENVIOS_BASE_URL` (por defecto `http://127.0.0.1:3001`). Inicia el API JSON con `npm start` desde `src/main/java/com/servicios/soa/clientes/api`.
- En perfil `local`, el adaptador simula la entrega y no envía correo, SMS ni mensajes reales. Fuera de ese perfil no se ha configurado un proveedor: `POST` devuelve `503` con el ID creado y estado `pendiente`, y las notificaciones se reintentan hasta el límite `serpost.notificaciones.reintentos.maximo` (por defecto 3). Consulta el ID para revisar el estado; los intentos se auditan en la base de datos.
- El contrato actual registra los campos inválidos y los intentos, pero no incluye valores anteriores/nuevos de los datos corregidos; para auditar versiones de datos (RNF-04.2) se debe ampliar la solicitud con esas versiones.
- El perfil local permite `POST /clientes` para crear el cliente de prueba. Usa el `id_cliente` de la respuesta en la notificación. Todas estas excepciones de seguridad local solo aplican al perfil `local`.



## Generar y consultar guías

El servicio Java también expone `POST /guias` y `GET /guias/{codigo_guia}`. Para emitir una guía, consulta el envío en `GET /envios/{id_envio}` y el cobro en `GET /cobros/{id_cobro}`; solo la guarda si Cobro responde `estado_pago: "aprobado"`. La guía queda asociada de forma única al envío.

Configura las direcciones de los servicios dependientes con `ENVIOS_BASE_URL` y `COBROS_BASE_URL`. Los valores locales predeterminados son `http://localhost:3001` y `http://127.0.0.1:8080`. La URL que devuelve para seguimiento se configura con `GUIAS_TRACKING_BASE_URL` (predeterminado: `http://localhost:8080/guias`). El token Bearer entrante se reenvía a ambos servicios.

Solicitud para Postman (`POST http://localhost:8080/guias`, con `Authorization: Bearer <access_token>`):

```json
{
  "id_envio": "8d4f9a2c-0e3b-4f61-9c72-5a1b3d7e8f90",
  "id_cobro": "a1957e60-826e-4f4c-9a52-53ec0f9a8b21"
}
```

La respuesta `201 Created` incluye `codigo_guia`, `url_seguimiento` y `fecha_emision`. Usa el código devuelto en `GET http://localhost:8080/guias/{codigo_guia}` para consultar la guía. Si un servicio dependiente no está disponible, responde `503`; un pago no aprobado responde `422`.

Las guias se guardan en `src/main/java/com/servicios/soa/clientes/api/guias.json`, junto a `db.json`. El archivo se crea al iniciar si todavia no existe. Puedes cambiar la ubicacion con `GUIAS_DB_PATH`. La base H2 en memoria sigue siendo la persistencia del servicio Java de clientes.

### Prueba local sin OAuth

Para pruebas locales de Postman puedes iniciar Spring Boot con el perfil `local`, que permite solicitudes sin Bearer token. Actívalo solo en tu entorno local con `$env:SPRING_PROFILES_ACTIVE = "local"` antes de iniciar la aplicación. Sin ese perfil, las rutas siguen exigiendo JWT. No uses el perfil local en producción.

## Registrar y consultar cobros

El servicio Java expone `POST /cobros` para registrar pagos y `GET /cobros/{id_cobro}` para consultarlos. Los registros se guardan en la tabla `cobros` de H2 junto con monto, moneda, medio, referencia, estado y fecha. La H2 actual es en memoria; los cobros se reinician al detener la aplicación.

En modo local, usa **No Auth** en Postman. Para registrar un pago, envía `id_envio`, `monto` positivo, moneda de tres letras, `medio_pago` (`efectivo`, `tarjeta` o `transferencia`) y `referencia_operacion` para pagos digitales. El servicio devuelve `id_cobro`, `estado_pago` y `fecha_hora`. Una referencia igual a `RECHAZADO` simula un rechazo para probar el `422`; el proyecto no está conectado a una pasarela bancaria real.

Guía consulta Cobros en `http://127.0.0.1:8080` de forma predeterminada, porque ambos módulos corren en esta aplicación. Puedes cambiarlo con `COBROS_BASE_URL`. En producción, `POST /cobros` requiere un JWT con `SCOPE_cajero`, `ROLE_CAJERO`, `SCOPE_servicio_autorizado` o `ROLE_SERVICIO_AUTORIZADO`.
