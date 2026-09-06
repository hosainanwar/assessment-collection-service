# TaxCollection

| | |
| Status | Planned |
| Table | `tax_collections` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/TaxCollection.php` |
| Permissions | `TAX_COLLECTION:READ`, `CREATE`, `UPDATE`, `DELETE` |
| UI | Collection — tax collection |

## Entity

Per-holding demand snapshot for a financial year (current tax + due).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `final_assessment_id` | FK | yes | [FinalAssesment](FINAL_ASSESSMENT.md) |
| `final_assessment_cost` | decimal | yes | Snapshot |
| `partial_amount` | string | no | |
| `due_starting_year` | string | if due &gt; 0 | Laravel required 9 chars |
| `total_due` | decimal | yes | |
| `updated_due_amount`, `updated_financial_year`, `due_amount_updated_at` | mixed | no | After [DueReduction](DUE_REDUCTION.md) |
| `is_*_tax_present` + percent + due_total | mixed | no | Five due heads |
| `is_*_bill_present` + percent + bill | mixed | no | Five current heads |
| `total_tax` | decimal | yes | Current demand |
| `installment_no` | int | no | |
| `extended_amount`, `extended_date` | mixed | no | Bordhito applied to collection |
| `financial_year` | string | yes | Jul–Jun |
| `grand_total` | decimal | yes | `total_tax + total_due` |
| `holding_status_type` | string | yes | |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to CustomerInfo, FinalAssesment, optionally BankInformation
- Has many [TaxBill](TAX_BILL.md)

## Business logic

- One primary collection per customer per FY, pointing at the latest final assessment.
- On create: `grand_total = total_tax + total_due`. If `total_due > 0`, `due_starting_year` is required.
- Upsert [FyCollectionInfo](FY_COLLECTION_INFO.md). Set `customer.is_due_available` from `total_due`.
- Applying an extension increments `extended_amount` and recalculates `grand_total`; marks ExtendedCollectionInfo applied.
- Ward scope: collection user’s [CollectionUserWiseWordAssign](COLLECTION_USER_WISE_WORD_ASSIGN.md).
- Laravel cascaded delete from FinalAssesment — **block delete** if any TaxBill is posted.
- `Due Tax Reduce` is [DueReduction](DUE_REDUCTION.md), not a silent edit of `total_due`.
