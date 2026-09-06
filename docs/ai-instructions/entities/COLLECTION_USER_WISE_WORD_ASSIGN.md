# CollectionUserWiseWordAssign

| | |
| Status | Planned |
| Table | `collection_user_wise_word_assigns` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/CollectionUserWiseWordAssign.php` |
| Permissions | `COLLECTION_WORD_ASSIGN:READ`, `CREATE`, `UPDATE`, `DELETE` |

## Entity

Collection user ↔ ward, with an active flag.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `collection_user_id` | FK | yes | Rewrite: `user_id` |
| `word_id` | FK | yes | |
| `status` | bool | yes | Soft toggle |
| unique | (user, word) | yes | |
| `pourashava_id` | FK | yes | |

## Business logic

- `scopeActive` / `isWordAssigned()` / `getUserAssignedWords()` in Laravel.
- Collection holding/bill lists filter `word_id IN active assignments`.
- Toggle `status` instead of deleting when you need history.
- Can share the [UserWiseWordAssign](USER_WISE_WORD_ASSIGN.md) table if assessment and collection use the same users — only keep two tables if scopes must differ.
