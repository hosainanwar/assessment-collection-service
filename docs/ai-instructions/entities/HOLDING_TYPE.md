# HoldingType

| | |
| Status | Planned |
| Table | `holding_types` |
| Tenant | optional historically; rewrite: national lookup (no tenant) unless a pourashava needs custom types |
| Laravel | `app/Models/HoldingType.php` |
| Permissions | none direct — required on holding create (`HOLDING:CREATE`) |

## Entity

Classification of a holding (residential / commercial / …).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `hold_type` | string | yes | Label |

### Relations

- Referenced by [CustomerInfo](CUSTOMER_INFO.md).`holding_type_id`

## Business logic

- Required on holding create. Cached lookup in Laravel (`CachesAllRecords`).
- Seed the common types. Do not let operators invent types unless a later admin screen is added.
- Updatable on holding-change / bordhito flows.
