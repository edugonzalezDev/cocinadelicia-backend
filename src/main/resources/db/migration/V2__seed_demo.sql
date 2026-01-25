-- =========================================
-- Cocina DeLicia - H2 Seed Demo (consolidado y expandido)
-- Datos limpios normalizados del menú completo
-- =========================================

-- ===============================
-- 1) Catálogo: categorías y tags
-- ===============================
INSERT INTO category (id, name, slug, description)
VALUES
  (1,  'Empanadas',           'empanadas',           'Empanadas caseras de diversos sabores'),
  (2,  'Pastas',              'pastas',              'Pastas frescas, canelones, ravioles, ñoquis y tallarines'),
  (3,  'Postres',             'postres',             'Dulces y postres caseros'),
  (4,  'Milanesas',           'milanesas',           'Milanesas caseras de carne, pollo, pescado y vegetales'),
  (5,  'Ensaladas',           'ensaladas',           'Ensaladas frescas y livianas'),
  (6,  'Platos Principales',  'platos-principales',  'Churrascos, pecetos, matambres, bondiolaas y más'),
  (7,  'Guisos y Cazuelas',   'guisos-cazuelas',     'Guisos de lentejas, mondongo, estofados'),
  (8,  'Tartas y Tortillas',  'tartas-tortillas',    'Tartas de J&Q, zapallito, puerro, pascualina'),
  (9,  'Pastel y Polenta',    'pastel-polenta',      'Pasteles y polentas rellenas'),
  (10, 'Verduras',            'verduras',            'Verduras al horno, rellenas, salteadas'),
  (11, 'Panadería y Pizzas',  'panaderia-pizzas',    'Pan casero, pizzas, fainá, pre-pizzas'),
  (12, 'Extras',              'extras',              'Guarniciones, propinas y adicionales')
;

INSERT INTO tag (id, name, slug)
VALUES
  (1, 'Vegetariano',  'vegetariano'),
  (2, 'Vegano',       'vegano'),
  (3, 'Sin TACC',     'sin-tacc'),
  (4, 'Light',        'light'),
  (5, 'Picante',      'picante'),
  (6, 'Sin Azúcar',   'sin-azucar'),
  (7, 'Entero',       'entero'),
  (8, 'Sin Panceta',  'sin-panceta')
;

-- ===============================
-- 2) Productos (menú completo normalizado)
-- ===============================

-- EMPANADAS (categoría 1)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (1,  1, 'Empanada de Carne',           'empanada-carne',           'Empanada de carne cortada a cuchillo', 10.00, TRUE),
  (2,  1, 'Empanada de Jamón y Queso',   'empanada-jamon-queso',     'Empanada de jamón y queso', 10.00, TRUE),
  (3,  1, 'Empanada Caprese',            'empanada-caprese',         'Mozzarella, tomate y albahaca', 10.00, TRUE),
  (4,  1, 'Empanada de Verdura',         'empanada-verdura',         'Verduras salteadas con especias', 10.00, TRUE),
  (5,  1, 'Empanada de Atún',            'empanada-atun',            'Atún con vegetales', 10.00, TRUE),
  (6,  1, 'Empanada de Pollo',           'empanada-pollo',           'Pollo desmenuzado', 10.00, TRUE),
  (7,  1, 'Empanada de Verdura y Ricota','empanada-verdura-ricota',  'Verduras con ricota y queso', 10.00, TRUE),
  (8,  1, 'Empanada de Carne y Pasas',   'empanada-carne-pasas',     'Carne con pasas de uva', 10.00, TRUE),
  (9,  1, 'Empanada de Queso y Choclo',  'empanada-queso-choclo',    'Queso con granos de choclo', 10.00, TRUE);

-- PASTAS (categoría 2)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (10, 2, 'Canelones de Carne',          'canelones-carne',          'Canelones rellenos de carne', 10.00, TRUE),
  (11, 2, 'Canelones de Verdura',        'canelones-verdura',        'Canelones de verdura', 10.00, TRUE),
  (12, 2, 'Canelones de Pollo',          'canelones-pollo',          'Canelones rellenos de pollo', 10.00, TRUE),
  (13, 2, 'Canelones de Choclo',         'canelones-choclo',         'Canelones de choclo cremoso', 10.00, TRUE),
  (14, 2, 'Canelones Mixtos',            'canelones-mixtos',         'Canelones de pollo y choclo', 10.00, TRUE),
  (15, 2, 'Ravioles de Verdura',         'ravioles-verdura',         'Ravioles rellenos de verdura', 10.00, TRUE),
  (16, 2, 'Ravioles Caprese',            'ravioles-caprese',         'Ravioles de mozzarella y tomate', 10.00, TRUE),
  (17, 2, 'Ravioles de Calabaza',        'ravioles-calabaza',        'Ravioles de calabaza y mozzarella', 10.00, TRUE),
  (18, 2, 'Ñoquis de Papa',              'noquis-papa',              'Ñoquis caseros de papa', 10.00, TRUE),
  (19, 2, 'Ñoquis de Espinaca',          'noquis-espinaca',          'Ñoquis de espinaca', 10.00, TRUE),
  (20, 2, 'Tallarines',                  'tallarines',               'Tallarines frescos', 10.00, TRUE),
  (21, 2, 'Tallarines de Espinaca',      'tallarines-espinaca',      'Tallarines de espinaca', 10.00, TRUE),
  (22, 2, 'Tallarines de Verdura',       'tallarines-verdura',       'Tallarines con vegetales', 10.00, TRUE),
  (23, 2, 'Lasagna',                     'lasagna',                  'Lasagna de carne', 10.00, TRUE),
  (24, 2, 'Lasagna de Verdura',          'lasagna-verdura',          'Lasagna de verduras', 10.00, TRUE),
  (25, 2, 'Lasagna de Berenjena',        'lasagna-berenjena',        'Lasagna con berenjenas', 10.00, TRUE),
  (26, 2, 'Lasagna de Pollo y Choclo',   'lasagna-pollo-choclo',     'Lasagna mixta de pollo y choclo', 10.00, TRUE);

-- POSTRES (categoría 3)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (28, 3, 'Flan Casero',                 'flan-casero',              'Flan de huevo con caramelo', 10.00, TRUE),
  (29, 3, 'Chajá',                       'chaja',                    'Postre uruguayo con merengue y duraznos', 10.00, TRUE),
  (30, 3, 'Lemon Pie',                   'lemon-pie',                'Tarta de limón con merengue', 10.00, TRUE),
  (31, 3, 'Cheesecake',                  'cheesecake',               'Tarta de queso', 10.00, TRUE),
  (32, 3, 'Cheesecake de Frutilla',      'cheesecake-frutilla',      'Cheesecake con frutillas', 10.00, TRUE),
  (33, 3, 'Cheesecake de Frutos Rojos',  'cheesecake-frutos-rojos',  'Cheesecake con frutos rojos', 10.00, TRUE),
  (34, 3, 'Cheesecake de Oreo',          'cheesecake-oreo',          'Cheesecake con galletas oreo', 10.00, TRUE),
  (35, 3, 'Arroz con Leche',             'arroz-leche',              'Arroz con leche casero', 10.00, TRUE),
  (36, 3, 'Crumble de Manzana',          'crumble-manzana',          'Crumble de manzana con canela', 10.00, TRUE),
  (37, 3, 'Mousse de Chocolate',         'mousse-chocolate',         'Mousse cremoso de chocolate', 10.00, TRUE),
  (38, 3, 'Mousse de Frutilla',          'mousse-frutilla',          'Mousse de frutilla', 10.00, TRUE),
  (39, 3, 'Brownie',                     'brownie',                  'Brownie de chocolate con dulce de leche', 10.00, TRUE),
  (40, 3, 'Pastafrola',                  'pastafrola',               'Pastafrola de membrillo', 10.00, TRUE),
  (41, 3, 'Torta de Chocolate',          'torta-chocolate',          'Torta húmeda de chocolate', 10.00, TRUE),
  (42, 3, 'Torta de Banana y Zanahoria', 'torta-banana-zanahoria',   'Torta saludable de banana y zanahoria', 10.00, TRUE),
  (43, 3, 'Torta de Avena y Banana',     'torta-avena-banana',       'Torta fit de avena y banana', 10.00, TRUE),
  (44, 3, 'Torta Negra Maluca',          'torta-negra-maluca',       'Torta húmeda de chocolate intenso', 10.00, TRUE),
  (45, 3, 'Delicia de Chocolate',        'delicia-chocolate',        'Postre cremoso de chocolate', 10.00, TRUE),
  (46, 3, 'Tarteleta de Frutilla',       'tarteleta-frutilla',       'Tarteleta con crema y frutilla', 10.00, TRUE),
  (47, 3, 'Tarta de Crema y Frutilla',   'tarta-crema-frutilla',     'Tarta grande de crema pastelera y frutilla', 10.00, TRUE),
  (48, 3, 'Noiset',                      'noiset',                   'Postre de nuez y chocolate', 10.00, TRUE),
  (49, 3, 'Ensalada de Frutas',          'ensalada-frutas',          'Ensalada de frutas frescas', 10.00, TRUE),
  (50, 3, 'Torta de Cumpleaños',         'torta-cumpleanos',         'Torta personalizada para eventos', 10.00, TRUE);

