-- =========================================
-- V5: Seed Customers - Clientes demo y reales
-- SIN IDs explícitos - lookup por email y role.name
-- =========================================

-- ===============================
-- 1. CLIENTES DEMO (del equipo de desarrollo)
-- ===============================
INSERT INTO app_user (cognito_user_id, first_name, last_name, email, phone, is_active) VALUES
  ('cog-cli-001', 'Juan',   'López',     'juan@cocinadelicia.test',   '+59800000002', TRUE),
  ('cog-cli-002', 'Lucía',  'Rodríguez', 'lucia@cocinadelicia.test',  '+59800000003', TRUE),
  ('cog-cli-003', 'Carlos', 'Silva',     'carlos@cocinadelicia.test', '+59800000004', TRUE),
  ('cog-cli-004', 'María',  'Fernández', 'maria@cocinadelicia.test',  '+59800000005', TRUE);

-- Asignar rol CUSTOMER a clientes demo
INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM app_user u, role r
WHERE u.email = 'juan@cocinadelicia.test' AND r.name = 'CUSTOMER';

INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM app_user u, role r
WHERE u.email = 'lucia@cocinadelicia.test' AND r.name = 'CUSTOMER';

INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM app_user u, role r
WHERE u.email = 'carlos@cocinadelicia.test' AND r.name = 'CUSTOMER';

INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM app_user u, role r
WHERE u.email = 'maria@cocinadelicia.test' AND r.name = 'CUSTOMER';

-- ===============================
-- 2. DIRECCIONES DE CLIENTES DEMO
-- ===============================
INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Casa', 'Av. Italia 1234', NULL, 'Montevideo', 'Montevideo', '11300', 'Portón negro'
FROM app_user u WHERE u.email = 'juan@cocinadelicia.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Trabajo', 'Colonia 555', 'Of. 301', 'Montevideo', 'Montevideo', '11000', 'Recepción 3er piso'
FROM app_user u WHERE u.email = 'juan@cocinadelicia.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Casa', 'Bvar. Artigas 1000', NULL, 'Montevideo', 'Montevideo', '11200', 'Apto 502 Torre Sur'
FROM app_user u WHERE u.email = 'lucia@cocinadelicia.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Casa', 'Rbla. Rep. del Perú 1234', 'Edificio Costa Brava', 'Montevideo', 'Montevideo', '11300', 'Portería 24h'
FROM app_user u WHERE u.email = 'carlos@cocinadelicia.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Casa', 'Av. Rivera 2500', NULL, 'Montevideo', 'Montevideo', '11300', 'Casa esquina'
FROM app_user u WHERE u.email = 'maria@cocinadelicia.test';

