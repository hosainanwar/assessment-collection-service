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

-- Pouroshova Infos
INSERT INTO pouroshova_infos (id, pouroshova_name, meyor_name, ps_name, ds_name, signature_name, subdomain, created_by, mayor_label_type, mayor_label_type_collection, created_at, updated_at)
VALUES (1, 'শ্রীপুর পৌরসভা', 'মোঃ আব্দুল করিম', 'শ্রীপুর', 'গাজীপুর', 'শ্রীপুর পৌরসভা', 'sreepur', 'admin', 'mayor', 'mayor', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pouroshova_infos (id, pouroshova_name, meyor_name, ps_name, ds_name, signature_name, subdomain, created_by, mayor_label_type, mayor_label_type_collection, created_at, updated_at)
VALUES (2, 'গাজীপুর পৌরসভা', 'মোঃ রফিকুল ইসলাম', 'গাজীপুর', 'গাজীপুর', 'গাজীপুর পৌরসভা', 'gazipur', 'admin', 'mayor', 'mayor', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Test User (password: admin123)
INSERT INTO users (id, name, username, email, password, status, subdomain, created_at, updated_at)
VALUES (1, 'Admin User', 'admin', 'admin@e-pourashava.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', true, 'sreepur', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sequence reset for H2
ALTER TABLE divisions ALTER COLUMN id RESTART WITH 9;
ALTER TABLE districts ALTER COLUMN id RESTART WITH 10;
ALTER TABLE pourashavas ALTER COLUMN id RESTART WITH 4;
ALTER TABLE pouroshova_infos ALTER COLUMN id RESTART WITH 3;
ALTER TABLE users ALTER COLUMN id RESTART WITH 2;
