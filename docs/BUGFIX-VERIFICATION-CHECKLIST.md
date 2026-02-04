# Checklist de Verificación - Bugfix NULL changed_by

## ✅ Correcciones Implementadas

### 1. AdminUserServiceImpl.java
- ✅ Método `logAuditAction()` actualizado (líneas 698-733)
  - Valida `changedBy` y usa "UNKNOWN" como fallback
  - Agrega logging de warning cuando `changedBy` es null/blank
  - Evita violación de constraint NOT NULL en columna `changed_by`

### 2. AdminUserController.java  
- ✅ Método helper `extractEmailFromJwt()` agregado (líneas 621-655)
  - Implementa estrategia de fallback: email → preferred_username → sub
  - Logging de warning/error según el claim usado
  - Garantiza retorno no-null
  
- ✅ Método `updateRoles()` actualizado (línea 377)
  - Usa `extractEmailFromJwt(jwt)` en lugar de `jwt.getClaim("email")`
  
- ✅ Método `updateStatus()` actualizado (línea 456)
  - Usa `extractEmailFromJwt(jwt)` en lugar de `jwt.getClaim("email")`

### 3. Documentación
- ✅ Creado `docs/BUGFIX-NULL-CHANGED-BY-AUDIT-LOG.md` con detalles completos

## 🧪 Tests en Ejecución

Tests ejecutándose: `AdminUserControllerTest`

Estos tests deberían pasar sin cambios ya que el comportamiento es backward-compatible.

## 🔍 Verificación Manual Recomendada

### Paso 1: Compilar y Arrancar la Aplicación

```bash
cd "e:\Spring Boot\cocinadelicia-backend"
./mvnw.cmd clean package -DskipTests
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

Verificar en logs:
- ✅ Aplicación arranca sin errores
- ✅ No aparecen warnings relacionados con `changedBy`

### Paso 2: Probar desde el Frontend

1. **Login como ADMIN**
   - Usuario: admin@cocinadelicia.com (o el que tengas configurado)

2. **Navegar a Gestión de Usuarios**
   - Menú → Admin → Usuarios

3. **Cambiar Roles de un Usuario**
   - Seleccionar un usuario (ej: userId=41)
   - Editar roles (ej: agregar rol CHEF o COURIER)
   - Guardar cambios
   - **Verificar**: No debe aparecer error 500
   - **Verificar**: Debe aparecer mensaje de éxito

4. **Verificar en Logs del Backend**
   ```bash
   tail -f logs/cdd-app.log | grep -E "(updateRoles|Audit log created)"
   ```
   
   Deberías ver algo como:
   ```
   [INFO] AdminUserController - AdminUserController.updateRoles called for userId=41 newRoles=[CUSTOMER, CHEF] by=admin@cocinadelicia.com
   [INFO] AdminUserServiceImpl - Audit log created: userId=41 action=ROLE_CHANGED by=admin@cocinadelicia.com
   [INFO] AdminUserController - Roles updated successfully for userId=41
   ```

5. **Verificar Auditoría en Base de Datos**
   ```sql
   SELECT * FROM user_audit_log 
   WHERE user_id = 41 
   ORDER BY changed_at DESC 
   LIMIT 5;
   ```
   
   El campo `changed_by` debe contener el email del admin (no "UNKNOWN").

### Paso 3: Probar Endpoint de Auditoría

Desde Swagger o Postman:

```http
GET /api/admin/users/41/audit-log?page=0&size=10
Authorization: Bearer {tu_token_jwt}
```

Verificar que la respuesta incluya los cambios de roles con el `changedBy` correcto.

### Paso 4: Verificar Manejo de JWT sin claim email (Opcional)

Si quieres probar el fallback, puedes:

1. Modificar temporalmente la configuración de Cognito para no incluir el claim `email`
2. Obtener un nuevo token
3. Intentar cambiar roles
4. Verificar en logs que aparece:
   ```
   [WARN] AdminUserController - JWT missing 'email' claim, using 'preferred_username': {username}
   ```

## 🚨 Casos de Error Esperados

### Si aparece "UNKNOWN" en changed_by:
- **Causa**: JWT no tiene ninguno de los claims esperados (email, preferred_username, sub)
- **Acción**: Revisar configuración de Cognito User Pool
- **Log esperado**: `JWT missing all expected identity claims`

### Si aparece warning "changedBy is null/blank":
- **Causa**: El método `extractEmailFromJwt()` retornó null (no debería pasar)
- **Acción**: Revisar que el método helper siempre devuelve un valor no-null

## ✨ Resultado Esperado

✅ **El error 500 debe estar completamente resuelto**

Antes:
```
PUT /api/admin/users/41/roles → 500 Internal Server Error
SQL Error: NULL not allowed for column "changed_by"
```

Después:
```
PUT /api/admin/users/41/roles → 200 OK
Audit log created with changed_by = admin@cocinadelicia.com
```

## 📊 Monitoreo Post-Deploy

Comandos útiles para monitorear:

```bash
# Ver logs en tiempo real
tail -f logs/cdd-app.log

# Buscar warnings de fallback (idealmente no deberían aparecer)
grep "JWT missing 'email' claim" logs/cdd-app.log

# Buscar warnings de changedBy null (no deberían aparecer)
grep "changedBy is null/blank" logs/cdd-app.log

# Buscar errores críticos de JWT
grep "JWT missing all expected identity claims" logs/cdd-app.log

# Ver todas las operaciones de cambio de roles
grep "ROLE_CHANGED" logs/cdd-app.log | tail -20
```

## 📝 Notas Adicionales

- **Backward Compatible**: Los tests existentes deberían pasar sin cambios
- **Sin Migración DB**: No se requiere ningún cambio en la base de datos
- **Sin Cambios Frontend**: El cliente funciona sin modificaciones
- **Manejo Robusto**: Implementa múltiples niveles de fallback para garantizar que nunca se inserte NULL

---

**Fecha**: 2026-02-04  
**Branch**: feature/S06-admin-users-panel  
**Archivos modificados**: 2  
**Tests**: En ejecución...
