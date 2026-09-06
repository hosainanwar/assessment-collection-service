# Month

| | |
| Status | Planned |
| Table | `months` |
| Tenant | no |
| Laravel | `app/Models/Month.php` |
| Permissions | none |

## Entity

Bengali/English month names for report headings.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | 1–12 |
| `month_name_en` | string | yes | |
| `month_name_bn` | string | yes | |

## Business logic

- Seed 12 rows. Used only for display. Do not use this table for installment math — that is month-number rules on [Pourashava](POURASHAVA.md) / [Kisti](KISTI.md).
