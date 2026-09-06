# UserWiseWordAssign

| | |
| Status | Planned |
| Table | `user_wise_word_assigns` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/UserWiseWordAssign.php` |
| Permissions | `WORD_ASSIGN:READ`, `CREATE`, `UPDATE`, `DELETE` |
| UI | ওয়ার্ড বরাদ্দ |

## Entity

Assessment user ↔ ward assignment. Second filter after tenant isolation.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `user_id` | FK | yes | [User](USER.md) |
| `word_id` | FK | yes | [Word](WORD.md) |
| `created_by` | user FK | no | |
| `pourashava_id` | FK | yes | |
| unique | (`user_id`, `word_id`) | yes | |

### Relations

- Belongs to User, Word

## Business logic

- Holding, assessment, mutation, and search queries for non-admin users: `word_id IN (assigned)`.
- `SUPER_ADMIN` and `POURASHAVA_ADMIN` with **no** rows see all wards in tenant.
- `OPERATOR` / `ASSESSOR` / `VIEWER` with no rows see **no** ward-scoped rows (rewrite; Laravel treated empty as all wards).
- Word and user must belong to the same pourashava.
- This is **not** a permission. A user can have `HOLDING:READ` and still see zero rows if unassigned.
