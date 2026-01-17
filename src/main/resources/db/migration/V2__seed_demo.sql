-- =========================================
-- Cocina DeLicia - PostgreSQL Seed Demo (consolidado)
-- Incluye: V4 + V5 + V7 + V10
-- =========================================

-- ===============================
-- 1) Catálogo: categorías y tags
-- ===============================
INSERT INTO category (id, name, slug, description)
VALUES
  (1, 'Empanadas',  'empanadas',  'Empanadas caseras'),
  (2, 'Pastas',     'pastas',     'Pastas frescas y salsas'),
  (3, 'Postres',    'postres',    'Dulces y postres caseros'),
  (4, 'Milanesas',  'milanesas',  'Milanesas caseras y al plato'),
  (5, 'Ensaladas',  'ensaladas',  'Ensaladas frescas y livianas')
;

INSERT INTO tag (id, name, slug)
VALUES
  (1, 'Veggie',   'veggie'),
  (2, 'Picante',  'picante'),
  (3, 'Sin TACC', 'sin-tacc'),
  (4, 'Light',    'light')
;

-- ===============================
-- 2) Productos
-- ===============================
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active)
VALUES
  (1, 1, 'Empanada de Carne', 'empanada-carne', 'Carne cortada a cuchillo', 10.00, TRUE),
  (2, 1, 'Empanada Caprese',  'empanada-caprese','Mozzarella, tomate, albahaca', 10.00, TRUE),
  (3, 2, 'Ñoquis Caseros',    'noquis-caseros', 'Ñoquis de papa con salsas', 10.00, TRUE),
  (4, 3, 'Flan Casero',       'flan-casero',    'Flan de huevo con caramelo', 10.00, TRUE),
  (5, 4, 'Milanesa Napolitana', 'milanesa-napolitana',
   'Milanesa de carne con salsa, muzzarella y papas fritas', 10.00, TRUE),
  (6, 5, 'Ensalada César', 'ensalada-cesar',
   'Ensalada César con pollo, crutones y parmesano', 10.00, TRUE)
;

-- Product ↔ Tags
INSERT INTO product_tag (product_id, tag_id) VALUES
  (2, 1), -- Caprese -> Veggie
  (3, 1), -- Ñoquis -> Veggie
  (1, 2), -- Empanada carne -> Picante
  (5, 2), -- Milanesa -> Picante (opción)
  (6, 1), -- Ensalada -> Veggie (versión sin pollo)
  (6, 4)  -- Ensalada -> Light
;

-- ===============================
-- 3) Variantes de producto
-- ===============================
INSERT INTO product_variant (id, product_id, sku, name, is_active)
VALUES
  (1, 1, 'EMP-CARNE-U',     'Unidad',       TRUE),
  (2, 1, 'EMP-CARNE-DOC',   'Docena',       TRUE),
  (3, 2, 'EMP-CAP-U',       'Unidad',       TRUE),
  (4, 2, 'EMP-CAP-DOC',     'Docena',       TRUE),
  (5, 3, 'NOQ-PORC',        'Porción 400g', TRUE),
  (6, 4, 'FLAN-PORC',       'Porción',      TRUE),
  (7, 5, 'MIL-NAPO-PORC',   'Porción con guarnición', TRUE),
  (8, 6, 'ENS-CE-IND',      'Porción individual',     TRUE)
;

