# Usage

| | |
| Status | Planned |
| Table | `usages` |
| Tenant | no |
| Laravel | `app/Models/Usage.php` |
| Permissions | none direct |

## Entity

Property usage lookup (residential, commercial, mixed, …).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `usage_type` | string | yes | |

### Relations

- Referenced by CustomerInfo.`usage_id`

## Business logic

- Required on holding create. May change during holding change and bordhito.
- Usage can affect which tax heads apply; keep it as a lookup, not a permission.
