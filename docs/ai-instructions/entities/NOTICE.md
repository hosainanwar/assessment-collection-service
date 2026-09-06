# Notice

| | |
| Status | Planned |
| Table | `notices` |
| Tenant | no (platform CMS) |
| Laravel | `app/Models/Notice.php` |
| Permissions | `NOTICE:READ`, `CREATE`, `UPDATE`, `DELETE` |

## Entity

CMS notice for assessment/collection apps.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `type`, `app`, `title`, `description` | string | yes | `app` = assessment / collection / both |
| `image`, `file` | string | no | |
| `is_active`, `is_featured` | bool | yes | |
| `extra` | JSON | no | |
| `created_by`, `updated_by` | user FK | no | |

## Business logic

- Public Laravel notice APIs were unauthenticated. Tenant UI may **read** active notices without a write permission; writes stay platform-admin.
- Featured + active flags drive the welcome/dashboard strip.
