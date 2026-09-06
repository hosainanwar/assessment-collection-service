# MutationMaster

| | |
| Status | Planned |
| Table | `mutation_masters` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/MutationMaster.php` |
| Permissions | `MUTATION:READ`, `CREATE`, `UPDATE`, `DELETE`, `SUBMIT`, `APPROVE_ASSESSOR`, `APPROVE_PNO`, `APPROVE_MAYOR`, `REJECT` (+ type-specific modules) |
| UI | মালিকানা পরিবর্তন and related menus |

## Entity

Case header for name change, separation, amalgamation, and other mutation types.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `mutation_type` | string | yes | See types below |
| `word_id`, `para_id`, `customer_info_id`, `tax_year_id` | FK | yes | |
| `financial_year` | string | yes | Jul–Jun label |
| `receipt_no`, `dolil_no` | string | no | |
| `receipt_date`, `dolil_date` | date | no | Normalize to `Y-m-d` |
| `mutation_date` | datetime | no | |
| `approval_status` | string | yes | Workflow state |
| `submitted_by/at`, `approved_by_accessor/at`, `approved_by_pno/at`, `approved_by_mayor/at`, `rejected_by/at` | mixed | no | |
| `due_amount`, `final_assessment_tax_amount` | decimal | no | |
| `installment_no` | tinyint | no | |
| `effective_date` | date | no | |
| `owner_source`, `jail_no`, `khatian_no`, `dag_no`, `land_amount` | mixed | no | Separation |
| `pourashava_id` | FK | yes | |

### Types

`MUTATION_NAME`, `SEPARATION`, `AMALGAMATION`, `APPORTIONMENT`, `CONVERSION`, `CHANGE_IN_OCCUPANCY`

Type-specific UI modules (`MUTATION_SEPARATION`, …) still share this table.

### Relations

- Belongs to Word, Para, CustomerInfo, TaxYear
- Has many [MutationDetail](MUTATION_DETAIL.md), [MutationFileInfo](MUTATION_FILE_INFO.md)

## Business logic

- Workflow (forward only except reject):

  `PENDING` → `SUBMIT` → `APPROVED_ASS` → `APPROVED_PNO` → `APPROVED_MAYOR`  
  or → `REJECTED` from an approval step.

- Each step needs the matching permission. Actor of a step cannot skip ahead.
- Assessor / PNO / Mayor roles (`ASSESSOR`, `PNO`, `MAYOR`) are the intended grants.
- On **mayor approval**, apply the change to [CustomerInfo](CUSTOMER_INFO.md) (owner fields, and for separation a new holding + `separation_from_id`).
- Reject does not mutate the holding.
- Dates stored as `Y-m-d` (Laravel mutators).
- Ward-scoped via `word_id` / assignment.
- Files required or optional by type — keep Laravel’s current behaviour unless product changes it.
