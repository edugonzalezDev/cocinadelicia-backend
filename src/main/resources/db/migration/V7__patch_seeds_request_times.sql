-- =========================================
-- V7 Patch: Ajuste de requested_at y delivered_at
-- para seeds cargados en V4 y V5.
--
-- NOTA:
-- - Usamos fechas fijas de demo (2025-01-01)
--   para reflejar los horarios de las notas.
-- - Son datos solo de ejemplo.
-- =========================================

-- ===============================
-- Pedidos iniciales (V4)
-- ===============================

-- Pedido 1 – Delivery (nota: 'Entrega estimada 20:30')
UPDATE customer_order
SET requested_at = '2025-01-01 20:30:00'
WHERE id = 1;

-- Pedido 2 – Pickup (nota: 'Retira 19:00')
UPDATE customer_order
SET requested_at = '2025-01-01 19:00:00'
WHERE id = 2;

-- ===============================
-- Seeds extra (V5: pedidos 3..22)
-- ===============================

-- Pedido 3 – Delivery – 'Entrega estimada 20:15'
UPDATE customer_order
SET requested_at = '2025-01-01 20:15:00'
WHERE id = 3;

-- Pedido 4 – Pickup – 'Retira 19:30'
UPDATE customer_order
SET requested_at = '2025-01-01 19:30:00'
WHERE id = 4;

-- Pedido 5 – Delivery – 'Priorizar este pedido' (sin hora específica)
UPDATE customer_order
SET requested_at = '2025-01-01 20:00:00'
WHERE id = 5;

-- Pedido 6 – Pickup – 'Listo para retirar a las 20:00'
UPDATE customer_order
SET requested_at = '2025-01-01 20:00:00'
WHERE id = 6;

-- Pedido 7 – Delivery – 'Repartidor en camino'
UPDATE customer_order
SET requested_at = '2025-01-01 20:10:00'
WHERE id = 7;

-- Pedido 8 – Pickup – 'Pedido retirado en mostrador'
UPDATE customer_order
SET requested_at = '2025-01-01 19:45:00',
    delivered_at = '2025-01-01 19:50:00'
WHERE id = 8;

-- Pedido 9 – Delivery – 'Cancelado por el cliente'
UPDATE customer_order
SET requested_at = '2025-01-01 20:00:00'
WHERE id = 9;

-- Pedido 10 – Pickup – 'Retira 21:00'
UPDATE customer_order
SET requested_at = '2025-01-01 21:00:00'
WHERE id = 10;

-- Pedido 11 – Delivery – 'Sin cebolla en las empanadas'
UPDATE customer_order
SET requested_at = '2025-01-01 20:30:00'
WHERE id = 11;

-- Pedido 12 – Pickup – 'Listo para retirar 19:45'
UPDATE customer_order
SET requested_at = '2025-01-01 19:45:00'
WHERE id = 12;

-- Pedido 13 – Delivery – 'Entregado al cliente'
UPDATE customer_order
SET requested_at = '2025-01-01 20:00:00',
    delivered_at = '2025-01-01 20:20:00'
WHERE id = 13;

-- Pedido 14 – Pickup – 'Uso interno para pruebas de estado'
UPDATE customer_order
SET requested_at = '2025-01-01 20:00:00'
WHERE id = 14;

-- Pedido 15 – Delivery – 'Confirmado por WhatsApp'
UPDATE customer_order
SET requested_at = '2025-01-01 20:10:00'
WHERE id = 15;

-- Pedido 16 – Pickup – 'Retirado y abonado en efectivo'
UPDATE customer_order
SET requested_at = '2025-01-01 19:00:00',
    delivered_at = '2025-01-01 19:15:00'
WHERE id = 16;

-- Pedido 17 – Delivery – 'Cancelado por demora'
UPDATE customer_order
SET requested_at = '2025-01-01 20:05:00'
WHERE id = 17;

-- Pedido 18 – Pickup – 'Flan para llevar'
UPDATE customer_order
SET requested_at = '2025-01-01 21:00:00'
WHERE id = 18;

-- Pedido 19 – Delivery – 'Milanesa para compartir'
UPDATE customer_order
SET requested_at = '2025-01-01 20:20:00'
WHERE id = 19;

-- Pedido 20 – Pickup – 'Retira 20:15'
UPDATE customer_order
SET requested_at = '2025-01-01 20:15:00'
WHERE id = 20;

-- Pedido 21 – Delivery – 'Pedido entregado sin incidentes'
UPDATE customer_order
SET requested_at = '2025-01-01 20:30:00',
    delivered_at = '2025-01-01 20:45:00'
WHERE id = 21;

-- Pedido 22 – Pickup – 'Confirmado para cumpleaños'
UPDATE customer_order
SET requested_at = '2025-01-01 21:00:00'
WHERE id = 22;
