-- =========================================
-- V9__add_marketing_flags_to_product_variant.sql
-- Agrega flags de visibilidad/marketing a product_variant
-- Compatible H2 / MySQL 8+
-- =========================================

ALTER TABLE product_variant
  ADD COLUMN is_featured BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE product_variant
  ADD COLUMN is_daily_menu BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE product_variant
  ADD COLUMN is_new BOOLEAN NOT NULL DEFAULT FALSE;
