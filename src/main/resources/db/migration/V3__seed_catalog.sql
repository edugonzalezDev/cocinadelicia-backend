-- =========================================
-- V3: Seed Catálogo - Productos, Variantes, Precios
-- SIN IDs explícitos - usa claves naturales (slug, sku)
-- Mantiene el dataset existente equivalente
-- =========================================

-- ===============================
-- 1. PRODUCTOS
-- ===============================

-- EMPANADAS
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Carne', 'empanada-carne', 'Empanada de carne cortada a cuchillo', 10.00, TRUE, FALSE, TRUE, FALSE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Jamón y Queso', 'empanada-jamon-queso', 'Empanada de jamón y queso', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada Caprese', 'empanada-caprese', 'Mozzarella, tomate y albahaca', 10.00, TRUE, TRUE, FALSE, FALSE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Verdura', 'empanada-verdura', 'Verduras salteadas con especias', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Atún', 'empanada-atun', 'Atún con vegetales', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Pollo', 'empanada-pollo', 'Pollo desmenuzado', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Verdura y Ricota', 'empanada-verdura-ricota', 'Verduras con ricota y queso', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Carne y Pasas', 'empanada-carne-pasas', 'Carne con pasas de uva', 10.00, TRUE, FALSE, FALSE, TRUE
FROM category c WHERE c.slug = 'empanadas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Empanada de Queso y Choclo', 'empanada-queso-choclo', 'Queso con granos de choclo', 10.00, TRUE, FALSE, FALSE, TRUE
FROM category c WHERE c.slug = 'empanadas';

-- PASTAS (muestra representativa)
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Canelones de Carne', 'canelones-carne', 'Canelones rellenos de carne', 10.00, TRUE, FALSE, TRUE, FALSE
FROM category c WHERE c.slug = 'pastas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Canelones de Verdura', 'canelones-verdura', 'Canelones de verdura', 10.00, TRUE, FALSE, TRUE, FALSE
FROM category c WHERE c.slug = 'pastas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Ñoquis de Papa', 'noquis-papa', 'Ñoquis caseros de papa', 10.00, TRUE, TRUE, TRUE, FALSE
FROM category c WHERE c.slug = 'pastas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Lasagna', 'lasagna', 'Lasagna de carne', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'pastas';

-- POSTRES (muestra representativa)
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Flan Casero', 'flan-casero', 'Flan de huevo con caramelo', 10.00, TRUE, FALSE, TRUE, FALSE
FROM category c WHERE c.slug = 'postres';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Chajá', 'chaja', 'Postre uruguayo con merengue y duraznos', 10.00, TRUE, TRUE, FALSE, FALSE
FROM category c WHERE c.slug = 'postres';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Cheesecake de Oreo', 'cheesecake-oreo', 'Cheesecake con galletas oreo', 10.00, TRUE, TRUE, FALSE, FALSE
FROM category c WHERE c.slug = 'postres';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Torta de Avena y Banana', 'torta-avena-banana', 'Torta fit de avena y banana', 10.00, TRUE, TRUE, TRUE, FALSE
FROM category c WHERE c.slug = 'postres';

-- MILANESAS (para demostrar variantes tipo + modifiers)
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Milanesa de Carne', 'milanesa-carne', 'Milanesa de carne vacuna', 10.00, TRUE, FALSE, TRUE, FALSE
FROM category c WHERE c.slug = 'milanesas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Milanesa de Pollo', 'milanesa-pollo', 'Milanesa de pechuga de pollo', 10.00, TRUE, TRUE, TRUE, FALSE
FROM category c WHERE c.slug = 'milanesas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Milanesa de Pescado', 'milanesa-pescado', 'Milanesa de filete de pescado', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'milanesas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Milanesa Napolitana', 'milanesa-napolitana', 'Milanesa con salsa, jamón y muzzarella', 10.00, TRUE, TRUE, TRUE, FALSE
FROM category c WHERE c.slug = 'milanesas';

