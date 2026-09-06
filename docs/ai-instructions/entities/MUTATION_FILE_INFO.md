# MutationFileInfo

| | |
| Status | Planned |
| Table | `mutation_file_infos` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/MutationFileInfo.php` |
| Permissions | `MUTATION:CREATE`, `MUTATION:UPDATE` |

## Entity

Deed / supporting file for a mutation case.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `mutation_master_id` | FK | yes | |
| `customer_info_id` | FK | yes | |
| `file_name` | string | yes | Storage path |
| `added_by` | user FK | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- Attached on create/edit. Server-generated storage keys only.
- Delete file from disk when the row is removed (and only while status is `PENDING` / `SUBMIT`, not after mayor approval).
