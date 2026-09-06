# Role

| | |
| Status | Implemented |
| Table | `roles` |
| Tenant | **no** — global catalogue (RBAC D1) |
| Java | `entity/Role.java` |
| Laravel | Spatie `roles` (had `subdomain` — do not port) |
| Permissions | `ROLE:READ`, `CREATE`, `UPDATE`, `DELETE` |
| UI | `/roles` |

## Entity

Named bundle of [Permission](PERMISSION.md) codes. Assignable to users of any pourashava.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `code` | string(50) | yes | Unique, uppercase `A-Z0-9_` |
| `name_bn` | string(100) | yes | UI label |
| `name_en` | string(100) | yes | |
| `description` | string | no | |
| `is_system` | boolean | yes | Seeded; cannot rename/delete |
| `status` | boolean | yes | Inactive roles hidden from assign lists |

### Relations

- Many-to-many Permission via `role_permissions`
- Many-to-many User via `user_roles`

## Business logic

- Roles are **global**. There is no `pourashava_id` on this table. Isolation stays on the user/data, not on the role definition.
- Code is trimmed and uppercased. Pattern `[A-Z][A-Z0-9_]{1,49}`. Duplicate code rejected.
- Create always sets `is_system=false`. Only Flyway/data.sql may insert system roles.
- System roles cannot be renamed or deleted. Delete also fails if any user still has the role.
- A role must have at least one permission. Unknown permission codes are rejected.
- Changing role permissions evicts the **entire** permission cache (`permissionResolver.evictAll()`).
- `GET /roles/assignable` returns only roles the current user may grant (subset rule + never `SUPER_ADMIN` unless actor is super admin).
- `GET /roles/permissions` lists the permission catalogue (route **before** `/{id}`).
- Seeded system roles: `SUPER_ADMIN`, `POURASHAVA_ADMIN`, `OPERATOR`, `VIEWER`. Later: `ASSESSOR`, `PNO`, `MAYOR`, `COLLECTOR` ([SECURITY.md](../SECURITY.md) §7).
- `POURASHAVA_ADMIN` does **not** get `ROLE:*`. They assign existing roles; they do not invent the catalogue unless given `ROLE:CREATE`.
- Do not recreate Laravel per-subdomain roles.
