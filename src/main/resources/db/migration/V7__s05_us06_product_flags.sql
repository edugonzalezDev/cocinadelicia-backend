-- S05.US06: product flags as source of truth
ALTER TABLE product
  ADD COLUMN IF NOT EXISTS is_featured BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE product
  ADD COLUMN IF NOT EXISTS is_daily_menu BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE product
  ADD COLUMN IF NOT EXISTS is_new BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE product p
SET
  is_featured = CASE
    WHEN EXISTS (
      SELECT 1 FROM product_variant v
      WHERE v.product_id = p.id
        AND v.is_featured = TRUE
        AND v.deleted_at IS NULL
    ) THEN TRUE ELSE FALSE END,
  is_daily_menu = CASE
    WHEN EXISTS (
      SELECT 1 FROM product_variant v
      WHERE v.product_id = p.id
        AND v.is_daily_menu = TRUE
        AND v.deleted_at IS NULL
    ) THEN TRUE ELSE FALSE END,
  is_new = CASE
    WHEN EXISTS (
      SELECT 1 FROM product_variant v
      WHERE v.product_id = p.id
        AND v.is_new = TRUE
        AND v.deleted_at IS NULL
    ) THEN TRUE ELSE FALSE END;
