# Bugfix: Error 500 al actualizar roles de usuario

**Fecha:** 2026-02-04  
**Skill:** cd-backend-bugfix  
**Issue:** Error 500 (Internal Server Error) al intentar actualizar roles desde el frontend

## Problema Original

Al intentar cambiar roles de usuario desde el frontend, se generaba un error 500:

```
PUT https://192.168.1.4:8443/api/admin/users/41/roles 500 (Internal Server Error)
```

### Causa Raíz

El error ocurría en la capa de auditoría al intentar insertar un registro en `user_audit_log`:

```sql
SQL Error: 23502, SQLState: 23502
La columna "changed_by" no permite valores nulos (NULL)
NULL not allowed for column "changed_by"
```

El problema era que:
1. El método `AdminUserController.updateRoles()` extraía el email del JWT usando `jwt.getClaim("email")`
2. Si el claim `email` no existía en el token, devolvía `null`
3. El `null` se pasaba al método `AdminUserServiceImpl.updateRoles()` como `performedBy`
4. Al registrar la auditoría con `logAuditAction()`, se intentaba guardar `changedBy = null`
5. La columna `changed_by` tiene restricción `NOT NULL`, causando el error SQL

## Solución Implementada

Se aplicaron **3 correcciones** según las mejoras solicitadas:

### 1. Validación en `logAuditAction()` (AdminUserServiceImpl.java)

**Archivo:** `src/main/java/com/cocinadelicia/backend/user/service/impl/AdminUserServiceImpl.java`  
**Líneas:** 698-741

```java
private void logAuditAction(Long userId, String action, String changedBy, String details) {
  try {
    // Usar "UNKNOWN" si changedBy es null o vacío
    String effectiveChangedBy =
        (changedBy != null && !changedBy.isBlank()) ? changedBy : "UNKNOWN";

    // Log warning si el changedBy era null
    if (changedBy == null || changedBy.isBlank()) {
      log.warn(
          "changedBy is null/blank for audit action userId={} action={}. Using 'UNKNOWN'. "
              + "Check JWT email claim extraction.",
          userId,
          action);
    }

    UserAuditLog auditLog =
        UserAuditLog.builder()
            .userId(userId)
            .action(action)
            .changedBy(effectiveChangedBy)  // ✅ Nunca null
            .details(details)
            .build();

    userAuditLogRepository.save(auditLog);
    log.info("Audit log created: userId={} action={} by={}", userId, action, effectiveChangedBy);
    // ...
  }
}
```

**Beneficios:**
- ✅ Evita violación de constraint `NOT NULL`
- ✅ Registra warning en logs para diagnóstico
- ✅ Usa valor por defecto "UNKNOWN" cuando no hay información del usuario

### 2. Método helper `extractEmailFromJwt()` (AdminUserController.java)

**Archivo:** `src/main/java/com/cocinadelicia/backend/user/controller/AdminUserController.java`  
**Líneas:** 622-659

```java
/**
 * Extrae el email del JWT para auditoría.
 *
 * Intenta obtener el email en el siguiente orden:
 * 1. Claim "email"
 * 2. Claim "preferred_username"
 * 3. Claim "sub" (último recurso)
 *
 * @param jwt token JWT
 * @return email o identificador del usuario, nunca null
 */
private String extractEmailFromJwt(org.springframework.security.oauth2.jwt.Jwt jwt) {
  String email = jwt.getClaim("email");
  if (email != null && !email.isBlank()) {
    return email;
  }

  String preferredUsername = jwt.getClaim("preferred_username");
  if (preferredUsername != null && !preferredUsername.isBlank()) {
    log.warn("JWT missing 'email' claim, using 'preferred_username': {}", preferredUsername);
    return preferredUsername;
  }

  String sub = jwt.getSubject();
  if (sub != null && !sub.isBlank()) {
    log.warn("JWT missing 'email' and 'preferred_username' claims, using 'sub': {}", sub);
    return sub;
  }

  log.error("JWT missing all expected identity claims (email, preferred_username, sub)");
  return "UNKNOWN_JWT_USER";
}
```

