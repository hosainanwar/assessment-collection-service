# OwnerType

| | |
| Status | Planned |
| Table | `owner_types` |
| Tenant | no |
| Laravel | `app/Models/OwnerType.php` |
| Permissions | none direct |

## Entity

Owner relationship label (father / husband / …).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `relation_type` | string | no | |

### Relations

- Referenced by CustomerInfo and [MutationDetail](MUTATION_DETAIL.md)

## Business logic

- Seeded lookup for holding and mutation forms.
- `house_holder_type` on CustomerInfo is a separate field (owner vs occupier); do not collapse the two.