-- MILANESAS (categoría 4)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (51, 4, 'Milanesa de Carne',           'milanesa-carne',           'Milanesa de carne vacuna', 10.00, TRUE),
  (52, 4, 'Milanesa de Pollo',           'milanesa-pollo',           'Milanesa de pechuga de pollo', 10.00, TRUE),
  (53, 4, 'Milanesa de Pescado',         'milanesa-pescado',         'Milanesa de filete de pescado', 10.00, TRUE),
  (54, 4, 'Milanesa de Soja',            'milanesa-soja',            'Milanesa vegetariana de soja', 10.00, TRUE),
  (55, 4, 'Milanesa de Berenjena',       'milanesa-berenjena',       'Milanesa de berenjena', 10.00, TRUE),
  (56, 4, 'Milanesa de Zapallito',       'milanesa-zapallito',       'Milanesa de zapallito', 10.00, TRUE),
  (57, 4, 'Milanesa de Zucchini',        'milanesa-zucchini',        'Milanesa de zucchini', 10.00, TRUE),
  (58, 4, 'Milanesa Napolitana',         'milanesa-napolitana',      'Milanesa con salsa, jamón y muzzarella', 10.00, TRUE),
  (59, 4, 'Milanesa al Pan',             'milanesa-pan',             'Milanesa servida en pan', 10.00, TRUE),
  (60, 4, 'Milanesa en 2 Panes',         'milanesa-2-panes',         'Milanesa doble en pan', 10.00, TRUE);

-- ENSALADAS (categoría 5)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (61, 5, 'Ensalada Mixta',              'ensalada-mixta',           'Lechuga, tomate, cebolla, zanahoria', 10.00, TRUE),
  (62, 5, 'Ensalada Rusa',               'ensalada-rusa',            'Papa, zanahoria, arvejas y mayonesa', 10.00, TRUE),
  (63, 5, 'Ensalada Alemana',            'ensalada-alemana',         'Papa, pepinillos y mayonesa', 10.00, TRUE),
  (64, 5, 'Ensalada César',              'ensalada-cesar',           'Lechuga, pollo, queso parmesano', 10.00, TRUE),
  (65, 5, 'Ensalada de Repollo',         'ensalada-repollo',         'Repollo con zanahoria', 10.00, TRUE),
  (66, 5, 'Ensalada de Vegetales',       'ensalada-vegetales',       'Mix de vegetales frescos', 10.00, TRUE),
  (67, 5, 'Ensalada de Pollo y Palta',   'ensalada-pollo-palta',     'Pollo, palta, pepino y choclo', 10.00, TRUE),
  (68, 5, 'Ensalada de Quinoa',          'ensalada-quinoa',          'Quinoa con vegetales', 10.00, TRUE),
  (69, 5, 'Ensalada de Brócoli',         'ensalada-brocoli',         'Brócoli, lechuga y choclo', 10.00, TRUE),
  (70, 5, 'Salpicón de Pollo',           'salpicon-pollo',           'Ensalada fría de pollo desmenuzado', 10.00, TRUE);

-- PLATOS PRINCIPALES (categoría 6)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (71, 6, 'Churrasco',                   'churrasco',                'Churrasco a la plancha', 10.00, TRUE),
  (72, 6, 'Churrasco Encebollado',       'churrasco-encebollado',    'Churrasco con cebolla salteada', 10.00, TRUE),
  (73, 6, 'Bife a la Portuguesa',        'bife-portuguesa',          'Bife con huevo frito', 10.00, TRUE),
  (74, 6, 'Colita de Cuadril',           'colita-cuadril',           'Colita de cuadril al horno', 10.00, TRUE),
  (75, 6, 'Peceto',                      'peceto',                   'Peceto mechado', 10.00, TRUE),
  (76, 6, 'Matambre a la Leche',         'matambre-leche',           'Matambre cocido en leche', 10.00, TRUE),
  (77, 6, 'Matambre Relleno',            'matambre-relleno',         'Matambre arrollado con verduras', 10.00, TRUE),
  (78, 6, 'Bondiola Estofada',           'bondiola-estofada',        'Bondiola de cerdo estofada', 10.00, TRUE),
  (79, 6, 'Bondiola Rellena',            'bondiola-rellena',         'Bondiola rellena con especias', 10.00, TRUE),
  (80, 6, 'Costillas de Cerdo',          'costillas-cerdo',          'Costillas al horno con especias', 10.00, TRUE),
  (81, 6, 'Pamplonas',                   'pamplonas',                'Carne arrollada con jamón y queso', 10.00, TRUE),
  (82, 6, 'Arrollado de Carne',          'arrollado-carne',          'Carne arrollada al horno', 10.00, TRUE),
  (83, 6, 'Pan de Carne',                'pan-carne',                'Pan de carne con verduras', 10.00, TRUE),
  (84, 6, 'Lengua a la Vinagreta',       'lengua-vinagreta',         'Lengua cocida con vinagreta', 10.00, TRUE),
  (85, 6, 'Muslo de Pollo',              'muslo-pollo',              'Muslo al horno con especias', 10.00, TRUE),
  (86, 6, 'Pechuga de Pollo',            'pechuga-pollo',            'Pechuga a la plancha', 10.00, TRUE),
  (87, 6, 'Pechuga Rellena',             'pechuga-rellena',          'Pechuga rellena con jamón y queso', 10.00, TRUE),
  (88, 6, 'Pollo Arrollado',             'pollo-arrollado',          'Pollo arrollado al horno', 10.00, TRUE),
  (89, 6, 'Strogonoff de Pollo',         'strogonoff-pollo',         'Pollo en salsa cremosa', 10.00, TRUE),
  (90, 6, 'Strogonoff de Carne',         'strogonoff-carne',         'Carne en salsa cremosa', 10.00, TRUE),
  (91, 6, 'Chop Suey de Pollo',          'chop-suey-pollo',          'Pollo salteado con vegetales al wok', 10.00, TRUE),
  (92, 6, 'Chop Suey de Cerdo',          'chop-suey-cerdo',          'Cerdo salteado con vegetales', 10.00, TRUE),
  (93, 6, 'Chop Suey de Carne',          'chop-suey-carne',          'Carne salteada con vegetales', 10.00, TRUE),
  (94, 6, 'Pescado a la Plancha',        'pescado-plancha',          'Filete de pescado a la plancha', 10.00, TRUE),
  (95, 6, 'Miniaturas de Pescado',       'miniaturas-pescado',       'Bocaditos de pescado rebozados', 10.00, TRUE),
  (96, 6, 'Albóndigas',                  'albondigas',               'Albóndigas de carne en salsa', 10.00, TRUE),
  (97, 6, 'Hamburguesas',                'hamburguesas',             'Hamburguesas caseras de carne', 10.00, TRUE),
  (98, 6, 'Hamburguesas Veganas',        'hamburguesas-veganas',     'Hamburguesas vegetales', 10.00, TRUE),
  (99, 6, 'Hamburguesas Vegetarianas',   'hamburguesas-vegetarianas','Hamburguesas de garbanzo y espinaca', 10.00, TRUE),
  (100,6, 'Hamburguesa Escondida',       'hamburguesa-escondida',    'Hamburguesa con queso dentro', 10.00, TRUE),
  (101,6, 'Chivito al Plato',            'chivito-plato',            'Chivito uruguayo completo', 10.00, TRUE),
  (102,6, 'Chivito',                     'chivito',                  'Chivito en pan', 10.00, TRUE);

