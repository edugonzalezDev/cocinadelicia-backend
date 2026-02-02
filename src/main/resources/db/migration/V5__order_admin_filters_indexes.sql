-- Indexes para filtros admin de ordenes
CREATE INDEX IF NOT EXISTS idx_order_item_product_id ON order_item (product_id);
CREATE INDEX IF NOT EXISTS idx_order_item_product_variant_id ON order_item (product_variant_id);
CREATE INDEX IF NOT EXISTS idx_order_fulfillment_requested_at
  ON customer_order (fulfillment, requested_at);
