# Access Matrix – Cocina DeLicia Backend

| Endpoint (glob) | Métodos | Rol requerido | Comentario |
| --- | --- | --- | --- |
| `/api/catalog/**` | GET | Público | Catálogo público y assets |
| `/api/orders/**` (cliente) | GET/POST/PATCH | Authenticated (cliente) | Requiere JWT válido; reglas de ownership en servicio |
| `/api/orders/{ops,admin,chef}/**` | GET | ADMIN o CHEF | Listado/gestión backoffice |
| `/api/admin/catalog/**` | ALL | ADMIN | CRUD de catálogo (productos, variantes, modifiers) |
| `/api/admin/modifier-groups/**` | ALL | ADMIN | CRUD modifiers (ruta técnica) |
| `/api/admin/orders/**` | ALL | ADMIN | Gestión de pedidos admin |
| `/chef/**` | ALL | ADMIN o CHEF | Rutas legacy para staff |
| `/admin/**` | ALL | ADMIN | Legacy admin (no API) |
| `/ws/**` | ALL | Público | WebSocket handshake |
| `/actuator/health`, `/v3/api-docs/**`, `/swagger-ui/**`, `/h2-console/**` | ALL | Público | Dev/ops |
| `/api/**` (resto) | ALL | Authenticated | Restringido a usuarios con JWT |
