-- =========================================
-- Cocina DeLicia - V5 Seed Demo (extra)
-- Agrega más categorías, productos, usuarios
-- y ~20 pedidos adicionales de ejemplo
-- Requiere V4__seed_demo.sql aplicado antes
-- =========================================

-- ===============================
-- 1) Nuevas categorías
-- ===============================
INSERT INTO category (id, name, slug, description)
VALUES
  (4, 'Milanesas', 'milanesas', 'Milanesas caseras y al plato'),
  (5, 'Ensaladas', 'ensaladas', 'Ensaladas frescas y livianas')
;

-- ===============================
-- 2) Nuevos productos
-- ===============================
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active)
VALUES
  (5, 4, 'Milanesa Napolitana', 'milanesa-napolitana',
   'Milanesa de carne con salsa, muzzarella y papas fritas', 10.00, TRUE),
  (6, 5, 'Ensalada César', 'ensalada-cesar',
   'Ensalada César con pollo, crutones y parmesano', 10.00, TRUE)
;

-- ===============================
-- 3) Variantes y precios nuevos
-- ===============================
INSERT INTO product_variant (id, product_id, sku, name, is_active)
VALUES
  (7, 5, 'MIL-NAPO-PORC', 'Porción con guarnición', TRUE),
  (8, 6, 'ENS-CE-IND',    'Porción individual',    TRUE)
;

INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to)
VALUES
  (7, 7, 420.00, 'UYU', CURRENT_TIMESTAMP, NULL), -- Milanesa Napolitana porción
  (8, 8, 350.00, 'UYU', CURRENT_TIMESTAMP, NULL)  -- Ensalada César porción
;

-- ===============================
-- 4) Nuevos tags y asociación
-- ===============================
INSERT INTO tag (id, name, slug)
VALUES
  (4, 'Light', 'light')
;

INSERT INTO product_tag (product_id, tag_id)
VALUES
  (5, 2), -- Milanesa Napolitana -> Picante (opción picante)
  (6, 1), -- Ensalada César -> Veggie (versión sin pollo)
  (6, 4)  -- Ensalada César -> Light
;

-- ===============================
-- 5) Nuevos usuarios (clientes)
-- ===============================
INSERT INTO app_user (id, cognito_user_id, first_name, last_name, email, phone, is_active)
VALUES
  (4, 'cog-cli-002', 'Lucía',  'Rodríguez', 'lucia@cocinadelicia.test',  '+59800000003', TRUE),
  (5, 'cog-cli-003', 'Carlos', 'Silva',     'carlos@cocinadelicia.test', '+59800000004', TRUE),
  (6, 'cog-cli-004', 'María',  'Fernández', 'maria@cocinadelicia.test',  '+59800000005', TRUE)
;

-- Asignación de roles (todos clientes)
INSERT INTO user_role (user_id, role_id, assigned_at) VALUES
  (4, 4, CURRENT_TIMESTAMP), -- Lucía  -> CUSTOMER
  (5, 4, CURRENT_TIMESTAMP), -- Carlos -> CUSTOMER
  (6, 4, CURRENT_TIMESTAMP)  -- María  -> CUSTOMER
;

