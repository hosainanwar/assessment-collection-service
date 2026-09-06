# Division

| | |
| Status | Implemented |
| Table | `divisions` |
| Tenant | no — national lookup |
| Java | `entity/Division.java` |
| Laravel | `app/Models/Division.php` |
| Permissions | `DIVISION:READ`, `DIVISION:CREATE`, `DIVISION:UPDATE`, `DIVISION:DELETE` |
| UI | `/divisions` |

## Entity

Bangladesh division. Pourashavas and districts hang off this.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | Identity |
| `name` | string | yes | Unique (API enforces) |

### Relations

- Has many [District](DISTRICT.md), [Pourashava](POURASHAVA.md)

## Business logic

- Name is unique across the table. Create/update reject a duplicate name.
- Name is required and non-blank (`DivisionValidatorService`).
- Super admin (or anyone with write permissions) maintains this list. Pourashava staff only need `DIVISION:READ` for dropdowns.
- Delete is allowed by the API today. **Do not delete** a division that still has districts or pourashavas — add that FK check when porting constraints.
- Seeded reference data in Laravel; keep seeds for Bangladesh’s eight divisions.
- UI: list/create/edit/delete gated by the matching `DIVISION:*` permission. Hide is not enough; the API already uses `@PreAuthorize`.
