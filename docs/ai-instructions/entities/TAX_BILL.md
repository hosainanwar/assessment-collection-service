# TaxBill

| | |
| Status | Planned |
| Table | `tax_bills` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/TaxBill.php` |
| Permissions | `TAX_BILL:*`, `BILL_POSTING:*` |
| UI | Tax bill / bill posting |

## Entity

One installment voucher for a [TaxCollection](TAX_COLLECTION.md).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `tax_collection_id` | FK | yes | |
| `word_id`, `para_id`, `customer_info_id`, `bank_information_id`, `bill_type_id` | FK | yes | |
| `financial_year` | string | yes | |
| `bill_number` | string | yes | Unique per pourashava |
| `last_number` | int | no | Sequence used to build bill_number |
| `issue_year`, `bill_issue_date`, `bill_paid_date` | string | no | |
| `installment_no` | string | yes | 1–4 |
| `total_installment` | int | yes | Usually 4 |
| `first`–`fourth_installment_rebate_amount` / `_amount` | decimal | no | Rebate tiers |
| `is_sar_charge_allowed`, `sar_charge_amount` | mixed | no | |
| `partial_amount`, `due_amount`, `total_demand_amount`, `net_tax` | decimal | yes | |
| `is_posting_done` | bool | yes | Receipt posted |
| `posting_date`, `posting_by`, `posting_total_amount`, `posting_due_amount`, `due_amount_calculation` | mixed | no | |
| `payment_type`, `payment_transaction_number` | string | no | |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to TaxCollection, BillType, BankInformation, CustomerInfo
- Has one [Payment](PAYMENT.md)
- Snapshot copied to [BillHistory](BILL_HISTORY.md) on posting

## Business logic

- `bill_number` unique per pourashava.
- Bank must be active and (for collectors) in [UserWiseBankAssign](USER_WISE_BANK_ASSIGN.md).
- Rebates: default tiers; override only with explicit rebate permissions (Laravel `Ten Percent Rebate` / `Zero Percent Rebate` — add `TAX_BILL:REBATE_TEN` / `TAX_BILL:REBATE_ZERO` if those stay).
- **Posting** sets `is_posting_done`, writes BillHistory, updates FyCollectionInfo collected amounts.
- Delete forbidden after posting unless `BILL_POSTING` special-delete permission (super admin).
- Public Laravel bill-lookup/print had **no auth**. If a citizen lookup remains, it is an explicit public exception and must not dump extra PII.
- Due figures come from FyCollectionInfo + daily collection views.
