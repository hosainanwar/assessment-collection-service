# District

| | |
| Status | Implemented |
| Table | `districts` |
| Tenant | no — national lookup |
| Java | `entity/District.java` |
| Laravel | `app/Models/District.php` |
| Permissions | `DISTRICT:READ`, `DISTRICT:CREATE`, `DISTRICT:UPDATE`, `DISTRICT:DELETE` |
| UI | `/districts` |

## Entity

District under a [Division](DIVISION.md). Used when creating a [Pourashava](POURASHAVA.md).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | Identity |
| `name` | string | yes | Bangla name; unique **within** the division |
| `en_name` | string | yes | English name |
| `division_id` | FK | yes | Parent division |

### Relations

- Belongs to Division
- Has many Pourashava

## Business logic

- `division_id` is required; missing division → `UserInformException`.
- Duplicate `name` in the same division is rejected. The same Bangla name may exist in another division.
- List-by-division (`GET` filtered by `divisionId`) feeds the pourashava form.
- **Do not** turn district names into permission codes (Laravel seeded `ঢাকা`, `গাজীপুর` as permissions). Geographic scope for platform users is `admin_district_scopes`, not RBAC.
- Delete is unrestricted in the API today. Block delete when pourashavas still reference the district.
- UI: `/districts` needs `DISTRICT:READ`. Create/edit/delete buttons use the matching action.
