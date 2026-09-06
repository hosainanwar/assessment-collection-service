# UserWiseBankAssign

| | |
| Status | Planned |
| Table | `user_wise_bank_assigns` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/UserWiseBankAssign.php` |
| Permissions | `BANK_ASSIGN:READ`, `CREATE`, `UPDATE`, `DELETE` |

## Entity

Collection user ↔ bank account.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `collection_user_id` | FK | yes | Rewrite: `user_id` → [User](USER.md) |
| `bank_information_id` | FK | yes | |
| unique | (user, bank) | yes | |
| `pourashava_id` | FK | yes | |

## Business logic

- Collectors only pick assigned banks when creating bills.
- Same tenant on user and bank.
- Empty assignment: follow the rewrite ward rule — collectors see **no** banks until assigned; pourashava admin sees all active banks.
