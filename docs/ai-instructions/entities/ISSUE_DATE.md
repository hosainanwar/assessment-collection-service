# IssueDate

| | |
| Status | Planned |
| Table | `issue_dates` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/IssueDate.php` |
| Permissions | `REPORT:NOTICE`, `REPORT:CHURANTO_UO` |

## Entity

Issue / effective date for a nirdharon or sharokno cycle.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `issuedate` | string | yes | |
| `extended` | int | yes | |
| `korn_nirdharon_id` | FK | no | |
| `sharokno_nirdharon_id` | FK | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- `finalDateIssue()` = issue date for the current `extended`.
- Printed on notice and churanto UO reports. Not a standalone CRUD screen.
