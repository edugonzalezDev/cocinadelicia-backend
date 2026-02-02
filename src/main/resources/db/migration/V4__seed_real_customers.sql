-- Seed de clientes reales demo (alta de clientes y direcciones)

-- ===============================
-- 1) Clientes (usuarios Cognito)
-- ===============================
INSERT INTO app_user (id, cognito_user_id, first_name, last_name, email, phone, is_active) VALUES
  (7,  'cog-cli-007',  'Cliente',   'Genérico',             'cliente.base@customers.cdl.test',        '+59820000007', TRUE),
  (8,  'cog-cli-008',  'Andrea',    '',         'andrea.cliente@customers.cdl.test',      '+59820000008', TRUE),
  (9,  'cog-cli-009',  'Liliana',   'Lupas',           'liliana.lupas@customers.cdl.test',       '+59820000009', TRUE),
  (10, 'cog-cli-010',  'Maria',     'Cliente',         'maria.1@customers.cdl.test',             '+59820000010', TRUE),
  (11, 'cog-cli-011',  'Rosario',   'Carniceria Arco', 'rosario.carniceria@customers.cdl.test',  '+59820000011', TRUE),
  (12, 'cog-cli-012',  'Nely',      'Mecanico',        'nely.mecanico@customers.cdl.test',       '+59820000012', TRUE),
  (13, 'cog-cli-013',  'Juana',     'Carvallo',        'juana.carvallo@customers.cdl.test',      '+59820000013', TRUE),
  (14, 'cog-cli-014',  'Iosef',      'Gak',         'iosi@customers.cdl.test',                '+59820000014', TRUE),
  (15, 'cog-cli-015',  'Silvia',    'Rodriguez',       'silvia.rodriguez@customers.cdl.test',    '+59820000015', TRUE),
  (16, 'cog-cli-016',  'Macarena',  'Cliente',         'macarena.cliente@customers.cdl.test',    '+59820000016', TRUE),
  (17, 'cog-cli-017',  'Alicia',    'Peluqueria',      'alicia.peluqueria@customers.cdl.test',   '+59820000017', TRUE),
  (18, 'cog-cli-018',  'Laura',     'Peluqueria',      'laura.peluqueria@customers.cdl.test',    '+59820000018', TRUE),
  (19, 'cog-cli-019',  'Maria',     'Cliente2',        'maria.2@customers.cdl.test',             '+59820000019', TRUE),
  (20, 'cog-cli-020',  'Victoria',  'Manicura',        'victoria.manicura@customers.cdl.test',   '+59820000020', TRUE),
  (21, 'cog-cli-021',  'Estefany',  'Cliente',         'estefany@customers.cdl.test',            '+59820000021', TRUE),
  (22, 'cog-cli-022',  'Alicia',    'Cliente3',        'alicia.extra@customers.cdl.test',        '+59820000022', TRUE),
  (23, 'cog-cli-023',  'Rodrigo',   'Cliente',         'rodrigo@customers.cdl.test',             '+59820000023', TRUE),
  (24, 'cog-cli-024',  'Daniel',    'Barberia',        'daniel.barberia@customers.cdl.test',     '+59820000024', TRUE),
  (25, 'cog-cli-025',  'Vecina',    'De Nely',         'vecina.nely@customers.cdl.test',         '+59820000025', TRUE),
  (26, 'cog-cli-026',  'Beatriz',   'Cliente',         'beatriz@customers.cdl.test',             '+59820000026', TRUE),
  (27, 'cog-cli-027',  'Mariana',   'Inm Julieta',     'mariana.julieta@customers.cdl.test',     '+59820000027', TRUE),
  (28, 'cog-cli-028',  'Graciela',  'Cliente',         'graciela@customers.cdl.test',            '+59820000028', TRUE),
  (29, 'cog-cli-029',  'Fernanda',  'Cliente',         'fernanda@customers.cdl.test',            '+59820000029', TRUE),
  (30, 'cog-cli-030',  'Alicia',    'Alex',            'alicia.alex@customers.cdl.test',         '+59820000030', TRUE),
  (31, 'cog-cli-031',  'Margot',    'Cliente',         'margot@customers.cdl.test',              '+59820000031', TRUE),
  (32, 'cog-cli-032',  'Estefani',  'Cliente',         'estefani@customers.cdl.test',            '+59820000032', TRUE),
  (33, 'cog-cli-033',  'Cecilia',   'Dante',           'cecilia.dante@customers.cdl.test',       '+59820000033', TRUE),
  (34, 'cog-cli-034',  'Diego',     'Gimnasio',         'diego@customers.cdl.test',               '+59820000034', TRUE),
  (35, 'cog-cli-035',  'Jaqueline', 'Steffano',         'jaqueline@customers.cdl.test',           '+59820000035', TRUE),
  (36, 'cog-cli-036',  'Silka',     'Cliente',         'silka@customers.cdl.test',               '+59820000036', TRUE),
  (37, 'cog-cli-037',  'Lilian',     'Freelook',        'lilia.freelook@customers.cdl.test',      '+59820000037', TRUE),
  (38, 'cog-cli-039',  'Nacho',     'Gimnasio',        'nacho.gimnasio@customers.cdl.test',      '+59820000039', TRUE),
  (39, 'cog-cli-040',  'Mariana',   'Inmobiliaria',    'mariana.inmobiliaria@customers.cdl.test','+59820000040', TRUE)
