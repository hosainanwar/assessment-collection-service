# TaxYear

| | |
| Status | Planned |
| Table | `tax_years` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/TaxYear.php` |
| Permissions | `TAX_YEAR:READ`, `CREATE`, `UPDATE`, `DELETE` |
| UI | করবর্ষ সেটআপ |

## Entity

Assessment tax-year period for one pourashava.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `tax_year` | string | yes | Display, e.g. `2025-2026` |
| `biss_year` | string | no | BS year |
| `start_year` | string | no | |
| `effect_date` | date/string | no | When rates take effect |
| `year` | string | no | |
| `status` | string | yes | `1` active, `0` inactive |
| `added_by` | string / user FK | no | |
| `pourashava_id` | FK | yes | Tenant |
| `subdomain` | string | yes | Copy from pourashava |

### Relations

- Has many [TaxSetup](TAX_SETUP.md)
- Referenced by [CustomerInfo](CUSTOMER_INFO.md), [MutationMaster](MUTATION_MASTER.md), [SharoknoNirdharon](SHAROKNO_NIRDHARON.md)

## Business logic

- **At most one active** tax year per pourashava (`status=1`). Activating a year deactivates the previous one.
- New holdings lock to `tax_year_id` at create time.
- [TaxSetup](TAX_SETUP.md) rates are loaded from the active year (`CustomerInfo.getCurrentTaxRate()` in Laravel).
- Delete is forbidden while any customer, mutation, or setup still references the year.
- Tenant filter + `TenantGuard` on writes.
- UI: menu `TAX_YEAR:READ`; cannot delete if “in use” — show the reason.