-- ENSALADAS
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Ensalada Mixta', 'ensalada-mixta', 'Lechuga, tomate, cebolla, zanahoria', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'ensaladas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Ensalada Rusa', 'ensalada-rusa', 'Papa, zanahoria, arvejas y mayonesa', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'ensaladas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Ensalada César', 'ensalada-cesar', 'Lechuga, pollo, queso parmesano', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'ensaladas';

-- PLATOS PRINCIPALES
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Churrasco', 'churrasco', 'Churrasco a la plancha', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'platos-principales';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Bondiola Estofada', 'bondiola-estofada', 'Bondiola de cerdo estofada', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'platos-principales';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Muslo de Pollo', 'muslo-pollo', 'Muslo al horno con especias', 10.00, TRUE, TRUE, TRUE, FALSE
FROM category c WHERE c.slug = 'platos-principales';

-- GUISOS
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Guiso de Lentejas', 'guiso-lentejas', 'Guiso casero de lentejas', 10.00, TRUE, FALSE, TRUE, FALSE
FROM category c WHERE c.slug = 'guisos-cazuelas';

-- TARTAS
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Tarta de Jamón y Queso', 'tarta-jamon-queso', 'Tarta clásica de jamón y queso', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'tartas-tortillas';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Tarta Caprese', 'tarta-caprese', 'Tarta de tomate, mozzarella y albahaca', 10.00, TRUE, TRUE, FALSE, FALSE
FROM category c WHERE c.slug = 'tartas-tortillas';

-- EXTRAS (guarniciones sueltas)
INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Puré de Papa', 'pure-papa', 'Puré de papa cremoso', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'extras';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Papas Fritas', 'papas-fritas', 'Porción de papas fritas', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'extras';

INSERT INTO product (category_id, name, slug, description, tax_rate_percent, is_active, is_featured, is_daily_menu, is_new)
SELECT c.id, 'Arroz Blanco', 'arroz-blanco', 'Porción de arroz', 10.00, TRUE, FALSE, FALSE, FALSE
FROM category c WHERE c.slug = 'extras';

-- ===============================
-- 2. VARIANTES DE PRODUCTO
-- ===============================

-- EMPANADA DE CARNE: Unidad y Docena (variantes de tamaño)
INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity, is_featured, is_daily_menu, is_new)
SELECT p.id, 'EMP-CAR-U', 'Unidad', TRUE, TRUE, 100, FALSE, TRUE, FALSE
FROM product p WHERE p.slug = 'empanada-carne';

INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity, is_featured, is_daily_menu, is_new)
SELECT p.id, 'EMP-CAR-D', 'Docena', TRUE, TRUE, 20, TRUE, FALSE, FALSE
FROM product p WHERE p.slug = 'empanada-carne';

-- EMPANADA JAMÓN Y QUESO
INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity)
SELECT p.id, 'EMP-JYQ-U', 'Unidad', TRUE, TRUE, 80
FROM product p WHERE p.slug = 'empanada-jamon-queso';

INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity)
SELECT p.id, 'EMP-JYQ-D', 'Docena', TRUE, TRUE, 15
FROM product p WHERE p.slug = 'empanada-jamon-queso';

-- EMPANADA CAPRESE
INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity, is_featured)
SELECT p.id, 'EMP-CAP-U', 'Unidad', TRUE, TRUE, 60, TRUE
FROM product p WHERE p.slug = 'empanada-caprese';

INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity)
SELECT p.id, 'EMP-CAP-D', 'Docena', TRUE, TRUE, 10
FROM product p WHERE p.slug = 'empanada-caprese';

-- EMPANADA VERDURA
INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity, is_daily_menu, is_new)
SELECT p.id, 'EMP-VER-U', 'Unidad', TRUE, TRUE, 90, TRUE, TRUE
FROM product p WHERE p.slug = 'empanada-verdura';

INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity, is_featured, is_new)
SELECT p.id, 'EMP-VER-D', 'Docena', TRUE, TRUE, 18, TRUE, TRUE
FROM product p WHERE p.slug = 'empanada-verdura';

