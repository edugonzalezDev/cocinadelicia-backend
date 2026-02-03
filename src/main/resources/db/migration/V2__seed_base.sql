-- =========================================
-- V2: Seed Base - Roles, Admin/Staff, Categorías, Tags
-- SIN IDs explícitos - usa claves naturales para lookups
-- =========================================

-- ===============================
-- 1. ROLES (base del sistema)
-- ===============================
INSERT INTO role (name, description) VALUES
  ('ADMIN',    'Administrador del sistema'),
  ('CHEF',     'Cocinero / preparación'),
  ('COURIER',  'Repartidor'),
  ('CUSTOMER', 'Cliente');

-- ===============================
-- 2. USUARIOS STAFF (admin, chef)
-- ===============================
INSERT INTO app_user (cognito_user_id, first_name, last_name, email, phone, is_active) VALUES
  ('cog-admin-001', 'Eduardo', 'González', 'admin@cocinadelicia.test',  '+59800000000', TRUE),
  ('cog-chef-001',  'Ana',     'Pérez',    'chef@cocinadelicia.test',   '+59800000001', TRUE);

-- Asignar roles a staff por email
INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM app_user u, role r
WHERE u.email = 'admin@cocinadelicia.test' AND r.name = 'ADMIN';

INSERT INTO user_role (user_id, role_id, assigned_at)
SELECT u.id, r.id, CURRENT_TIMESTAMP
FROM app_user u, role r
WHERE u.email = 'chef@cocinadelicia.test' AND r.name = 'CHEF';

-- ===============================
-- 3. CATEGORÍAS
-- ===============================
INSERT INTO category (name, slug, description) VALUES
  ('Empanadas',          'empanadas',           'Empanadas caseras de diversos sabores'),
  ('Pastas',             'pastas',              'Pastas frescas, canelones, ravioles, ñoquis y tallarines'),
  ('Postres',            'postres',             'Dulces y postres caseros'),
  ('Milanesas',          'milanesas',           'Milanesas caseras de carne, pollo, pescado y vegetales'),
  ('Ensaladas',          'ensaladas',           'Ensaladas frescas y livianas'),
  ('Platos Principales', 'platos-principales',  'Churrascos, pecetos, matambres, bondiolas y más'),
  ('Guisos y Cazuelas',  'guisos-cazuelas',     'Guisos de lentejas, mondongo, estofados'),
  ('Tartas y Tortillas', 'tartas-tortillas',    'Tartas de J&Q, zapallito, puerro, pascualina'),
  ('Pastel y Polenta',   'pastel-polenta',      'Pasteles y polentas rellenas'),
  ('Verduras',           'verduras',            'Verduras al horno, rellenas, salteadas'),
  ('Panadería y Pizzas', 'panaderia-pizzas',    'Pan casero, pizzas, fainá, pre-pizzas'),
  ('Extras',             'extras',              'Guarniciones, propinas y adicionales');

-- ===============================
-- 4. TAGS
-- ===============================
INSERT INTO tag (name, slug) VALUES
  ('Vegetariano',  'vegetariano'),
  ('Vegano',       'vegano'),
  ('Sin TACC',     'sin-tacc'),
  ('Light',        'light'),
  ('Picante',      'picante'),
  ('Sin Azúcar',   'sin-azucar'),
  ('Entero',       'entero'),
  ('Sin Panceta',  'sin-panceta');
