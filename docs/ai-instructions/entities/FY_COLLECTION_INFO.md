# FyCollectionInfo

| | |
| Status | Planned |
| Table | `fy_collection_infos` |
| Tenant | `pouroshova_info_id` / add `pourashava_id` |
| Laravel | `app/Models/FyCollectionInfo.php` |
| Permissions | `TAX_COLLECTION:READ` |

## Entity

Per-customer per-FY demand vs collected, for dashboards and due SQL.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `financial_year` | string | yes | |
| `due_amount`, `tax_amount` | decimal | yes | Demand |
| `due_amount_collection`, `tax_amount_collection` | decimal | yes | Collected |
| `holding_type_status` | string | no | |
| `pouroshova_info_id` | FK | yes | [PouroshovaInfo](POUROSHOVA_INFO.md) |
| unique | (customer, FY, pouroshova_info) | yes | |

## Business logic

- Upserted on TaxCollection create/update and on bill posting.
- TaxBill due calculations read this row. Keep it consistent in the same transaction as the collection/bill write.
- Prefer `pourashava_id` in the rewrite; `pouroshova_info_id` can stay if migrating 1:1.
