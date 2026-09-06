# BillHistory

| | |
| Status | Planned |
| Table | `bill_histories` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/BillHistory.php` |
| Permissions | `BILL_POSTING:READ`, collection report codes |

## Entity

Denormalized snapshot of a posted bill for reports.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `tax_bill_id` | FK | yes | Unique |
| snapshots | mixed | yes | customer, holding, bank, word, para, FY, amounts, rebates, posting_by, payment |

## Business logic

- Insert on successful posting. One row per `tax_bill_id`.
- Collection reports read this table, not live TaxBill, so later bill edits do not rewrite history.
- No public update API.
