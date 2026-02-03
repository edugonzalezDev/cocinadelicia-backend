-- =========================================
-- V6: Seed Orders - Pedidos demo con variantes y modifiers
-- SIN IDs explícitos - lookup por claves naturales
-- Demuestra Sprint 5: variantes seleccionadas + modifiers en pedido
-- =========================================

-- ===============================
-- 1. PEDIDOS DEMO
-- ===============================

-- Pedido 1: Juan - DELIVERY con empanadas y flan
INSERT INTO customer_order (
  user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_region, ship_postal_code, ship_reference,
  notes, requested_at
)
SELECT
  u.id, 'CREATED', 'DELIVERY', 'UYU',
  1020.00, 102.00, 0.00, 1122.00,
  'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo', 'Montevideo', '11300', 'Portón negro',
  'Entrega estimada 20:30', TIMESTAMP '2026-01-26 22:30:00'
FROM app_user u WHERE u.email = 'juan@cocinadelicia.test';

-- Pedido 2: Juan - PICKUP confirmado
INSERT INTO customer_order (
  user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  notes, requested_at
)
SELECT
  u.id, 'CONFIRMED', 'PICKUP', 'UYU',
  90.00, 9.00, 0.00, 99.00,
  'Retira 19:00', TIMESTAMP '2026-01-26 22:00:00'
FROM app_user u WHERE u.email = 'juan@cocinadelicia.test';

-- Pedido 3: Lucía - PREPARING con milanesa + modifier
INSERT INTO customer_order (
  user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  notes, requested_at
)
SELECT
  u.id, 'PREPARING', 'PICKUP', 'UYU',
  330.00, 33.00, 0.00, 363.00,
  'Milanesa con puré agregado', TIMESTAMP '2026-01-26 19:30:00'
FROM app_user u WHERE u.email = 'lucia@cocinadelicia.test';

-- Pedido 4: Carlos - DELIVERED
INSERT INTO customer_order (
  user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_region, ship_postal_code, ship_reference,
  notes, requested_at, delivered_at
)
SELECT
  u.id, 'DELIVERED', 'DELIVERY', 'UYU',
  640.00, 64.00, 0.00, 704.00,
  'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava', 'Montevideo', 'Montevideo', '11300', 'Portería 24h',
  'Entregado sin incidentes', TIMESTAMP '2026-01-26 20:00:00', TIMESTAMP '2026-01-26 20:25:00'
FROM app_user u WHERE u.email = 'carlos@cocinadelicia.test';

-- Pedido 5: María - READY (con ñoquis XL y extras)
INSERT INTO customer_order (
  user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  notes, requested_at
)
SELECT
  u.id, 'READY', 'PICKUP', 'UYU',
  420.00, 42.00, 0.00, 462.00,
  'Ñoquis XL con extras', TIMESTAMP '2026-01-26 20:00:00'
FROM app_user u WHERE u.email = 'maria@cocinadelicia.test';

-- Pedido 6: Cliente real - CANCELLED
INSERT INTO customer_order (
  user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  notes, requested_at
)
SELECT
  u.id, 'CANCELLED', 'PICKUP', 'UYU',
  500.00, 50.00, 0.00, 550.00,
  'Cancelado por el cliente', TIMESTAMP '2026-01-26 21:00:00'
FROM app_user u WHERE u.email = 'iosi@customers.cdl.test';

-- Pedido 7: Juan - OUT_FOR_DELIVERY
INSERT INTO customer_order (
  user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_region, ship_postal_code, ship_reference,
  notes, requested_at
)
SELECT
  u.id, 'OUT_FOR_DELIVERY', 'DELIVERY', 'UYU',
  1200.00, 120.00, 0.00, 1320.00,
  'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo', 'Montevideo', '11300', 'Portón negro',
  'Repartidor en camino', TIMESTAMP '2026-01-26 22:10:00'
FROM app_user u WHERE u.email = 'juan@cocinadelicia.test';

-- ===============================
-- 2. ORDER ITEMS (usando lookups)
-- ===============================