-- ===============================
-- 3. CLIENTES REALES (datos representativos)
-- ===============================
INSERT INTO app_user (cognito_user_id, first_name, last_name, email, phone, is_active) VALUES
  ('cog-cli-007', 'Cliente',   'Genérico',         'cliente.base@customers.cdl.test',        '+59820000007', TRUE),
  ('cog-cli-008', 'Andrea',    '',                 'andrea.cliente@customers.cdl.test',      '+59820000008', TRUE),
  ('cog-cli-009', 'Liliana',   'Lupas',            'liliana.lupas@customers.cdl.test',       '+59820000009', TRUE),
  ('cog-cli-010', 'Maria',     'Cliente',          'maria.1@customers.cdl.test',             '+59820000010', TRUE),
  ('cog-cli-011', 'Rosario',   'Carniceria Arco',  'rosario.carniceria@customers.cdl.test',  '+59820000011', TRUE),
  ('cog-cli-012', 'Nely',      'Mecanico',         'nely.mecanico@customers.cdl.test',       '+59820000012', TRUE),
  ('cog-cli-013', 'Juana',     'Carvallo',         'juana.carvallo@customers.cdl.test',      '+59820000013', TRUE),
  ('cog-cli-014', 'Iosef',     'Gak',              'iosi@customers.cdl.test',                '+59820000014', TRUE),
  ('cog-cli-015', 'Silvia',    'Rodriguez',        'silvia.rodriguez@customers.cdl.test',    '+59820000015', TRUE),
  ('cog-cli-016', 'Macarena',  'Cliente',          'macarena.cliente@customers.cdl.test',    '+59820000016', TRUE),
  ('cog-cli-017', 'Alicia',    'Peluqueria',       'alicia.peluqueria@customers.cdl.test',   '+59820000017', TRUE),
  ('cog-cli-018', 'Laura',     'Peluqueria',       'laura.peluqueria@customers.cdl.test',    '+59820000018', TRUE),
  ('cog-cli-019', 'Maria',     'Cliente2',         'maria.2@customers.cdl.test',             '+59820000019', TRUE),
  ('cog-cli-020', 'Victoria',  'Manicura',         'victoria.manicura@customers.cdl.test',   '+59820000020', TRUE),
  ('cog-cli-021', 'Estefany',  'Cliente',          'estefany@customers.cdl.test',            '+59820000021', TRUE),
  ('cog-cli-022', 'Alicia',    'Cliente3',         'alicia.extra@customers.cdl.test',        '+59820000022', TRUE),
  ('cog-cli-023', 'Rodrigo',   'Cliente',          'rodrigo@customers.cdl.test',             '+59820000023', TRUE),
  ('cog-cli-024', 'Daniel',    'Barberia',         'daniel.barberia@customers.cdl.test',     '+59820000024', TRUE),
  ('cog-cli-025', 'Vecina',    'De Nely',          'vecina.nely@customers.cdl.test',         '+59820000025', TRUE),
  ('cog-cli-026', 'Beatriz',   'Cliente',          'beatriz@customers.cdl.test',             '+59820000026', TRUE),
  ('cog-cli-027', 'Mariana',   'Inm Julieta',      'mariana.julieta@customers.cdl.test',     '+59820000027', TRUE),
  ('cog-cli-028', 'Graciela',  'Cliente',          'graciela@customers.cdl.test',            '+59820000028', TRUE),
  ('cog-cli-029', 'Fernanda',  'Cliente',          'fernanda@customers.cdl.test',            '+59820000029', TRUE),
  ('cog-cli-030', 'Alicia',    'Alex',             'alicia.alex@customers.cdl.test',         '+59820000030', TRUE),
  ('cog-cli-031', 'Margot',    'Cliente',          'margot@customers.cdl.test',              '+59820000031', TRUE),
  ('cog-cli-032', 'Estefani',  'Cliente',          'estefani@customers.cdl.test',            '+59820000032', TRUE),
  ('cog-cli-033', 'Cecilia',   'Dante',            'cecilia.dante@customers.cdl.test',       '+59820000033', TRUE),
  ('cog-cli-034', 'Diego',     'Gimnasio',         'diego@customers.cdl.test',               '+59820000034', TRUE),
  ('cog-cli-035', 'Jaqueline', 'Steffano',         'jaqueline@customers.cdl.test',           '+59820000035', TRUE),
  ('cog-cli-036', 'Silka',     'Cliente',          'silka@customers.cdl.test',               '+59820000036', TRUE),
  ('cog-cli-037', 'Lilian',    'Freelook',         'lilia.freelook@customers.cdl.test',      '+59820000037', TRUE),
  ('cog-cli-039', 'Nacho',     'Gimnasio',         'nacho.gimnasio@customers.cdl.test',      '+59820000039', TRUE),
  ('cog-cli-040', 'Mariana',   'Inmobiliaria',     'mariana.inmobiliaria@customers.cdl.test','+59820000040', TRUE);

-- Asignar rol CUSTOMER a todos los clientes reales (bulk por pattern de email)
INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM app_user u
CROSS JOIN role r
WHERE r.name = 'CUSTOMER'
  AND u.email LIKE '%@customers.cdl.test'
  AND NOT EXISTS (
    SELECT 1 FROM user_role ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
  );

-- ===============================
-- 4. DIRECCIONES DE CLIENTES REALES (muestra)
-- ===============================
INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Casa', 'Calle Demo 123', NULL, 'Montevideo', 'Montevideo', '11000', 'Referencia pendiente'
FROM app_user u WHERE u.email = 'cliente.base@customers.cdl.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Casa', 'Calle Andrea 456', NULL, 'Montevideo', 'Montevideo', '11001', 'Casa blanca'
FROM app_user u WHERE u.email = 'andrea.cliente@customers.cdl.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Local', 'Av Carniceria 11', NULL, 'Montevideo', 'Montevideo', '11011', 'Local comercial'
FROM app_user u WHERE u.email = 'rosario.carniceria@customers.cdl.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Taller', 'Calle Mecanico 12', NULL, 'Montevideo', 'Montevideo', '11012', 'Taller mecánico'
FROM app_user u WHERE u.email = 'nely.mecanico@customers.cdl.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Local', 'Calle Peluqueria 17', NULL, 'Montevideo', 'Montevideo', '11017', 'Peluquería Alicia'
FROM app_user u WHERE u.email = 'alicia.peluqueria@customers.cdl.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Local', 'Calle Barberia 24', NULL, 'Montevideo', 'Montevideo', '11024', 'Barbería Daniel'
FROM app_user u WHERE u.email = 'daniel.barberia@customers.cdl.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Casa', 'Calle Iosi 789', NULL, 'Montevideo', 'Montevideo', '11014', 'Casa de Iosi'
FROM app_user u WHERE u.email = 'iosi@customers.cdl.test';

INSERT INTO customer_address (user_id, label, line1, line2, city, region, postal_code, reference)
SELECT u.id, 'Local', 'Gimnasio Fit 34', NULL, 'Montevideo', 'Montevideo', '11034', 'Gimnasio'
FROM app_user u WHERE u.email = 'diego@customers.cdl.test';
