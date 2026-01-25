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
- `mvwn test` pasa
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
name: cd-backend-endpoint
description: Implementar un endpoint REST en Cocina DeLicia backend respetando capas controller/dto/service/impl/repository, validación y tests.
---

## Inputs mínimos
- Ruta y método (ej: POST /api/orders)
- Rol requerido (RoleName)
- DTO request/response esperado
- Cambios de DB (si aplica)

## Proceso
1) Plan (3–7 bullets) + archivos a tocar.
2) Crear/ajustar DTOs en `*/dto`.
3) Service interface en `*/service` y lógica en `*/service/impl`.
4) Repos/specs en `*/repository` y `*/repository/spec` si es filtrado.
5) Controller en `*/controller`.
6) Errores: usar `BadRequestException`, `NotFoundException`, `DomainException` y dejar que `GlobalExceptionHandler` traduzca.
7) Seguridad: aplicar restricciones en SecurityConfig/SecurityConfigLocal si requiere rol.
8) Tests:
    - Unit test para regla de dominio si aplica (ej: validator)
    - Controller test (MockMvc) si corresponde

## Done
- `mvwn test` ok
- No hardcode de secrets/config
- Si hay schema: migration nueva en `db/migration`

---
name: cd-backend-bugfix
description: Corregir un bug en backend con enfoque reproducible: identificar causa raíz, agregar test de regresión, fix incremental.
---

## Proceso
1) Reproducir:
    - Ubicar controller/service involucrado y el path exacto.
    - Revisar logs relevantes (log4j2) y stacktrace si existe.
2) Hipótesis:
    - Enumerar 2–3 causas posibles (ordenadas por probabilidad).
3) Test de regresión:
    - Agregar/ajustar test en `src/test` (controller o unit).
4) Fix:
    - Cambios mínimos en `service/impl`, `domain`, o `repository`.
5) Verificación:
    - `mvwn test`
    - Checklist de impacto (seguridad, DB, websockets)

## Reglas
- No refactor masivo.
- Si el bug es de datos: proponer migration o fix de seed (V2__seed_demo.sql) solo si aplica a tests.

---
name: cd-backend-s3-images
description: Implementar o corregir flujo de imágenes (presigned upload, CDN URL, ProductImage) en Cocina DeLicia backend.
---

## Contexto del repo
- Presign/config en `common/s3/*` (S3PresignerConfig, ImagePresignService, CdnUrlBuilder)
- Admin endpoints de imágenes en `catalog/admin/controller/*ProductImage*Controller.java`
- Persistencia en `product/model/ProductImage.java` y `product/repository/ProductImageRepository.java`

## Proceso recomendado
1) Confirmar flujo:
    - Request presign (DTO) -> generar URL -> frontend sube -> backend registra metadata -> respuesta con URL CDN.
2) Validaciones:
    - Tipo de imagen/extension permitida (si existe regla)
    - Path/key naming consistente
3) Persistencia:
    - Si se asocia a Product/ProductVariant: asegurar cascade/orphanRemoval correcto
    - Evitar borrar imágenes existentes si vienen con id (patch/merge)
4) Respuesta:
    - Siempre devolver URLs canonical (CDN) usando `CdnUrlBuilder` si corresponde

## Tests
- Unit test para naming/generación de key si hay lógica
- Controller test para endpoints admin (si están cubiertos)

## Done
- `mvwn test`
- No logging de URLs presign completas si contienen query sensitive (firma)

---
name: cd-backend-architecture-map
description: Explicar rápidamente la arquitectura y dónde tocar código en el backend de Cocina DeLicia (módulos, capas, reglas).
---

## Mapa del backend
- `catalog/`: catálogo público (controller + dto + service/impl).
- `catalog/admin/`: administración de categorías/productos/variantes/imágenes.
- `order/`: módulo referencia (domain, events, mapper, repository/spec, service/impl).
- `product/`: entidades y repos (Product, Variant, Image, Category, Tag) + mappers admin.
- `user/`: usuarios, roles, direcciones, current-user.
- `common/`: config, exceptions, global error handler, s3 presign/CDN.
- `auth/cognito/`: integración Cognito (userinfo client).

## Cómo decidir dónde tocar
- Nuevo endpoint: `*/controller` + `*/dto` + `*/service/impl` + `*/repository`.
- Regla de negocio: `*/domain`.
- Cambios de seguridad: `common/config/SecurityConfig*`.
- Imágenes/p_toggle S3: `common/s3/*` + controllers admin de imágenes.
- Errores/validación: exceptions + `GlobalExceptionHandler`.

## Regla
- Ante dudas, imitar el estilo de `order/`.


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
