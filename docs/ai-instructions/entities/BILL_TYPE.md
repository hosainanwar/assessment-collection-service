# BillType

| | |
| Status | Planned |
| Table | `bill_types` |
| Tenant | no (global) unless a pourashava needs custom labels |
| Laravel | `app/Models/BillType.php` |
| Permissions | `BILL_TYPE:READ`, `CREATE`, `UPDATE`, `DELETE` |

## Entity

Installment bill-type definition.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `name` | string | yes | |
| `installment_no` | string | yes | `1`–`4` |
| `max_inst_number` | int | no | |

## Business logic

- Constants FIRST=1 … FOURTH=4. Drives labeling and rebate rules on [TaxBill](TAX_BILL.md).
- Keep aligned with [Kisti](KISTI.md) numbering.
