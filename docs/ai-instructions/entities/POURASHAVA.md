# Pourashava

| | |
| Status | Implemented (status/feature columns still planned) |
| Table | `pourashavas` |
| Tenant | self — `subdomain` is the tenant key |
| Java | `entity/Pourashava.java` |
| Laravel | `app/Models/Pourashava.php` |
| Permissions | `POURASHAVA:READ`, `POURASHAVA:CREATE`, `POURASHAVA:UPDATE`, `POURASHAVA:DELETE` |
| UI | `/pourashavas` |

## Entity

Municipality / tenant registry. Every tenant-owned row points here via `pourashava_id`.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | Identity; JWT `pourashavaId` |
| `division_id` | FK | yes | [Division](DIVISION.md) |
| `district_id` | FK | yes | [District](DISTRICT.md) |
| `subdomain` | string | yes | Unique; login `tenantId`; leftover Laravel column on child tables |
| `bn_name` | string | yes | Bangla name |
| `en_name` | string | yes | English name |
| `features` | JSON string | no | Module flags, e.g. `direct_final_tax` |
| `created_by` | long | yes | User/admin id |
| `updated_by` | long | no | |
| `ip_address` | string(45) | yes | Creator IP |

### Planned fields (Laravel, not on Java entity yet)

| Column | Notes |
|--------|-------|
| `assessment_app_status` | `active` or suspended → 403 `SERVICE_SUSPENDED` on assessment APIs |
| `collection_app_status` | Same for collection APIs |

### Relations

- Belongs to Division, District
- Has many User, Word, Para, PouroshovaInfo, and (planned) all tenant tables

## Business logic

- Subdomain is unique. Create/update reject a clash.
- Division and district are required and must exist.
- Financial year is **1 July – 30 June**, computed from “today”, not stored on this row (Laravel `Pourashava::financialYear()`).
- Feature JSON hides optional modules. UI and API both require the flag **and** the permission (e.g. `DIRECT_FINAL_TAX` + `FINAL_ASSESSMENT:DIRECT`).
- Super admin lives on the seeded `demo` pourashava so `users.pourashava_id` stays NOT NULL. Super admin bypasses the Hibernate tenant filter by **role**, not because they are in `demo`.
- Login `tenantId` must equal the user’s pourashava subdomain (or numeric id). Super admin still logs in as `demo`.
- `POURASHAVA:CREATE` / `DELETE` stay off `POURASHAVA_ADMIN`. Only platform roles create municipalities.
- Do not allow pourashava staff to change `subdomain` after users and holdings exist — add that guard when holdings land.
- Installment number from month: Jul–Sep = 1, Oct–Dec = 2, Jan–Mar = 3, Apr–Jun = 4.