;

-- ===============================
-- 2) Roles de cliente
-- ===============================
INSERT INTO user_role (user_id, role_id, assigned_at) VALUES
  (7, 4, CURRENT_TIMESTAMP),
  (8, 4, CURRENT_TIMESTAMP),
  (9, 4, CURRENT_TIMESTAMP),
  (10,4, CURRENT_TIMESTAMP),
  (11,4, CURRENT_TIMESTAMP),
  (12,4, CURRENT_TIMESTAMP),
  (13,4, CURRENT_TIMESTAMP),
  (14,4, CURRENT_TIMESTAMP),
  (15,4, CURRENT_TIMESTAMP),
  (16,4, CURRENT_TIMESTAMP),
  (17,4, CURRENT_TIMESTAMP),
  (18,4, CURRENT_TIMESTAMP),
  (19,4, CURRENT_TIMESTAMP),
  (20,4, CURRENT_TIMESTAMP),
  (21,4, CURRENT_TIMESTAMP),
  (22,4, CURRENT_TIMESTAMP),
  (23,4, CURRENT_TIMESTAMP),
  (24,4, CURRENT_TIMESTAMP),
  (25,4, CURRENT_TIMESTAMP),
  (26,4, CURRENT_TIMESTAMP),
  (27,4, CURRENT_TIMESTAMP),
  (28,4, CURRENT_TIMESTAMP),
  (29,4, CURRENT_TIMESTAMP),
  (30,4, CURRENT_TIMESTAMP),
  (31,4, CURRENT_TIMESTAMP),
  (32,4, CURRENT_TIMESTAMP),
  (33,4, CURRENT_TIMESTAMP),
  (34,4, CURRENT_TIMESTAMP),
  (35,4, CURRENT_TIMESTAMP),
  (36,4, CURRENT_TIMESTAMP),
  (37,4, CURRENT_TIMESTAMP),
  (38,4, CURRENT_TIMESTAMP),
  (39,4, CURRENT_TIMESTAMP);

