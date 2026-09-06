# DueReduction

| | |
| Status | Planned |
| Table | `due_reductions` |
| Tenant | via customer — add `pourashava_id` |
| Laravel | `app/Models/DueReduction.php` |
| Permissions | map Laravel `Due Tax Reduce` → `TAX_COLLECTION` special or `DUE_REDUCTION:CREATE` |

## Entity

Audit of a percentage due relief.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `due_percentage` | decimal(12,2) | yes | |
| `previous_due` | decimal | yes | Before |
| `current_due` | decimal | yes | After |
| `reduction_due` | decimal | yes | Amount relieved |
| `created_by` | user FK | yes | |

## Business logic

- Insert-only. Then update [TaxCollection](TAX_COLLECTION.md) `updated_due_*` and FyCollectionInfo.
- Never edit `total_due` without a DueReduction row.
- Restrict to pourashava admin / super admin unless a dedicated permission is granted.
