# CustomerInfo (Holding)

| | |
| Status | Planned |
| Table | `customer_infos` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/CustomerInfo.php` |
| Permissions | `HOLDING:READ`, `CREATE`, `UPDATE`, `DELETE`, `CHANGE_STATUS`, `CREATE_NEW`, `UPDATE_ADDRESS` |
| UI | হোল্ডিং এর তথ্য |

## Entity

Taxpayer / holding master. Collection, assessment, mutation, SMS, and bills all hang off this row.

Rewrite name in APIs/UI: **Holding**. Keep table `customer_infos` if migrating data; Java entity may be `CustomerInfo` or `Holding`.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `tax_year_id` | FK | yes | [TaxYear](TAX_YEAR.md) at create |
| `tax_year`, `year` | string | no | Denormalized labels |
| `word_id` | FK | yes | [Word](WORD.md) |
| `para_id` | FK | yes | [Para](PARA.md) |
| `holding_id_present` | string | yes | Current holding number |
| `holding_id_ex` | string | no | Previous holding number |
| `owner_name` | string | yes | |
| `owner_type_id` | FK | no | [OwnerType](OWNER_TYPE.md) |
| `owners_parent_name` | string | no | |
| `nid`, `email`, `mobile`, `address` | string | no | Address edit may be a separate permission |
| `holding_type_id` | FK | yes | [HoldingType](HOLDING_TYPE.md) |
| `condition_id` | FK | yes | [Condition](CONDITION.md) |
| `usage_id` | FK | yes | [Usage](USAGE.md) |
| `kor_fix_type` | int | no | Tax-fix path |
| `status` | string | no | |
| `is_freedom_fighter` | bool | no | |
| `house_holder_type` | string | yes | |
| `land_tax`, `garbage_tax`, `road_light_rate`, `water_rate`, `sewerage_tax` | int | yes | 0/1 flags for bill heads |
| `holding_status_type` | string | yes | `NORMAL` / `CHANGE` / `EXTENDED` |
| `holding_count` | int | no | |
| `previous_customer_id` | FK | no | |
| `separation_from_id` | FK | no | Parent holding after separation |
| `owner_source`, `deed_no`, `deed_date`, `jail_no`, `khatian_no`, `dag_no`, `land_amount` | mixed | no | Separation / mutation land data |
| `is_active` | bool | yes | Default true; global list hides inactive |
| `deactivation_reason`, `deactivated_at` | mixed | no | |
| `is_due_available` | bool | no | Set by [TaxCollection](TAX_COLLECTION.md) |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to Word, Para, OwnerType, Condition, Usage, HoldingType, TaxYear, Pourashava
- Has many [HoldingInfo](HOLDING_INFO.md), [HoldingNumber](HOLDING_NUMBER.md), [KornNirdharon](KORN_NIRDHARON.md), [SharoknoNirdharon](SHAROKNO_NIRDHARON.md), [FinalAssesment](FINAL_ASSESSMENT.md), [HoldingImageInfo](HOLDING_IMAGE_INFO.md), [SmsReport](SMS_REPORT.md), [CollectionLog](COLLECTION_LOG.md), [TaxBill](TAX_BILL.md)
- Has one TaxCollection (current FY snapshot)

## Business logic

- Default list **only active** holdings (`is_active=true`). Inactive stay in DB for history.
- Create requires tax year, word, para, holding number, owner, holding type, condition, usage, floor dimensions (on HoldingInfo), house_holder_type, address.
- Uniqueness: `(pourashava_id, word_id, para_id, holding_id_present)` among active rows.
- Lists scoped by [UserWiseWordAssign](USER_WISE_WORD_ASSIGN.md).
- `holding_status_type`: `NORMAL` → `CHANGE` ([holding change](HOLDING_CHANGE.md)) → `EXTENDED` ([bordhito](BORDHITO.md)).
- Tax flags decide which of the five heads appear on sharokno / final / bills.
- Soft-deactivate (`is_active=false`) instead of hard delete when bills exist. Laravel hard-deleted and cascaded korn/sharokno/holdings — **do not cascade-delete financial history**.
- `HOLDING:UPDATE_ADDRESS` for address-only edits if that must stay split from full edit.
- `HOLDING:CHANGE_STATUS` for active/inactive or status workflows.
- `HOLDING:CREATE_NEW` maps Laravel `New Customer Info Create` (new-year / new-info path).
- `is_due_available` is written by collection, not by the holding form.
- Separation copies `holding_id_ex` from the parent and sets `separation_from_id`.
- `lastExtendedNumber()` = max `FinalAssesment.extended` + 1 for the next bordhito version.
