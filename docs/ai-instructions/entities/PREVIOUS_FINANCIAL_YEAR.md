# PreviousFinancialYear

| | |
| Status | Planned |
| Table | `previous_financial_years` |
| Tenant | via customer — add `pourashava_id` |
| Laravel | `app/Models/PreviousFinancialYear.php` |
| Permissions | `TAX_BILL:CREATE`, `TAX_BILL:UPDATE` |

## Entity

Links a tax bill to a prior FY for due rollover.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `tax_bill_id` | FK | yes | |
| `financial_year` | string | yes | |

## Business logic

- `TaxBill.getNextFinancialYear()` uses the latest row to derive the next due FY.
- Written when creating a bill that carries prior-year due.
