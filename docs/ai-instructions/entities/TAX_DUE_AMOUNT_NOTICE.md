# TaxDueAmountNotice

| | |
| Status | Planned |
| Table | `tax_due_amount_notices` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/TaxDueAmountNotice.php` |
| Permissions | Laravel `Single Create`, `Block Wise Create`, `Block Wise Print` → `REPORT:NOTICE` plus a generate action if split |

## Entity

Generated due-demand notice for a defaulter.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `notice_number` | int | yes | Sequential per pourashava |
| `tax_due_amount` | decimal | yes | |
| `due_starting_year` | string | yes | |
| `financial_year` | string | yes | |
| `issue_date`, `notice_date` | datetime | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- Single and block-wise generation (`DueAmountNoticeController`).
- `notice_number` is sequential per tenant; do not reuse.
- Ward-scoped. Print uses [PouroshovaInfo](POUROSHOVA_INFO.md) letterhead.
