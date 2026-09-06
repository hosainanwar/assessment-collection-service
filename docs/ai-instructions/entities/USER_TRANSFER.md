# UserTransfer

| | |
| Status | Planned |
| Table | `user_transfers` |
| Tenant | previous + current pourashava |
| Laravel | `app/Models/UserTransfer.php` |
| Permissions | `USER_TRANSFER:READ`, `USER_TRANSFER:CREATE` |

## Entity

Audit when a user moves from one pourashava to another.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `user_id` | FK | yes | |
| `email` | string | yes | Snapshot |
| `previous_subdomain` | string | yes | |
| `current_subdomain` | string | yes | |
| `application` | string | no | assessment / collection |
| `transferred_by` | user FK | no | |

## Business logic

- Platform-admin only. On transfer: update `users.pourashava_id` + `subdomain`, drop old ward/bank assignments, evict permission cache, insert this row.
- Do not keep the user readable under the old tenant filter after transfer.
- Super admin is not “transferred”; they stay on `demo`.