**Beneficios:**
- ✅ Implementa estrategia de fallback robusta
- ✅ Logging detallado cuando usa claims alternativos
- ✅ Garantiza que el método siempre devuelve un valor no-null
- ✅ Compatible con diferentes configuraciones de Cognito

### 3. Actualización de métodos del controller

**Archivos modificados:**
- `updateRoles()` - línea 377
- `updateStatus()` - línea 456

**Antes:**
```java
String performedBy = jwt.getClaim("email");  // ❌ Puede ser null
```

**Después:**
```java
String performedBy = extractEmailFromJwt(jwt);  // ✅ Nunca null
```

**Beneficios:**
- ✅ Usa el método helper con fallbacks
- ✅ Garantiza que `performedBy` nunca sea null
- ✅ Mejora diagnóstico con logging cuando se usan claims alternativos

## Testing

### Tests afectados (deben pasar sin cambios)

1. **AdminUserControllerTest**
   - `updateRoles_happyPath_returns200()`
   - `updateRoles_promoteToAdminWithConfirmation_returns200()`
   - `updateRoles_userNotFound_returns404()`
   - `updateRoles_emptyRoles_returns400()`
   - `updateStatus_activateUser_returns200()`

2. **AdminUserServiceImplTest**
   - `updateRoles_happyPath_shouldUpdateRolesAndSyncCognito()`
   - `updateRoles_promoteToAdminWithoutConfirmation_shouldThrowBadRequest()`
   - `updateRoles_selfDemotionAttempt_shouldThrowBadRequest()`

### Verificación manual recomendada

1. **Iniciar aplicación** y verificar logs al arrancar (sin errores)
2. **Desde el frontend:**
   - Login como ADMIN
   - Ir a gestión de usuarios
   - Cambiar roles de un usuario (ej: agregar rol CHEF)
   - Verificar que la operación se completa sin error 500
3. **Verificar auditoría:**
   - Consultar endpoint `GET /api/admin/users/{id}/audit-log`
   - Verificar que el campo `changedBy` tiene el email del admin (no "UNKNOWN")
4. **Verificar logs de aplicación:**
   - Buscar líneas: `Audit log created: userId=... action=ROLE_CHANGED by=...`
   - No debería aparecer warning de "changedBy is null/blank" en condiciones normales

## Casos de Borde Manejados

| Escenario | Comportamiento |
|-----------|----------------|
| JWT tiene claim `email` | ✅ Usa email, operación normal |
| JWT sin `email`, con `preferred_username` | ⚠️ Usa `preferred_username`, log warning |
| JWT sin `email` ni `preferred_username`, con `sub` | ⚠️ Usa `sub`, log warning |
| JWT sin ningún claim de identidad | ❌ Usa "UNKNOWN_JWT_USER", log error |
| `performedBy` es string vacío | ⚠️ Usa "UNKNOWN", log warning |

## Compatibilidad

- ✅ **Cambios backward-compatible:** No rompe funcionalidad existente
- ✅ **Base de datos:** No requiere migración (solo usa valores por defecto)
- ✅ **Tests:** Todos los tests existentes deberían pasar sin cambios
- ✅ **Frontend:** No requiere cambios en el cliente

## Configuración de Cognito Recomendada

Para evitar usar fallbacks, asegurar que el User Pool de Cognito esté configurado para incluir el claim `email` en los tokens:

1. En Cognito User Pool → **App client settings**
2. Verificar **Token claims** incluye `email`
3. En **Attribute mappings**, mapear `email` al atributo correspondiente

## Monitoreo Post-Deploy

Monitorear logs para detectar:

```bash
# Warnings de fallback (no deberían aparecer en producción)
grep "JWT missing 'email' claim" logs/cdd-app.log

# Warnings de changedBy null (indica problema más grave)
grep "changedBy is null/blank" logs/cdd-app.log

# Errores críticos (JWT sin ningún claim)
grep "JWT missing all expected identity claims" logs/cdd-app.log
```

## Referencias

- Sprint 6 - US05: Actualizar roles de usuario
- Sprint 6 - US07: Auditoría de cambios de usuario
- Issue relacionado: Error 500 al sincronizar con Cognito (corregido previamente)
