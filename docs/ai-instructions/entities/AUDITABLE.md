# Auditable

| | |
| Status | Implemented |
| Table | — (mapped superclass) |
| Tenant | no |
| Java | `entity/Auditable.java` |

## Entity

Base columns on every persisted entity.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `created_at` | datetime | yes | Set once; not updatable |
| `updated_at` | datetime | no | Last write |

## Business logic

- JPA auditing (`AuditingEntityListener`) fills both timestamps. Services must not set them by hand.
- Planned entities inherit the same two columns. Do not add Laravel-style `added_by` string columns unless a real user FK is needed; prefer `created_by` / `updated_by` as `users.id` when auditing who acted.
- `ActivityLog` records field diffs; it ignores `created_at` / `updated_at`.
