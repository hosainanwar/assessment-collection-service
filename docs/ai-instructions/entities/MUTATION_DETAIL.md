# MutationDetail

| | |
| Status | Planned |
| Table | `mutation_details` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/MutationDetail.php` |
| Permissions | same as [MutationMaster](MUTATION_MASTER.md) |

## Entity

Before/after owner snapshot (and amount) for one mutation case.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `mutation_master_id` | FK | yes | |
| `customer_info_id` | FK | yes | |
| `previous_*` / `present_*` | mixed | yes | house_holder_type, owner_name, owner_type_id, parent, nid, email, mobile, address |
| `amount` | decimal | no | Used in due calcs |
| `financial_year` | string | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- One or more details per master (amalgamation / separation can have several holdings).
- Immutable after mayor approval except via a new mutation.
- `amount` feeds due calculations on approval; do not silently drop it.
