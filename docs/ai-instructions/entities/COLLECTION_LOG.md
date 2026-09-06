# CollectionLog

| | |
| Status | Planned |
| Table | `collection_logs` |
| Tenant | via customer — add `pourashava_id` |
| Laravel | `app/Models/CollectionLog.php` |
| Permissions | none direct (audit) |

## Entity

Coarser transaction log (assessment vs collection action).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `transaction_table` | string | yes | Source table |
| `transaction_id` | string | yes | |
| `action_type`, `action_by`, `action_time`, `action_form_name` | string | no | |
| `application_type` | string | yes | `Collection` or `Assesment` |

## Business logic

- Written on collection, mutation, and bordhito CRUD.
- Filter by `application_type` + `transaction_table`.
- Can later merge into [ActivityLog](ACTIVITY_LOG.md); until then keep both so reports that already use this table still work.
