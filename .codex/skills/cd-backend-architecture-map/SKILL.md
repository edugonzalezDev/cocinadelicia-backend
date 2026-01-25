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
