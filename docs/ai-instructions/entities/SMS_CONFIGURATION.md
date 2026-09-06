# SMSConfiguration

| | |
| Status | Planned — legacy; prefer [SmsSetup](SMS_SETUP.md) |
| Table | `s_m_s_configurations` |
| Tenant | no (global) |
| Laravel | `app/Models/SMSConfiguration.php` |
| Permissions | none direct |

## Entity

Old global SMS API row (`url`, `json_data`, `company_name`).

## Business logic

- Superseded by per-tenant SmsSetup. Port only if a pourashava still depends on it.
- Do not add a UI unless product still needs a platform-wide fallback.
- Laravel `SMSBODY` mapped provider codes 1002–1019 to messages and required an 11-digit mobile — keep that in the send service.
