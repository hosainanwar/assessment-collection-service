# SmsReport

| | |
| Status | Planned |
| Table | `sms_reports` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/SmsReport.php` |
| Permissions | `SMS:SEND`, `SMS:READ_REPORT` |

## Entity

Log of one SMS attempt for a holding.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `datefield` | string | yes | |
| `timefield` | datetime | yes | |
| `nirdharon_type` | string | no | |
| `response` | string | no | Provider result |
| `pourashava_id` | FK | yes | |

## Business logic

- Insert after every send (success or fail). Never delete from the UI.
- `SMS:READ_REPORT` for the report screen; send remains `SMS:SEND`.
- Laravel left some SMS API routes unauthenticated — all send endpoints require auth.
