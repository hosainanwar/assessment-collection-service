# BankInformation

| | |
| Status | Planned |
| Table | `bank_information` |
| Tenant | yes — `pourashava_id` |
| Laravel | `app/Models/BankInformation.php` |
| Permissions | `BANK:READ`, `CREATE`, `UPDATE`, `DELETE` |

## Entity

Bank or mobile-wallet account that can receive tax bills.

### Fields

| Column | Type | Required | Notes |
|--------|------|----------|-------|
| `name` | string | yes | |
| `branch_name` | string | yes | |
| `biller_id` | string | no | |
| `account_number` | string | yes | |
| `routing_number` | string | no | |
| `payment_status` | string | yes | `A` Bank, `B` Bkash, `N` Nagad, `I` Internet |
| `vendor_id`, `extra` | string | no | |
| `is_mobile_bank` | bool | no | |
| `is_active` | bool | yes | Inactive hidden on bill create |
| `pourashava_id` | FK | yes | |

### Relations

- Has many TaxBill
- Assigned via [UserWiseBankAssign](USER_WISE_BANK_ASSIGN.md)

## Business logic

- Required on every TaxBill.
- Bill create lists only `is_active=true` banks, further filtered by collector assignment.
- Do not delete a bank that has posted bills; deactivate instead.
