# Cocina DeLicia — Backend (Spring Boot)

## Objetivo del repo
API REST para Cocina DeLicia con catálogo, pedidos, usuarios/roles y soporte de WebSocket.
Integraciones: AWS Cognito (JWT/OAuth2), S3 presigned URLs para imágenes, DB MySQL/Aurora.
Migraciones con Flyway en `src/main/resources/db/migration`.

---

## Stack y convenciones del código (NO inventar nuevas capas)
- Entradas HTTP: `*/controller/*Controller.java`
- DTOs: `*/dto/*Request | *Response.java`
- Servicios: `*/service/*Service.java`
- Implementaciones: `*/service/impl/*ServiceImpl.java`
- Persistencia: `*/repository/*Repository.java` (Spring Data JPA)
- Specs JPA: `*/repository/spec/*`
- Dominio (reglas puras): `*/domain/*`
- Eventos: `*/events/*`
- Excepciones: `common/exception/*`
- Error handling: `common/web/GlobalExceptionHandler.java`
- Configuración: `common/config/*` + `config/OpenApiConfig.java`
- Auth Cognito: `auth/cognito/*`
- S3 / imágenes / CDN: `common/s3/*`

⚠️ **Regla**: si hay dudas de estructura, **copiar el estilo del módulo `order/`**.

---

## Mapa mental del backend (orientación rápida)
- `catalog/`: catálogo público (lectura).
- `catalog/admin/`: ABM de categorías, productos, variantes e imágenes.
- `order/`: módulo de referencia (domain, mapper, spec, events, tests).
- `product/`: entidades JPA + mappers admin.
- `user/`: registro, roles, direcciones, current user.
- `common/`: infraestructura transversal (security, websockets, errores, s3).

---

## Reglas de trabajo (cómo espero que actúe Codex)
- Antes de codear: proponer **plan (3–7 bullets)** + **archivos a tocar**.
- Cambios pequeños y reversibles (evitar refactors masivos).
- No agregar dependencias sin pedir confirmación.
- No hardcodear secretos, URLs, ARNs ni buckets.
- Usar excepciones del dominio (`BadRequestException`, `NotFoundException`, `DomainException`).
- Dejar que los errores HTTP los traduzca `GlobalExceptionHandler`.

---

## Perfiles y configuración
- Entornos: `application-dev.yml`, `application-local.yml`, `application-prod.yml`, `application-test.yml`
- Logging: log4j2 (`log4j2-spring*.xml`) — no loguear PII ni tokens
- DB:
    - Migraciones con Flyway
    - **Nunca editar migraciones ya aplicadas**
    - Cambios de schema ⇒ nueva migration

---

## Testing esperado (mínimo)
- Nueva regla / endpoint ⇒ test unitario o controller test
- Usar ejemplos existentes (`OrderControllerTest`, validators de domain)
- Mantener `OpenApiSmokeTest` funcionando

---

## Checklist de Done (obligatorio)
- `mvn test` pasa
- No warnings críticos al levantar la app
- DTOs validados si aplica
- Si tocaste DB ⇒ migration nueva aplicada correctamente

---

## Skills disponibles en este repo
- **cd-backend-endpoint** → nuevo endpoint / cambio funcional
- **cd-backend-bugfix** → bug, error, comportamiento incorrecto
- **cd-backend-s3-images** → imágenes, presigned URLs, ProductImage
- **cd-backend-architecture-map** → orientación / dónde tocar código

---

## Auto-detección de Skills (OBLIGATORIO)

Antes de implementar cualquier cambio:
1. Elegí **automáticamente UNA skill**
2. Declarala en la **primera línea del mensaje**
3. Seguí el checklist de esa skill

### Reglas de selección automática

- Si el pedido incluye:
    - `endpoint`, `controller`, `POST`, `PUT`, `PATCH`, `DTO`, `service`, `repository`
      ⇒ **cd-backend-endpoint**

- Si el pedido incluye:
    - `bug`, `error`, `falla`, `exception`, `stacktrace`, `no funciona`, `rompe`, `regression`
      ⇒ **cd-backend-bugfix**

- Si el pedido incluye:
    - `imagen`, `image`, `S3`, `presign`, `presigned`, `ProductImage`, `CDN`
      ⇒ **cd-backend-s3-images**

- Si el pedido es:
    - exploratorio, de entendimiento, “¿dónde implemento…?”
      ⇒ **cd-backend-architecture-map**

- Si hay duda razonable:
  ⇒ usar **cd-backend-endpoint** y explicar por qué

---

## Señal obligatoria de uso de Skill (verificación)

Cuando empieces una respuesta técnica:
1. Primera línea:  
   `Skill: <nombre-skill> (auto)`
2. Checklist breve de la skill (3–6 bullets)
3. Lista de archivos que planeás tocar
