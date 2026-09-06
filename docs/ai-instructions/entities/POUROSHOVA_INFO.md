# PouroshovaInfo

| | |
| Status | Implemented |
| Table | `pouroshova_infos` |
| Tenant | yes — `pourashava_id` + Hibernate filter |
| Java | `entity/PouroshovaInfo.java` |
| Laravel | `app/Models/PouroshovaInfo.php` |
| Permissions | `POUROSHOVA_INFO:READ`, `CREATE`, `UPDATE`, `DELETE` |
| UI | `/pouroshova-infos` |

## Entity

Per-pourashava letterhead: names, labels, logo, and signatures used on every PDF/bill.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `id` | long | yes | |
| `pouroshova_name` | string | yes | Display name |
| `meyor_name` | string | yes | Mayor / administrator name |
| `kor_nirdharok_name` | string | no | Assessor |
| `ps_name` | string | yes | |
| `ds_name` | string | yes | |
| `signature_name` | string | yes | |
| `mayor_sign` | string | no | File path |
| `assessor_sign` | string | no | File path |
| `tax_collector_type` | string | no | Which collector sign to print |
| `tax_collector_sign` | string | no | File path |
| `created_by` | string | no | Legacy string; prefer user id later |
| `subdomain` | string | yes | Copied from pourashava |
| `pourashava_id` | FK | yes | Tenant |
| `mayor_label_type` | string | yes | Default `mayor` (vs administrator) |
| `mayor_label_type_collection` | string | yes | Label on collection PDFs |
| `logo` | string | no | File path |
| `mobile` | string | no | |
| `nirdharon_mobile` | string | no | |

### Relations

- Belongs to [Pourashava](POURASHAVA.md)
- Planned: [FyCollectionInfo](FY_COLLECTION_INFO.md) references this id

## Business logic

- **One row per pourashava.** Create rejects if a row already exists for that subdomain.
- Tenant is resolved via `TenantGuard.resolvePourashava`. Non–super-admin writes are forced to their own pourashava.
- Update/delete call `TenantGuard.assertSameTenant`.
- Required on create: name, mayor, PS, DS, signature name, subdomain (`PouroshovaInfoValidatorService`).
- Reports and bills read this row by tenant. Missing info should fail the PDF with a clear error, not print blanks.
- Laravel split `Sign Delete` from general edit. Rewrite: `POUROSHOVA_INFO:UPDATE` covers logo/sign upload. Add `POUROSHOVA_INFO:DELETE_SIGN` only if sign-delete must be a separate permission.
- `mayor_label_type` switches “মেয়র” vs “প্রশাসক” on assessment prints; `mayor_label_type_collection` does the same on collection prints.