-- ===============================
-- 3) Direcciones
-- ===============================
INSERT INTO customer_address (id, user_id, label, line1, line2, city, region, postal_code, reference) VALUES
  (6,  7,  'Casa',   'Calle Demo 7 123',    NULL, 'Montevideo', 'Montevideo', '11007', 'Referencia pendiente'),
  (7,  8,  'Casa',   'Calle Demo 8 123',    NULL, 'Montevideo', 'Montevideo', '11008', 'Referencia pendiente'),
  (8,  9,  'Casa',   'Calle Demo 9 123',    NULL, 'Montevideo', 'Montevideo', '11009', 'Referencia pendiente'),
  (9,  10, 'Casa',   'Calle Demo 10 123',   NULL, 'Montevideo', 'Montevideo', '11010', 'Referencia pendiente'),
  (10, 11, 'Local',  'Av Carniceria 11',    NULL, 'Montevideo', 'Montevideo', '11011', 'Actualizar referencia'),
  (11, 12, 'Taller', 'Calle Mecanico 12',   NULL, 'Montevideo', 'Montevideo', '11012', 'Actualizar referencia'),
  (12, 13, 'Casa',   'Calle Demo 13 123',   NULL, 'Montevideo', 'Montevideo', '11013', 'Referencia pendiente'),
  (13, 14, 'Casa',   'Calle Demo 14 123',   NULL, 'Montevideo', 'Montevideo', '11014', 'Referencia pendiente'),
  (14, 15, 'Casa',   'Calle Demo 15 123',   NULL, 'Montevideo', 'Montevideo', '11015', 'Referencia pendiente'),
  (15, 16, 'Casa',   'Calle Demo 16 123',   NULL, 'Montevideo', 'Montevideo', '11016', 'Referencia pendiente'),
  (16, 17, 'Local',  'Calle Peluqueria 17', NULL, 'Montevideo', 'Montevideo', '11017', 'Actualizar referencia'),
  (17, 18, 'Local',  'Calle Peluqueria 18', NULL, 'Montevideo', 'Montevideo', '11018', 'Actualizar referencia'),
  (18, 19, 'Casa',   'Calle Demo 19 123',   NULL, 'Montevideo', 'Montevideo', '11019', 'Referencia pendiente'),
  (19, 20, 'Local',  'Calle Manicura 20',   NULL, 'Montevideo', 'Montevideo', '11020', 'Actualizar referencia'),
  (20, 21, 'Casa',   'Calle Demo 21 123',   NULL, 'Montevideo', 'Montevideo', '11021', 'Referencia pendiente'),
  (21, 22, 'Casa',   'Calle Demo 22 123',   NULL, 'Montevideo', 'Montevideo', '11022', 'Referencia pendiente'),
  (22, 23, 'Casa',   'Calle Demo 23 123',   NULL, 'Montevideo', 'Montevideo', '11023', 'Referencia pendiente'),
  (23, 24, 'Local',  'Calle Barberia 24',   NULL, 'Montevideo', 'Montevideo', '11024', 'Actualizar referencia'),
  (24, 25, 'Casa',   'Calle Demo 25 123',   NULL, 'Montevideo', 'Montevideo', '11025', 'Referencia pendiente'),
  (25, 26, 'Casa',   'Calle Demo 26 123',   NULL, 'Montevideo', 'Montevideo', '11026', 'Referencia pendiente'),
  (26, 27, 'Local',  'Calle Inm Julieta 27',NULL, 'Montevideo', 'Montevideo', '11027', 'Actualizar referencia'),
  (27, 28, 'Casa',   'Calle Demo 28 123',   NULL, 'Montevideo', 'Montevideo', '11028', 'Referencia pendiente'),
  (28, 29, 'Casa',   'Calle Demo 29 123',   NULL, 'Montevideo', 'Montevideo', '11029', 'Referencia pendiente'),
  (29, 30, 'Local',  'Calle Alex 30',       NULL, 'Montevideo', 'Montevideo', '11030', 'Actualizar referencia'),
  (30, 31, 'Casa',   'Calle Demo 31 123',   NULL, 'Montevideo', 'Montevideo', '11031', 'Referencia pendiente'),
  (31, 32, 'Casa',   'Calle Demo 32 123',   NULL, 'Montevideo', 'Montevideo', '11032', 'Referencia pendiente'),
  (32, 33, 'Casa',   'Calle Demo 33 123',   NULL, 'Montevideo', 'Montevideo', '11033', 'Referencia pendiente'),
  (33, 34, 'Casa',   'Calle Demo 34 123',   NULL, 'Montevideo', 'Montevideo', '11034', 'Referencia pendiente'),
  (34, 35, 'Casa',   'Calle Demo 35 123',   NULL, 'Montevideo', 'Montevideo', '11035', 'Referencia pendiente'),
  (35, 36, 'Casa',   'Calle Demo 36 123',   NULL, 'Montevideo', 'Montevideo', '11036', 'Referencia pendiente'),
  (36, 37, 'Local',  'Calle Freelook 37',   NULL, 'Montevideo', 'Montevideo', '11037', 'Actualizar referencia'),
  (37, 38, 'Casa',   'Calle Demo 38 123',   NULL, 'Montevideo', 'Montevideo', '11038', 'Referencia pendiente'),
  (38, 39, 'Local',  'Calle Gimnasio 39',   NULL, 'Montevideo', 'Montevideo', '11039', 'Actualizar referencia');

-- ===============================
-- 4) Ajuste de identidades
-- ===============================
ALTER TABLE app_user         ALTER COLUMN id RESTART WITH 41;
ALTER TABLE customer_address ALTER COLUMN id RESTART WITH 40;
