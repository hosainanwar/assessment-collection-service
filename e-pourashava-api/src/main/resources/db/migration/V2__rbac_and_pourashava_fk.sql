-- RBAC tables, pourashava_id on tenant-scoped entities, demo pourashava, seed roles.

CREATE TABLE IF NOT EXISTS roles (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    name_bn     VARCHAR(100) NOT NULL,
    name_en     VARCHAR(100) NOT NULL,
    description TEXT,
    is_system   BOOLEAN      NOT NULL DEFAULT FALSE,
    status      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_roles_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS permissions (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,
    module      VARCHAR(50)  NOT NULL,
    action      VARCHAR(50)  NOT NULL,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_permissions_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS pourashava_id BIGINT;
ALTER TABLE words ADD COLUMN IF NOT EXISTS pourashava_id BIGINT;
ALTER TABLE paras ADD COLUMN IF NOT EXISTS pourashava_id BIGINT;
ALTER TABLE pouroshova_infos ADD COLUMN IF NOT EXISTS pourashava_id BIGINT;

INSERT INTO pourashavas (division_id, district_id, subdomain, bn_name, en_name, created_by, ip_address, created_at, updated_at)
SELECT 1, 1, 'demo', 'ডেমো পৌরসভা', 'Demo Pourashava', 1, '127.0.0.1', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM pourashavas WHERE subdomain = 'demo');

UPDATE users u
SET pourashava_id = p.id
FROM pourashavas p
WHERE u.pourashava_id IS NULL AND u.subdomain = p.subdomain;

UPDATE users
SET pourashava_id = (SELECT id FROM pourashavas WHERE subdomain = 'demo' LIMIT 1)
WHERE pourashava_id IS NULL;

UPDATE words w
SET pourashava_id = p.id
FROM pourashavas p
WHERE w.pourashava_id IS NULL AND w.subdomain = p.subdomain;

UPDATE paras r
SET pourashava_id = p.id
FROM pourashavas p
WHERE r.pourashava_id IS NULL AND r.subdomain = p.subdomain;

UPDATE pouroshova_infos i
SET pourashava_id = p.id
FROM pourashavas p
WHERE i.pourashava_id IS NULL AND i.subdomain = p.subdomain;

DO $$
BEGIN
    ALTER TABLE users ALTER COLUMN pourashava_id SET NOT NULL;
EXCEPTION WHEN others THEN NULL;
END $$;

DO $$
BEGIN
    ALTER TABLE users ADD CONSTRAINT fk_users_pourashava FOREIGN KEY (pourashava_id) REFERENCES pourashavas(id);
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

CREATE INDEX IF NOT EXISTS idx_users_pourashava_id ON users(pourashava_id);
CREATE INDEX IF NOT EXISTS idx_words_pourashava_id ON words(pourashava_id);
CREATE INDEX IF NOT EXISTS idx_paras_pourashava_id ON paras(pourashava_id);
CREATE INDEX IF NOT EXISTS idx_pouroshova_infos_pourashava_id ON pouroshova_infos(pourashava_id);

INSERT INTO roles (code, name_bn, name_en, description, is_system, status, created_at, updated_at)
VALUES
    ('SUPER_ADMIN', 'সুপার অ্যাডমিন', 'Super Admin', 'Platform operator', TRUE, TRUE, NOW(), NOW()),
    ('POURASHAVA_ADMIN', 'পৌরসভা অ্যাডমিন', 'Pourashava Admin', 'Municipality administrator', TRUE, TRUE, NOW(), NOW()),
    ('OPERATOR', 'অপারেটর', 'Operator', 'Day-to-day data entry', TRUE, TRUE, NOW(), NOW()),
    ('VIEWER', 'পর্যবেক্ষক', 'Viewer', 'Read-only access', TRUE, TRUE, NOW(), NOW())
ON CONFLICT (code) DO NOTHING;

INSERT INTO permissions (code, module, action, description, created_at, updated_at)
VALUES
    ('USER:READ', 'USER', 'READ', 'Read users', NOW(), NOW()),
    ('USER:CREATE', 'USER', 'CREATE', 'Create users', NOW(), NOW()),
    ('USER:UPDATE', 'USER', 'UPDATE', 'Update users', NOW(), NOW()),
    ('USER:DELETE', 'USER', 'DELETE', 'Delete users', NOW(), NOW()),
    ('USER:ASSIGN_ROLE', 'USER', 'ASSIGN_ROLE', 'Assign roles to users', NOW(), NOW()),
    ('ROLE:READ', 'ROLE', 'READ', 'Read roles', NOW(), NOW()),
    ('ROLE:CREATE', 'ROLE', 'CREATE', 'Create roles', NOW(), NOW()),
    ('ROLE:UPDATE', 'ROLE', 'UPDATE', 'Update roles', NOW(), NOW()),
    ('ROLE:DELETE', 'ROLE', 'DELETE', 'Delete roles', NOW(), NOW()),
    ('POURASHAVA:READ', 'POURASHAVA', 'READ', 'Read pourashavas', NOW(), NOW()),
    ('POURASHAVA:CREATE', 'POURASHAVA', 'CREATE', 'Create pourashavas', NOW(), NOW()),
    ('POURASHAVA:UPDATE', 'POURASHAVA', 'UPDATE', 'Update pourashavas', NOW(), NOW()),
    ('POURASHAVA:DELETE', 'POURASHAVA', 'DELETE', 'Delete pourashavas', NOW(), NOW()),
    ('POUROSHOVA_INFO:READ', 'POUROSHOVA_INFO', 'READ', 'Read pourashava info', NOW(), NOW()),
    ('POUROSHOVA_INFO:CREATE', 'POUROSHOVA_INFO', 'CREATE', 'Create pourashava info', NOW(), NOW()),
    ('POUROSHOVA_INFO:UPDATE', 'POUROSHOVA_INFO', 'UPDATE', 'Update pourashava info', NOW(), NOW()),
    ('POUROSHOVA_INFO:DELETE', 'POUROSHOVA_INFO', 'DELETE', 'Delete pourashava info', NOW(), NOW()),
    ('DIVISION:READ', 'DIVISION', 'READ', 'Read divisions', NOW(), NOW()),
    ('DIVISION:CREATE', 'DIVISION', 'CREATE', 'Create divisions', NOW(), NOW()),
    ('DIVISION:UPDATE', 'DIVISION', 'UPDATE', 'Update divisions', NOW(), NOW()),
    ('DIVISION:DELETE', 'DIVISION', 'DELETE', 'Delete divisions', NOW(), NOW()),
    ('DISTRICT:READ', 'DISTRICT', 'READ', 'Read districts', NOW(), NOW()),
    ('DISTRICT:CREATE', 'DISTRICT', 'CREATE', 'Create districts', NOW(), NOW()),
    ('DISTRICT:UPDATE', 'DISTRICT', 'UPDATE', 'Update districts', NOW(), NOW()),
    ('DISTRICT:DELETE', 'DISTRICT', 'DELETE', 'Delete districts', NOW(), NOW()),
    ('WORD:READ', 'WORD', 'READ', 'Read wards', NOW(), NOW()),
    ('WORD:CREATE', 'WORD', 'CREATE', 'Create wards', NOW(), NOW()),
    ('WORD:UPDATE', 'WORD', 'UPDATE', 'Update wards', NOW(), NOW()),
    ('WORD:DELETE', 'WORD', 'DELETE', 'Delete wards', NOW(), NOW()),
    ('PARA:READ', 'PARA', 'READ', 'Read paras', NOW(), NOW()),
    ('PARA:CREATE', 'PARA', 'CREATE', 'Create paras', NOW(), NOW()),
    ('PARA:UPDATE', 'PARA', 'UPDATE', 'Update paras', NOW(), NOW()),
    ('PARA:DELETE', 'PARA', 'DELETE', 'Delete paras', NOW(), NOW())
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p
WHERE r.code = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code IN (
    'USER:READ','USER:CREATE','USER:UPDATE','USER:DELETE','USER:ASSIGN_ROLE',
    'POURASHAVA:READ',
    'POUROSHOVA_INFO:READ','POUROSHOVA_INFO:CREATE','POUROSHOVA_INFO:UPDATE','POUROSHOVA_INFO:DELETE',
    'DIVISION:READ','DISTRICT:READ',
    'WORD:READ','WORD:CREATE','WORD:UPDATE','WORD:DELETE',
    'PARA:READ','PARA:CREATE','PARA:UPDATE','PARA:DELETE'
)
WHERE r.code = 'POURASHAVA_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.code IN (
    'USER:READ','POURASHAVA:READ',
    'POUROSHOVA_INFO:READ','DIVISION:READ','DISTRICT:READ',
    'WORD:READ','WORD:CREATE','WORD:UPDATE',
    'PARA:READ','PARA:CREATE','PARA:UPDATE'
)
WHERE r.code = 'OPERATOR'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.action = 'READ'
WHERE r.code = 'VIEWER'
ON CONFLICT DO NOTHING;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = current_schema() AND table_name = 'users' AND column_name = 'role'
    ) THEN
        EXECUTE $sql$
            INSERT INTO user_roles (user_id, role_id)
            SELECT u.id, r.id
            FROM users u
            JOIN roles r ON r.code = CASE
                WHEN u.role = 'SUPER_ADMIN' THEN 'SUPER_ADMIN'
                WHEN u.role IN ('ADMIN', 'POURASHAVA_ADMIN') THEN 'POURASHAVA_ADMIN'
                WHEN u.role = 'OPERATOR' THEN 'OPERATOR'
                ELSE 'VIEWER'
            END
            WHERE NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id)
        $sql$;
    END IF;
END $$;

ALTER TABLE users DROP COLUMN IF EXISTS role;
