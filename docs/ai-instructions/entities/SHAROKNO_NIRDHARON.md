# SharoknoNirdharon

| | |
| Status | Planned |
| Table | `sharokno_nirdharons` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/SharoknoNirdharon.php` |
| Permissions | `ASSESSMENT:*`, `TAX_APPROVAL:READ`, `TAX_APPROVAL:UPDATE` |
| UI | কর অনুমোদন |

## Entity

Official building-tax order (স্মারক নং নির্ধারণ) with five tax heads and an approval state.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `tax_year_id` | FK | yes | |
| `nirdharon_type` | int | yes | |
| `last_number` | int | no | Sequence |
| `sharok_no` | string | yes | Memo number |
| `effectdate` | string | no | |
| `kistino` | int | yes | 1–4 |
| `building_bill`, `wastage_bill`, `light_bill`, `water_bill`, `sewerage_bill` | string | yes | Five heads |
| `land_*`, `wastage_*`, `water_*`, `light_*`, `sewerage_*` info + percent | mixed | no | Rate snapshot |
| `status` | string | yes | `1` = active nirdharon |
| `approval_status` | string | yes | `pending` / `approved` / `rejected` |
| `status_updated_by`, `status_updated_at` | mixed | no | |
| `total_tax` | int | yes | Sum of heads (min tax applied) |
| `extended` | int | yes | |
| `issuedate` | string | no | |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to CustomerInfo, TaxYear
- Has one [IssueDate](ISSUE_DATE.md)
- Has many [ApprovalHistory](APPROVAL_HISTORY.md)

## Business logic

- Required before a normal [FinalAssesment](FINAL_ASSESSMENT.md) for the same `extended` (`finalAssessmentStatus()`).
- Installment amount = `total_tax / 4`.
- Approval: `pending` → `approved` or `rejected` via `TAX_APPROVAL:UPDATE`. Write [ApprovalHistory](APPROVAL_HISTORY.md) every time. Bulk approve is allowed.
- Only `approved` sharokno may proceed to churanto (unless Direct Churanto).
- `status=1` is the active order for that extended version.
- PNO cannot re-approve their own step (Laravel tax-approval UI). Encode that in the approval service.