-- ===============================
-- 6) Direcciones para nuevos clientes
-- ===============================
INSERT INTO customer_address (
  id, user_id, label, line1, line2, city, region, postal_code, reference
) VALUES
  (3, 4, 'Casa', 'Bvar. Artigas 1000', NULL,
   'Montevideo', 'Montevideo', '11200', 'Apto 502 Torre Sur'),
  (4, 5, 'Casa', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo', 'Montevideo', '11300', 'Portería 24h'),
  (5, 6, 'Casa', 'Av. Rivera 2500', NULL,
   'Montevideo', 'Montevideo', '11300', 'Casa esquina')
;

-- ===============================
-- 7) Pedidos adicionales
--    IDs 3..22 (20 pedidos nuevos)
-- ===============================
INSERT INTO customer_order (
  id, user_id, status, fulfillment, currency,
  subtotal_amount, tax_amount, discount_amount, total_amount,
  ship_name, ship_phone, ship_line1, ship_line2, ship_city, ship_region, ship_postal_code, ship_reference,
  notes
) VALUES
  -- 3) Juan, DELIVERY, CREATED
  (3, 3, 'CREATED', 'DELIVERY', 'UYU',
   380.00, 38.00, 0.00, 418.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL,
   'Montevideo', 'Montevideo', '11300', 'Portón negro',
   'Entrega estimada 20:15'),

  -- 4) Lucía, PICKUP, CONFIRMED
  (4, 4, 'CONFIRMED', 'PICKUP', 'UYU',
   1430.00, 143.00, 0.00, 1573.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Retira 19:30'),

  -- 5) Carlos, DELIVERY, PREPARING
  (5, 5, 'PREPARING', 'DELIVERY', 'UYU',
   570.00, 57.00, 0.00, 627.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo', 'Montevideo', '11300', 'Portería 24h',
   'Priorizar este pedido'),

  -- 6) María, PICKUP, READY
  (6, 6, 'READY', 'PICKUP', 'UYU',
   580.00, 58.00, 0.00, 638.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Listo para retirar a las 20:00'),

  -- 7) Juan, DELIVERY, OUT_FOR_DELIVERY
  (7, 3, 'OUT_FOR_DELIVERY', 'DELIVERY', 'UYU',
   770.00, 77.00, 0.00, 847.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL,
   'Montevideo', 'Montevideo', '11300', 'Portón negro',
   'Repartidor en camino'),

  -- 8) Lucía, PICKUP, DELIVERED
  (8, 4, 'DELIVERED', 'PICKUP', 'UYU',
   990.00, 99.00, 0.00, 1089.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Pedido retirado en mostrador'),

  -- 9) Carlos, DELIVERY, CANCELED
  (9, 5, 'CANCELED', 'DELIVERY', 'UYU',
   880.00, 88.00, 0.00, 968.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo', 'Montevideo', '11300', 'Portería 24h',
   'Cancelado por el cliente'),

  -- 10) María, PICKUP, CREATED
  (10, 6, 'CREATED', 'PICKUP', 'UYU',
   380.00, 38.00, 0.00, 418.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Retira 21:00'),

  -- 11) Juan, DELIVERY, PREPARING
  (11, 3, 'PREPARING', 'DELIVERY', 'UYU',
   860.00, 86.00, 0.00, 946.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL,
   'Montevideo', 'Montevideo', '11300', 'Portón negro',
   'Sin cebolla en las empanadas'),

  -- 12) Lucía, PICKUP, READY
  (12, 4, 'READY', 'PICKUP', 'UYU',
   840.00, 84.00, 0.00, 924.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Listo para retirar 19:45'),

  -- 13) Carlos, DELIVERY, DELIVERED
  (13, 5, 'DELIVERED', 'DELIVERY', 'UYU',
   990.00, 99.00, 0.00, 1089.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo', 'Montevideo', '11300', 'Portería 24h',
   'Entregado al cliente'),

  -- 14) María, PICKUP, OUT_FOR_DELIVERY (ejemplo intermedio)
  (14, 6, 'OUT_FOR_DELIVERY', 'PICKUP', 'UYU',
   945.00, 94.50, 0.00, 1039.50,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Uso interno para pruebas de estado'),

  -- 15) Juan, DELIVERY, CONFIRMED
  (15, 3, 'CONFIRMED', 'DELIVERY', 'UYU',
   1050.00, 105.00, 0.00, 1155.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL,
   'Montevideo', 'Montevideo', '11300', 'Portón negro',
   'Confirmado por WhatsApp'),

  -- 16) Lucía, PICKUP, DELIVERED
  (16, 4, 'DELIVERED', 'PICKUP', 'UYU',
   1180.00, 118.00, 0.00, 1298.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Retirado y abonado en efectivo'),

  -- 17) Carlos, DELIVERY, CANCELED
  (17, 5, 'CANCELED', 'DELIVERY', 'UYU',
   870.00, 87.00, 0.00, 957.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo', 'Montevideo', '11300', 'Portería 24h',
   'Cancelado por demora'),

  -- 18) María, PICKUP, CREATED
  (18, 6, 'CREATED', 'PICKUP', 'UYU',
   220.00, 22.00, 0.00, 242.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Flan para llevar'),

  -- 19) Juan, DELIVERY, PREPARING
  (19, 3, 'PREPARING', 'DELIVERY', 'UYU',
   420.00, 42.00, 0.00, 462.00,
   'Juan López', '+59800000002', 'Av. Italia 1234', NULL,
   'Montevideo', 'Montevideo', '11300', 'Portón negro',
   'Milanesa para compartir'),

  -- 20) Lucía, PICKUP, READY
  (20, 4, 'READY', 'PICKUP', 'UYU',
   570.00, 57.00, 0.00, 627.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Retira 20:15'),

  -- 21) Carlos, DELIVERY, DELIVERED
  (21, 5, 'DELIVERED', 'DELIVERY', 'UYU',
   770.00, 77.00, 0.00, 847.00,
   'Carlos Silva', '+59800000004', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava',
   'Montevideo', 'Montevideo', '11300', 'Portería 24h',
   'Pedido entregado sin incidentes'),

  -- 22) María, PICKUP, CONFIRMED
  (22, 6, 'CONFIRMED', 'PICKUP', 'UYU',
   1280.00, 128.00, 0.00, 1408.00,
   NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
   'Confirmado para cumpleaños')
