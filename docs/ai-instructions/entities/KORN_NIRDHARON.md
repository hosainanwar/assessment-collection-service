# KornNirdharon

| | |
| Status | Planned |
| Table | `korn_nirdharons` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/KornNirdharon.php` |
| Permissions | `ASSESSMENT:READ`, `CREATE`, `UPDATE`, `DELETE` |
| UI | কর নির্ধারণ |

## Entity

Per-floor proposed tax (কর নির্ধারণ) before sharokno / final.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `holding_info_id` | FK | yes | Floor |
| `nirdharon_type` | int | yes | `1` = rent, `2` = built |
| `squirefeet` | string | yes | Area |
| `cost_per_squire` | string | yes | Rate |
| `totalcost` | string | yes | Area × rate |
| `extended` | int | yes | Matches holding/bordhito version |
| `holding_status_type` | string | yes | |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to CustomerInfo, HoldingInfo

## Business logic

- `totalcost` = square feet × cost per square (Bangla digits converted).
- `total_value()` sums `totalcost` for the holding version. `own_total()` sums owned floors only.
- Maintenance: rent type uses total/12×2; built/own uses the own variant.
- Must exist (current `extended`) before [SharoknoNirdharon](SHAROKNO_NIRDHARON.md) / final assessment, except the **Direct Churanto** path.
- Created from Nirdharon / TaxFix / RentTaxFix flows. Keep one service with `nirdharon_type` rather than three entities.
- Versioned with `extended`. Bordhito clones these rows with the new number.
- Ward scope via the parent holding’s `word_id`.
