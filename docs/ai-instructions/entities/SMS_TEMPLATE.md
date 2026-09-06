# SMSTemplate

| | |
| Status | Planned |
| Table | `s_m_s_templates` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/SMSTemplate.php` |
| Permissions | `SMS:SEND` (manage templates: `SMS_SETUP:UPDATE`) |

## Entity

Reusable SMS body per pourashava.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `name` | string | yes | |
| `smsbody` | string | yes | Placeholders for customer/tax |
| `active` | bool | yes | |
| `pourashava_id` | FK | yes | |

## Business logic

- Substitute placeholders at send time. Do not let operators inject unescaped user input into other tenants’ messages.
- Only `active` templates appear in the send form.
