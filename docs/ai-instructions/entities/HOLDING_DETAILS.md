# HoldingDetails

| | |
| Status | Planned |
| Table | `holding_details` |
| Tenant | no |
| Laravel | `app/Models/HoldingDetails.php` |
| Permissions | none direct |

## Entity

Building-detail type lookup (room / floor category) referenced by each [HoldingInfo](HOLDING_INFO.md) row.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `holdingdetails_type` | string | yes | |

### Relations

- Has many HoldingInfo

## Business logic

- Required on customer/holding-floor create (`holddetail` in Laravel form).
- Seeded, cached globally.
