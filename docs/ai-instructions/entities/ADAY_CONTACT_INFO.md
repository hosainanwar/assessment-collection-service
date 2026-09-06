# AdayContactInfo

| | |
| Status | Planned |
| Table | `aday_contact_infos` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/AdayContactInfo.php` |
| Permissions | `COLLECTION_INFO:READ` / `UPDATE` (Laravel had no explicit can) |

## Entity

Collection-office contact directory for a pourashava.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `name` | string | yes | |
| `email` | string | no | |
| `phone` | string | yes | |
| `extra` | JSON | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- CRUD in collection ContactController. Gate it; do not leave it authenticated-only.
- Printed on some collection documents.
