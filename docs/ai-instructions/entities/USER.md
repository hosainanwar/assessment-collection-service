# User

| | |
| Status | Implemented |
| Table | `users` |
| Tenant | yes — `pourashava_id` + Hibernate filter |
| Java | `entity/User.java` |
| Laravel | `User`, `Admin`, `CollectionUser` (three tables → one) |
| Permissions | `USER:READ`, `CREATE`, `UPDATE`, `DELETE`, `ASSIGN_ROLE` |
| UI | `/users` |

## Entity

One login identity for assessment, collection, and platform-admin. Roles decide which modules they see.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | JWT `userId` |
| `name` | string | yes | |
| `username` | string | yes | Unique; login id |
| `email` | string | yes | Unique |
| `password` | string | yes | BCrypt |
| `department` | string | no | |
| `designation` | string | no | |
| `address` | string | no | |
| `division` | string | no | Free text profile, not FK |
| `district` | string | no | Free text profile, not FK |
| `postal_code` | string | no | |
| `avatar` | string | no | |
| `cover_image` | string | no | |
| `bio` | text | no | |
| `mobile_no` | string | no | |
| `subdomain` | string | no | Copied from pourashava |
| `pourashava_id` | FK | yes | Tenant; NEVER null |
| `status` | boolean | yes | Default `true`; false disables login |

### Relations

- Belongs to [Pourashava](POURASHAVA.md)
- Many-to-many [Role](ROLE.md) via `user_roles`
- Planned: [UserWiseWordAssign](USER_WISE_WORD_ASSIGN.md), [ActivityLog](ACTIVITY_LOG.md)

## Business logic

- Username and email unique globally.
- Create requires name, username, email, password. Password is BCrypt-encoded; never stored or returned in plain text.
- Update may omit password; empty password means “keep current”.
- Tenant: `TenantGuard.resolvePourashava` on create; `assertSameTenant` on update/delete. Super admin can create users on any pourashava.
- Roles: request may send `roleCodes[]` or legacy `role`. Codes pass through `RoleCodes.fromLegacy`. If none sent, default is `VIEWER`.
- Role assignment uses the **subset rule** (`RoleService.assertCanAssign`): actor cannot grant a role whose permissions they do not hold, and cannot grant `SUPER_ADMIN` unless they are super admin. Needs `USER:ASSIGN_ROLE` on the endpoint that changes roles.
- After create/update/delete, evict `user-permissions` cache for that user.
- Disabled user (`status=false`) cannot refresh a token.
- Login: password match **and** `tenantId` equals the user’s pourashava. Super admin logs in as tenant `demo`.
- JWT carries `userId`, `pourashavaId`, `subdomain`, `roles` — **not** permissions. Permissions are resolved from roles (Redis cache 1h).
- Do not port Laravel’s `administrator` “log into any subdomain” bypass or email allowlists.
- Seeded: `admin` / `admin123` / `sreepur` (`POURASHAVA_ADMIN`); `superadmin` / `admin123` / `demo` (`SUPER_ADMIN`).