-- GUISOS Y CAZUELAS (categoría 7)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (103,7, 'Guiso de Lentejas',           'guiso-lentejas',           'Guiso casero de lentejas', 10.00, TRUE),
  (104,7, 'Guiso de Campo',              'guiso-campo',              'Guiso de campo con verduras', 10.00, TRUE),
  (105,7, 'Cazuela de Mondongo',         'cazuela-mondongo',         'Cazuela de mondongo tradicional', 10.00, TRUE),
  (106,7, 'Feijoada',                    'feijoada',                 'Guiso brasileño de porotos negros', 10.00, TRUE),
  (107,7, 'Carne Estofada',              'carne-estofada',           'Carne estofada con verduras', 10.00, TRUE),
  (108,7, 'Estofado de Verduras',        'estofado-verduras',        'Estofado vegetariano', 10.00, TRUE),
  (109,7, 'Sopa de Verduras',            'sopa-verduras',            'Sopa de verduras casera', 10.00, TRUE),
  (110,7, 'Sopa de Pollo',               'sopa-pollo',               'Sopa de pollo con fideos', 10.00, TRUE),
  (111,7, 'Puchero',                     'puchero',                  'Puchero tradicional uruguayo', 10.00, TRUE);

-- TARTAS Y TORTILLAS (categoría 8)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (112,8, 'Tarta de Jamón y Queso',      'tarta-jamon-queso',        'Tarta clásica de jamón y queso', 10.00, TRUE),
  (113,8, 'Tarta de Zapallito',          'tarta-zapallito',          'Tarta de zapallitos verdes', 10.00, TRUE),
  (114,8, 'Tarta de Puerro',             'tarta-puerro',             'Tarta de puerros', 10.00, TRUE),
  (115,8, 'Tarta de Atún',               'tarta-atun',               'Tarta de atún con vegetales', 10.00, TRUE),
  (116,8, 'Tarta Caprese',               'tarta-caprese',            'Tarta de tomate, mozzarella y albahaca', 10.00, TRUE),
  (117,8, 'Tarta de Pollo',              'tarta-pollo',              'Tarta de pollo desmenuzado', 10.00, TRUE),
  (118,8, 'Tarta Napolitana',            'tarta-napolitana',         'Tarta con salsa, jamón y queso', 10.00, TRUE),
  (119,8, 'Tarta de Queso y Cebolla',    'tarta-queso-cebolla',      'Tarta de queso y cebolla caramelizada', 10.00, TRUE),
  (120,8, 'Pascualina',                  'pascualina',               'Tarta de espinaca y huevo', 10.00, TRUE),
  (121,8, 'Tortilla de Papa',            'tortilla-papa',            'Tortilla española de papa', 10.00, TRUE),
  (122,8, 'Tortilla de Verdura',         'tortilla-verdura',         'Tortilla de verduras variadas', 10.00, TRUE);

-- PASTEL Y POLENTA (categoría 9)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (123,9, 'Pastel de Verdura',           'pastel-verdura',           'Pastel de verdura al horno', 10.00, TRUE),
  (124,9, 'Pastel de Carne',             'pastel-carne',             'Pastel de carne con puré', 10.00, TRUE),
  (125,9, 'Pastel de Calabaza',          'pastel-calabaza',          'Pastel de calabaza', 10.00, TRUE),
  (126,9, 'Pastel de Papa y Verdura',    'pastel-papa-verdura',      'Pastel combinado', 10.00, TRUE),
  (127,9, 'Polenta Rellena',             'polenta-rellena',          'Polenta con carne y queso', 10.00, TRUE);

-- VERDURAS (categoría 10)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (128,10,'Verduras Salteadas',          'verduras-salteadas',       'Mix de verduras salteadas', 10.00, TRUE),
  (129,10,'Verduras al Horno',           'verduras-horno',           'Verduras asadas al horno', 10.00, TRUE),
  (130,10,'Verduras al Wok',             'verduras-wok',             'Verduras salteadas al wok', 10.00, TRUE),
  (131,10,'Verduras a la Plancha',       'verduras-plancha',         'Verduras grilladas', 10.00, TRUE),
  (132,10,'Zapallito Relleno',           'zapallito-relleno',        'Zapallito relleno gratinado', 10.00, TRUE),
  (133,10,'Zapallitos Rellenos',         'zapallitos-rellenos',      'Zapallitos rellenos diversos', 10.00, TRUE),
  (134,10,'Morrón Relleno',              'morron-relleno',           'Morrón relleno de verdura o carne', 10.00, TRUE),
  (135,10,'Tomates Rellenos',            'tomates-rellenos',         'Tomates rellenos de atún', 10.00, TRUE),
  (136,10,'Zucchini Relleno',            'zucchini-relleno',         'Zucchini gratinado relleno', 10.00, TRUE),
  (137,10,'Calabaza a la Plancha',       'calabaza-plancha',         'Rodajas de calabaza grilladas', 10.00, TRUE),
  (138,10,'Papas al Horno',              'papas-horno',              'Papas al horno con especias', 10.00, TRUE),
  (139,10,'Papas Noiset',                'papas-noiset',             'Papas noisette doradas', 10.00, TRUE),
  (140,10,'Croquetas de Espinaca',       'croquetas-espinaca',       'Croquetas de espinaca y queso', 10.00, TRUE),
  (141,10,'Croquetas de Verdura',        'croquetas-verdura',        'Croquetas vegetales', 10.00, TRUE),
  (142,10,'Croquetas de Arroz',          'croquetas-arroz',          'Croquetas de arroz y perejil', 10.00, TRUE),
  (143,10,'Croquetas de Papa',           'croquetas-papa',           'Croquetas de papa', 10.00, TRUE),
  (144,10,'Soufflé de Zapallito',        'souffle-zapallito',        'Soufflé de zapallito', 10.00, TRUE),
  (145,10,'Soufflé de Calabaza',         'souffle-calabaza',         'Soufflé de calabaza', 10.00, TRUE),
  (146,10,'Soufflé de Verdura',          'souffle-verdura',          'Soufflé de verduras variadas', 10.00, TRUE),
  (147,10,'Arrollados Primavera',        'arrollados-primavera',     'Arrollados primavera fritos', 10.00, TRUE),
  (148,10,'Aritos de Cebolla',           'aritos-cebolla',           'Aros de cebolla rebozados', 10.00, TRUE),
  (149,10,'Budin de Verdura',            'budin-verdura',            'Budín de verduras', 10.00, TRUE);

-- PANADERÍA Y PIZZAS (categoría 11)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (150,11,'Pan Casero',                  'pan-casero',               'Pan casero artesanal', 10.00, TRUE),
  (151,11,'Pizza',                       'pizza',                    'Pizza común con muzzarella', 10.00, TRUE),
  (152,11,'Pizza Rellena',               'pizza-rellena',            'Pizza rellena de jamón, queso y morrón', 10.00, TRUE),
  (153,11,'Pizza con Ananá',             'pizza-anana',              'Pizza con ananá y jamón', 10.00, TRUE),
  (154,11,'Pizza con Anchoas',           'pizza-anchoas',            'Pizza con muzzarella y anchoas', 10.00, TRUE),
  (155,11,'Figazza',                     'figazza',                  'Fugazza con cebolla', 10.00, TRUE),
  (156,11,'Fainá',                       'faina',                    'Fainá de harina de garbanzo', 10.00, TRUE),
  (157,11,'Pre-Pizza',                   'pre-pizza',                'Pre-pizza grande para cocinar', 10.00, TRUE),
  (158,11,'Scones de Queso',             'scones-queso',             'Scones salados de queso', 10.00, TRUE),
  (159,11,'Risoles de Pollo',            'risoles-pollo',            'Risoles rellenos de pollo', 10.00, TRUE),
  (160,11,'Risoles de Jamón y Queso',    'risoles-jamon-queso',      'Risoles de J&Q', 10.00, TRUE),
  (161,11,'Risoles de Carne',            'risoles-carne',            'Risoles de carne', 10.00, TRUE),
  (162,11,'Risoles de Queso y Choclo',   'risoles-queso-choclo',     'Risoles de queso y choclo', 10.00, TRUE);

-- EXTRAS (categoría 12)
INSERT INTO product (id, category_id, name, slug, description, tax_rate_percent, is_active) VALUES
  (163,12,'Propina',                     'propina',                  'Propina opcional', 0.00, TRUE),
  (164,12,'Arroz Blanco',                'arroz-blanco',             'Porción de arroz', 10.00, TRUE),
  (165,12,'Papas Fritas',                'papas-fritas',             'Porción de papas fritas', 10.00, TRUE),
  (166,12,'Puré de Papa',                'pure-papa',                'Puré de papa', 10.00, TRUE),
  (167,12,'Puré de Calabaza',            'pure-calabaza',            'Puré de calabaza', 10.00, TRUE),
  (168,12,'Puré de Boniato',             'pure-boniato',             'Puré de boniato', 10.00, TRUE);

