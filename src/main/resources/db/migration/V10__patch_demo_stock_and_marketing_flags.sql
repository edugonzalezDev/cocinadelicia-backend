-- =========================================
-- V10__patch_demo_stock_and_marketing_flags.sql
-- Completa datos de stock y flags de marketing
-- para las variantes de ejemplo (IDs 1..8).
--
-- Pensado para entorno demo / dev, sin
-- tocar variantes futuras no seed.
-- =========================================

-- -----------------------------------------
-- 1) Stock demo para product_variant
-- -----------------------------------------
-- Empanadas: manejan stock real
UPDATE product_variant
SET manages_stock   = TRUE,
    stock_quantity  = 40
WHERE id = 1;  -- EMP-CARNE-U (Unidad)

UPDATE product_variant
SET manages_stock   = TRUE,
    stock_quantity  = 8
WHERE id = 2;  -- EMP-CARNE-DOC (Docena)

UPDATE product_variant
SET manages_stock   = TRUE,
    stock_quantity  = 36
WHERE id = 3;  -- EMP-CAP-U (Unidad)

-- Dejamos una docena sin stock para probar "Sin stock"
UPDATE product_variant
SET manages_stock   = TRUE,
    stock_quantity  = 0
WHERE id = 4;  -- EMP-CAP-DOC (Docena) -> Sin stock

-- Ñoquis / Ensalada / Milanesa: por defecto los tratamos
-- como "a pedido" (no manejan stock físico en este sprint)
UPDATE product_variant
SET manages_stock   = FALSE,
    stock_quantity  = 0
WHERE id IN (5, 7, 8);

-- Flan: manejamos stock real, pocas unidades
UPDATE product_variant
SET manages_stock   = TRUE,
    stock_quantity  = 6
WHERE id = 6;  -- FLAN-PORC (Porción)

-- Nota:
-- - Con esta configuración tendrás:
--   * Empanadas con stock real (incluyendo 1 variante sin stock).
--   * Flan con stock limitado.
--   * Ñoquis / Milanesa / Ensalada como "a pedido"
--     (no dependen de stock -> siempre "Disponible").


-- -----------------------------------------
-- 2) Flags de marketing para product_variant
-- -----------------------------------------
-- Objetivo:
-- - is_daily_menu: variantes que queremos mostrar en
--   la sección "Menú del día".
-- - is_featured: productos destacados en el catálogo.
-- - is_new: productos "nuevos" (principalmente los de V5).

-- 🔹 Menú del día (daily_menu)
-- Elegimos opciones representativas del día:
-- - Empanada de Carne unidad
-- - Ñoquis porción
-- - Milanesa Napolitana porción
UPDATE product_variant
SET is_daily_menu = TRUE
WHERE id IN (1, 5, 7);

-- 🔹 Destacados (featured)
-- Destacamos:
-- - Docena de carne
-- - Docena caprese
-- - Milanesa Napolitana porción
-- - Ensalada César porción
UPDATE product_variant
SET is_featured = TRUE
WHERE id IN (2, 4, 7, 8);

-- 🔹 Nuevos (is_new)
-- Los productos “nuevos” de V5:
-- - Milanesa Napolitana (id 7)
-- - Ensalada César (id 8)
UPDATE product_variant
SET is_new = TRUE
WHERE id IN (7, 8);

-- =========================================
-- Fin V10__patch_demo_stock_and_marketing_flags.sql
-- =========================================
