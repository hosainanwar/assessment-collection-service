# Word

| | |
| Status | Implemented |
| Table | `words` |
| Tenant | yes — `pourashava_id` + Hibernate filter |
| Java | `entity/Word.java` |
| Laravel | `app/Models/Word.php` |
| Permissions | `WORD:READ`, `WORD:CREATE`, `WORD:UPDATE`, `WORD:DELETE` |
| UI | `/words` |

## Entity

Ward (ওয়ার্ড) inside a pourashava. Named `Word` to match the existing schema.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `word_name` | string | yes | Display name |
| `subdomain` | string | yes | Copied from pourashava |
| `pourashava_id` | FK | yes | Tenant |
| `created_by` | string | no | Legacy |

### Relations

- Belongs to [Pourashava](POURASHAVA.md)
- Has many [Para](PARA.md)
- Planned: CustomerInfo, MutationMaster, TaxBill, [UserWiseWordAssign](USER_WISE_WORD_ASSIGN.md)

## Business logic

- `word_name` and subdomain are required.
- Create copies `pourashava_id` / `subdomain` from `TenantGuard`. A pourashava admin cannot create a ward in another tenant.
- Update/delete assert same tenant.
- Laravel blocked delete when customers still used the ward. **Add that check** when [CustomerInfo](CUSTOMER_INFO.md) lands. Also block delete when paras exist.
- List/search stay inside the tenant filter. Super admin sees all wards.
- After [UserWiseWordAssign](USER_WISE_WORD_ASSIGN.md) lands: operators with assignment rows only see those wards. `SUPER_ADMIN` / `POURASHAVA_ADMIN` with no rows see all wards in tenant. Operators with no rows see **none** (not Laravel’s empty-assignment = all-wards).
- UI: `/words` = `WORD:READ`. Create/edit/delete buttons use the matching action.