;

-- ===============================
-- 8) Ítems de los pedidos 3..22
-- ===============================
INSERT INTO order_item (
  id, order_id, product_id, product_variant_id,
  product_name, variant_name, unit_price, quantity, line_total
) VALUES
  -- Pedido 3: 4 empanadas de carne unidad
  (4, 3, 1, 1, 'Empanada de Carne', 'Unidad', 95.00, 4, 380.00),

  -- Pedido 4: docena carne + 2 flanes
  (5, 4, 1, 2, 'Empanada de Carne', 'Docena', 990.00, 1, 990.00),
  (6, 4, 4, 6, 'Flan Casero',       'Porción', 220.00, 2, 440.00),

  -- Pedido 5: 6 empanadas caprese
  (7, 5, 2, 3, 'Empanada Caprese', 'Unidad', 95.00, 6, 570.00),

  -- Pedido 6: 2 porciones de ñoquis
  (8, 6, 3, 5, 'Ñoquis Caseros', 'Porción 400g', 290.00, 2, 580.00),

  -- Pedido 7: 1 milanesa + 1 ensalada
  (9, 7, 5, 7, 'Milanesa Napolitana', 'Porción con guarnición', 420.00, 1, 420.00),
  (10, 7, 6, 8, 'Ensalada César',     'Porción individual',     350.00, 1, 350.00),

  -- Pedido 8: docena caprese
  (11, 8, 2, 4, 'Empanada Caprese', 'Docena', 990.00, 1, 990.00),

  -- Pedido 9: 4 flanes
  (12, 9, 4, 6, 'Flan Casero', 'Porción', 220.00, 4, 880.00),

  -- Pedido 10: mix carne + caprese
  (13, 10, 1, 1, 'Empanada de Carne',  'Unidad', 95.00, 2, 190.00),
  (14, 10, 2, 3, 'Empanada Caprese',   'Unidad', 95.00, 2, 190.00),

  -- Pedido 11: ñoquis + flan + ensalada
  (15, 11, 3, 5, 'Ñoquis Caseros', 'Porción 400g', 290.00, 1, 290.00),
  (16, 11, 4, 6, 'Flan Casero',    'Porción',      220.00, 1, 220.00),
  (17, 11, 6, 8, 'Ensalada César', 'Porción individual', 350.00, 1, 350.00),

  -- Pedido 12: 2 milanesas
  (18, 12, 5, 7, 'Milanesa Napolitana', 'Porción con guarnición', 420.00, 2, 840.00),

  -- Pedido 13: docena carne
  (19, 13, 1, 2, 'Empanada de Carne', 'Docena', 990.00, 1, 990.00),

  -- Pedido 14: 3 caprese + 3 flanes
  (20, 14, 2, 3, 'Empanada Caprese', 'Unidad', 95.00, 3, 285.00),
  (21, 14, 4, 6, 'Flan Casero',      'Porción', 220.00, 3, 660.00),

  -- Pedido 15: 3 ensaladas César
  (22, 15, 6, 8, 'Ensalada César', 'Porción individual', 350.00, 3, 1050.00),

  -- Pedido 16: 1 carne unidad + 1 docena + 1 caprese unidad
  (23, 16, 1, 1, 'Empanada de Carne',  'Unidad', 95.00, 1, 95.00),
  (24, 16, 1, 2, 'Empanada de Carne',  'Docena', 990.00, 1, 990.00),
  (25, 16, 2, 3, 'Empanada Caprese',   'Unidad', 95.00, 1, 95.00),

  -- Pedido 17: 3 porciones de ñoquis
  (26, 17, 3, 5, 'Ñoquis Caseros', 'Porción 400g', 290.00, 3, 870.00),

  -- Pedido 18: 1 flan
  (27, 18, 4, 6, 'Flan Casero', 'Porción', 220.00, 1, 220.00),

  -- Pedido 19: 1 milanesa
  (28, 19, 5, 7, 'Milanesa Napolitana', 'Porción con guarnición', 420.00, 1, 420.00),

  -- Pedido 20: 6 empanadas de carne
  (29, 20, 1, 1, 'Empanada de Carne', 'Unidad', 95.00, 6, 570.00),

  -- Pedido 21: 2 caprese + 2 ñoquis
  (30, 21, 2, 3, 'Empanada Caprese', 'Unidad', 95.00, 2, 190.00),
  (31, 21, 3, 5, 'Ñoquis Caseros',   'Porción 400g', 290.00, 2, 580.00),

  -- Pedido 22: docena caprese + 1 porción de ñoquis
  (32, 22, 2, 4, 'Empanada Caprese', 'Docena', 990.00, 1, 990.00),
  (33, 22, 3, 5, 'Ñoquis Caseros',   'Porción 400g', 290.00, 1, 290.00)
