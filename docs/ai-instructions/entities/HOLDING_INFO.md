# HoldingInfo

| | |
| Status | Planned |
| Table | `holding_infos` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/HoldingInfo.php` |
| Permissions | via parent holding / bordhito / holding-change |

## Entity

One physical floor/building line on a holding, versioned by `extended`.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | [CustomerInfo](CUSTOMER_INFO.md) |
| `holding_detail_id` | FK | yes | [HoldingDetails](HOLDING_DETAILS.md) |
| `own_rent_details` | string | yes | `1` = owned |
| `ground_details`, `details_details`, `class_details`, `floor_details` | string | no | |
| `height`, `width`, `squire_details` | string | yes | Area inputs |
| `height_details_bangla`, `width_details_bangla`, `squire_details_bangla` | string | no | BN digits |
| `extended` | int | yes | `0` = original; increment on bordhito |
| `serial` | int | no | Line order |
| `holding_status_type` | string | yes | `NORMAL` / `CHANGE` / `EXTENDED` |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to CustomerInfo, HoldingDetails
- Has many HoldingImageInfo
- Referenced by KornNirdharon

## Business logic

- Created with the holding. Initial `extended=0`, `holding_status_type=NORMAL`.
- Area = height × width × floor. Accept Bangla digits; convert before math.
- `own_status()` is true if any row has `own_rent_details=1`. Owned vs rented changes maintenance in [KornNirdharon](KORN_NIRDHARON.md) (rent: total/12×2).
- Bordhito clones rows at the new `extended`.
- Holding change: old `NORMAL` rows become `CHANGE`; new rows insert as `CHANGE` at `extended=0`.
- Each floor can have its own korn-nirdharon row (`holding_info_id`).