-- Product ↔ Tags
INSERT INTO product_tag (product_id, tag_id) VALUES
  -- Vegetarianos
  (3,1), (4,1), (7,1), (9,1), (11,1), (13,1), (14,1), (15,1), (16,1), (19,1), (22,1), (24,1), (25,1),
  (54,1), (55,1), (56,1), (57,1), (61,1), (62,1), (63,1), (66,1), (68,1), (69,1),
  (98,1), (99,1), (100,1), (108,1), (109,1), (113,1), (114,1), (116,1), (119,1), (120,1), (122,1),
  (123,1), (125,1), (126,1), (127,1), (128,1), (129,1), (130,1), (131,1), (132,1), (133,1), (134,1), (136,1),
  (140,1), (141,1), (144,1), (145,1), (146,1), (147,1),
  -- Veganos
  (98,2), (109,2), (128,2), (130,2), (131,2),
  -- Light
  (61,4), (66,4), (67,4), (68,4), (69,4), (70,4), (109,4),
  -- Sin azúcar
  (35,6), (37,6), (38,6),
  -- Entero
  (29,7), (40,7), (41,7), (42,7), (43,7), (47,7), (50,7), (77,7), (88,7), (112,7), (113,7), (114,7), (115,7), (116,7), (117,7), (120,7);

-- ===============================
-- 3) Variantes de producto (con guarniciones y tamaños)
-- ===============================

-- EMPANADAS (1-9): Unidad y Docena
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, stock_quantity, is_featured, is_daily_menu, is_new) VALUES
  (1,  1, 'EMP-CAR-U',    'Unidad',  TRUE, TRUE,  100, FALSE, TRUE,  FALSE),
  (2,  1, 'EMP-CAR-D',    'Docena',  TRUE, TRUE,  20,  FALSE, FALSE, FALSE),
  (3,  2, 'EMP-JYQ-U',    'Unidad',  TRUE, TRUE,  80,  FALSE, FALSE, FALSE),
  (4,  2, 'EMP-JYQ-D',    'Docena',  TRUE, TRUE,  15,  FALSE, FALSE, FALSE),
  (5,  3, 'EMP-CAP-U',    'Unidad',  TRUE, TRUE,  60,  TRUE,  FALSE, FALSE),
  (6,  3, 'EMP-CAP-D',    'Docena',  TRUE, TRUE,  10,  FALSE, FALSE, FALSE),
  (7,  4, 'EMP-VER-U',    'Unidad',  TRUE, TRUE,  90,  FALSE, FALSE, FALSE),
  (8,  4, 'EMP-VER-D',    'Docena',  TRUE, TRUE,  18,  FALSE, FALSE, FALSE),
  (9,  5, 'EMP-ATU-U',    'Unidad',  TRUE, TRUE,  50,  FALSE, FALSE, FALSE),
  (10, 5, 'EMP-ATU-D',    'Docena',  TRUE, TRUE,  8,   FALSE, FALSE, FALSE),
  (11, 6, 'EMP-POL-U',    'Unidad',  TRUE, TRUE,  70,  FALSE, FALSE, FALSE),
  (12, 6, 'EMP-POL-D',    'Docena',  TRUE, TRUE,  12,  FALSE, FALSE, FALSE),
  (13, 7, 'EMP-VRI-U',    'Unidad',  TRUE, TRUE,  60,  FALSE, FALSE, FALSE),
  (14, 7, 'EMP-VRI-D',    'Docena',  TRUE, TRUE,  10,  FALSE, FALSE, FALSE),
  (15, 8, 'EMP-CPU-U',    'Unidad',  TRUE, TRUE,  40,  FALSE, FALSE, TRUE),
  (16, 8, 'EMP-CPU-D',    'Docena',  TRUE, TRUE,  5,   FALSE, FALSE, TRUE),
  (17, 9, 'EMP-QCH-U',    'Unidad',  TRUE, TRUE,  50,  FALSE, FALSE, TRUE),
  (18, 9, 'EMP-QCH-D',    'Docena',  TRUE, TRUE,  8,   FALSE, FALSE, TRUE);

