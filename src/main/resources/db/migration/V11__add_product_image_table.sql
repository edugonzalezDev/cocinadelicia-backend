-- =========================================
-- V11__add_product_image_table.sql
-- Define tabla product_image para galería de imágenes de productos
-- Compatible H2 / MySQL 8+
-- =========================================

CREATE TABLE IF NOT EXISTS product_image (
  id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id  BIGINT NOT NULL,

  -- Clave estable del objeto en S3 (ej: products/{productId}/{uuid}.webp)
  object_key  VARCHAR(512) NOT NULL,

  -- Marca de imagen principal por producto
  is_main     BOOLEAN NOT NULL DEFAULT FALSE,

  -- Orden visual de la galería (0,1,2,...)
  sort_order  INT NOT NULL DEFAULT 0,

  -- Auditoría y soft delete (alineado con BaseAudit)
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at  TIMESTAMP,

  CONSTRAINT fk_product_image_product
    FOREIGN KEY (product_id) REFERENCES product(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,

  -- Evitamos sort_order negativos
  CONSTRAINT chk_product_image_sort_order_non_negative
    CHECK (sort_order >= 0),

  -- Evitamos duplicar la misma key para un producto
  CONSTRAINT uk_product_image_product_key
    UNIQUE (product_id, object_key)
);

-- Índices de apoyo para consultas típicas
CREATE INDEX idx_product_image_product
  ON product_image (product_id);

CREATE INDEX idx_product_image_main
  ON product_image (product_id, is_main);

CREATE INDEX idx_product_image_sort
  ON product_image (product_id, sort_order);