-- EMPANADA CARNE Y PASAS
INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity, is_new)
SELECT p.id, 'EMP-CPU-U', 'Unidad', TRUE, TRUE, 40, TRUE
FROM product p WHERE p.slug = 'empanada-carne-pasas';

INSERT INTO product_variant (product_id, sku, name, is_active, manages_stock, stock_quantity, is_new)
SELECT p.id, 'EMP-CPU-D', 'Docena', TRUE, TRUE, 5, TRUE
FROM product p WHERE p.slug = 'empanada-carne-pasas';

-- CANELONES DE CARNE
INSERT INTO product_variant (product_id, sku, name, is_active, is_daily_menu)
SELECT p.id, 'CAN-CAR-P', 'Porción', TRUE, TRUE
FROM product p WHERE p.slug = 'canelones-carne';

-- CANELONES DE VERDURA
INSERT INTO product_variant (product_id, sku, name, is_active, is_daily_menu)
SELECT p.id, 'CAN-VER-P', 'Porción', TRUE, TRUE
FROM product p WHERE p.slug = 'canelones-verdura';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'CAN-VER-CH', 'Porción chica', TRUE
FROM product p WHERE p.slug = 'canelones-verdura';

-- ÑOQUIS DE PAPA: Variantes de tamaño (Normal/XL) y tipo de salsa
INSERT INTO product_variant (product_id, sku, name, is_active, is_featured, is_daily_menu)
SELECT p.id, 'NOQ-PAP-BOL', 'Con bolognesa', TRUE, TRUE, TRUE
FROM product p WHERE p.slug = 'noquis-papa';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'NOQ-PAP-SAL', 'Con salsa blanca', TRUE
FROM product p WHERE p.slug = 'noquis-papa';

INSERT INTO product_variant (product_id, sku, name, is_active, is_daily_menu)
SELECT p.id, 'NOQ-PAP-TUC', 'Con tuco de pollo', TRUE, TRUE
FROM product p WHERE p.slug = 'noquis-papa';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'NOQ-PAP-XL', 'XL con bolognesa', TRUE
FROM product p WHERE p.slug = 'noquis-papa';

-- LASAGNA
INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'LAS-CAR-P', 'Porción', TRUE
FROM product p WHERE p.slug = 'lasagna';

-- FLAN CASERO
INSERT INTO product_variant (product_id, sku, name, is_active, is_daily_menu)
SELECT p.id, 'FLA-IND', 'Individual', TRUE, TRUE
FROM product p WHERE p.slug = 'flan-casero';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'FLA-ENT', 'Entero', TRUE
FROM product p WHERE p.slug = 'flan-casero';

-- CHAJÁ: Variantes de tamaño
INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'CHA-IND', 'Individual', TRUE
FROM product p WHERE p.slug = 'chaja';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'CHA-20CM', '20cm', TRUE
FROM product p WHERE p.slug = 'chaja';

INSERT INTO product_variant (product_id, sku, name, is_active, is_featured)
SELECT p.id, 'CHA-25CM', '25cm', TRUE, TRUE
FROM product p WHERE p.slug = 'chaja';

-- CHEESECAKE DE OREO
INSERT INTO product_variant (product_id, sku, name, is_active, is_featured)
SELECT p.id, 'CHE-ORE-IND', 'Individual', TRUE, TRUE
FROM product p WHERE p.slug = 'cheesecake-oreo';

-- TORTA AVENA Y BANANA
INSERT INTO product_variant (product_id, sku, name, is_active, is_featured, is_daily_menu)
SELECT p.id, 'TOR-AVE-IND', 'Individual', TRUE, TRUE, TRUE
FROM product p WHERE p.slug = 'torta-avena-banana';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'TOR-AVE-ENT', 'Entera', TRUE
FROM product p WHERE p.slug = 'torta-avena-banana';

