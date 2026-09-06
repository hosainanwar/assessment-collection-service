# HoldingImageInfo

| | |
| Status | Planned |
| Table | `holding_image_infos` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/HoldingImageInfo.php` |
| Permissions | `HOLDING:UPDATE`, `EXTENSION:UPDATE` |

## Entity

Photo attached to a holding or a floor row.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `customer_info_id` | FK | yes | |
| `holding_info_id` | FK | no | Floor-level image |
| `image` | string | yes | Storage path |
| `image_name` | string | no | |
| `file_source_type` | string | no | |
| `pourashava_id` | FK | yes | |

## Business logic

- Uploaded on holding edit / bordhito. Store via the same file service as signs/logos.
- Delete image files from storage when the row is removed.
- Do not accept arbitrary paths from the client — only server-generated keys.
