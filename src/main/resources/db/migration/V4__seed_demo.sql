-- =========================================
-- Cocina DeLicia - V2 Seed Demo (H2/MySQL 8+)
-- Inserta datos de ejemplo mínimos y consistentes
-- =========================================

-- ===============================
-- 1) Catálogo: categorías y tags
-- ===============================
INSERT INTO category (id, name, slug, description)
VALUES
  (1, 'Empanadas',  'empanadas',  'Empanadas caseras'),
  (2, 'Pastas',     'pastas',     'Pastas frescas y salsas'),
  (3, 'Postres',    'postres',    'Dulces y postres caseros')
;

INSERT INTO tag (id, name, slug)
VALUES
  (1, 'Veggie',   'veggie'),
  (2, 'Picante',  'picante'),
  (3, 'Sin TACC', 'sin-tacc')
;

-- ===============================
-- 2) Productos
-- ===============================
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active)
VALUES
  (1, 1, 'Empanada de Carne', 'empanada-carne', 'Carne cortada a cuchillo', 10.00, TRUE),
  (2, 1, 'Empanada Caprese',  'empanada-caprese','Mozzarella, tomate, albahaca', 10.00, TRUE),
  (3, 2, 'Ñoquis Caseros',    'noquis-caseros', 'Ñoquis de papa con salsas', 10.00, TRUE),
  (4, 3, 'Flan Casero',       'flan-casero',    'Flan de huevo con caramelo', 10.00, TRUE)
;

-- Product ↔ Tags
INSERT INTO product_tag (product_id, tag_id) VALUES
  (2, 1), -- Caprese -> Veggie
  (3, 1), -- Ñoquis -> Veggie (si salsa veggie)
  (1, 2)  -- Empanada carne -> Picante (variante picante opcional)
;

-- ===============================
-- 3) Variantes de producto
-- ===============================
INSERT INTO product_variant (id, product_id, sku, name, is_active)
VALUES
  (1, 1, 'EMP-CARNE-U',     'Unidad',      TRUE),
  (2, 1, 'EMP-CARNE-DOC',   'Docena',      TRUE),
  (3, 2, 'EMP-CAP-U',       'Unidad',      TRUE),
  (4, 2, 'EMP-CAP-DOC',     'Docena',      TRUE),
  (5, 3, 'NOQ-PORC',        'Porción 400g',TRUE),
  (6, 4, 'FLAN-PORC',       'Porción',     TRUE)
;

-- ===============================
-- 4) Historial de precios (UYU)
--    Precio vigente: valid_to NULL
-- ===============================
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to)
VALUES
  (1, 1,  95.00, 'UYU', CURRENT_TIMESTAMP, NULL),   -- Empanada carne unidad
  (2, 2, 990.00, 'UYU', CURRENT_TIMESTAMP, NULL),   -- Empanada carne docena
  (3, 3,  95.00, 'UYU', CURRENT_TIMESTAMP, NULL),   -- Caprese unidad
  (4, 4, 990.00, 'UYU', CURRENT_TIMESTAMP, NULL),   -- Caprese docena
  (5, 5, 290.00, 'UYU', CURRENT_TIMESTAMP, NULL),   -- Ñoquis porción
  (6, 6, 220.00, 'UYU', CURRENT_TIMESTAMP, NULL)    -- Flan porción
;

-- ===============================
-- 5) Usuarios y Roles
-- ===============================
-- Roles
INSERT INTO role (id, name, description)
VALUES
  (1, 'ADMIN',   'Administrador'),
  (2, 'CHEF',    'Cocinero'),
  (3, 'COURIER', 'Repartidor'),
  (4, 'CUSTOMER','Cliente')
;

-- Usuarios (cognito_user_id: placeholder; en prod se actualiza tras signup)
INSERT INTO app_user (id, cognito_user_id, first_name, last_name, email, phone, is_active)
VALUES
  (1, 'cog-admin-001', 'Eduardo', 'González', 'admin@cocinadelicia.test',  '+59800000000', TRUE),
  (2, 'cog-chef-001',  'Ana',     'Pérez',    'chef@cocinadelicia.test',   '+59800000001', TRUE),
  (3, 'cog-cli-001',   'Juan',    'López',    'juan@cocinadelicia.test',   '+59800000002', TRUE)
;

-- Asignación de roles
INSERT INTO user_role (user_id, role_id, assigned_at) VALUES
  (1, 1, CURRENT_TIMESTAMP), -- admin: ADMIN
  (2, 2, CURRENT_TIMESTAMP), -- chef: CHEF
  (3, 4, CURRENT_TIMESTAMP)  -- juan: CUSTOMER
;

-- ===============================
-- 6) Direcciones del cliente
-- ===============================
INSERT INTO customer_address (id, user_id, label, line1, line2, city, region, postal_code, reference)
VALUES
  (1, 3, 'Casa', 'Av. Italia 1234', NULL, 'Montevideo', 'Montevideo', '11300', 'Portón negro'),
  (2, 3, 'Trabajo', 'Colonia 555', 'Of. 301', 'Montevideo', 'Montevideo', '11000', 'Recepción 3er piso')
;

-- ===============================
-- 7) Pedido de ejemplo (DELIVERY)
-- ===============================
-- Totales: ejemplo simple con dos ítems
-- Ítem 1: Empanada carne docena (990.00)
-- Ítem 2: Flan porción (220.00)
-- Subtotal = 1210.00; IVA 10% = 121.00; Total = 1331.00 (solo ilustrativo)
INSERT INTO customer_order (
  id, user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_region, ship_postal_code, ship_reference,
  notes
) VALUES (
  1, 3, 'CREATED', 'DELIVERY', 'UYU',
  1210.00, 121.00, 0.00, 1331.00,
  'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo', 'Montevideo', '11300', 'Portón negro',
  'Entrega estimada 20:30'
);

-- Ítems del pedido (snapshot de nombres y precios)
INSERT INTO order_item (
  id, order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total
) VALUES
  (1, 1, 1, 2, 'Empanada de Carne', 'Docena', 990.00, 1, 990.00),
  (2, 1, 4, 6, 'Flan Casero',       'Porción', 220.00, 1, 220.00)
;

-- ===============================
-- 8) Pago (pendiente) asociado al pedido
-- ===============================
INSERT INTO payment (
  id, order_id, method, status, amount, currency, provider_tx_id, note
) VALUES
  (1, 1, 'MERCADOPAGO', 'PENDING', 1331.00, 'UYU', NULL, 'Link de pago enviado al cliente')
;

-- ===============================
-- 9) Pedido listo para probar (PICKUP)
-- ===============================
INSERT INTO customer_order (
  id, user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  notes
) VALUES (
  2, 3, 'CONFIRMED', 'PICKUP', 'UYU',
  95.00, 9.50, 0.00, 104.50,
  'Retira 19:00'
);

INSERT INTO order_item (
  id, order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total
) VALUES
  (3, 2, 2, 3, 'Empanada Caprese', 'Unidad', 95.00, 1, 95.00)
;

INSERT INTO payment (
  id, order_id, method, status, amount, currency, provider_tx_id, note
) VALUES
  (2, 2, 'CASH', 'AUTHORIZED', 104.50, 'UYU', NULL, 'Paga al retirar')
;
