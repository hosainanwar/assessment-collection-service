# SoInformation

| | |
| Status | Planned |
| Table | `so_information` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/SoInformation.php` |
| Permissions | `FINAL_ASSESSMENT:CREATE` (via churanto chain) |

## Entity

SO-form fee / receipt captured in the assessment chain.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `tax_year_id` | FK | yes | |
| `date` | string | no | |
| `last_number` | int | no | |
| `roshidno` | string | no | Receipt no |
| `feecost` | string | yes | |
| `biddomantax` | string | no | |
| `correctedtax` | string | no | |
| `extended` | int | yes | One row per extended cycle |
| `holding_status_type` | string | yes | |
| `pourashava_id` | FK | yes | |

## Business logic

- One per customer + `extended`. `soformstatus()` is “exists for this version”.
- Some pourashavas require SO before final assessment. If that remains a rule, reject final create when missing.
- Used on SO reports (`REPORT:SO`).