-- MILANESA DE CARNE: Sin guarnición (base para modifiers)
INSERT INTO product_variant (product_id, sku, name, is_active, is_daily_menu)
SELECT p.id, 'MIL-CAR-SG', 'Sin guarnición', TRUE, TRUE
FROM product p WHERE p.slug = 'milanesa-carne';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'MIL-CAR-PUR', 'Con puré', TRUE
FROM product p WHERE p.slug = 'milanesa-carne';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'MIL-CAR-FRI', 'Con fritas', TRUE
FROM product p WHERE p.slug = 'milanesa-carne';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'MIL-CAR-RUS', 'Con rusa', TRUE
FROM product p WHERE p.slug = 'milanesa-carne';

-- MILANESA DE POLLO
INSERT INTO product_variant (product_id, sku, name, is_active, is_featured, is_daily_menu)
SELECT p.id, 'MIL-POL-SG', 'Sin guarnición', TRUE, FALSE, TRUE
FROM product p WHERE p.slug = 'milanesa-pollo';

INSERT INTO product_variant (product_id, sku, name, is_active, is_featured, is_daily_menu)
SELECT p.id, 'MIL-POL-PUR', 'Con puré', TRUE, TRUE, TRUE
FROM product p WHERE p.slug = 'milanesa-pollo';

-- MILANESA DE PESCADO
INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'MIL-PES-SG', 'Sin guarnición', TRUE
FROM product p WHERE p.slug = 'milanesa-pescado';

INSERT INTO product_variant (product_id, sku, name, is_active, is_featured)
SELECT p.id, 'MIL-PES-ALE', 'Con ensalada alemana', TRUE, TRUE
FROM product p WHERE p.slug = 'milanesa-pescado';

-- MILANESA NAPOLITANA
INSERT INTO product_variant (product_id, sku, name, is_active, is_featured, is_daily_menu)
SELECT p.id, 'MIL-NAP-CAR', 'De carne', TRUE, TRUE, TRUE
FROM product p WHERE p.slug = 'milanesa-napolitana';

INSERT INTO product_variant (product_id, sku, name, is_active, is_featured)
SELECT p.id, 'MIL-NAP-FRI', 'Con fritas', TRUE, TRUE
FROM product p WHERE p.slug = 'milanesa-napolitana';

-- ENSALADAS
INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'ENS-MIX-IND', 'Individual', TRUE
FROM product p WHERE p.slug = 'ensalada-mixta';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'ENS-RUS-IND', 'Individual', TRUE
FROM product p WHERE p.slug = 'ensalada-rusa';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'ENS-CES-IND', 'Individual', TRUE
FROM product p WHERE p.slug = 'ensalada-cesar';

-- PLATOS PRINCIPALES
INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'CHU-ENS', 'Con ensalada', TRUE
FROM product p WHERE p.slug = 'churrasco';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'CHU-PUR', 'Con puré y fritas', TRUE
FROM product p WHERE p.slug = 'churrasco';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'BON-EST-PUR', 'Con puré', TRUE
FROM product p WHERE p.slug = 'bondiola-estofada';

INSERT INTO product_variant (product_id, sku, name, is_active, is_featured, is_daily_menu)
SELECT p.id, 'MUS-POL-PYB', 'Con papas y boniatos', TRUE, TRUE, TRUE
FROM product p WHERE p.slug = 'muslo-pollo';

INSERT INTO product_variant (product_id, sku, name, is_active, is_daily_menu)
SELECT p.id, 'MUS-POL-PUR', 'Con puré', TRUE, TRUE
FROM product p WHERE p.slug = 'muslo-pollo';

-- GUISO
INSERT INTO product_variant (product_id, sku, name, is_active, is_daily_menu)
SELECT p.id, 'GUI-LEN-P', 'Porción', TRUE, TRUE
FROM product p WHERE p.slug = 'guiso-lentejas';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'GUI-LEN-CH', 'Chica', TRUE
FROM product p WHERE p.slug = 'guiso-lentejas';

-- TARTAS
INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'TAR-JYQ-IND', 'Individual', TRUE
FROM product p WHERE p.slug = 'tarta-jamon-queso';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'TAR-JYQ-ENT', 'Entera', TRUE
FROM product p WHERE p.slug = 'tarta-jamon-queso';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'TAR-CAP-IND', 'Individual', TRUE
FROM product p WHERE p.slug = 'tarta-caprese';

