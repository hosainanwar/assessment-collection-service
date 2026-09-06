# ApprovalHistory

| | |
| Status | Planned |
| Table | `approval_histories` |
| Tenant | via customer / sharokno (`pourashava_id` recommended) |
| Laravel | `app/Models/ApprovalHistory.php` |
| Permissions | `TAX_APPROVAL:READ`, `TAX_APPROVAL:UPDATE` |

## Entity

Audit row for each sharokno approval decision.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `sharokno_nirdharon_id` | FK | yes | |
| `status` | string | yes | approved / rejected / … |
| `status_updated_by` | string / user FK | yes | |
| `status_updater_role` | string | no | Role code at the time |
| `comment` | text | no | |
| `status_updated_at` | datetime | yes | |

## Business logic

- Insert-only. Do not update or delete history.
- Written from tax-approval (single and bulk).
- Show on the holding / approval screen as a timeline.