-- PASTAS (10-27): Porción individual o con salsa
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_featured, is_daily_menu) VALUES
  (19, 10, 'CAN-CAR-P',   'Porción', TRUE, FALSE, FALSE, TRUE),
  (20, 11, 'CAN-VER-P',   'Porción', TRUE, FALSE, FALSE, TRUE),
  (21, 11, 'CAN-VER-S',   'Porción chica', TRUE, FALSE, FALSE, FALSE),
  (22, 12, 'CAN-POL-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (23, 13, 'CAN-CHO-P',   'Porción', TRUE, FALSE, TRUE,  TRUE),
  (24, 14, 'CAN-MIX-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (25, 15, 'RAV-VER-BOL', 'Con bolognesa', TRUE, FALSE, FALSE, FALSE),
  (26, 15, 'RAV-VER-SAL', 'Con salsa', TRUE, FALSE, FALSE, FALSE),
  (27, 16, 'RAV-CAP-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (28, 17, 'RAV-CAL-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (29, 18, 'NOQ-PAP-BOL', 'Con bolognesa', TRUE, FALSE, TRUE,  TRUE),
  (30, 18, 'NOQ-PAP-SAL', 'Con salsa', TRUE, FALSE, FALSE, FALSE),
  (31, 18, 'NOQ-PAP-TUC', 'Con tuco de pollo', TRUE, FALSE, FALSE, TRUE),
  (32, 18, 'NOQ-PAP-RSA', 'Con salsa rosa', TRUE, FALSE, FALSE, FALSE),
  (33, 18, 'NOQ-PAP-XL',  'XL con bolognesa', TRUE, FALSE, FALSE, FALSE),
  (34, 19, 'NOQ-ESP-BOL', 'Con bolognesa', TRUE, FALSE, FALSE, FALSE),
  (35, 19, 'NOQ-ESP-SAL', 'Con salsa', TRUE, FALSE, FALSE, FALSE),
  (36, 19, 'NOQ-ESP-TUC', 'Con tuco de pollo', TRUE, FALSE, FALSE, FALSE),
  (37, 20, 'TAL-BOL',     'Con bolognesa', TRUE, FALSE, FALSE, FALSE),
  (38, 20, 'TAL-CAR',     'Con caruso', TRUE, FALSE, FALSE, FALSE),
  (39, 20, 'TAL-TUC',     'Con tuco de pollo', TRUE, FALSE, FALSE, FALSE),
  (40, 20, 'TAL-RSA',     'Con salsa rosa', TRUE, FALSE, FALSE, FALSE),
  (41, 21, 'TAL-ESP-BOL', 'De espinaca con bolognesa', TRUE, FALSE, FALSE, FALSE),
  (42, 21, 'TAL-ESP-SAL', 'De espinaca con salsa', TRUE, FALSE, FALSE, FALSE),
  (43, 22, 'TAL-VER-WOK', 'Con verduras al wok', TRUE, FALSE, FALSE, FALSE),
  (44, 23, 'LAS-CAR-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (45, 24, 'LAS-VER-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (46, 25, 'LAS-BER-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (47, 26, 'LAS-PCH-P',   'Porción', TRUE, FALSE, FALSE, FALSE);

-- POSTRES (28-50): Individual o Entero
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_featured, is_daily_menu) VALUES
  (50, 28, 'FLA-IND',     'Individual', TRUE, FALSE, FALSE, TRUE),
  (51, 28, 'FLA-ENT',     'Entero',     TRUE, FALSE, FALSE, FALSE),
  (52, 29, 'CHA-IND',     'Individual', TRUE, FALSE, TRUE,  FALSE),
  (53, 29, 'CHA-20CM',    '20cm',       TRUE, FALSE, FALSE, FALSE),
  (54, 29, 'CHA-25CM',    '25cm',       TRUE, FALSE, TRUE,  FALSE),
  (55, 29, 'CHA-ENT',     'Entero',     TRUE, FALSE, FALSE, FALSE),
  (56, 30, 'LEM-IND',     'Individual', TRUE, FALSE, FALSE, FALSE),
  (57, 31, 'CHE-IND',     'Individual', TRUE, FALSE, FALSE, FALSE),
  (58, 32, 'CHE-FRU-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (59, 33, 'CHE-FRO-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (60, 34, 'CHE-ORE-IND', 'Individual', TRUE, FALSE, TRUE,  FALSE),
  (61, 35, 'ARR-LEC-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (62, 35, 'ARR-LEC-SC',  'Sin azúcar', TRUE, FALSE, FALSE, FALSE),
  (63, 36, 'CRU-MAN-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (64, 37, 'MOU-CHO-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (65, 37, 'MOU-CHO-SA',  'Sin azúcar', TRUE, FALSE, FALSE, FALSE),
  (66, 38, 'MOU-FRU-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (67, 39, 'BRO-IND',     'Individual', TRUE, FALSE, FALSE, FALSE),
  (68, 40, 'PAS-ENT',     'Entera',     TRUE, FALSE, FALSE, FALSE),
  (69, 40, 'PAS-IND',     'Individual', TRUE, FALSE, FALSE, FALSE),
  (70, 41, 'TOR-CHO-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (71, 41, 'TOR-CHO-ENT', 'Entera',     TRUE, FALSE, FALSE, FALSE),
  (72, 42, 'TOR-BAN-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (73, 42, 'TOR-BAN-ENT', 'Entera',     TRUE, FALSE, FALSE, FALSE),
  (74, 43, 'TOR-AVE-IND', 'Individual', TRUE, FALSE, TRUE,  TRUE),
  (75, 43, 'TOR-AVE-ENT', 'Entera',     TRUE, FALSE, FALSE, FALSE),
  (76, 44, 'TOR-NEG-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (77, 45, 'DEL-CHO-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (78, 46, 'TAR-FRU-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (79, 47, 'TAR-CRE-ENT', 'Entera',     TRUE, FALSE, FALSE, FALSE),
  (80, 48, 'NOI-POR',     'Porción',    TRUE, FALSE, FALSE, FALSE),
  (81, 49, 'ENS-FRU-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (82, 50, 'TOR-CUM-CH',  'Chica',      TRUE, FALSE, FALSE, FALSE),
  (83, 50, 'TOR-CUM-GR',  'Grande',     TRUE, FALSE, TRUE,  FALSE);

-- MILANESAS (51-60): Variantes con guarniciones
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_featured, is_daily_menu) VALUES
  (84,  51, 'MIL-CAR-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (85,  51, 'MIL-CAR-PUR', 'Con puré', TRUE, FALSE, FALSE, TRUE),
  (86,  51, 'MIL-CAR-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (87,  51, 'MIL-CAR-FRI', 'Con fritas', TRUE, FALSE, FALSE, FALSE),
  (88,  51, 'MIL-CAR-PYB', 'Con papas y boniatos', TRUE, FALSE, FALSE, FALSE),
  (89,  51, 'MIL-CAR-MIX', 'Con ensalada mixta', TRUE, FALSE, FALSE, FALSE),
  (90,  51, 'MIL-CAR-REP', 'Con ensalada de repollo', TRUE, FALSE, FALSE, FALSE),
  (91,  51, 'MIL-CAR-CRO', 'Con croquetas', TRUE, FALSE, FALSE, FALSE),
  (92,  51, 'MIL-CAR-NOI', 'Con papas noiset', TRUE, FALSE, FALSE, FALSE),
  (93,  52, 'MIL-POL-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (94,  52, 'MIL-POL-PUR', 'Con puré', TRUE, FALSE, TRUE,  TRUE),
  (95,  52, 'MIL-POL-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (96,  52, 'MIL-POL-FRI', 'Con fritas', TRUE, FALSE, FALSE, FALSE),
  (97,  52, 'MIL-POL-PYB', 'Con papas y boniatos', TRUE, FALSE, FALSE, TRUE),
  (98,  52, 'MIL-POL-MIX', 'Con mixta', TRUE, FALSE, FALSE, FALSE),
  (99,  52, 'MIL-POL-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (100, 53, 'MIL-PES-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (101, 53, 'MIL-PES-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (102, 53, 'MIL-PES-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (103, 53, 'MIL-PES-FRI', 'Con fritas', TRUE, FALSE, FALSE, FALSE),
  (104, 53, 'MIL-PES-ALE', 'Con ensalada alemana', TRUE, FALSE, TRUE,  FALSE),
  (105, 53, 'MIL-PES-REP', 'Con ensalada de repollo', TRUE, FALSE, FALSE, FALSE),
  (106, 54, 'MIL-SOJ-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (107, 54, 'MIL-SOJ-PUR', 'Con puré', TRUE, FALSE, TRUE,  TRUE),
  (108, 54, 'MIL-SOJ-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (109, 54, 'MIL-SOJ-FRI', 'Con fritas', TRUE, FALSE, FALSE, FALSE),
  (110, 54, 'MIL-SOJ-VER', 'Con verdura salteada', TRUE, FALSE, FALSE, FALSE),
  (111, 54, 'MIL-SOJ-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (112, 55, 'MIL-BER-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (113, 55, 'MIL-BER-VER', 'Con verduras a la plancha', TRUE, FALSE, FALSE, FALSE),
  (114, 55, 'MIL-BER-PYB', 'Con papas y boniatos', TRUE, FALSE, FALSE, FALSE),
  (115, 55, 'MIL-BER-ALE', 'Con ensalada alemana', TRUE, FALSE, FALSE, FALSE),
  (116, 55, 'MIL-BER-ENS', 'Con ensalada cocida', TRUE, FALSE, FALSE, FALSE),
  (117, 56, 'MIL-ZAP-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (118, 56, 'MIL-ZAP-FRI', 'Con fritas', TRUE, FALSE, FALSE, FALSE),
  (119, 57, 'MIL-ZUC-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (120, 58, 'MIL-NAP-CAR', 'De carne', TRUE, FALSE, TRUE,  TRUE),
  (121, 58, 'MIL-NAP-POL', 'De pollo', TRUE, FALSE, FALSE, FALSE),
  (122, 58, 'MIL-NAP-FRI', 'Con fritas', TRUE, FALSE, TRUE,  FALSE),
  (123, 58, 'MIL-NAP-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (124, 58, 'MIL-NAP-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (125, 59, 'MIL-PAN-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (126, 60, 'MIL-2PA-P',   'Porción', TRUE, FALSE, FALSE, FALSE);

-- ENSALADAS (61-70): Porciones individuales
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock) VALUES
  (127, 61, 'ENS-MIX-IND', 'Individual', TRUE, FALSE),
  (128, 62, 'ENS-RUS-IND', 'Individual', TRUE, FALSE),
  (129, 63, 'ENS-ALE-IND', 'Individual', TRUE, FALSE),
  (130, 64, 'ENS-CES-IND', 'Individual', TRUE, FALSE),
  (131, 65, 'ENS-REP-IND', 'Individual', TRUE, FALSE),
  (132, 66, 'ENS-VEG-IND', 'Individual', TRUE, FALSE),
  (133, 67, 'ENS-PPA-IND', 'Individual', TRUE, FALSE),
  (134, 68, 'ENS-QUI-IND', 'Individual', TRUE, FALSE),
  (135, 69, 'ENS-BRO-IND', 'Individual', TRUE, FALSE),
  (136, 70, 'SAL-POL-IND', 'Individual', TRUE, FALSE);

-- PLATOS PRINCIPALES (71-102): Variantes con guarniciones
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_featured, is_daily_menu) VALUES
  (137, 71, 'CHU-ENS',     'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (138, 71, 'CHU-PUR-FRI', 'Con puré y fritas', TRUE, FALSE, FALSE, FALSE),
  (139, 72, 'CHU-ENC-PYB', 'Con papas, boniatos, rusa y huevos', TRUE, FALSE, TRUE,  FALSE),
  (140, 73, 'BIF-POR-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (141, 74, 'COL-CUA-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (142, 74, 'COL-CUA-BON', 'Con boniatos', TRUE, FALSE, FALSE, FALSE),
  (143, 75, 'PEC-PUR',     'Con puré', TRUE, FALSE, FALSE, FALSE),
  (144, 75, 'PEC-GUI',     'Con guarnición', TRUE, FALSE, FALSE, FALSE),
  (145, 76, 'MAT-LEC-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (146, 76, 'MAT-LEC-BON', 'Con boniatos', TRUE, FALSE, FALSE, FALSE),
  (147, 76, 'MAT-LEC-XL',  'XL con puré', TRUE, FALSE, FALSE, FALSE),
  (148, 77, 'MAT-REL-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (149, 77, 'MAT-REL-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (150, 77, 'MAT-REL-PYB', 'Con papas y boniatos', TRUE, FALSE, FALSE, FALSE),
  (151, 77, 'MAT-REL-ENT', 'Entero', TRUE, FALSE, TRUE,  FALSE),
  (152, 78, 'BON-EST-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (153, 78, 'BON-EST-ARR', 'Con arroz', TRUE, FALSE, FALSE, FALSE),
  (154, 79, 'BON-REL-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (155, 79, 'BON-REL-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (156, 79, 'BON-REL-MIX', 'Con mixta', TRUE, FALSE, FALSE, FALSE),
  (157, 80, 'COS-CER-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (158, 80, 'COS-CER-PYB', 'Con papas y boniatos', TRUE, FALSE, TRUE,  FALSE),
  (159, 80, 'COS-CER-BON', 'Con boniato', TRUE, FALSE, FALSE, FALSE),
  (160, 81, 'PAM-PUR',     'Con puré', TRUE, FALSE, FALSE, FALSE),
  (161, 81, 'PAM-MIX',     'Con mixta', TRUE, FALSE, FALSE, FALSE),
  (162, 81, 'PAM-RUS',     'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (163, 81, 'PAM-PYB',     'Con papas y boniatos', TRUE, FALSE, FALSE, FALSE),
  (164, 81, 'PAM-ALE',     'Con ensalada alemana', TRUE, FALSE, FALSE, FALSE),
  (165, 82, 'ARR-CAR-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (166, 83, 'PAN-CAR-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (167, 83, 'PAN-CAR-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (168, 84, 'LEN-VIN-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (169, 84, 'LEN-VIN-P',   'Con guarnición', TRUE, FALSE, FALSE, FALSE),
  (170, 85, 'MUS-POL-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (171, 85, 'MUS-POL-PUR', 'Con puré', TRUE, FALSE, FALSE, TRUE),
  (172, 85, 'MUS-POL-PYB', 'Con papas y boniatos', TRUE, FALSE, TRUE,  TRUE),
  (173, 85, 'MUS-POL-MIX', 'Con mixta', TRUE, FALSE, FALSE, FALSE),
  (174, 85, 'MUS-POL-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (175, 85, 'MUS-POL-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (176, 85, 'MUS-POL-BON', 'Con boniatos', TRUE, FALSE, FALSE, FALSE),
  (177, 85, 'MUS-POL-XL',  'XL con ensalada', TRUE, FALSE, FALSE, FALSE),
  (178, 86, 'PEC-POL-GUI', 'Con guarnición', TRUE, FALSE, FALSE, FALSE),
  (179, 87, 'PEC-REL-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (180, 87, 'PEC-REL-PUR', 'Con puré', TRUE, FALSE, TRUE,  FALSE),
  (181, 87, 'PEC-REL-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (182, 87, 'PEC-REL-PYB', 'Con papas y boniatos', TRUE, FALSE, FALSE, FALSE),
  (183, 87, 'PEC-REL-NAR', 'Con puré naranja', TRUE, FALSE, FALSE, FALSE),
  (184, 88, 'POL-ARR-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (185, 88, 'POL-ARR-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (186, 88, 'POL-ARR-MIX', 'Con mixta', TRUE, FALSE, FALSE, FALSE),
  (187, 88, 'POL-ARR-ENT', 'Entero', TRUE, FALSE, TRUE,  FALSE),
  (188, 89, 'STR-POL-ARR', 'Con arroz', TRUE, FALSE, FALSE, FALSE),
  (189, 89, 'STR-POL-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (190, 90, 'STR-CAR-ARR', 'Con arroz', TRUE, FALSE, FALSE, FALSE),
  (191, 91, 'CHO-POL-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (192, 91, 'CHO-POL-FID', 'Con fideos', TRUE, FALSE, FALSE, FALSE),
  (193, 92, 'CHO-CER-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (194, 93, 'CHO-CAR-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (195, 94, 'PES-PLA-GUI', 'Con guarnición', TRUE, FALSE, FALSE, FALSE),
  (196, 94, 'PES-PLA-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (197, 95, 'MIN-PES-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (198, 95, 'MIN-PES-TOR', 'Con tortilla', TRUE, FALSE, FALSE, FALSE),
  (199, 95, 'MIN-PES-REP', 'Con ensalada de repollo', TRUE, FALSE, TRUE,  FALSE),
  (200, 95, 'MIN-PES-RUS', 'Con rusa', TRUE, FALSE, FALSE, FALSE),
  (201, 95, 'MIN-PES-FRI', 'Con fritas', TRUE, FALSE, FALSE, FALSE),
  (202, 95, 'MIN-PES-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (203, 95, 'MIN-PES-ALE', 'Con ensalada alemana', TRUE, FALSE, FALSE, FALSE),
  (204, 95, 'MIN-PES-ARR', 'Con arroz', TRUE, FALSE, FALSE, FALSE),
  (205, 96, 'ALB-PUR',     'Con puré', TRUE, FALSE, FALSE, FALSE),
  (206, 96, 'ALB-ARR',     'Con arroz', TRUE, FALSE, FALSE, FALSE),
  (207, 96, 'ALB-POR',     'Porción', TRUE, FALSE, FALSE, FALSE),
  (208, 97, 'HAM-FRI',     'Con fritas', TRUE, FALSE, FALSE, FALSE),
  (209, 97, 'HAM-PUR',     'Con puré', TRUE, FALSE, FALSE, FALSE),
  (210, 97, 'HAM-ARR',     'Con arroz', TRUE, FALSE, FALSE, FALSE),
  (211, 97, 'HAM-GRA',     'Empanadas con fritas', TRUE, FALSE, FALSE, FALSE),
  (212, 97, 'HAM-PGR',     'Con papas gratinadas', TRUE, FALSE, FALSE, FALSE),
  (213, 98, 'HAM-VEG-RUS', 'Con rusa', TRUE, FALSE, TRUE,  FALSE),
  (214, 98, 'HAM-VEG-PUR', 'Con puré', TRUE, FALSE, FALSE, FALSE),
  (215, 98, 'HAM-VEG-CAL', 'Con calabaza a la plancha', TRUE, FALSE, FALSE, FALSE),
  (216, 99, 'HAM-VET-NOI', 'Con papas noiset', TRUE, FALSE, FALSE, FALSE),
  (217, 99, 'HAM-VET-CAL', 'Con zapallo a la plancha', TRUE, FALSE, FALSE, FALSE),
  (218, 100,'HAM-ESC-NOR', 'Normal', TRUE, FALSE, FALSE, FALSE),
  (219, 100,'HAM-ESC-SPA', 'Sin panceta', TRUE, FALSE, FALSE, FALSE),
  (220, 100,'HAM-ESC-VEG', 'Vegetariana', TRUE, FALSE, TRUE,  FALSE),
  (221, 100,'HAM-ESC-CAR', 'Con carne picada', TRUE, FALSE, FALSE, FALSE),
  (222, 101,'CHI-PLA-P',   'Porción', TRUE, FALSE, FALSE, FALSE),
  (223, 101,'CHI-PLA-FRI', 'Con fritas', TRUE, FALSE, TRUE,  FALSE),
  (224, 102,'CHI-PAN',     'En pan', TRUE, FALSE, FALSE, FALSE);

-- GUISOS Y CAZUELAS (103-111): Porciones
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_daily_menu) VALUES
  (225, 103,'GUI-LEN-P',   'Porción', TRUE, FALSE, TRUE),
  (226, 103,'GUI-LEN-CH',  'Chica', TRUE, FALSE, FALSE),
  (227, 103,'CAZ-LEN-P',   'Cazuela', TRUE, FALSE, FALSE),
  (228, 104,'GUI-CAM-P',   'Porción', TRUE, FALSE, TRUE),
  (229, 105,'CAZ-MON-P',   'Porción', TRUE, FALSE, FALSE),
  (230, 105,'CAZ-MON-PR',  'Promo', TRUE, FALSE, FALSE),
  (231, 106,'FEI-P',       'Porción', TRUE, FALSE, FALSE),
  (232, 107,'CAR-EST-P',   'Con verduras', TRUE, FALSE, FALSE),
  (233, 108,'EST-VER-P',   'Porción', TRUE, FALSE, FALSE),
  (234, 109,'SOP-VER-P',   'Porción', TRUE, FALSE, FALSE),
  (235, 109,'SOP-VPO-P',   'Con pollo', TRUE, FALSE, FALSE),
  (236, 110,'SOP-POL-P',   'Porción', TRUE, FALSE, FALSE),
  (237, 111,'PUC-P',       'Porción', TRUE, FALSE, FALSE);

-- TARTAS Y TORTILLAS (112-122): Individual y Entera
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_featured, is_daily_menu) VALUES
  (238, 112,'TAR-JYQ-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (239, 112,'TAR-JYQ-ENT', 'Entera', TRUE, FALSE, FALSE, FALSE),
  (240, 113,'TAR-ZAP-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (241, 113,'TAR-ZAP-ENT', 'Entera', TRUE, FALSE, TRUE,  FALSE),
  (242, 114,'TAR-PUE-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (243, 114,'TAR-PUE-ENT', 'Entera', TRUE, FALSE, FALSE, FALSE),
  (244, 115,'TAR-ATU-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (245, 115,'TAR-ATU-ENT', 'Entera', TRUE, FALSE, FALSE, FALSE),
  (246, 116,'TAR-CAP-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (247, 116,'TAR-CAP-ENT', 'Entera', TRUE, FALSE, TRUE,  FALSE),
  (248, 117,'TAR-POL-ENT', 'Entera', TRUE, FALSE, FALSE, FALSE),
  (249, 118,'TAR-NAP-ENT', 'Entera', TRUE, FALSE, FALSE, FALSE),
  (250, 119,'TAR-QCE-IND', 'Individual', TRUE, FALSE, FALSE, FALSE),
  (251, 120,'PASC-IND',    'Individual', TRUE, FALSE, FALSE, FALSE),
  (252, 120,'PASC-ENT',    'Entera', TRUE, FALSE, FALSE, FALSE),
  (253, 121,'TOR-PAP-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (254, 121,'TOR-PAP-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (255, 121,'TOR-PAP-MIX', 'Con mixta', TRUE, FALSE, FALSE, FALSE),
  (256, 122,'TOR-VER-SG',  'Sin guarnición', TRUE, FALSE, FALSE, FALSE),
  (257, 122,'TOR-VER-SQU', 'Sin queso con mixta', TRUE, FALSE, FALSE, FALSE),
  (258, 122,'TOR-VER-ENS', 'Con ensalada', TRUE, FALSE, FALSE, FALSE),
  (259, 122,'TOR-VER-MIX', 'Con mixta', TRUE, FALSE, FALSE, FALSE);

-- PASTEL Y POLENTA (123-127): Porciones
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_daily_menu) VALUES
  (260, 123,'PAS-VER-P',   'Porción', TRUE, FALSE, TRUE),
  (261, 124,'PAS-CAR-P',   'Porción', TRUE, FALSE, TRUE),
  (262, 124,'PAS-CAR-BON', 'Con boniatos', TRUE, FALSE, FALSE),
  (263, 125,'PAS-CAL-P',   'Porción', TRUE, FALSE, FALSE),
  (264, 125,'PAS-CAL-VER', 'Y verdura', TRUE, FALSE, FALSE),
  (265, 125,'PAS-CAL-CAR', 'De carne de calabaza', TRUE, FALSE, FALSE),
  (266, 126,'PAS-PPV-P',   'Porción', TRUE, FALSE, FALSE),
  (267, 127,'POL-REL-P',   'Con carne', TRUE, FALSE, FALSE),
  (268, 127,'POL-REL-SJ',  'Sin jamón', TRUE, FALSE, FALSE),
  (269, 127,'POL-REL-BOL', 'Con bolognesa', TRUE, FALSE, FALSE),
  (270, 127,'POL-REL-PYM', 'Con panceta y muzzarella', TRUE, FALSE, FALSE);

-- VERDURAS (128-149): Porciones y guarniciones
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock) VALUES
  (271, 128,'VER-SAL-P',   'Porción', TRUE, FALSE),
  (272, 129,'VER-HOR-P',   'Porción', TRUE, FALSE),
  (273, 130,'VER-WOK-P',   'Porción', TRUE, FALSE),
  (274, 131,'VER-PLA-P',   'Porción', TRUE, FALSE),
  (275, 131,'VER-PLA-XL',  'XL', TRUE, FALSE),
  (276, 132,'ZAP-REL-CHO', 'Con choclo', TRUE, FALSE),
  (277, 132,'ZAP-REL-CAR', 'Con carne', TRUE, FALSE),
  (278, 132,'ZAP-REL-ATU', 'Con atún', TRUE, FALSE),
  (279, 133,'ZAP-REL-VER', 'De verdura', TRUE, FALSE),
  (280, 133,'ZAP-REL-CAP', 'Caprese', TRUE, FALSE),
  (281, 133,'ZAP-REL-CHQ', 'Con choclo y queso', TRUE, FALSE),
  (282, 134,'MOR-REL-VER', 'De verdura', TRUE, FALSE),
  (283, 134,'MOR-REL-CAP', 'Caprese', TRUE, FALSE),
  (284, 134,'MOR-REL-CHO', 'Con choclo', TRUE, FALSE),
  (285, 134,'MOR-REL-MIX', 'Mixto', TRUE, FALSE),
  (286, 135,'TOM-REL-ATU', 'De atún', TRUE, FALSE),
  (287, 136,'ZUC-REL-P',   'Porción', TRUE, FALSE),
  (288, 137,'CAL-PLA-P',   'Porción', TRUE, FALSE),
  (289, 138,'PAP-HOR-P',   'Porción', TRUE, FALSE),
  (290, 139,'PAP-NOI-P',   'Porción', TRUE, FALSE),
  (291, 140,'CRO-ESP-U',   'Unidad', TRUE, FALSE),
  (292, 140,'CRO-ESP-POR', 'Porción', TRUE, FALSE),
  (293, 141,'CRO-VER-POR', 'Porción', TRUE, FALSE),
  (294, 141,'CRO-VER-SQU', 'Sin queso', TRUE, FALSE),
  (295, 142,'CRO-ARR-U',   'Unidad', TRUE, FALSE),
  (296, 142,'CRO-ARR-POR', 'Porción 8 unidades', TRUE, FALSE),
  (297, 143,'CRO-PAP-POR', 'Porción', TRUE, FALSE),
  (298, 143,'CRO-PAP-ENS', 'Con ensalada', TRUE, FALSE),
  (299, 144,'SOU-ZAP-P',   'Porción', TRUE, FALSE),
  (300, 145,'SOU-CAL-P',   'Porción', TRUE, FALSE),
  (301, 146,'SOU-VER-P',   'Porción', TRUE, FALSE),
  (302, 147,'ARO-PRI-P',   'Porción', TRUE, FALSE),
  (303, 147,'ARO-PRI-ARI', 'Con aritos de cebolla', TRUE, FALSE),
  (304, 148,'ARI-CEB-P',   'Porción', TRUE, FALSE),
  (305, 149,'BUD-VER-P',   'Porción', TRUE, FALSE);

-- PANADERÍA Y PIZZAS (150-162): Individual o Entero
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock, is_featured) VALUES
  (306, 150,'PAN-CAS-CH',  'Chico', TRUE, FALSE, FALSE),
  (307, 150,'PAN-CAS-GR',  'Grande', TRUE, FALSE, FALSE),
  (308, 151,'PIZ-COM-P',   'Común', TRUE, FALSE, FALSE),
  (309, 151,'PIZ-MUZ-P',   'Con muzzarella', TRUE, FALSE, FALSE),
  (310, 152,'PIZ-REL-P',   'Rellena', TRUE, FALSE, FALSE),
  (311, 153,'PIZ-ANA-P',   'Con ananá y jamón', TRUE, FALSE, FALSE),
  (312, 154,'PIZ-ANC-P',   'Con anchoas', TRUE, FALSE, FALSE),
  (313, 155,'FIG-P',       'Porción', TRUE, FALSE, FALSE),
  (314, 156,'FAI-P',       'Porción', TRUE, FALSE, FALSE),
  (315, 156,'FAI-CRE-P',   'Con crema de choclo', TRUE, FALSE, FALSE),
  (316, 157,'PRE-PIZ-GR',  'Grande', TRUE, FALSE, FALSE),
  (317, 158,'SCO-QUE-U',   'Unidad', TRUE, FALSE, FALSE),
  (318, 159,'RIS-POL-U',   'Unidad', TRUE, FALSE, FALSE),
  (319, 160,'RIS-JYQ-U',   'Unidad', TRUE, FALSE, FALSE),
  (320, 161,'RIS-CAR-U',   'Unidad', TRUE, FALSE, FALSE),
  (321, 162,'RIS-QCH-U',   'Unidad', TRUE, FALSE, FALSE);

-- EXTRAS (163-168): Guarniciones y Extras
INSERT INTO product_variant (id, product_id, sku, name, is_active, manages_stock) VALUES
  (322, 163,'PRO-20',      '$20', TRUE, FALSE),
  (323, 163,'PRO-30',      '$30', TRUE, FALSE),
  (324, 163,'PRO-100',     '$100', TRUE, FALSE),
  (325, 164,'ARR-BLA-P',   'Porción', TRUE, FALSE),
  (326, 165,'PAP-FRI-P',   'Porción', TRUE, FALSE),
  (327, 166,'PUR-PAP-P',   'Porción', TRUE, FALSE),
  (328, 167,'PUR-CAL-P',   'Porción', TRUE, FALSE),
  (329, 168,'PUR-BON-P',   'Porción', TRUE, FALSE);

-- ===============================
-- 4) Historial de precios (UYU) - Menú completo
-- ===============================

-- EMPANADAS (1-18)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (1,  1,  80.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (2,  2,  900.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (3,  3,  90.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (4,  4,  1000.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (5,  5,  90.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (6,  6,  1000.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (7,  7,  80.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (8,  8,  900.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (9,  9,  100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (10, 10, 1100.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (11, 11, 80.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (12, 12, 900.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (13, 13, 80.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (14, 14, 900.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (15, 15, 90.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (16, 16, 1000.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (17, 17, 90.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (18, 18, 1000.00,'UYU',CURRENT_TIMESTAMP, NULL);

-- PASTAS (19-49)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (19, 19, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (20, 20, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (21, 21, 280.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (22, 22, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (23, 23, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (24, 24, 320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (25, 25, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (26, 26, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (27, 27, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (28, 28, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (29, 29, 290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (30, 30, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (31, 31, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (32, 32, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (33, 33, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (34, 34, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (35, 35, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (36, 36, 330.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (37, 37, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (38, 38, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (39, 39, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (40, 40, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (41, 41, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (42, 42, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (43, 43, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (44, 44, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (45, 45, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (46, 46, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (47, 47, 350.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- POSTRES (48-81)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (48, 50, 120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (49, 51, 600.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (50, 52, 120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (51, 53, 700.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (52, 54, 1100.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (53, 55, 1000.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (54, 56, 140.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (55, 57, 160.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (56, 58, 180.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (57, 59, 180.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (58, 60, 200.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (59, 61, 100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (60, 62, 120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (61, 63, 120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (62, 64, 100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (63, 65, 120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (64, 66, 150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (65, 67, 140.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (66, 68, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (67, 69, 130.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (68, 70, 160.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (69, 71, 1200.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (70, 72, 200.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (71, 73, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (72, 74, 120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (73, 75, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (74, 76, 140.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (75, 77, 180.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (76, 78, 100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (77, 79, 850.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (78, 80, 250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (79, 81, 160.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (80, 82, 2600.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (81, 83, 3000.00,'UYU',CURRENT_TIMESTAMP, NULL);

-- MILANESAS (82-124)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (82,  84, 280.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (83,  85, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (84,  86, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (85,  87, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (86,  88, 300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (87,  89, 320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (88,  90, 320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (89,  91, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (90,  92, 350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (91,  93, 280.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (92,  94, 290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (93,  95, 290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (94,  96, 320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (95,  97, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (96,  98, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (97,  99, 310.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (98, 100,160.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (99, 101,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (100, 102,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (101, 103,450.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (102, 104,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (103, 105,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (104, 106,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (105, 107,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (106, 108,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (107, 109,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (108, 110,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (109, 111,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (110, 112,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (111, 113,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (112, 114,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (113, 115,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (114, 116,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (115, 117,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (116, 118,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (117, 119,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (118, 120,500.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (119, 121,700.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (120, 122,450.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (121, 123,500.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (122, 124,500.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (123, 125,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (124, 126,580.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- ENSALADAS (125-134)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (125, 127,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (126, 128,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (127, 129,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (128, 130,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (129, 131,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (130, 132,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (131, 133,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (132, 134,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (133, 135,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (134, 136,350.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- PLATOS PRINCIPALES (135-222) - Primera parte
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (135, 137,400.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (136, 138,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (137, 139,750.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (138, 140,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (139, 141,430.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (140, 142,400.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (141, 143,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (142, 144,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (143, 145,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (144, 146,370.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (145, 147,450.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (146, 148,330.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (147, 149,380.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (148, 150,380.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (149, 151,1200.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (150, 152,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (151, 153,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (152, 154,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (153, 155,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (154, 156,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (155, 157,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (156, 158,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (157, 159,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (158, 160,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (159, 161,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (160, 162,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (161, 163,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (162, 164,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (163, 165,400.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (164, 166,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (165, 167,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (166, 168,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (167, 169,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (168, 170,270.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (169, 171,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (170, 172,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (171, 173,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (172, 174,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (173, 175,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (174, 176,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (175, 177,450.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (176, 178,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (177, 179,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (178, 180,350.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- PLATOS PRINCIPALES (179-220) - Segunda parte
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (179, 181,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (180, 182,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (181, 183,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (182, 184,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (183, 185,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (184, 186,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (185, 187,1200.00,'UYU',CURRENT_TIMESTAMP, NULL),
  (186, 188,400.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (187, 189,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (188, 190,370.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (189, 191,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (190, 192,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (191, 193,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (192, 194,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (193, 195,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (194, 196,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (195, 197,220.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (196, 198,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (197, 199,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (198, 200,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (199, 201,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (200, 202,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (201, 203,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (202, 204,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (203, 205,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (204, 206,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (205, 207,150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (206, 208,390.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (207, 209,370.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (208, 210,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (209, 211,390.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (210, 212,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (211, 213,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (212, 214,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (213, 215,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (214, 216,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (215, 217,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (216, 218,180.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (217, 219,180.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (218, 220,200.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (219, 221,200.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (220, 222,450.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (221, 223,520.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (222, 224,580.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- GUISOS Y CAZUELAS (223-235)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (223, 225,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (224, 226,200.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (225, 227,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (226, 228,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (227, 229,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (228, 230,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (229, 231,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (230, 232,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (231, 233,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (232, 234,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (233, 235,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (234, 236,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (235, 237,380.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- TARTAS Y TORTILLAS (236-257)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (236, 238,120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (237, 239,550.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (238, 240,200.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (239, 241,550.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (240, 242,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (241, 243,550.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (242, 244,180.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (243, 245,700.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (244, 246,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (245, 247,550.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (246, 248,600.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (247, 249,500.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (248, 250,100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (249, 251,120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (250, 252,380.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (251, 253,160.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (252, 254,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (253, 255,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (254, 256,80.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (255, 257,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (256, 258,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (257, 259,290.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- PASTEL Y POLENTA (258-268)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (258, 260,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (259, 261,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (260, 262,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (261, 263,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (262, 264,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (263, 265,320.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (264, 266,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (265, 267,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (266, 268,330.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (267, 269,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (268, 270,350.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- VERDURAS (269-303)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (269, 271,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (270, 272,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (271, 273,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (272, 274,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (273, 275,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (274, 276,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (275, 277,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (276, 278,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (277, 279,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (278, 280,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (279, 281,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (280, 282,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (281, 283,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (282, 284,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (283, 285,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (284, 286,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (285, 287,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (286, 288,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (287, 289,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (288, 290,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (289, 291,25.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (290, 292,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (291, 293,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (292, 294,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (293, 295,40.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (294, 296,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (295, 297,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (296, 298,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (297, 299,150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (298, 300,150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (299, 301,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (300, 302,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (301, 303,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (302, 304,290.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (303, 305,130.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- PANADERÍA Y PIZZAS (304-319)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (304, 306,140.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (305, 307,150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (306, 308,250.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (307, 309,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (308, 310,550.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (309, 311,500.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (310, 312,500.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (311, 313,350.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (312, 314,150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (313, 315,400.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (314, 316,180.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (315, 317,25.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (316, 318,100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (317, 319,100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (318, 320,120.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (319, 321,120.00,'UYU', CURRENT_TIMESTAMP, NULL);

-- EXTRAS (320-327)
INSERT INTO price_history (id, product_variant_id, price, currency, valid_from, valid_to) VALUES
  (320, 322,20.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (321, 323,30.00, 'UYU', CURRENT_TIMESTAMP, NULL),
  (322, 324,100.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (323, 325,0.00,  'UYU', CURRENT_TIMESTAMP, NULL),
  (324, 326,300.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (325, 327,150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (326, 328,150.00,'UYU', CURRENT_TIMESTAMP, NULL),
  (327, 329,150.00,'UYU', CURRENT_TIMESTAMP, NULL);

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