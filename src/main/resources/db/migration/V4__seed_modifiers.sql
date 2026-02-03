-- =========================================
-- V4: Seed Modifiers - Grupos y Opciones de modificadores
-- Demuestra el modelo Sprint 5 de modifiers
-- SIN IDs explícitos
-- =========================================

-- ===============================
-- 1. MODIFIER GROUPS PARA MILANESA SIN GUARNICIÓN
-- ===============================

-- Milanesa de Carne - Sin guarnición: Grupo "Guarnición"
-- El cliente puede elegir qué guarnición agregar (opcional, máximo 1)
INSERT INTO modifier_group (product_variant_id, name, min_select, max_select, selection_mode, sort_order, is_active)
SELECT v.id, 'Guarnición', 0, 1, 'SINGLE', 0, TRUE
FROM product_variant v WHERE v.sku = 'MIL-CAR-SG';

-- Milanesa de Pollo - Sin guarnición: Grupo "Guarnición"
INSERT INTO modifier_group (product_variant_id, name, min_select, max_select, selection_mode, sort_order, is_active)
SELECT v.id, 'Guarnición', 0, 1, 'SINGLE', 0, TRUE
FROM product_variant v WHERE v.sku = 'MIL-POL-SG';

-- ===============================
-- 2. MODIFIER OPTIONS
-- ===============================

-- Opciones para Milanesa de Carne SG - Guarnición
-- Opción 1: Puré (sin costo extra, linkeado a producto existente)
INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive, linked_product_variant_id)
SELECT
  mg.id,
  'Puré de papa',
  0,
  TRUE,
  0.00,
  FALSE,
  (SELECT pv.id FROM product_variant pv WHERE pv.sku = 'PUR-PAP-P')
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'MIL-CAR-SG' AND mg.name = 'Guarnición';

-- Opción 2: Papas Fritas (+$50)
INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive, linked_product_variant_id)
SELECT
  mg.id,
  'Papas fritas',
  1,
  TRUE,
  50.00,
  FALSE,
  (SELECT pv.id FROM product_variant pv WHERE pv.sku = 'PAP-FRI-P')
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'MIL-CAR-SG' AND mg.name = 'Guarnición';

-- Opción 3: Ensalada Rusa (+$30)
INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive, linked_product_variant_id)
SELECT
  mg.id,
  'Ensalada rusa',
  2,
  TRUE,
  30.00,
  FALSE,
  (SELECT pv.id FROM product_variant pv WHERE pv.sku = 'ENS-RUS-IND')
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'MIL-CAR-SG' AND mg.name = 'Guarnición';

-- Opción 4: Arroz blanco (sin costo, viene incluido en pedidos de almuerzo)
INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive, linked_product_variant_id)
SELECT
  mg.id,
  'Arroz blanco',
  3,
  TRUE,
  0.00,
  FALSE,
  (SELECT pv.id FROM product_variant pv WHERE pv.sku = 'ARR-BLA-P')
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'MIL-CAR-SG' AND mg.name = 'Guarnición';

-- Opciones para Milanesa de Pollo SG - Guarnición
INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive, linked_product_variant_id)
SELECT
  mg.id,
  'Puré de papa',
  0,
  TRUE,
  0.00,
  FALSE,
  (SELECT pv.id FROM product_variant pv WHERE pv.sku = 'PUR-PAP-P')
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'MIL-POL-SG' AND mg.name = 'Guarnición';

INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive, linked_product_variant_id)
SELECT
  mg.id,
  'Papas fritas',
  1,
  TRUE,
  50.00,
  FALSE,
  (SELECT pv.id FROM product_variant pv WHERE pv.sku = 'PAP-FRI-P')
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'MIL-POL-SG' AND mg.name = 'Guarnición';

INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive, linked_product_variant_id)
SELECT
  mg.id,
  'Ensalada mixta',
  2,
  TRUE,
  20.00,
  FALSE,
  (SELECT pv.id FROM product_variant pv WHERE pv.sku = 'ENS-MIX-IND')
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'MIL-POL-SG' AND mg.name = 'Guarnición';

-- ===============================
-- 3. EJEMPLO: GRUPO CON MÚLTIPLES SELECCIONES
-- ===============================

-- Ñoquis XL: Grupo "Extras" (puede elegir hasta 2 extras)
INSERT INTO modifier_group (product_variant_id, name, min_select, max_select, selection_mode, sort_order, is_active)
SELECT v.id, 'Extras', 0, 2, 'MULTI', 0, TRUE
FROM product_variant v WHERE v.sku = 'NOQ-PAP-XL';

-- Opciones de extras para Ñoquis XL
INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive)
SELECT
  mg.id,
  'Queso rallado extra',
  0,
  TRUE,
  30.00,
  FALSE
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'NOQ-PAP-XL' AND mg.name = 'Extras';

INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive)
SELECT
  mg.id,
  'Salsa extra',
  1,
  TRUE,
  40.00,
  FALSE
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'NOQ-PAP-XL' AND mg.name = 'Extras';

INSERT INTO modifier_option (group_id, name, sort_order, is_active, price_delta, is_exclusive)
SELECT
  mg.id,
  'Pan de ajo',
  2,
  TRUE,
  50.00,
  FALSE
FROM modifier_group mg
JOIN product_variant v ON mg.product_variant_id = v.id
WHERE v.sku = 'NOQ-PAP-XL' AND mg.name = 'Extras';

-- ===============================
-- 4. SET DEFAULT OPTIONS (opcional)
-- ===============================

-- Setear "Puré de papa" como opción por defecto para Milanesa de Carne SG
UPDATE modifier_group mg
SET default_option_id = (
  SELECT mo.id
  FROM modifier_option mo
  WHERE mo.group_id = mg.id AND mo.name = 'Puré de papa'
  LIMIT 1
)
WHERE mg.id IN (
  SELECT mg2.id
  FROM modifier_group mg2
  JOIN product_variant v ON mg2.product_variant_id = v.id
  WHERE v.sku = 'MIL-CAR-SG' AND mg2.name = 'Guarnición'
);
