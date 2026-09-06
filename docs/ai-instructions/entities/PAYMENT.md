# Payment

| | |
| Status | Planned |
| Table | `payments` (model exists; confirm migration on port) |
| Tenant | via TaxBill |
| Laravel | `app/Models/Payment.php` |
| Permissions | via bill posting / collection |

## Entity

Online-gateway attempt for one [TaxBill](TAX_BILL.md).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `tax_bill_id` | FK | yes | |
| `gateway` | string | yes | |
| `transaction_id` | string | yes | |
| `amount` | decimal | yes | |
| `status` | string | yes | `pending` / `success` / `failed` |
| `notes` | string | no | |

## Business logic

- Status constants only those three values.
- Success should set TaxBill `payment_type` / `payment_transaction_number` and may trigger posting — keep that in one service so gateway callbacks cannot double-post.
- Do not trust client-sent `success`; verify with the gateway.