-- Pedido 1: Empanada docena + Flan individual
INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Empanada de Carne',
  'Docena',
  900.00,
  1,
  900.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'empanada-carne'
JOIN product_variant v ON v.sku = 'EMP-CAR-D'
WHERE u.email = 'juan@cocinadelicia.test'
  AND co.status = 'CREATED'
  AND co.notes LIKE 'Entrega estimada%'
LIMIT 1;

INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Flan Casero',
  'Individual',
  120.00,
  1,
  120.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'flan-casero'
JOIN product_variant v ON v.sku = 'FLA-IND'
WHERE u.email = 'juan@cocinadelicia.test'
  AND co.status = 'CREATED'
  AND co.notes LIKE 'Entrega estimada%'
LIMIT 1;

-- Pedido 2: Empanada Caprese unidad
INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Empanada Caprese',
  'Unidad',
  90.00,
  1,
  90.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'empanada-caprese'
JOIN product_variant v ON v.sku = 'EMP-CAP-U'
WHERE u.email = 'juan@cocinadelicia.test'
  AND co.status = 'CONFIRMED'
LIMIT 1;

-- Pedido 3: Milanesa de Carne SG + modifier (puré)
INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Milanesa de Carne',
  'Sin guarnición',
  280.00,
  1,
  280.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'milanesa-carne'
JOIN product_variant v ON v.sku = 'MIL-CAR-SG'
WHERE u.email = 'lucia@cocinadelicia.test'
  AND co.status = 'PREPARING'
LIMIT 1;

INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Ensalada Mixta',
  'Individual',
  50.00,
  1,
  50.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'ensalada-mixta'
JOIN product_variant v ON v.sku = 'ENS-MIX-IND'
WHERE u.email = 'lucia@cocinadelicia.test'
  AND co.status = 'PREPARING'
LIMIT 1;

-- Pedido 4: Ñoquis con bolognesa + Chajá 25cm
INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Ñoquis de Papa',
  'Con bolognesa',
  290.00,
  1,
  290.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'noquis-papa'
JOIN product_variant v ON v.sku = 'NOQ-PAP-BOL'
WHERE u.email = 'carlos@cocinadelicia.test'
  AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Chajá',
  'Individual',
  120.00,
  1,
  120.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'chaja'
JOIN product_variant v ON v.sku = 'CHA-IND'
WHERE u.email = 'carlos@cocinadelicia.test'
  AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Tarta de Jamón y Queso',
  'Individual',
  120.00,
  1,
  120.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'tarta-jamon-queso'
JOIN product_variant v ON v.sku = 'TAR-JYQ-IND'
WHERE u.email = 'carlos@cocinadelicia.test'
  AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Ensalada César',
  'Individual',
  110.00,
  1,
  110.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'ensalada-cesar'
JOIN product_variant v ON v.sku = 'ENS-CES-IND'
WHERE u.email = 'carlos@cocinadelicia.test'
  AND co.status = 'DELIVERED'
LIMIT 1;

-- Pedido 5: Ñoquis XL (con extras via modifiers)
INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Ñoquis de Papa',
  'XL con bolognesa',
  350.00,
  1,
  350.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'noquis-papa'
JOIN product_variant v ON v.sku = 'NOQ-PAP-XL'
WHERE u.email = 'maria@cocinadelicia.test'
  AND co.status = 'READY'
LIMIT 1;

INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Cheesecake de Oreo',
  'Individual',
  70.00,
  1,
  70.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'cheesecake-oreo'
JOIN product_variant v ON v.sku = 'CHE-ORE-IND'
WHERE u.email = 'maria@cocinadelicia.test'
  AND co.status = 'READY'
LIMIT 1;

-- Pedido 6: Milanesa Napolitana (cancelado)
INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Milanesa Napolitana',
  'De carne',
  500.00,
  1,
  500.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'milanesa-napolitana'
