# ExtendedCollectionInfo

| | |
| Status | Planned |
| Table | `extended_collection_infos` |
| Tenant | via customer — add `pourashava_id` |
| Laravel | `app/Models/ExtendedCollectionInfo.php` |
| Permissions | `EXTENSION:UPDATE`, `TAX_COLLECTION:UPDATE` |

## Entity

Bordhito amount waiting to be applied on collection.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `final_assesment_id` | FK | yes | |
| `customer_info_id` | FK | yes | |
| `extended_amount` | decimal | yes | |
| `extended_number` | decimal/int | yes | Matches FinalAssesment.extended |
| `kisti_no` | decimal/int | no | |
| `extended_date` | string | no | |
| `is_extended_apply` | string | yes | `YES` / `NO` |
| `holding_status_type` | string | no | |
| `is_bill_created` | bool | yes | |

## Business logic

- Created when FinalAssesment `extended ≥ 1` (and for `CHANGE` at extended 0).
- `is_extended_apply=NO` until collection applies it. While `NO`, block final-assessment edit.
- Apply: set `YES`, add `extended_amount` to TaxCollection, set `is_bill_created` when a bill is cut.
- Use the YES/NO constants; do not mix with boolean in the same column.
