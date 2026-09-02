-- Seed data for testing (H2)

-- Divisions
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (1, 'ঢাকা বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (2, 'চট্টগ্রাম বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (3, 'রাজশাহী বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (4, 'খুলনা বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (5, 'বরিশাল বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (6, 'সিলেট বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (7, 'রংপুর বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO divisions (id, name, created_at, updated_at) VALUES (8, 'ময়মনসিংহ বিভাগ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Districts (ঢাকা বিভাগ)
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (1, 'ঢাকা', 'Dhaka', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (2, 'গাজীপুর', 'Gazipur', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (3, 'নারায়ণগঞ্জ', 'Narayanganj', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (4, 'মুন্সিগঞ্জ', 'Munshiganj', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (5, 'মানিকগঞ্জ', 'Manikganj', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (6, 'ঢাকা', 'Dhaka', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Districts (চট্টগ্রাম বিভাগ)
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (7, 'চট্টগ্রাম', 'Chittagong', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (8, 'কক্সবাজার', 'Coxs Bazar', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO districts (id, name, en_name, division_id, created_at, updated_at) VALUES (9, 'কুমিল্লা', 'Comilla', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Pourashavas
INSERT INTO pourashavas (id, division_id, district_id, subdomain, bn_name, en_name, created_by, ip_address, created_at, updated_at)
VALUES (1, 1, 1, 'sreepur', 'শ্রীপুর পৌরসভা', 'Sreepur Pourashava', 1, '127.0.0.1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pourashavas (id, division_id, district_id, subdomain, bn_name, en_name, created_by, ip_address, created_at, updated_at)
VALUES (2, 1, 2, 'gazipur', 'গাজীপুর পৌরসভা', 'Gazipur Pourashava', 1, '127.0.0.1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pourashavas (id, division_id, district_id, subdomain, bn_name, en_name, created_by, ip_address, created_at, updated_at)
VALUES (3, 2, 7, 'kishoreganj', 'কিশোরগঞ্জ পৌরসভা', 'Kishoreganj Pourashava', 1, '127.0.0.1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pourashavas (id, division_id, district_id, subdomain, bn_name, en_name, created_by, ip_address, created_at, updated_at)
VALUES (4, 1, 1, 'demo', 'ডেমো পৌরসভা', 'Demo Pourashava', 1, '127.0.0.1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Pouroshova Infos
INSERT INTO pouroshova_infos (id, pouroshova_name, meyor_name, ps_name, ds_name, signature_name, subdomain, pourashava_id, created_by, mayor_label_type, mayor_label_type_collection, created_at, updated_at)
VALUES (1, 'শ্রীপুর পৌরসভা', 'মোঃ আব্দুল করিম', 'শ্রীপুর', 'গাজীপুর', 'শ্রীপুর পৌরসভা', 'sreepur', 1, 'admin', 'mayor', 'mayor', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pouroshova_infos (id, pouroshova_name, meyor_name, ps_name, ds_name, signature_name, subdomain, pourashava_id, created_by, mayor_label_type, mayor_label_type_collection, created_at, updated_at)
VALUES (2, 'গাজীপুর পৌরসভা', 'মোঃ রফিকুল ইসলাম', 'গাজীপুর', 'গাজীপুর', 'গাজীপুর পৌরসভা', 'gazipur', 2, 'admin', 'mayor', 'mayor', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Roles
INSERT INTO roles (id, code, name_bn, name_en, description, is_system, status, created_at, updated_at) VALUES
(1, 'SUPER_ADMIN', 'সুপার অ্যাডমিন', 'Super Admin', 'Platform operator', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'POURASHAVA_ADMIN', 'পৌরসভা অ্যাডমিন', 'Pourashava Admin', 'Municipality administrator', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'OPERATOR', 'অপারেটর', 'Operator', 'Day-to-day data entry', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'VIEWER', 'পর্যবেক্ষক', 'Viewer', 'Read-only access', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permissions
INSERT INTO permissions (id, code, module, action, description, created_at, updated_at) VALUES
(1,  'USER:READ', 'USER', 'READ', 'Read users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2,  'USER:CREATE', 'USER', 'CREATE', 'Create users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3,  'USER:UPDATE', 'USER', 'UPDATE', 'Update users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4,  'USER:DELETE', 'USER', 'DELETE', 'Delete users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5,  'USER:ASSIGN_ROLE', 'USER', 'ASSIGN_ROLE', 'Assign roles to users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6,  'ROLE:READ', 'ROLE', 'READ', 'Read roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7,  'ROLE:CREATE', 'ROLE', 'CREATE', 'Create roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8,  'ROLE:UPDATE', 'ROLE', 'UPDATE', 'Update roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9,  'ROLE:DELETE', 'ROLE', 'DELETE', 'Delete roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'POURASHAVA:READ', 'POURASHAVA', 'READ', 'Read pourashavas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'POURASHAVA:CREATE', 'POURASHAVA', 'CREATE', 'Create pourashavas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'POURASHAVA:UPDATE', 'POURASHAVA', 'UPDATE', 'Update pourashavas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'POURASHAVA:DELETE', 'POURASHAVA', 'DELETE', 'Delete pourashavas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'POUROSHOVA_INFO:READ', 'POUROSHOVA_INFO', 'READ', 'Read pourashava info', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'POUROSHOVA_INFO:CREATE', 'POUROSHOVA_INFO', 'CREATE', 'Create pourashava info', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'POUROSHOVA_INFO:UPDATE', 'POUROSHOVA_INFO', 'UPDATE', 'Update pourashava info', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 'POUROSHOVA_INFO:DELETE', 'POUROSHOVA_INFO', 'DELETE', 'Delete pourashava info', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'DIVISION:READ', 'DIVISION', 'READ', 'Read divisions', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 'DIVISION:CREATE', 'DIVISION', 'CREATE', 'Create divisions', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 'DIVISION:UPDATE', 'DIVISION', 'UPDATE', 'Update divisions', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 'DIVISION:DELETE', 'DIVISION', 'DELETE', 'Delete divisions', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, 'DISTRICT:READ', 'DISTRICT', 'READ', 'Read districts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, 'DISTRICT:CREATE', 'DISTRICT', 'CREATE', 'Create districts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(24, 'DISTRICT:UPDATE', 'DISTRICT', 'UPDATE', 'Update districts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'DISTRICT:DELETE', 'DISTRICT', 'DELETE', 'Delete districts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, 'WORD:READ', 'WORD', 'READ', 'Read wards', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(27, 'WORD:CREATE', 'WORD', 'CREATE', 'Create wards', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(28, 'WORD:UPDATE', 'WORD', 'UPDATE', 'Update wards', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, 'WORD:DELETE', 'WORD', 'DELETE', 'Delete wards', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(30, 'PARA:READ', 'PARA', 'READ', 'Read paras', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(31, 'PARA:CREATE', 'PARA', 'CREATE', 'Create paras', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(32, 'PARA:UPDATE', 'PARA', 'UPDATE', 'Update paras', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(33, 'PARA:DELETE', 'PARA', 'DELETE', 'Delete paras', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions WHERE code IN (
    'USER:READ','USER:CREATE','USER:UPDATE','USER:DELETE','USER:ASSIGN_ROLE',
    'POURASHAVA:READ',
    'POUROSHOVA_INFO:READ','POUROSHOVA_INFO:CREATE','POUROSHOVA_INFO:UPDATE','POUROSHOVA_INFO:DELETE',
    'DIVISION:READ','DISTRICT:READ',
    'WORD:READ','WORD:CREATE','WORD:UPDATE','WORD:DELETE',
    'PARA:READ','PARA:CREATE','PARA:UPDATE','PARA:DELETE'
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, id FROM permissions WHERE code IN (
    'USER:READ','POURASHAVA:READ',
    'POUROSHOVA_INFO:READ','DIVISION:READ','DISTRICT:READ',
    'WORD:READ','WORD:CREATE','WORD:UPDATE',
    'PARA:READ','PARA:CREATE','PARA:UPDATE'
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, id FROM permissions WHERE action = 'READ';

-- Users (password: admin123)
INSERT INTO users (id, name, username, email, password, status, subdomain, pourashava_id, created_at, updated_at)
VALUES (1, 'Admin User', 'admin', 'admin@e-pourashava.com', '$2b$10$9MImjcy6Z4KvJPxfr8bF1eMkp3zeaq/kyY9aLT.gQ4/Tz8bJsTC7m', true, 'sreepur', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, name, username, email, password, status, subdomain, pourashava_id, created_at, updated_at)
VALUES (2, 'Super Admin', 'superadmin', 'superadmin@e-pourashava.com', '$2b$10$9MImjcy6Z4KvJPxfr8bF1eMkp3zeaq/kyY9aLT.gQ4/Tz8bJsTC7m', true, 'demo', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1);

-- Sequence reset for H2
ALTER TABLE divisions ALTER COLUMN id RESTART WITH 9;
ALTER TABLE districts ALTER COLUMN id RESTART WITH 10;
ALTER TABLE pourashavas ALTER COLUMN id RESTART WITH 5;
ALTER TABLE pouroshova_infos ALTER COLUMN id RESTART WITH 3;
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
ALTER TABLE roles ALTER COLUMN id RESTART WITH 5;
ALTER TABLE permissions ALTER COLUMN id RESTART WITH 34;
