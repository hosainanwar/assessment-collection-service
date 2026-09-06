# ActivityLog

| | |
| Status | Planned |
| Table | `activity_logs` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/ActivityLog.php` + `LogsModelActivity` |
| Permissions | `ACTIVITY_LOG:READ` |
| UI | লগ রিপোর্ট |

## Entity

Polymorphic change audit.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | long | no | When the change is holding-related |
| `loggable_type`, `loggable_id` | morph | yes | |
| `performed_by` | FK | no | [User](USER.md) |
| `action_by` | string | no | Display name |
| `event` | string(32) | yes | create / update / delete |
| `changed_attributes`, `old_attributes` | JSON | no | |
| `ip_address` | string | no | |
| `user_agent` | string | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- Auto-log create/update/delete on holdings, assessments, bills, mutations, users (Laravel: ~30 models). Ignore `created_at` / `updated_at` in diffs.
- Read-only API. Super admin can see all tenants; others see their tenant.
- Prefer this over Laravel email-gated “delete data” screens.
