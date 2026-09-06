# Kisti

| | |
| Status | Planned |
| Table | `kistis` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/Kisti.php` |
| Permissions | none direct — used by final assessment / bills |

## Entity

Installment (কিস্তি) schedule: name + due date for a pourashava.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | [FinalAssesment](FINAL_ASSESSMENT.md).`kisti_id` |
| `name` | string | yes | e.g. 1st / 2nd |
| `date` | string/date | yes | Due date label |
| `pourashava_id` | FK | yes | |

### Relations

- Referenced by FinalAssesment

## Business logic

- Four installments per financial year.
- Current installment from calendar month: Jul–Sep=1, Oct–Dec=2, Jan–Mar=3, Apr–Jun=4.
- Sharokno installment amount = `total_tax / 4`.
- [BillType](BILL_TYPE.md) also encodes installment 1–4; keep both in sync or collapse later — do not invent a third numbering.
