# TaxSetup

| | |
| Status | Planned |
| Table | `tax_setups` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/TaxSetup.php` |
| Permissions | `TAX_RATE:READ`, `TAX_RATE:UPDATE`, `TAX_RATE:SETUP` |
| UI | ট্যাক্স সেটআপ |

## Entity

Rate sheet for one [TaxYear](TAX_YEAR.md): five tax heads plus land-development bands and a minimum tax.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `tax_year_id` | FK | yes | |
| `building_land`, `wastage`, `water`, `light`, `sewerage` | string/decimal | yes | Head rates |
| `land_dev_ind`, `land_dev_bis`, `land_dev_local` | string | no | Land-dev bands |
| `minimun` | string | no | Minimum tax (typo kept from Laravel unless renamed in Flyway) |
| `imarot_tax`, `wastage_tax`, `light_tax`, `water_tax`, `sewerage_tax` | string | no | Alternate rate fields used in nirdharon |
| `pourashava_id` | FK | yes | |

### Relations

- Belongs to TaxYear

## Business logic

- One setup per tax year per pourashava.
- [KornNirdharon](KORN_NIRDHARON.md) / [SharoknoNirdharon](SHAROKNO_NIRDHARON.md) apply these percentages. If computed tax &lt; minimum, use minimum.
- Customer tax flags (`land_tax`, `garbage_tax`, …) decide which heads appear on the bill; a zero flag omits that head even if a rate exists.
- Edit requires `TAX_RATE:UPDATE`. There is no create of a second sheet for the same year — update in place.
- Changing rates does **not** rewrite existing final assessments. New nirdharon uses the current sheet.
