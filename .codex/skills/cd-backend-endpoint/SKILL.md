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
- `mvn test` ok
- No hardcode de secrets/config
- Si hay schema: migration nueva en `db/migration`