-- ===============================
-- 4) Historial de precios (UYU)
-- ===============================
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to)
VALUES
  (1, 1,  95.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (2, 2, 990.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (3, 3,  95.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (4, 4, 990.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (5, 5, 290.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (6, 6, 220.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (7, 7, 420.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (8, 8, 350.00, 'UYU', CURRENT_TIMESTAMP, NULL)
;

-- ===============================
-- 5) Usuarios y Roles
-- ===============================
INSERT INTO role (id, name, description)
VALUES
  (1, 'ADMIN',    'Administrador'),
  (2, 'CHEF',     'Cocinero'),
  (3, 'COURIER',  'Repartidor'),
  (4, 'CUSTOMER', 'Cliente')
;

INSERT INTO app_user (id, cognito_user_id, first_name, last_name, email, phone, is_active)
VALUES
  (1, 'cog-admin-001', 'Eduardo', 'González', 'admin@cocinadelicia.test',  '+59800000000', TRUE),
  (2, 'cog-chef-001',  'Ana',     'Pérez',    'chef@cocinadelicia.test',   '+59800000001', TRUE),
  (3, 'cog-cli-001',   'Juan',    'López',    'juan@cocinadelicia.test',   '+59800000002', TRUE),
  (4, 'cog-cli-002',   'Lucía',   'Rodríguez','lucia@cocinadelicia.test',  '+59800000003', TRUE),
  (5, 'cog-cli-003',   'Carlos',  'Silva',    'carlos@cocinadelicia.test', '+59800000004', TRUE),
  (6, 'cog-cli-004',   'María',   'Fernández','maria@cocinadelicia.test',  '+59800000005', TRUE)
;

INSERT INTO user_role (user_id, role_id, assigned_at) VALUES
  (1, 1, CURRENT_TIMESTAMP),
  (2, 2, CURRENT_TIMESTAMP),
  (3, 4, CURRENT_TIMESTAMP),
  (4, 4, CURRENT_TIMESTAMP),
  (5, 4, CURRENT_TIMESTAMP),
  (6, 4, CURRENT_TIMESTAMP)
;

-- ===============================
-- 6) Direcciones
-- ===============================
INSERT INTO customer_address (id, user_id, label, line1, line2, city, region, postal_code, reference)
VALUES
  (1, 3, 'Casa',    'Av. Italia 1234', NULL,      'Montevideo', 'Montevideo', '11300', 'Portón negro'),
  (2, 3, 'Trabajo', 'Colonia 555',     'Of. 301', 'Montevideo', 'Montevideo', '11000', 'Recepción 3er piso'),
  (3, 4, 'Casa',    'Bvar. Artigas 1000', NULL, 'Montevideo', 'Montevideo', '11200', 'Apto 502 Torre Sur'),
  (4, 5, 'Casa',    'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava', 'Montevideo', 'Montevideo', '11300', 'Portería 24h'),
  (5, 6, 'Casa',    'Av. Rivera 2500', NULL, 'Montevideo', 'Montevideo', '11300', 'Casa esquina')
;

-- ===============================
-- 7) Pedidos demo (1..22)
-- ===============================

-- Pedidos 1 y 2
INSERT INTO customer_order (
  id, user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_region, ship_postal_code, ship_reference,
  notes
) VALUES
  (1, 3, 'CREATED', 'DELIVERY', 'UYU',
   1210.00, 121.00, 0.00, 1331.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo', 'Montevideo', '11300', 'Portón negro',
   'Entrega estimada 20:30'),

  (2, 3, 'CONFIRMED', 'PICKUP', 'UYU',
   95.00, 9.50, 0.00, 104.50,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Retira 19:00')
;

-- Pedidos 3..22
INSERT INTO customer_order (
  id, user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_region, ship_postal_code, ship_reference,
  notes
) VALUES
  (3, 3, 'CREATED', 'DELIVERY', 'UYU', 380.00, 38.00, 0.00, 418.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo','Montevideo','11300','Portón negro',
   'Entrega estimada 20:15'),

  (4, 4, 'CONFIRMED', 'PICKUP', 'UYU', 1430.00, 143.00, 0.00, 1573.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Retira 19:30'),

  (5, 5, 'PREPARING', 'DELIVERY', 'UYU', 570.00, 57.00, 0.00, 627.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo','Montevideo','11300','Portería 24h',
   'Priorizar este pedido'),

  (6, 6, 'READY', 'PICKUP', 'UYU', 580.00, 58.00, 0.00, 638.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Listo para retirar a las 20:00'),

  (7, 3, 'OUT_FOR_DELIVERY', 'DELIVERY', 'UYU', 770.00, 77.00, 0.00, 847.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo','Montevideo','11300','Portón negro',
   'Repartidor en camino'),

  (8, 4, 'DELIVERED', 'PICKUP', 'UYU', 990.00, 99.00, 0.00, 1089.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Pedido retirado en mostrador'),

  (9, 5, 'CANCELED', 'DELIVERY', 'UYU', 880.00, 88.00, 0.00, 968.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo','Montevideo','11300','Portería 24h',
   'Cancelado por el cliente'),

  (10, 6, 'CREATED', 'PICKUP', 'UYU', 380.00, 38.00, 0.00, 418.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Retira 21:00'),

  (11, 3, 'PREPARING', 'DELIVERY', 'UYU', 860.00, 86.00, 0.00, 946.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo','Montevideo','11300','Portón negro',
   'Sin cebolla en las empanadas'),

  (12, 4, 'READY', 'PICKUP', 'UYU', 840.00, 84.00, 0.00, 924.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Listo para retirar 19:45'),

  (13, 5, 'DELIVERED', 'DELIVERY', 'UYU', 990.00, 99.00, 0.00, 1089.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo','Montevideo','11300','Portería 24h',
   'Entregado al cliente'),

  (14, 6, 'OUT_FOR_DELIVERY', 'PICKUP', 'UYU', 945.00, 94.50, 0.00, 1039.50,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Uso interno para pruebas de estado'),

  (15, 3, 'CONFIRMED', 'DELIVERY', 'UYU', 1050.00, 105.00, 0.00, 1155.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo','Montevideo','11300','Portón negro',
   'Confirmado por WhatsApp'),

  (16, 4, 'DELIVERED', 'PICKUP', 'UYU', 1180.00, 118.00, 0.00, 1298.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Retirado y abonado en efectivo'),

  (17, 5, 'CANCELED', 'DELIVERY', 'UYU', 870.00, 87.00, 0.00, 957.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo','Montevideo','11300','Portería 24h',
   'Cancelado por demora'),

  (18, 6, 'CREATED', 'PICKUP', 'UYU', 220.00, 22.00, 0.00, 242.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Flan para llevar'),

  (19, 3, 'PREPARING', 'DELIVERY', 'UYU', 420.00, 42.00, 0.00, 462.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL, 'Montevideo','Montevideo','11300','Portón negro',
   'Milanesa para compartir'),

  (20, 4, 'READY', 'PICKUP', 'UYU', 570.00, 57.00, 0.00, 627.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Retira 20:15'),

  (21, 5, 'DELIVERED', 'DELIVERY', 'UYU', 770.00, 77.00, 0.00, 847.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo','Montevideo','11300','Portería 24h',
   'Pedido entregado sin incidentes'),

  (22, 6, 'CONFIRMED', 'PICKUP', 'UYU', 1280.00, 128.00, 0.00, 1408.00,
   NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, 'Confirmado para cumpleaños')
;

-- ===============================
-- 8) Ítems de pedidos (1..22)
-- ===============================
INSERT INTO order_item (
  id, order_id, product_id, product_variant_id,
  product_name, variant_name, unit_price, quantity, line_total
) VALUES
  (1, 1, 1, 2, 'Empanada de Carne', 'Docena', 990.00, 1, 990.00),
  (2, 1, 4, 6, 'Flan Casero',       'Porción', 220.00, 1, 220.00),
  (3, 2, 2, 3, 'Empanada Caprese', 'Unidad', 95.00, 1, 95.00),

  (4, 3, 1, 1, 'Empanada de Carne', 'Unidad', 95.00, 4, 380.00),
  (5, 4, 1, 2, 'Empanada de Carne', 'Docena', 990.00, 1, 990.00),
  (6, 4, 4, 6, 'Flan Casero',       'Porción', 220.00, 2, 440.00),
  (7, 5, 2, 3, 'Empanada Caprese',  'Unidad', 95.00, 6, 570.00),
  (8, 6, 3, 5, 'Ñoquis Caseros',    'Porción 400g', 290.00, 2, 580.00),
  (9, 7, 5, 7, 'Milanesa Napolitana', 'Porción con guarnición', 420.00, 1, 420.00),
  (10,7, 6, 8, 'Ensalada César',      'Porción individual',     350.00, 1, 350.00),
  (11,8, 2, 4, 'Empanada Caprese',  'Docena', 990.00, 1, 990.00),
  (12,9, 4, 6, 'Flan Casero',       'Porción', 220.00, 4, 880.00),
  (13,10,1, 1, 'Empanada de Carne', 'Unidad', 95.00, 2, 190.00),
  (14,10,2, 3, 'Empanada Caprese',  'Unidad', 95.00, 2, 190.00),
  (15,11,3, 5, 'Ñoquis Caseros',    'Porción 400g', 290.00, 1, 290.00),
  (16,11,4, 6, 'Flan Casero',       'Porción', 220.00, 1, 220.00),
  (17,11,6, 8, 'Ensalada César',    'Porción individual', 350.00, 1, 350.00),
  (18,12,5, 7, 'Milanesa Napolitana','Porción con guarnición', 420.00, 2, 840.00),
  (19,13,1, 2, 'Empanada de Carne', 'Docena', 990.00, 1, 990.00),
  (20,14,2, 3, 'Empanada Caprese',  'Unidad', 95.00, 3, 285.00),
  (21,14,4, 6, 'Flan Casero',       'Porción', 220.00, 3, 660.00),
  (22,15,6, 8, 'Ensalada César',    'Porción individual', 350.00, 3, 1050.00),
  (23,16,1, 1, 'Empanada de Carne', 'Unidad', 95.00, 1, 95.00),
  (24,16,1, 2, 'Empanada de Carne', 'Docena', 990.00, 1, 990.00),
  (25,16,2, 3, 'Empanada Caprese',  'Unidad', 95.00, 1, 95.00),
  (26,17,3, 5, 'Ñoquis Caseros',    'Porción 400g', 290.00, 3, 870.00),
  (27,18,4, 6, 'Flan Casero',       'Porción', 220.00, 1, 220.00),
  (28,19,5, 7, 'Milanesa Napolitana','Porción con guarnición', 420.00, 1, 420.00),
  (29,20,1, 1, 'Empanada de Carne', 'Unidad', 95.00, 6, 570.00),
  (30,21,2, 3, 'Empanada Caprese',  'Unidad', 95.00, 2, 190.00),
  (31,21,3, 5, 'Ñoquis Caseros',    'Porción 400g', 290.00, 2, 580.00),
  (32,22,2, 4, 'Empanada Caprese',  'Docena', 990.00, 1, 990.00),
  (33,22,3, 5, 'Ñoquis Caseros',    'Porción 400g', 290.00, 1, 290.00)
;

-- ===============================
-- 9) Pagos de pedidos (1..22)
-- ===============================
INSERT INTO payment (id, order_id, method, status, amount, currency, provider_tx_id, note)
VALUES
  (1, 1, 'MERCADOPAGO', 'PENDING',    1331.00, 'UYU', NULL, 'Link de pago enviado al cliente'),
  (2, 2, 'CASH',        'AUTHORIZED',  104.50, 'UYU', NULL, 'Paga al retirar'),

  (3,  3, 'MERCADOPAGO', 'PENDING',     418.00,  'UYU', NULL, 'Link de pago enviado (MP)'),
  (4,  4, 'CASH',        'PENDING',    1573.00,  'UYU', NULL, 'Abona al retirar'),
  (5,  5, 'MERCADOPAGO', 'AUTHORIZED',  627.00,  'UYU', 'MP-DEMO-0005', 'Pago autorizado, a capturar al entregar'),
  (6,  6, 'CASH',        'AUTHORIZED',  638.00,  'UYU', NULL, 'Se cobra al entregar en mostrador'),
  (7,  7, 'CREDIT_CARD', 'AUTHORIZED',  847.00,  'UYU', 'CC-DEMO-0007', 'Pago con tarjeta en delivery'),
  (8,  8, 'MERCADOPAGO', 'PAID',       1089.00,  'UYU', 'MP-DEMO-0008', 'Pago online aprobado'),
  (9,  9, 'DEBIT_CARD',  'REFUNDED',    968.00,  'UYU', 'DB-DEMO-0009', 'Pedido cancelado, importe devuelto'),
  (10,10, 'CASH',        'PENDING',     418.00,  'UYU', NULL, 'Paga al retirar'),
  (11,11, 'BANK_TRANSFER','PENDING',    946.00,  'UYU', NULL, 'Transferencia pendiente de confirmación'),
  (12,12, 'MERCADOPAGO', 'AUTHORIZED',  924.00,  'UYU', 'MP-DEMO-0012', 'Pago autorizado'),
  (13,13, 'CREDIT_CARD', 'PAID',       1089.00,  'UYU', 'CC-DEMO-0013', 'Pago en línea completo'),
  (14,14, 'DEBIT_CARD',  'AUTHORIZED', 1039.50,  'UYU', 'DB-DEMO-0014', 'Pago autorizado parcialmente'),
  (15,15, 'CASH',        'PENDING',    1155.00,  'UYU', NULL, 'Confirma pago en efectivo al entregar'),
  (16,16, 'BANK_TRANSFER','PAID',      1298.00,  'UYU', NULL, 'Transferencia recibida'),
  (17,17, 'MERCADOPAGO', 'REFUNDED',    957.00,  'UYU', 'MP-DEMO-0017', 'Cancelado y reembolsado'),
  (18,18, 'CREDIT_CARD', 'PENDING',     242.00,  'UYU', 'CC-DEMO-0018', 'Pago en caja al retirar'),
  (19,19, 'CASH',        'AUTHORIZED',  462.00,  'UYU', NULL, 'Pago a contraentrega'),
  (20,20, 'MERCADOPAGO', 'AUTHORIZED',  627.00,  'UYU', 'MP-DEMO-0020', 'Pago online pendiente de captura'),
  (21,21, 'DEBIT_CARD',  'PAID',        847.00,  'UYU', 'DB-DEMO-0021', 'Pago completado con débito'),
  (22,22, 'BANK_TRANSFER','PENDING',   1408.00,  'UYU', NULL, 'Pendiente de confirmación de banco')
;

-- ===============================
-- 10) Patch: requested_at / delivered_at (V7 consolidado)
-- ===============================
UPDATE customer_order SET requested_at = '2025-01-01 20:30:00' WHERE id = 1;
UPDATE customer_order SET requested_at = '2025-01-01 19:00:00' WHERE id = 2;

UPDATE customer_order SET requested_at = '2025-01-01 20:15:00' WHERE id = 3;
UPDATE customer_order SET requested_at = '2025-01-01 19:30:00' WHERE id = 4;
UPDATE customer_order SET requested_at = '2025-01-01 20:00:00' WHERE id = 5;
UPDATE customer_order SET requested_at = '2025-01-01 20:00:00' WHERE id = 6;
UPDATE customer_order SET requested_at = '2025-01-01 20:10:00' WHERE id = 7;

UPDATE customer_order
SET requested_at = '2025-01-01 19:45:00',
    delivered_at = '2025-01-01 19:50:00'
WHERE id = 8;

UPDATE customer_order SET requested_at = '2025-01-01 20:00:00' WHERE id = 9;
UPDATE customer_order SET requested_at = '2025-01-01 21:00:00' WHERE id = 10;
UPDATE customer_order SET requested_at = '2025-01-01 20:30:00' WHERE id = 11;
UPDATE customer_order SET requested_at = '2025-01-01 19:45:00' WHERE id = 12;

UPDATE customer_order
SET requested_at = '2025-01-01 20:00:00',
    delivered_at = '2025-01-01 20:20:00'
WHERE id = 13;

UPDATE customer_order SET requested_at = '2025-01-01 20:00:00' WHERE id = 14;
UPDATE customer_order SET requested_at = '2025-01-01 20:10:00' WHERE id = 15;

UPDATE customer_order
SET requested_at = '2025-01-01 19:00:00',
    delivered_at = '2025-01-01 19:15:00'
WHERE id = 16;

UPDATE customer_order SET requested_at = '2025-01-01 20:05:00' WHERE id = 17;
UPDATE customer_order SET requested_at = '2025-01-01 21:00:00' WHERE id = 18;
UPDATE customer_order SET requested_at = '2025-01-01 20:20:00' WHERE id = 19;
UPDATE customer_order SET requested_at = '2025-01-01 20:15:00' WHERE id = 20;

UPDATE customer_order
SET requested_at = '2025-01-01 20:30:00',
    delivered_at = '2025-01-01 20:45:00'
WHERE id = 21;

UPDATE customer_order SET requested_at = '2025-01-01 21:00:00' WHERE id = 22;

-- ===============================
-- 11) Patch: stock + flags marketing (V10 consolidado)
-- ===============================
UPDATE product_variant SET manages_stock = TRUE,  stock_quantity = 40 WHERE id = 1;
UPDATE product_variant SET manages_stock = TRUE,  stock_quantity = 8  WHERE id = 2;
UPDATE product_variant SET manages_stock = TRUE,  stock_quantity = 36 WHERE id = 3;
UPDATE product_variant SET manages_stock = TRUE,  stock_quantity = 0  WHERE id = 4;

UPDATE product_variant SET manages_stock = FALSE, stock_quantity = 0 WHERE id IN (5, 7, 8);

UPDATE product_variant SET manages_stock = TRUE,  stock_quantity = 6 WHERE id = 6;

UPDATE product_variant SET is_daily_menu = TRUE WHERE id IN (1, 5, 7);
UPDATE product_variant SET is_featured  = TRUE WHERE id IN (2, 4, 7, 8);
UPDATE product_variant SET is_new       = TRUE WHERE id IN (7, 8);

-- ===============================
-- 12) Sync de identidades (H2)
-- ===============================
-- Ajusta los IDENTITY para que los próximos inserts sin ID no choquen
ALTER TABLE category          ALTER COLUMN id RESTART WITH 6;
ALTER TABLE tag               ALTER COLUMN id RESTART WITH 5;
ALTER TABLE product           ALTER COLUMN id RESTART WITH 7;
ALTER TABLE product_variant   ALTER COLUMN id RESTART WITH 9;
ALTER TABLE price_history     ALTER COLUMN id RESTART WITH 9;
ALTER TABLE app_user          ALTER COLUMN id RESTART WITH 7;
ALTER TABLE role              ALTER COLUMN id RESTART WITH 5;
ALTER TABLE customer_address  ALTER COLUMN id RESTART WITH 6;
ALTER TABLE customer_order    ALTER COLUMN id RESTART WITH 23;
ALTER TABLE order_item        ALTER COLUMN id RESTART WITH 34;
ALTER TABLE payment           ALTER COLUMN id RESTART WITH 23;

-- Si no insertás imágenes en el seed, podés dejarla en 1
ALTER TABLE product_image     ALTER COLUMN id RESTART WITH 1;