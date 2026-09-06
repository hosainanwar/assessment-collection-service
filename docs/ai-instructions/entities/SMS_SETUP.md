# SmsSetup

| | |
| Status | Planned |
| Table | `sms_setups` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/SmsSetup.php` |
| Permissions | `SMS_SETUP:READ`, `SMS_SETUP:UPDATE` |
| UI | এসএমএস সেটআপ |

## Entity

Per-tenant SMS gateway config.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `sender_name`, `sender_number`, `sending_type` | string | yes | |
| `api_key`, `api_secret` | string | yes | Secrets — never return in list APIs |
| `provider` | string | yes | |
| `notification_preference` | JSON | no | |
| `is_active` | bool | yes | |
| `pourashava_id` | FK | yes | |

## Business logic

- One active config per pourashava.
- API keys stay encrypted at rest if possible; never log them.
- Send flows read the active setup. Missing/inactive setup → 409 with a clear message, not a silent skip.
- Laravel `SMSBODY` was a service class, not a table — keep dispatch in a service, not an entity.
