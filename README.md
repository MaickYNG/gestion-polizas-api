# gestion-polizas-api

API REST para la gestion de pólizas de arrendamiento, construida con Spring Boot 3 y base de datos H2 en memoria

## Requisitos

- Java 21
- Maven 3.9+
- No requiere base de datos externa (H2 en memoria)

## Ejecución

```bash
mvn spring-boot:run
```

La aplicación inicia en `http://localhost:8080`. Al arrancar, se cargan automaticamente tres polizas de prueba mediante `DataInitializer`

## Consola H2

Disponible en `http://localhost:8080/h2-console` con los siguientes parámetros:

| Parámetro | Valor |
|-----------|-------|
| JDBC URL | `jdbc:h2:mem:polizasdb` |
| Usuario | `sa` |
| Contraseña | *(vacía)* |

> La consola H2 no requiere API Key

## Autenticación

Todos los endpoints (excepto `/h2-console/**`) requieren el header:

```
x-api-key: 123456
```

Si el header está ausente o es incorrecto, la API responde con HTTP `401 Unauthorized`:

```json
{
  "codigo": 401,
  "mensaje": "API Key ausente o inválida",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Arquitectura

```
com.gestionpolizas
├── config/          AppProperties, DataInitializer
├── controller/      PolizaController, RiesgoController, CoreMockController
├── dto/             DTOs de request y response (Java records)
├── exception/       BusinessRuleException, ResourceNotFoundException, GlobalExceptionHandler
├── model/           Entidades JPA y Enums
├── repository/      PolizaRepository, RiesgoRepository
├── security/        ApiKeyAuthFilter
└── service/         PolizaService, RiesgoService, CoreIntegrationService
```

**Flujo de una petición:**

```
HTTP Request
    → ApiKeyAuthFilter (valida x-api-key)
    → Controller (deserializa y delega)
    → Service (aplica reglas de negocio)
    → Repository (accede a H2)
    → CoreIntegrationService (registra evento vía SLF4J)
    → DTO → HTTP Response
```

## Reglas de negocio

- Una poliza **CANCELADA** no puede renovarse
- Cancelar una póliza cancela automáticamente todos sus riesgos asociados
- Solo se pueden agregar riesgos a pólizas de tipo **COLECTIVA** mediante `POST /polizas/{id}/riesgos`
- Una poliza **INDIVIDUAL** admite como máximo 1 riesgo
- Al renovar, si no se envía `porcentajeIpc`, se aplica el valor configurado en `app.ipc.porcentaje-defecto` (por defecto: `9.28`)

## Endpoints

### Pólizas

#### Listar pólizas

```
GET /polizas
GET /polizas?tipo=INDIVIDUAL
GET /polizas?estado=ACTIVA
GET /polizas?tipo=COLECTIVA&estado=ACTIVA
```

**Headers:** `x-api-key: 123456`

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "numeroPoliza": "POL-2024-001",
    "tipo": "INDIVIDUAL",
    "estado": "ACTIVA",
    "tomador": "Carlos Mendoza",
    "asegurado": "Carlos Mendoza",
    "beneficiario": "Laura Mendoza",
    "canonMensual": 850000.00,
    "mesesVigencia": 12,
    "prima": 10200000.00,
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-12-31",
    "riesgos": []
  }
]
```

#### Renovar póliza (con IPC explícito)

```
POST /polizas/{id}/renovar
```

**Headers:** `x-api-key: 123456`, `Content-Type: application/json`

**Body (opcional):**
```json
{ "porcentajeIpc": 5.5 }
```

Sin body, se aplica `app.ipc.porcentaje-defecto=9.28`.

**Response `200 OK`:** póliza actualizada con nuevo `canonMensual`, `prima` y estado `RENOVADA`

**Response `400 Bad Request`** si la póliza está en estado `CANCELADA`

#### Cancelar póliza

```
POST /polizas/{id}/cancelar
```

**Headers:** `x-api-key: 123456`

**Response `200 OK`:** poliza en estado `CANCELADA` con todos sus riesgos en `CANCELADO`

#### Listar riesgos de una póliza

```
GET /polizas/{id}/riesgos
```

**Headers:** `x-api-key: 123456`

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "direccionInmueble": "Calle 72 # 45-10, Bogotá",
    "arrendatarioAsegurado": "Carlos Mendoza",
    "arrendadorBeneficiario": "Laura Mendoza",
    "estado": "ACTIVO",
    "polizaId": 1
  }
]
```

#### Agregar riesgo a poliza

```
POST /polizas/{id}/riesgos
```

**Headers:** `x-api-key: 123456`, `Content-Type: application/json`

**Body:**
```json
{
  "direccionInmueble": "Carrera 7 # 32-15, Bogotá",
  "arrendatarioAsegurado": "Nombre Arrendatario",
  "arrendadorBeneficiario": "Nombre Arrendador"
}
```

**Response `201 Created`:** riesgo creado.

**Response `400 Bad Request`** si la poliza es `INDIVIDUAL` o está `CANCELADA`.

### Riesgos

#### Cancelar riesgo

```
POST /riesgos/{id}/cancelar
```

**Headers:** `x-api-key: 123456`

**Response `200 OK`:** riesgo en estado `CANCELADO`.

### Mock CORE

#### Registrar evento

```
POST /core-mock/evento
```

**Headers:** `x-api-key: 123456`, `Content-Type: application/json`

**Body:**
```json
{ "evento": "ACTUALIZACION", "polizaId": 1 }
```

**Response `200 OK`:**
```json
{
  "estado": "ENVIADO",
  "evento": "ACTUALIZACION",
  "polizaId": "1"
}
```

## Manejo de errores

| HTTP | Tipo | Causa |
|------|------|-------|
| `400` | `BusinessRuleException` | Violación de regla de negocio |
| `400` | Validación Bean | Campo obligatorio ausente o inválido |
| `401` | `ApiKeyAuthFilter` | Header `x-api-key` ausente o incorrecto |
| `404` | `ResourceNotFoundException` | Póliza o riesgo no encontrado |
| `500` | `Exception` | Error interno no controlado |

**Formato de error:**
```json
{
  "codigo": 400,
  "mensaje": "No se puede renovar una póliza en estado CANCELADA.",
  "errores": [],
  "timestamp": "2024-01-15T10:30:00"
}
```

## Propiedades configurables

| Propiedad | Valor por defecto | Descripción |
|-----------|-------------------|-------------|
| `app.security.api-key` | `123456` | Clave de autenticación del header |
| `app.ipc.porcentaje-defecto` | `9.28` | IPC aplicado al renovar si no se envía uno explícito |
| `spring.h2.console.enabled` | `true` | Habilita la consola H2 |
