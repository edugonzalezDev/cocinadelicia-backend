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
- `mvn test`
- No logging de URLs presign completas si contienen query sensitive (firma)