JOIN product_variant v ON v.sku = 'MIL-NAP-CAR'
WHERE u.email = 'iosi@customers.cdl.test'
  AND co.status = 'CANCELLED'
LIMIT 1;

-- Pedido 7: Chajá 25cm + Empanadas docena
INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Chajá',
  '25cm',
  1100.00,
  1,
  1100.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'chaja'
JOIN product_variant v ON v.sku = 'CHA-25CM'
WHERE u.email = 'juan@cocinadelicia.test'
  AND co.status = 'OUT_FOR_DELIVERY'
LIMIT 1;

INSERT INTO order_item (order_id, product_id, product_variant_id, product_name, variant_name, unit_price, quantity, line_total)
SELECT
  co.id,
  p.id,
  v.id,
  'Tarta Caprese',
  'Individual',
  100.00,
  1,
  100.00
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
JOIN product p ON p.slug = 'tarta-caprese'
JOIN product_variant v ON v.sku = 'TAR-CAP-IND'
WHERE u.email = 'juan@cocinadelicia.test'
  AND co.status = 'OUT_FOR_DELIVERY'
LIMIT 1;

-- ===============================
-- 3. ORDER ITEM MODIFIERS (Sprint 5)
-- ===============================

-- Pedido 3: Milanesa con modifier "Puré de papa" seleccionado
INSERT INTO order_item_modifier (
  order_item_id, modifier_option_id, quantity,
  option_name_snapshot, price_delta_snapshot, unit_price_snapshot, total_price_snapshot,
  linked_product_variant_id_snapshot
)
SELECT
  oi.id,
  mo.id,
  1,
  'Puré de papa',
  0.00,
  0.00,
  0.00,
  mo.linked_product_variant_id
FROM order_item oi
JOIN customer_order co ON oi.order_id = co.id
JOIN app_user u ON co.user_id = u.id
JOIN product_variant v ON oi.product_variant_id = v.id
JOIN modifier_group mg ON mg.product_variant_id = v.id
JOIN modifier_option mo ON mo.group_id = mg.id AND mo.name = 'Puré de papa'
WHERE u.email = 'lucia@cocinadelicia.test'
  AND co.status = 'PREPARING'
  AND v.sku = 'MIL-CAR-SG'
LIMIT 1;

-- Pedido 5: Ñoquis XL con modifier "Queso rallado extra" y "Salsa extra"
INSERT INTO order_item_modifier (
  order_item_id, modifier_option_id, quantity,
  option_name_snapshot, price_delta_snapshot, unit_price_snapshot, total_price_snapshot
)
SELECT
  oi.id,
  mo.id,
  1,
  'Queso rallado extra',
  30.00,
  30.00,
  30.00
FROM order_item oi
JOIN customer_order co ON oi.order_id = co.id
JOIN app_user u ON co.user_id = u.id
JOIN product_variant v ON oi.product_variant_id = v.id
JOIN modifier_group mg ON mg.product_variant_id = v.id AND mg.name = 'Extras'
JOIN modifier_option mo ON mo.group_id = mg.id AND mo.name = 'Queso rallado extra'
WHERE u.email = 'maria@cocinadelicia.test'
  AND co.status = 'READY'
  AND v.sku = 'NOQ-PAP-XL'
LIMIT 1;

INSERT INTO order_item_modifier (
  order_item_id, modifier_option_id, quantity,
  option_name_snapshot, price_delta_snapshot, unit_price_snapshot, total_price_snapshot
)
SELECT
  oi.id,
  mo.id,
  1,
  'Salsa extra',
  40.00,
  40.00,
  40.00
FROM order_item oi
JOIN customer_order co ON oi.order_id = co.id
JOIN app_user u ON co.user_id = u.id
JOIN product_variant v ON oi.product_variant_id = v.id
JOIN modifier_group mg ON mg.product_variant_id = v.id AND mg.name = 'Extras'
JOIN modifier_option mo ON mo.group_id = mg.id AND mo.name = 'Salsa extra'
WHERE u.email = 'maria@cocinadelicia.test'
  AND co.status = 'READY'
  AND v.sku = 'NOQ-PAP-XL'
