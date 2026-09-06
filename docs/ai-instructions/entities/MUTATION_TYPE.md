# MutationType

| | |
| Status | Planned |
| Table | `mutation_types` |
| Tenant | no |
| Laravel | `app/Models/MutationType.php` |
| Permissions | `MUTATION:READ` |

## Entity

Lookup of mutation type labels. The live case stores a **string constant** on [MutationMaster](MUTATION_MASTER.md).`mutation_type`.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `name` | string | yes | English / code |
| `bn_name` | string | yes | UI |

## Business logic

- Seed the six types. Controllers must set `mutation_type` from this catalogue, not free text.
- Laravel sometimes hardcoded `mutation_type_id=1` — use the code string instead.
