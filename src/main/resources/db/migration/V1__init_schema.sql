-- =========================================
-- Cocina DeLicia - V1 Schema (H2/MySQL 8+)
-- Convenciones: snake_case, soft delete, CHECK enums
-- =========================================

-- (Opcional) crea el schema si lo usas explícito
-- CREATE SCHEMA IF NOT EXISTS cocinadelicia;
-- SET SCHEMA cocinadelicia;

-- ===============================
-- Helpers ENUM (via VARCHAR + CHECK)
-- ===============================
-- Notas:
-- - MySQL 8 aplica CHECK; H2 también.
-- - Evitamos tipos ENUM nativos para portabilidad.

-- ===============================
-- Catálogo
-- ===============================

CREATE TABLE IF NOT EXISTS category (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(191) NOT NULL UNIQUE,
  slug             VARCHAR(191) NOT NULL UNIQUE,
  description      TEXT,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tag (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(191) NOT NULL UNIQUE,
  slug             VARCHAR(191) NOT NULL UNIQUE,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  category_id      BIGINT NOT NULL,
  name             VARCHAR(191) NOT NULL,
  slug             VARCHAR(191) NOT NULL UNIQUE,
  description      TEXT,
  tax_rate_percent DECIMAL(5,2) NOT NULL,
  is_active        BOOLEAN NOT NULL DEFAULT TRUE,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP,
  CONSTRAINT fk_product_category
    FOREIGN KEY (category_id) REFERENCES category(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS product_tag (
  product_id       BIGINT NOT NULL,
  tag_id           BIGINT NOT NULL,
  CONSTRAINT pk_product_tag PRIMARY KEY (product_id, tag_id),
  CONSTRAINT fk_pt_product FOREIGN KEY (product_id) REFERENCES product(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_pt_tag FOREIGN KEY (tag_id) REFERENCES tag(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS product_variant (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id       BIGINT NOT NULL,
  sku              VARCHAR(191) UNIQUE,
  name             VARCHAR(191) NOT NULL,
  is_active        BOOLEAN NOT NULL DEFAULT TRUE,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP,
  CONSTRAINT fk_variant_product
    FOREIGN KEY (product_id) REFERENCES product(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- currency_code: UYU, USD
CREATE TABLE IF NOT EXISTS price_history (
  id                 BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_variant_id BIGINT NOT NULL,
  price              DECIMAL(10,2) NOT NULL,
  currency           VARCHAR(3) NOT NULL,
  valid_from         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  valid_to           TIMESTAMP,
  created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at         TIMESTAMP,
  CONSTRAINT fk_ph_variant FOREIGN KEY (product_variant_id) REFERENCES product_variant(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT chk_currency CHECK (currency IN ('UYU','USD'))
);

CREATE INDEX IF NOT EXISTS idx_ph_variant_from ON price_history (product_variant_id, valid_from);
CREATE INDEX IF NOT EXISTS idx_ph_variant_to   ON price_history (product_variant_id, valid_to);

-- ===============================
-- Usuarios y Roles (Cognito + BD)
-- ===============================

CREATE TABLE IF NOT EXISTS app_user (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  cognito_user_id  VARCHAR(191) NOT NULL UNIQUE,
  first_name       VARCHAR(191) NOT NULL,
  last_name        VARCHAR(191) NOT NULL,
  email            VARCHAR(191) NOT NULL UNIQUE,
  phone            VARCHAR(50),
  is_active        BOOLEAN NOT NULL DEFAULT TRUE,
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP
);

-- role_name: ADMIN, CHEF, COURIER, CUSTOMER
CREATE TABLE IF NOT EXISTS role (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(50) NOT NULL UNIQUE,
  description      VARCHAR(255),
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP,
  CONSTRAINT chk_role_name CHECK (name IN ('ADMIN','CHEF','COURIER','CUSTOMER'))
);

CREATE TABLE IF NOT EXISTS user_role (
  user_id          BIGINT NOT NULL,
  role_id          BIGINT NOT NULL,
  assigned_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES app_user(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES role(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- ===============================
-- Pedidos / Checkout
-- ===============================

CREATE TABLE IF NOT EXISTS customer_address (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  label            VARCHAR(100),
  line1            VARCHAR(191) NOT NULL,
  line2            VARCHAR(191),
  city             VARCHAR(100) NOT NULL,
  region           VARCHAR(100),
  postal_code      VARCHAR(20),
  reference        VARCHAR(191),
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP,
  CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES app_user(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- order_status: CREATED, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELED
-- fulfillment_type: PICKUP, DELIVERY
-- currency_code: UYU, USD
CREATE TABLE IF NOT EXISTS customer_order (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT NOT NULL,
  status           VARCHAR(30) NOT NULL,
  fulfillment      VARCHAR(30) NOT NULL,
  currency         VARCHAR(3)  NOT NULL,
  subtotal_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  tax_amount       DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  discount_amount  DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  total_amount     DECIMAL(10,2) NOT NULL DEFAULT 0.00,

  ship_name        VARCHAR(191),
  ship_phone       VARCHAR(50),
  ship_line1       VARCHAR(191),
  ship_line2       VARCHAR(191),
  ship_city        VARCHAR(100),
  ship_region      VARCHAR(100),
  ship_postal_code VARCHAR(20),
  ship_reference   VARCHAR(191),

  notes            TEXT,

  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP,

  CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES app_user(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT chk_order_status CHECK (
    status IN ('CREATED','CONFIRMED','PREPARING','READY','OUT_FOR_DELIVERY','DELIVERED','CANCELED')
  ),
  CONSTRAINT chk_fulfillment CHECK (fulfillment IN ('PICKUP','DELIVERY')),
  CONSTRAINT chk_order_currency CHECK (currency IN ('UYU','USD'))
);

CREATE INDEX IF NOT EXISTS idx_order_user_created   ON customer_order (user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_order_status_created ON customer_order (status, created_at);

CREATE TABLE IF NOT EXISTS order_item (
  id                 BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id           BIGINT NOT NULL,
  product_id         BIGINT NOT NULL,
  product_variant_id BIGINT NOT NULL,
  product_name       VARCHAR(191) NOT NULL,
  variant_name       VARCHAR(191) NOT NULL,
  unit_price         DECIMAL(10,2) NOT NULL,
  quantity           INT NOT NULL DEFAULT 1,
  line_total         DECIMAL(10,2) NOT NULL,
  created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at         TIMESTAMP,
  CONSTRAINT fk_item_order   FOREIGN KEY (order_id) REFERENCES customer_order(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_item_variant FOREIGN KEY (product_variant_id) REFERENCES product_variant(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_item_order ON order_item (order_id);

-- payment_method: CASH, BANK_TRANSFER, MERCADOPAGO, CREDIT_CARD, DEBIT_CARD
-- payment_status: PENDING, AUTHORIZED, PAID, FAILED, REFUNDED, CANCELED
-- currency_code: UYU, USD
CREATE TABLE IF NOT EXISTS payment (
  id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id         BIGINT NOT NULL,
  method           VARCHAR(30) NOT NULL,
  status           VARCHAR(30) NOT NULL,
  amount           DECIMAL(10,2) NOT NULL,
  currency         VARCHAR(3) NOT NULL,
  provider_tx_id   VARCHAR(191),
  note             VARCHAR(255),
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at       TIMESTAMP,
  CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES customer_order(id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT chk_payment_method CHECK (
    method IN ('CASH','BANK_TRANSFER','MERCADOPAGO','CREDIT_CARD','DEBIT_CARD')
  ),
  CONSTRAINT chk_payment_status CHECK (
    status IN ('PENDING','AUTHORIZED','PAID','FAILED','REFUNDED','CANCELED')
  ),
  CONSTRAINT chk_payment_currency CHECK (currency IN ('UYU','USD'))
);

CREATE INDEX IF NOT EXISTS idx_payment_order         ON payment (order_id);
CREATE INDEX IF NOT EXISTS idx_payment_status_created ON payment (status, created_at);
