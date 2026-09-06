# SpecialPersonsNumber

| | |
| Status | Planned |
| Table | `special_persons_numbers` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/SpecialPersonsNumber.php` |
| Permissions | Laravel had none — gate with `SMS_SETUP:UPDATE` or `COLLECTION_INFO:UPDATE` |

## Entity

VIP / special contacts for SMS or outreach.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `name` | string | yes | |
| `designation`, `address`, `description`, `relation` | string | no | |
| `mobileno` | string | yes | 11-digit |
| `status` | string | yes | Default `1` = active |
| `pourashava_id` | FK | yes | |

## Business logic

- Status toggles active. Send-to-special-list uses active rows only.
- Validate mobile format in the send service.
