# HoldingNumber

| | |
| Status | Planned |
| Table | `holding_numbers` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/HoldingNumber.php` |
| Permissions | none direct |

## Entity

Historical holding-number snapshot per customer / tax year.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `holding_id_present` | string | yes | |
| `holding_id_ex` | string | no | |
| `tax_year` | string | no | |
| `year` | string | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- Written when the holding number changes (mutation / holding change / new tax year).
- No standalone CRUD screen. Read-only history on the holding profile.
