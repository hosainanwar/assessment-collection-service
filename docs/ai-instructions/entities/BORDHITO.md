# Bordhito / extended (workflow)

| | |
| Status | Planned — **no dedicated header table** |
| Tables touched | CustomerInfo, HoldingInfo, KornNirdharon, FinalAssesment, [ExtendedCollectionInfo](EXTENDED_COLLECTION_INFO.md) |
| Laravel | `ExtendedController` |
| Permissions | `EXTENSION:READ`, `EXTENSION:UPDATE` |
| UI | বর্ধিত |

## Entity

Year-to-year / mid-year extension of a holding (বর্ধিত). Version key is `extended` (integer), not a new customer.

## Business logic

- `lastExtendedNumber()` = max FinalAssesment.extended + 1.
- Set `holding_status_type = EXTENDED`.
- Clone HoldingInfo and KornNirdharon at the new `extended`.
- Create FinalAssesment at that `extended`, then ExtendedCollectionInfo with `is_extended_apply=NO`.
- Collection applies the extension later (`is_extended_apply=YES`, increment `TaxCollection.extended_amount`).
- Block final-assessment edit while an unapplied extension exists.
- Feature-flag nothing here — this is core. Direct Churanto is a different flag.
