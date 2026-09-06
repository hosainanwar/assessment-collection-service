# Para

| | |
| Status | Implemented |
| Table | `paras` |
| Tenant | yes — `pourashava_id` + Hibernate filter |
| Java | `entity/Para.java` |
| Laravel | `app/Models/Para.php` |
| Permissions | `PARA:READ`, `PARA:CREATE`, `PARA:UPDATE`, `PARA:DELETE` |
| UI | `/paras` |

## Entity

Para / block / road (পাড়া) under a [Word](WORD.md).

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `pbr_name` | string | yes | Para/block/road name |
| `word_id` | FK | yes | Parent ward |
| `subdomain` | string | yes | Copied from pourashava |
| `pourashava_id` | FK | yes | Tenant |
| `created_by` | string | no | Legacy |

### Relations

- Belongs to Word, Pourashava
- Planned: CustomerInfo, MutationMaster, TaxBill

## Business logic

- `pbr_name`, `word_id`, and subdomain are required.
- Parent word must exist. Prefer also asserting the word belongs to the same pourashava (add if missing).
- Tenant resolved on create; update/delete assert same tenant.
- Lists: all, by word, by subdomain, by word+subdomain.
- Holding create will require a para. Delete must be blocked once holdings reference it (Laravel did this).
- UI dropdowns: load paras for the selected word only (`PARA:READ`).
