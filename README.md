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
