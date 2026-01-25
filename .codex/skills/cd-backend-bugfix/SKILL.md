---
name: cd-backend-bugfix
description: Corregir un bug en backend con enfoque reproducible- identificar causa raíz, agregar test de regresión, fix incremental.
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
    - `mvn test`
    - Checklist de impacto (seguridad, DB, websockets)

## Reglas
- No refactor masivo.
- Si el bug es de datos: proponer migration o fix de seed (V2__seed_demo.sql) solo si aplica a tests.