LIMIT 1;

-- ===============================
-- 4. PAGOS
-- ===============================

INSERT INTO payment (order_id, method, status, amount, currency, provider_tx_id, note)
SELECT co.id, 'MERCADOPAGO', 'PENDING', 1122.00, 'UYU', NULL, 'Link de pago enviado'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'juan@cocinadelicia.test' AND co.status = 'CREATED' AND co.notes LIKE 'Entrega estimada%'
LIMIT 1;

INSERT INTO payment (order_id, method, status, amount, currency, provider_tx_id, note)
SELECT co.id, 'CASH', 'AUTHORIZED', 99.00, 'UYU', NULL, 'Paga al retirar'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'juan@cocinadelicia.test' AND co.status = 'CONFIRMED'
LIMIT 1;

INSERT INTO payment (order_id, method, status, amount, currency, provider_tx_id, note)
SELECT co.id, 'CASH', 'AUTHORIZED', 363.00, 'UYU', NULL, 'Abona en mostrador'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'lucia@cocinadelicia.test' AND co.status = 'PREPARING'
LIMIT 1;

INSERT INTO payment (order_id, method, status, amount, currency, provider_tx_id, note)
SELECT co.id, 'MERCADOPAGO', 'PAID', 704.00, 'UYU', 'MP-DEMO-004', 'Pago aprobado'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'carlos@cocinadelicia.test' AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO payment (order_id, method, status, amount, currency, provider_tx_id, note)
SELECT co.id, 'CREDIT_CARD', 'AUTHORIZED', 462.00, 'UYU', 'CC-DEMO-005', 'Pago autorizado'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'maria@cocinadelicia.test' AND co.status = 'READY'
LIMIT 1;

INSERT INTO payment (order_id, method, status, amount, currency, provider_tx_id, note)
SELECT co.id, 'DEBIT_CARD', 'REFUNDED', 550.00, 'UYU', 'DB-DEMO-006', 'Pedido cancelado, importe devuelto'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'iosi@customers.cdl.test' AND co.status = 'CANCELLED'
LIMIT 1;

INSERT INTO payment (order_id, method, status, amount, currency, provider_tx_id, note)
SELECT co.id, 'MERCADOPAGO', 'AUTHORIZED', 1320.00, 'UYU', 'MP-DEMO-007', 'Pago a capturar al entregar'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'juan@cocinadelicia.test' AND co.status = 'OUT_FOR_DELIVERY'
LIMIT 1;

-- ===============================
-- 5. ORDER STATUS HISTORY (ejemplo)
-- ===============================

INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, reason)
SELECT co.id, NULL, 'CREATED', 'system', 'Pedido creado'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'carlos@cocinadelicia.test' AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, reason)
SELECT co.id, 'CREATED', 'CONFIRMED', 'admin@cocinadelicia.test', 'Confirmado por admin'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'carlos@cocinadelicia.test' AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, reason)
SELECT co.id, 'CONFIRMED', 'PREPARING', 'chef@cocinadelicia.test', 'Cocina comenzó preparación'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'carlos@cocinadelicia.test' AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, reason)
SELECT co.id, 'PREPARING', 'READY', 'chef@cocinadelicia.test', 'Pedido listo'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'carlos@cocinadelicia.test' AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, reason)
SELECT co.id, 'READY', 'OUT_FOR_DELIVERY', 'admin@cocinadelicia.test', 'Repartidor asignado'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'carlos@cocinadelicia.test' AND co.status = 'DELIVERED'
LIMIT 1;

INSERT INTO order_status_history (order_id, from_status, to_status, changed_by, reason)
SELECT co.id, 'OUT_FOR_DELIVERY', 'DELIVERED', 'courier@cocinadelicia.test', 'Entregado al cliente'
FROM customer_order co
JOIN app_user u ON co.user_id = u.id
WHERE u.email = 'carlos@cocinadelicia.test' AND co.status = 'DELIVERED'
LIMIT 1;
