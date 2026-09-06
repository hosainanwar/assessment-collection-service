# Condition

| | |
| Status | Planned |
| Table | `conditions` |
| Tenant | no |
| Laravel | `app/Models/Condition.php` |
| Permissions | none direct |

## Entity

Property condition lookup.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `condition_type` | string | yes | |

### Relations

- Referenced by CustomerInfo.`condition_id`

## Business logic

- Required on holding create. May change during [holding change](HOLDING_CHANGE.md) and [bordhito](BORDHITO.md).
