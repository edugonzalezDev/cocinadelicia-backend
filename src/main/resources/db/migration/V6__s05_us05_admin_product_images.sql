-- S05.US05: asegurar invariantes de orden y indices de imagenes
ALTER TABLE product_image
  DROP CONSTRAINT IF EXISTS chk_product_image_sort_order_non_negative;

ALTER TABLE product_image
  ADD CONSTRAINT chk_product_image_sort_order_non_negative
  CHECK (sort_order >= 0);

CREATE INDEX IF NOT EXISTS idx_product_image_product ON product_image (product_id);
CREATE INDEX IF NOT EXISTS idx_product_image_main ON product_image (product_id, is_main);
CREATE INDEX IF NOT EXISTS idx_product_image_sort ON product_image (product_id, sort_order);
