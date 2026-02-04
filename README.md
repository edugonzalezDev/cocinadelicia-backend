# Cocina DeLicia – Backend (Spring Boot)

> **Estado:** Sprint 1 · Historia 1 · Versión extendida inicial (documento vivo)

Microservicio **backend** para *Cocina DeLicia* construido con **Spring Boot 3**, **Spring Data JPA**, **Lombok**, **Log4j2** y **Springdoc OpenAPI** (Swagger UI). Seguridad con **JWT + Cognito** será integrada en sprints posteriores.

---

## 🧭 Índice

* [Visión general](#-visión-general)
* [Stack técnico](#-stack-técnico)
* [Requisitos previos](#-requisitos-previos)
* [Instalación y ejecución](#-instalación-y-ejecución)
* [Configuración de entorno](#-configuración-de-entorno)
* [Estructura de proyecto](#-estructura-de-proyecto)
* [Convenciones y ramas](#-convenciones-y-ramas)
* [Roadmap breve](#-roadmap-breve)
* [CI/CD (placeholder)](#-cicd-placeholder)
* [Despliegue (placeholder)](#-despliegue-placeholder)
* [Troubleshooting](#-troubleshooting)
* [Licencia](#-licencia)
* [Contacto](#-contacto)

---

## 🎯 Visión general

* **Objetivo:** exponer APIs REST para **pedidos, productos, usuarios** y funcionalidades administrativas.
* **Dominio API (prod):** por definir (ej: `https://api.lacocinadelicia.com`)
* **Base de datos:** **Aurora Serverless v2 (MySQL)** — *endpoint por definir*.

> En **Sprint 1** se prioriza la base del repo, estructura, scripts, logging y Swagger. Seguridad JWT/Cognito y eventos asincrónicos llegarán más adelante.

---

## 🧪 Stack técnico

* **Java 17**, **Maven** (sin wrapper por ahora)
* Spring Boot 3 (Web, Validation)
* Spring Data JPA + Hibernate
* Lombok
* Log4j2
* Springdoc OpenAPI (Swagger UI)
* **Flyway** (planificado) / `import.sql` (provisorio en Sprint 1)

---

## 🔧 Requisitos previos

* Java 17 (JDK)
* Maven 3.9+
* MySQL/Aurora accesible (o local para desarrollo)
* Git

Verificá:

```bash
java -version
mvn -v
```

---

## 🚀 Instalación y ejecución

```bash
# clonar
git clone <URL_DEL_REPO_BACKEND>
cd cocinadelicia-backend

# compilar y correr
your_mvn_command_here clean install
./mvnw spring-boot:run  # si luego agregás wrapper
# o
mvn spring-boot:run
```

**Jar ejecutable (ejemplo):**

```bash
mvn -DskipTests package
java -jar target/cocinadelicia-backend-0.0.1-SNAPSHOT.jar
```

**Swagger UI:**

```
http://localhost:8080/swagger-ui.html
```

---

## ⚙️ Configuración de entorno

Usá un archivo `application.yml`/`application.properties` con perfiles `dev` y `prod`. Variables de entorno esperadas (placeholders):

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/<db>?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<password>

# CORS
ALLOWED_ORIGINS=http://localhost:5173,https://lacocinadelicia.com

# Cognito (para Sprint Seguridad)
COGNITO_REGION=us-xx
COGNITO_USER_POOL_ID=us-xx_XXXXXXXXX
# Alternativamente JWKS directo
COGNITO_JWKS_URI=https://cognito-idp.us-xx.amazonaws.com/us-xx_XXXXXXXXX/.well-known/jwks.json

# Logging
LOG_LEVEL_ROOT=INFO
```

> **Datos de ejemplo:** durante Sprint 1 podés usar `import.sql` para precargar registros. En sprints siguientes migraremos a **Flyway**.

---

## 🗂️ Estructura de proyecto

> Basada en **Convenciones.md** (puede evolucionar a multi-módulo si agregamos más microservicios).

```
src/
├─ main/
│  ├─ java/com/cocinadelicia/
│  │  ├─ controller/
│  │  ├─ service/
│  │  ├─ model/
│  │  ├─ repository/
│  │  ├─ dto/
│  │  ├─ config/
│  │  └─ exception/
│  └─ resources/
│     ├─ application.yml
│     └─ import.sql   # datos demo (provisorio)
└─ test/
```

> **Paquete base:** `com.cocinadelicia`
> **ArtifactId:** `cocinadelicia-backend`

---

## 🔀 Convenciones y ramas

* **Rama principal:** `main`
* **Ramas de trabajo:** `feature/<nombre>`, `bugfix/<nombre>`, `hotfix/<nombre>`
* **Commits:** *Conventional Commits* (ej: `feat: inicializar proyecto Spring Boot`)
* **Estilo de código:** Spotless + Google Java Format (planificado)

---

## ✨ Estilo de Código y Commits

- **Java Style:** [Google Java Format] aplicado con **Spotless**.
- **Verificación automática en CI:** `spotless:check` corre antes de tests/build.
- **Commits:** seguimos **Conventional Commits** (ej.: `feat: añadir endpoints de pedidos`).

### Comandos locales
```bash
# Aplicar formato a todo el proyecto
./mvnw spotless:apply

# Verificar formato (falla si hay violaciones)
./mvnw spotless:check
```
>Sugerido: activá también .editorconfig para uniformar fin de línea y whitespace en el IDE.
Ver detalles y ejemplos en Convenciones.md del repo raíz.

---

## 🗺️ Roadmap breve

* **Sprint 1:** base del repo, Swagger, logs, modelo preliminar
* **Sprint 2:** endpoints de pedidos (crear/listar/actualizar)
* **Sprint 3:** soporte en tiempo real (WebSocket) para visor de pedidos (chef)
* **Sprint 4:** catálogo e imágenes (S3)
* **Sprint 5:** autenticación y roles (Cognito + Security)

*(Basado en `Plan_Sprints_CocinaDeLicia.md`)*


---
## 📦 Pedidos (Sprint 2)

Esta sección describe el **flujo de pedido** y los **endpoints principales** implementados en el Sprint 2.
Los ejemplos asumen que el usuario está autenticado vía **JWT (Cognito)** y que el frontend utiliza
el `apiClient` con `Authorization: Bearer <token>`.

> Nota: los nombres de paths/roles se sincronizan con `OrderController` y la lógica de negocio de `OrderServiceImpl`.

### 🔁 Flujo de estados de pedido

Los pedidos (`CustomerOrder`) representan órdenes realizadas por usuarios finales. Cada pedido tiene
un `status` basado en el enum `OrderStatus`:

- `CREATED` → pedido recién creado por el cliente.
- `CONFIRMED` → **(reservado para futuro)**, posible etapa intermedia antes de preparar.
- `PREPARING` → el equipo de cocina está preparando el pedido.
- `READY` → el pedido está listo para retirar o salir a reparto.
- `OUT_FOR_DELIVERY` → el pedido está en camino (delivery).
- `DELIVERED` → el pedido fue entregado al cliente.
- `CANCELLED` → el pedido fue cancelado (por cliente o staff).

Transiciones válidas (conceptual, alineado a `OrderStatusTransitionValidator` y al frontend `AdminOrdersPage`):

- `CREATED` → `PREPARING` | `CANCELLED`
- `CONFIRMED` → `PREPARING` | `CANCELLED`
- `PREPARING` → `READY` | `CANCELLED`
- `READY` → `DELIVERED`
- `OUT_FOR_DELIVERY` → `DELIVERED`
- `DELIVERED` → *(estado final, sin transiciones posteriores)*
- `CANCELLED` → *(estado final, sin transiciones posteriores)*

Estas reglas se validan en backend. Las transiciones inválidas producen un **error 400** con código
de negocio (ej.: `INVALID_STATUS_TRANSITION`) y se registran en logs (`WARN`).

---

### 🔌 Endpoints de pedidos

Todos los endpoints están bajo el prefijo ` /api/orders` y documentados en Swagger/OpenAPI con el
tag `orders`. Se requiere JWT para acceder.

#### 1. Crear pedido

- **Método/Path:** `POST /api/orders`
- **Quién lo usa:** cliente autenticado (web/app).
- **Descripción:** crea un nuevo pedido asociado al usuario actual. Calcula precios y totales en base
  al precio vigente de cada variante.
- **Auth:** `Bearer JWT`
- **Roles:** cualquier usuario autenticado.

**Request (ejemplo – delivery):**

```json
{
  "fulfillment": "DELIVERY",
  "notes": "Sin cebolla y poco picante",
  "items": [
    {
      "productId": 1,
      "productVariantId": 10,
      "quantity": 2
    },
    {
      "productId": 2,
      "productVariantId": 20,
      "quantity": 1
    }
  ],
  "shipping": {
    "name": "Juan Pérez",
    "phone": "091234567",
    "line1": "Av. Siempre Viva 123",
    "line2": "Apto 201",
    "city": "Ciudad de la Costa",
    "region": "Canelones",
    "postalCode": "15000",
    "reference": "Frente a la plaza"
  }
}
```

**Response 201 (ejemplo simplificado):**

```json
{
  "id": 42,
  "status": "CREATED",
  "fulfillment": "DELIVERY",
  "currency": "UYU",
  "subtotalAmount": "520.00",
  "taxAmount": "0.00",
  "discountAmount": "0.00",
  "totalAmount": "520.00",
  "notes": "Sin cebolla y poco picante",
  "shipName": "Juan Pérez",
  "shipPhone": "091234567",
  "shipLine1": "Av. Siempre Viva 123",
  "shipLine2": "Apto 201",
  "shipCity": "Ciudad de la Costa",
  "shipRegion": "Canelones",
  "shipPostalCode": "15000",
  "shipReference": "Frente a la plaza",
  "items": [
    {
      "productId": 1,
      "productVariantId": 10,
      "productName": "Hamburguesa Clásica",
      "variantName": "Doble carne",
      "unitPrice": "220.00",
      "quantity": 2,
      "lineTotal": "440.00"
    },
    {
      "productId": 2,
      "productVariantId": 20,
      "productName": "Papas fritas",
      "variantName": "Grande",
      "unitPrice": "80.00",
      "quantity": 1,
      "lineTotal": "80.00"
    }
  ],
  "createdAt": "2025-11-12T14:32:10Z"
}
```

---

#### 2. Listar pedidos del usuario

- **Método/Path:** `GET /api/orders/mine`
- **Quién lo usa:** cliente autenticado (área “Mis pedidos” / “Área del Cliente”).
- **Descripción:** devuelve una **página** de pedidos pertenecientes al usuario actual, ordenados por
  `createdAt` descendente.
- **Auth:** `Bearer JWT`
- **Roles:** cualquier usuario autenticado.

Parámetros estándar de paginación (Spring Data):

- `page` (0-based, default 0)
- `size` (tamaño de página, default 10)
- `sort` (campo de orden, default `createdAt,desc`)

Ejemplo de uso desde frontend:

```ts
// useOrderStore.fetchMyOrders
GET /api/orders/mine?page=0&size=10
Authorization: Bearer <token>
```

---

#### 3. Listar pedidos para backoffice (ADMIN/CHEF)

- **Método/Path:** `GET /api/orders/ops` *(alias: `/admin`, `/chef`)*
- **Quién lo usa:** panel administrativo (cocina / backoffice).
- **Descripción:** lista paginada de pedidos con filtros por estado y rango de fechas.
- **Auth:** `Bearer JWT`
- **Roles:** `ADMIN` o `CHEF`.

Parámetros de filtro:

- `status` → lista CSV de estados (ej.: `CREATED,PREPARING,READY`).
- `from` → fecha desde (inclusive), formato `YYYY-MM-DD`.
- `to` → fecha hasta (inclusive), formato `YYYY-MM-DD`.
- Parámetros de paginación estándares (`page`, `size`, `sort`). El tamaño máximo se limita a 50.

Ejemplo:

```http
GET /api/orders/ops?status=CREATED,PREPARING&from=2025-11-01&to=2025-11-30&page=0&size=20
Authorization: Bearer <token ADMIN/CHEF>
```

La respuesta se envuelve en un `OrderPageResponse<OrderResponse>` con metadatos de paginación.

---

#### 4. Cambiar estado de un pedido (ADMIN/CHEF)

- **Método/Path:** `PATCH /api/orders/{id}/status`
- **Quién lo usa:** panel administrativo (cocina / backoffice).
- **Descripción:** cambia el estado de un pedido existente. Registra en logs quién realizó el cambio
  (`performedBy`) y la nota opcional.
- **Auth:** `Bearer JWT`
- **Roles:** `ADMIN` o `CHEF`.

**Request (ejemplo):**

```json
{
  "status": "PREPARING",
  "note": "Pedido prioritario por horario del cliente"
}
```

**Response 200 (ejemplo simplificado):**

```json
{
  "id": 42,
  "status": "PREPARING",
  "fulfillment": "DELIVERY",
  "totalAmount": "520.00",
  "currency": "UYU",
  "createdAt": "2025-11-12T14:32:10Z",
  "updatedAt": "2025-11-12T14:40:00Z"
}
```

Si la transición no es válida para el estado actual, el backend responde:

- **HTTP 400** con código de dominio (ej.: `INVALID_STATUS_TRANSITION`).
- Registro `WARN` en logs: `InvalidOrderStatusTransition orderId=... from=... to=... by=...`.

---

### ⚠️ Errores de negocio y formato de error

Todos los errores de negocio pasan por el `GlobalExceptionHandler` y utilizan el modelo `ApiError`:

```json
{
  "timestamp": "2025-11-12T15:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Debe agregar al menos un ítem.",
  "path": "/api/orders",
  "code": "ORDER_ITEMS_EMPTY"
}
```

Principales códigos de error relacionados a pedidos:

- `ORDER_ITEMS_EMPTY` → no se envió ningún ítem en el pedido.
- `INVALID_QUANTITY` → alguna cantidad es menor a 1.
- `FULFILLMENT_REQUIRED` → no se indicó `fulfillment`.
- `DELIVERY_ADDRESS_REQUIRED` → falta información obligatoria de envío cuando `fulfillment=DELIVERY`.
- `PRODUCT_NOT_FOUND` → algún `productId` no existe.
- `VARIANT_NOT_FOUND` → algún `productVariantId` no existe.
- `VARIANT_MISMATCH` → la variante no pertenece al producto indicado.
- `PRICE_NOT_FOUND` → no hay precio vigente para una variante.
- `ORDER_NOT_FOUND` → pedido inexistente o no perteneciente al usuario.
- `STATUS_REQUIRED` → se intentó cambiar el estado sin indicar `status`.
- `INVALID_STATUS_TRANSITION` → transición de estado no permitida según las reglas de negocio.

Además:

- Errores de validación (`@Valid`, `@NotNull`, etc.) devuelven **400** con estructura:
  ```json
  {
    "timestamp": "2025-11-12T15:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/orders",
    "fields": {
      "shipping.name": "no debe estar vacío",
      "items[0].quantity": "debe ser mayor o igual a 1"
    }
  }
  ```
  Este formato es consumido por el frontend para mostrar errores inline (ej.: `NewOrder.jsx`).

- Errores de permisos (`AccessDeniedException`) devuelven **403** con `code="ACCESS_DENIED"`.
- Cualquier error inesperado pasa por el handler genérico y devuelve **500** con mensaje controlado
  (sin exponer el stacktrace al cliente).

---

## 👥 Gestión de Usuarios - Admin (Sprint 6)

Esta sección documenta los endpoints de gestión de usuarios para el panel administrativo, implementados en el Sprint 6.

> **Nota:** Todos los endpoints requieren rol `ADMIN` y autenticación JWT.

### 🔌 Endpoints de gestión de usuarios

#### 1. Listar usuarios (Admin)

- **Método/Path:** `GET /api/admin/users`
- **Quién lo usa:** Panel administrativo (solo ADMIN).
- **Descripción:** Lista paginada de usuarios con búsqueda y filtros avanzados.
- **Auth:** `Bearer JWT`
- **Roles:** `ADMIN`

**Parámetros de filtro (todos opcionales):**

- `q` → búsqueda por email, nombre, apellido o teléfono (case-insensitive)
- `roles` → filtrar por roles (multi-select, OR lógico). Ej: `ADMIN,CHEF`
- `isActive` → filtrar por estado activo/inactivo (`true` o `false`)
- `hasPendingOrders` → usuarios con/sin pedidos pendientes (`true` o `false`)
- `page` → número de página (0-based, default: 0)
- `size` → tamaño de página (default: 20, max: 100)
- `sort` → ordenamiento (ej: `email,asc` o `createdAt,desc`)

**Ejemplo de uso:**

```http
GET /api/admin/users?q=juan&roles=CUSTOMER&isActive=true&hasPendingOrders=false&page=0&size=20&sort=email,asc
Authorization: Bearer <ADMIN_TOKEN>
```

**Response 200 (ejemplo):**

```json
{
  "content": [
    {
      "id": 15,
      "cognitoUserId": "abc123-def456-...",
      "email": "juan.perez@example.com",
      "firstName": "Juan",
      "lastName": "Pérez",
      "phone": "+59899123456",
      "isActive": true,
      "roles": ["CUSTOMER"],
      "hasPendingOrders": false
    },
    {
      "id": 23,
      "cognitoUserId": "xyz789-uvw012-...",
      "email": "juana.garcia@example.com",
      "firstName": "Juana",
      "lastName": "García",
      "phone": "+59899654321",
      "isActive": true,
      "roles": ["CUSTOMER", "CHEF"],
      "hasPendingOrders": true
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1
}
```

**Errores comunes:**

- **401 Unauthorized:** Token JWT inválido o ausente
- **403 Forbidden:** Usuario no tiene rol ADMIN

**Notas técnicas:**

- "Pedidos pendientes" se define como: pedidos con `status NOT IN ('DELIVERED', 'CANCELLED')`
- La búsqueda por texto (`q`) aplica a: email, firstName, lastName y phone
- El filtro de roles es inclusivo (OR): un usuario con múltiples roles aparecerá si tiene al menos uno de los roles especificados
- El tamaño máximo de página está limitado a 100 para evitar queries excesivas

---

## ❗ Errores típicos en el flujo de Chef

Esta sección resume los errores más frecuentes desde la perspectiva de la **vista de Chef** y del
panel administrativo, alineados con las respuestas reales del backend.

### 1. TRANSICIÓN_INVALIDA (HTTP 400)

**Escenario:**  
Intentar cambiar un pedido desde un estado no permitido por la lógica de negocio  
(p. ej. `CREATED -> DELIVERED` directamente).

**Request de ejemplo:**

```http
PATCH /api/orders/42/status HTTP/1.1
Authorization: Bearer <token-con-rol-chef>
Content-Type: application/json

{
  "status": "DELIVERED",
  "note": "Marcado como entregado desde cocina"
}
```

**Response:**

```json
{
  "timestamp": "2025-11-23T15:32:10.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "No se puede pasar de CREATED a DELIVERED.",
  "path": "/api/orders/42/status",
  "code": "INVALID_STATUS_TRANSITION"
}
```

---

### 2. PEDIDO_NO_ENCONTRADO (HTTP 404)

**Escenario:**  
Intentar operar sobre un pedido inexistente o, en el caso de cliente, que no le pertenece.

**Request de ejemplo:**

```http
PATCH /api/orders/999999/status HTTP/1.1
Authorization: Bearer <token-con-rol-chef>
Content-Type: application/json

{
  "status": "READY"
}
```

**Response:**

```json
{
  "timestamp": "2025-11-23T15:40:55.987Z",
  "status": 404,
  "error": "Not Found",
  "message": "Pedido not encontrado.",
  "path": "/api/orders/999999/status",
  "code": "ORDER_NOT_FOUND"
}
```

> Nota: En algunos casos de seguridad (por ejemplo, un cliente intentando acceder a un pedido de otro usuario), también se devuelve `ORDER_NOT_FOUND` para no filtrar información sobre la existencia del recurso.

---

### 3. PEDIDO_NO_VISIBLE_PARA_ROL (HTTP 403)

**Escenario:**  
Intentar cambiar el estado de un pedido sin tener el rol adecuado (por ejemplo, un usuario sin rol `ADMIN`/`CHEF` llamando al endpoint de cambio de estado).

**Request de ejemplo:**

```http
PATCH /api/orders/42/status HTTP/1.1
Authorization: Bearer <token-sin-rol-chef-ni-admin>
Content-Type: application/json

{
  "status": "PREPARING"
}
```

**Response:**

```json
{
  "timestamp": "2025-11-23T15:45:02.456Z",
  "status": 403,
  "error": "Forbidden",
  "message": "No tiene permisos para realizar esta acción.",
  "path": "/api/orders/42/status",
  "code": "ACCESS_DENIED"
}
```

> En la documentación funcional podés referirte a este caso como  
> **“PEDIDO_NO_VISIBLE_PARA_ROL”**, aunque el `code` técnico devuelto por la API sea `ACCESS_DENIED`.

---

### Resumen de códigos de error relevantes para Chef

| Caso funcional                | HTTP | `code` técnico              |
|------------------------------|------|-----------------------------|
| TRANSICIÓN_INVALIDA          | 400  | `INVALID_STATUS_TRANSITION` |
| PEDIDO_NO_ENCONTRADO         | 404  | `ORDER_NOT_FOUND`           |
| PEDIDO_NO_VISIBLE_PARA_ROL   | 403  | `ACCESS_DENIED`             |

---

## 🔄 CI/CD

**GitHub Actions** ejecuta:
1. **Lint de commits** (Conventional Commits) en `push` y `pull_request`.
2. **Spotless Check** (`./mvnw -B -ntp spotless:check`).
3. **Tests** (`./mvnw -B -ntp test`).
4. **Package** (sin saltar tests).
5. **Deploy a EC2** (SSH + `systemd`) en `main`.

### Variables y Secrets requeridos (GitHub → Settings)
- **Secrets**
  - `EC2_HOST` → IP o hostname
  - `EC2_USER` → usuario con sudo (p.ej. `ubuntu`)
  - `EC2_SSH_KEY` → clave privada **PEM** (contenido)
  - `EC2_SERVICE_NAME` → nombre del servicio `systemd` (p.ej. `cocinadelicia.service`)
  - `DEPLOY_DIR` *(opcional)* → default: `/opt/cocinadelicia/backend`
- **Opcionales** (si usás OIDC u otros)
  - `AWS_REGION` si integrás otros pasos (no requerido para SSH puro)

> El pipeline **falla** si:
> - El mensaje de commit no respeta convención.
> - `spotless:check` detecta formato incorrecto.
> - Tests fallan o el servicio no queda `active` en EC2.

---

## ☁️ Despliegue

**Estrategia actual:** EC2 con Java 17, JAR como servicio **systemd**, Nginx (o ALB) al frente.

- **Ruta de despliegue remoto** (ej.): `/opt/cocinadelicia/backend`
  - `releases/` → versiones fechadas
  - `current.jar` → symlink al release activo
- **Reinicio**:
  ```bash
  sudo systemctl daemon-reload
  sudo systemctl restart <EC2_SERVICE_NAME>
  sudo systemctl status <EC2_SERVICE_NAME> --no-pager
  ```
- Healthcheck: GET /actuator/health en la propia instancia.

  >Seguridad:
  >
  >- Restringí SG a IPs de administración.
  >
  >- Logs con journalctl -u <service> y/o CloudWatch (futuro).

---

## 🧰 Troubleshooting

* **No arranca por datasource:** revisá `SPRING_DATASOURCE_URL` y credenciales
* **CORS bloquea llamadas desde frontend:** actualizá `ALLOWED_ORIGINS`
* **Swagger no carga:** revisá dependencia springdoc y ruta `/swagger-ui.html`
* **Errores de encoding/zonas horarias:** agregá `serverTimezone=UTC` en la URL JDBC
* **Falla Spotless en CI (verify)**: corré `./mvnw spotless:apply` localmente y commiteá los cambios.
* **`systemd` queda en `failed`**: inspeccioná logs con:
  ```bash
  sudo journalctl -u ${EC2_SERVICE_NAME} -n 200 --no-pager
    ```
* **`current.jar` apunta a un destino inexistente:** recrear symlink:
* ```bash
  sudo ln -sfn /opt/cocinadelicia/backend/releases/<archivo.jar> /opt/cocinadelicia/backend/current.jar
  sudo chown -h app:app /opt/cocinadelicia/backend/current.jar
  ```

---

## 📄 Licencia

Proyecto **privado** por el momento. **All rights reserved © Eduardo González**.

---

## 📬 Contacto

* Autor: **Eduardo González**
* Sitio/Portfolio: *(por definir)*
* Email: *(por definir)*