INSERT INTO product_variant (product_id, sku, name, is_active, is_featured)
SELECT p.id, 'TAR-CAP-ENT', 'Entera', TRUE, TRUE
FROM product p WHERE p.slug = 'tarta-caprese';

-- EXTRAS (guarniciones sueltas)
INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'PUR-PAP-P', 'Porción', TRUE
FROM product p WHERE p.slug = 'pure-papa';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'PAP-FRI-P', 'Porción', TRUE
FROM product p WHERE p.slug = 'papas-fritas';

INSERT INTO product_variant (product_id, sku, name, is_active)
SELECT p.id, 'ARR-BLA-P', 'Porción', TRUE
FROM product p WHERE p.slug = 'arroz-blanco';

-- ===============================
-- 3. PRECIOS (UYU)
-- ===============================

-- EMPANADAS
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 80.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-CAR-U';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 900.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-CAR-D';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 90.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-JYQ-U';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 1000.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-JYQ-D';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 90.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-CAP-U';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 1000.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-CAP-D';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 80.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-VER-U';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 900.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-VER-D';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 90.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-CPU-U';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 1000.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'EMP-CPU-D';

-- PASTAS
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 310.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CAN-CAR-P';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 310.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CAN-VER-P';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 280.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CAN-VER-CH';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 290.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'NOQ-PAP-BOL';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 310.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'NOQ-PAP-SAL';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 310.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'NOQ-PAP-TUC';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'NOQ-PAP-XL';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'LAS-CAR-P';

-- POSTRES
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 120.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'FLA-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 600.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'FLA-ENT';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 120.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CHA-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 700.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CHA-20CM';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 1100.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CHA-25CM';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 200.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CHE-ORE-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 120.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'TOR-AVE-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'TOR-AVE-ENT';

-- MILANESAS
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 280.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-CAR-SG';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 300.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-CAR-PUR';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-CAR-FRI';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 300.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-CAR-RUS';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 280.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-POL-SG';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 290.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-POL-PUR';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 160.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-PES-SG';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-PES-ALE';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 500.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-NAP-CAR';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 450.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MIL-NAP-FRI';

-- ENSALADAS
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 250.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'ENS-MIX-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 250.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'ENS-RUS-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'ENS-CES-IND';

-- PLATOS PRINCIPALES
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 400.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CHU-ENS';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'CHU-PUR';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'BON-EST-PUR';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 350.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MUS-POL-PYB';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 290.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'MUS-POL-PUR';

-- GUISOS
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 300.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'GUI-LEN-P';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 200.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'GUI-LEN-CH';

-- TARTAS
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 120.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'TAR-JYQ-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 550.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'TAR-JYQ-ENT';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 300.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'TAR-CAP-IND';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 550.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'TAR-CAP-ENT';

-- EXTRAS (guarniciones)
INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 150.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'PUR-PAP-P';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 300.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'PAP-FRI-P';

INSERT INTO price_history (product_variant_id, price, currency, valid_from)
SELECT v.id, 0.00, 'UYU', CURRENT_TIMESTAMP FROM product_variant v WHERE v.sku = 'ARR-BLA-P';

-- ===============================
-- 4. TAGS DE PRODUCTOS
-- ===============================
INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'empanada-caprese' AND t.slug = 'vegetariano';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'empanada-verdura' AND t.slug = 'vegetariano';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'empanada-verdura-ricota' AND t.slug = 'vegetariano';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'canelones-verdura' AND t.slug = 'vegetariano';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'noquis-papa' AND t.slug = 'vegetariano';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'ensalada-mixta' AND t.slug = 'vegetariano';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'ensalada-mixta' AND t.slug = 'light';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'ensalada-cesar' AND t.slug = 'light';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'tarta-caprese' AND t.slug = 'vegetariano';

INSERT INTO product_tag (product_id, tag_id)
SELECT p.id, t.id FROM product p, tag t
WHERE p.slug = 'torta-avena-banana' AND t.slug = 'sin-azucar';