;

-- ===============================
-- 9) Pagos de los pedidos 3..22
-- ===============================
INSERT INTO payment (
  id, order_id, method, status, amount, currency, provider_tx_id, note
) VALUES
  (3,  3,  'MERCADOPAGO', 'PENDING',   418.00, 'UYU', NULL,
   'Link de pago enviado (MP)'),
  (4,  4,  'CASH',        'PENDING',  1573.00, 'UYU', NULL,
   'Abona al retirar'),
  (5,  5,  'MERCADOPAGO', 'AUTHORIZED', 627.00, 'UYU', 'MP-DEMO-0005',
   'Pago autorizado, a capturar al entregar'),
  (6,  6,  'CASH',        'AUTHORIZED', 638.00, 'UYU', NULL,
   'Se cobra al entregar en mostrador'),
  (7,  7,  'CREDIT_CARD', 'AUTHORIZED', 847.00, 'UYU', 'CC-DEMO-0007',
   'Pago con tarjeta en delivery'),
  (8,  8,  'MERCADOPAGO', 'PAID',    1089.00, 'UYU', 'MP-DEMO-0008',
   'Pago online aprobado'),
  (9,  9,  'DEBIT_CARD',  'REFUNDED',  968.00, 'UYU', 'DB-DEMO-0009',
   'Pedido cancelado, importe devuelto'),
  (10, 10, 'CASH',        'PENDING',   418.00, 'UYU', NULL,
   'Paga al retirar'),
  (11, 11, 'BANK_TRANSFER','PENDING',  946.00, 'UYU', NULL,
   'Transferencia pendiente de confirmación'),
  (12, 12, 'MERCADOPAGO', 'AUTHORIZED', 924.00, 'UYU', 'MP-DEMO-0012',
   'Pago autorizado'),
  (13, 13, 'CREDIT_CARD', 'PAID',    1089.00, 'UYU', 'CC-DEMO-0013',
   'Pago en línea completo'),
  (14, 14, 'DEBIT_CARD',  'AUTHORIZED', 1039.50, 'UYU', 'DB-DEMO-0014',
   'Pago autorizado parcialmente'),
  (15, 15, 'CASH',        'PENDING',  1155.00, 'UYU', NULL,
   'Confirma pago en efectivo al entregar'),
  (16, 16, 'BANK_TRANSFER','PAID',   1298.00, 'UYU', NULL,
   'Transferencia recibida'),
  (17, 17, 'MERCADOPAGO', 'REFUNDED',  957.00, 'UYU', 'MP-DEMO-0017',
   'Cancelado y reembolsado'),
  (18, 18, 'CREDIT_CARD', 'PENDING',   242.00, 'UYU', 'CC-DEMO-0018',
   'Pago en caja al retirar'),
  (19, 19, 'CASH',        'AUTHORIZED', 462.00, 'UYU', NULL,
   'Pago a contraentrega'),
  (20, 20, 'MERCADOPAGO', 'AUTHORIZED', 627.00, 'UYU', 'MP-DEMO-0020',
   'Pago online pendiente de captura'),
  (21, 21, 'DEBIT_CARD',  'PAID',     847.00, 'UYU', 'DB-DEMO-0021',
   'Pago completado con débito'),
  (22, 22, 'BANK_TRANSFER','PENDING', 1408.00, 'UYU', NULL,
   'Pendiente de confirmación de banco')
;
