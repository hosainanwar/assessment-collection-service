# FinalAssesment

| | |
| Status | Planned |
| Table | `final_assesments` (keep Laravel spelling on the table) |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/FinalAssesment.php` |
| Permissions | `FINAL_ASSESSMENT:READ`, `CREATE`, `UPDATE`, `DELETE`, `DIRECT`, `NEW_YEAR` |
| UI | চূড়ান্ত কর |

## Entity

Authoritative (চূড়ান্ত) tax for a holding version. Collection demand starts here.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `kisti_id` | FK | yes | [Kisti](KISTI.md) |
| `so_information_id` | FK | no | |
| `apply_type_id` | int | no | |
| `apply` / `instruction` / `effect_date_1` / `effect_date_2` | string | no | |
| `final_asses_cost` | string | no | |
| `land_bill`, `wastage_bill`, `water_bill`, `light_bill`, `sewerage_bill` | string | yes | Five heads |
| `*_info`, `*_tax_parcent` | mixed | no | Rate snapshot |
| `extended` | int | yes | `0` original; ≥1 bordhito |
| `total_tax` | int | yes | Sum of five bills |
| `holding_status_type` | string | yes | |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to CustomerInfo
- Referenced by [TaxCollection](TAX_COLLECTION.md) (`final_assessment_id`, Laravel cascade delete — **do not cascade** if bills exist)
- Creates [ExtendedCollectionInfo](EXTENDED_COLLECTION_INFO.md) when `extended ≥ 1`

## Business logic

- Normal path: approved [SharoknoNirdharon](SHAROKNO_NIRDHARON.md) for the same `extended` must exist.
- **Direct Churanto** (`FINAL_ASSESSMENT:DIRECT`) skips sharokno. Also requires pourashava feature `direct_final_tax`.
- `total_tax` = sum of five heads (BN→EN). Apply tax flags from the holding.
- `extended ≥ 1` inserts ExtendedCollectionInfo with `is_extended_apply=NO`. Block further final edit while unapplied.
- `CHANGE` holdings get an extended-collection row at `extended=0`.
- Upsert [FyCollectionInfo](FY_COLLECTION_INFO.md).`tax_amount` for the current FY.
- `FINAL_ASSESSMENT:NEW_YEAR` maps `New Final Assessment Create`.
- `SPECIAL_EDIT:UPDATE` is the dangerous “final + aday special edit” — `SUPER_ADMIN` / explicit grant only.
- Ward-scoped via parent holding.
