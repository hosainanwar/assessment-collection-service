# Permission

| | |
| Status | Implemented (current modules); catalogue grows with features |
| Table | `permissions` |
| Tenant | no — global codes |
| Java | `entity/Permission.java`, `security/PermissionCodes.java` |
| Laravel | Spatie `permissions` (human sentences — do not port names) |
| Permissions | Managed with `ROLE:*` (no public permission CRUD) |

## Entity

Atomic capability `MODULE:ACTION`. Never assigned directly to a user — only through a [Role](ROLE.md).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `code` | string(100) | yes | Unique, e.g. `WORD:READ` |
| `module` | string(50) | yes | `WORD`, `USER`, … |
| `action` | string(50) | yes | `READ`, `CREATE`, … |
| `description` | string | no | |

### Relations

- Many-to-many Role via `role_permissions`

## Business logic

- Code format is `MODULE:ACTION` only. Constants live in `PermissionCodes`. Seed rows must match the constant and `@PreAuthorize("hasAuthority('…')")` and UI `*hasPermission`.
- New capability = new row + constant + annotation + UI gate + deploy. Roles can be composed at runtime from **existing** codes; new codes need a release.
- Laravel used Title Case, snake_case, and lowercase names that did not always match the sidebar. Do not invent a second spelling.
- Do not expose a public `/permissions` write API (Laravel `PermissionMakeController` had no auth). Read catalogue via `GET /roles/permissions`.
- Do not seed district names as permissions.
- When a module is ported, add its rows in Flyway, grant them to `SUPER_ADMIN` and the right seeded roles, then lock the controller.
- User permissions are cached in Redis (`user-permissions`, 1h). Evict user on role change; evict all on role-permission change.
