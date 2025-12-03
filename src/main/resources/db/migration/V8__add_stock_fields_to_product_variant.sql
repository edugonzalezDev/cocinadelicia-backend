-- =========================================
-- V8__add_stock_fields_to_product_variant.sql
-- Agrega campos de stock a product_variant
-- Compatible H2 / MySQL 8+
-- =========================================

ALTER TABLE product_variant
  ADD COLUMN manages_stock BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE product_variant
  ADD COLUMN stock_quantity INT NOT NULL DEFAULT 0;

-- Opcional pero recomendado: evitar cantidades negativas
ALTER TABLE product_variant
  ADD CONSTRAINT chk_variant_stock_quantity_non_negative
    CHECK (stock_quantity >= 0);
