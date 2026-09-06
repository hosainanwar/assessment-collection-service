# Holding change (workflow)

| | |
| Status | Planned — **no dedicated table** |
| Tables touched | [CustomerInfo](CUSTOMER_INFO.md), [HoldingInfo](HOLDING_INFO.md), [KornNirdharon](KORN_NIRDHARON.md) |
| Laravel | `HoldingChangeController` |
| Permissions | `HOLDING_CHANGE:READ`, `HOLDING_CHANGE:UPDATE` |
| UI | পরিবর্তন তথ্য প্রবেশ |

## Entity

Not a row type. A workflow that rewrites the holding’s structure and marks it `CHANGE`.

## Business logic

- Preconditions: holding exists, tenant + ward scope, `HOLDING_CHANGE:UPDATE`.
- Set `customer.holding_status_type = CHANGE`.
- Update tax flags, condition, usage as submitted.
- Clone floors: existing `NORMAL` HoldingInfo → `CHANGE`; insert new floor rows as `CHANGE` at `extended=0`.
- Recalculate [KornNirdharon](KORN_NIRDHARON.md) for the new structure.
- `CHANGE` customers also get an [ExtendedCollectionInfo](EXTENDED_COLLECTION_INFO.md) row at `extended=0` when final assessment is written.
- Do not invent a `holding_changes` table unless audit needs a header; [ActivityLog](ACTIVITY_LOG.md) + old/new HoldingInfo rows are enough.
