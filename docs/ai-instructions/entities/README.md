# Entity catalogue

One file per domain entity. Each file has **Entity** (fields, relations) then **Business logic** (rules the API and UI must implement).

Permissions: [SECURITY.md](../SECURITY.md). Auth/tenant: [RBAC_DESIGN.md](../api/RBAC_DESIGN.md).

**Status:** `Implemented` = live in `e-pourashava-api`. `Planned` = Laravel behaviour to port; do not invent a second model.

Laravel `Admin` and `CollectionUser` are **not** separate entities in the rewrite — they are `User` rows with different roles.

## Implemented

| Entity | File |
|--------|------|
| Auditable (base) | [AUDITABLE.md](AUDITABLE.md) |
| Division | [DIVISION.md](DIVISION.md) |
| District | [DISTRICT.md](DISTRICT.md) |
| Pourashava | [POURASHAVA.md](POURASHAVA.md) |
| PouroshovaInfo | [POUROSHOVA_INFO.md](POUROSHOVA_INFO.md) |
| Word | [WORD.md](WORD.md) |
| Para | [PARA.md](PARA.md) |
| User | [USER.md](USER.md) |
| Role | [ROLE.md](ROLE.md) |
| Permission | [PERMISSION.md](PERMISSION.md) |

## Planned — setup & lookups

| Entity | File |
|--------|------|
| TaxYear | [TAX_YEAR.md](TAX_YEAR.md) |
| TaxSetup | [TAX_SETUP.md](TAX_SETUP.md) |
| HoldingType | [HOLDING_TYPE.md](HOLDING_TYPE.md) |
| OwnerType | [OWNER_TYPE.md](OWNER_TYPE.md) |
| Condition | [CONDITION.md](CONDITION.md) |
| Usage | [USAGE.md](USAGE.md) |
| HoldingDetails | [HOLDING_DETAILS.md](HOLDING_DETAILS.md) |
| Kisti | [KISTI.md](KISTI.md) |
| Month | [MONTH.md](MONTH.md) |
| UserWiseWordAssign | [USER_WISE_WORD_ASSIGN.md](USER_WISE_WORD_ASSIGN.md) |

## Planned — holding / assessment

| Entity | File |
|--------|------|
| CustomerInfo (holding) | [CUSTOMER_INFO.md](CUSTOMER_INFO.md) |
| HoldingInfo | [HOLDING_INFO.md](HOLDING_INFO.md) |
| HoldingNumber | [HOLDING_NUMBER.md](HOLDING_NUMBER.md) |
| HoldingImageInfo | [HOLDING_IMAGE_INFO.md](HOLDING_IMAGE_INFO.md) |
| KornNirdharon | [KORN_NIRDHARON.md](KORN_NIRDHARON.md) |
| SharoknoNirdharon | [SHAROKNO_NIRDHARON.md](SHAROKNO_NIRDHARON.md) |
| ApprovalHistory | [APPROVAL_HISTORY.md](APPROVAL_HISTORY.md) |
| SoInformation | [SO_INFORMATION.md](SO_INFORMATION.md) |
| IssueDate | [ISSUE_DATE.md](ISSUE_DATE.md) |
| FinalAssesment | [FINAL_ASSESSMENT.md](FINAL_ASSESSMENT.md) |
| Holding change (workflow) | [HOLDING_CHANGE.md](HOLDING_CHANGE.md) |
| Bordhito / extended (workflow) | [BORDHITO.md](BORDHITO.md) |

## Planned — mutation

| Entity | File |
|--------|------|
| MutationMaster | [MUTATION_MASTER.md](MUTATION_MASTER.md) |
| MutationDetail | [MUTATION_DETAIL.md](MUTATION_DETAIL.md) |
| MutationType | [MUTATION_TYPE.md](MUTATION_TYPE.md) |
| MutationFileInfo | [MUTATION_FILE_INFO.md](MUTATION_FILE_INFO.md) |

## Planned — collection

| Entity | File |
|--------|------|
| TaxCollection | [TAX_COLLECTION.md](TAX_COLLECTION.md) |
| TaxBill | [TAX_BILL.md](TAX_BILL.md) |
| BillType | [BILL_TYPE.md](BILL_TYPE.md) |
| BillHistory | [BILL_HISTORY.md](BILL_HISTORY.md) |
| Payment | [PAYMENT.md](PAYMENT.md) |
| DueReduction | [DUE_REDUCTION.md](DUE_REDUCTION.md) |
| ExtendedCollectionInfo | [EXTENDED_COLLECTION_INFO.md](EXTENDED_COLLECTION_INFO.md) |
| FyCollectionInfo | [FY_COLLECTION_INFO.md](FY_COLLECTION_INFO.md) |
| PreviousFinancialYear | [PREVIOUS_FINANCIAL_YEAR.md](PREVIOUS_FINANCIAL_YEAR.md) |
| BankInformation | [BANK_INFORMATION.md](BANK_INFORMATION.md) |
| UserWiseBankAssign | [USER_WISE_BANK_ASSIGN.md](USER_WISE_BANK_ASSIGN.md) |
| CollectionUserWiseWordAssign | [COLLECTION_USER_WISE_WORD_ASSIGN.md](COLLECTION_USER_WISE_WORD_ASSIGN.md) |
| AdayContactInfo | [ADAY_CONTACT_INFO.md](ADAY_CONTACT_INFO.md) |

## Planned — SMS, notices, audit

| Entity | File |
|--------|------|
| SmsSetup | [SMS_SETUP.md](SMS_SETUP.md) |
| SMSTemplate | [SMS_TEMPLATE.md](SMS_TEMPLATE.md) |
| SMSConfiguration | [SMS_CONFIGURATION.md](SMS_CONFIGURATION.md) |
| SmsReport | [SMS_REPORT.md](SMS_REPORT.md) |
| Notice | [NOTICE.md](NOTICE.md) |
| TaxDueAmountNotice | [TAX_DUE_AMOUNT_NOTICE.md](TAX_DUE_AMOUNT_NOTICE.md) |
| SpecialPersonsNumber | [SPECIAL_PERSONS_NUMBER.md](SPECIAL_PERSONS_NUMBER.md) |
| ActivityLog | [ACTIVITY_LOG.md](ACTIVITY_LOG.md) |
| CollectionLog | [COLLECTION_LOG.md](COLLECTION_LOG.md) |
| UserTransfer | [USER_TRANSFER.md](USER_TRANSFER.md) |

## Cross-entity flow

```
CustomerInfo + HoldingInfo
  → KornNirdharon → SharoknoNirdharon → [approval]
  → FinalAssesment → TaxCollection → TaxBill → [posting] → BillHistory

Bordhito: increment extended → clone holdings/korn/final → ExtendedCollectionInfo
Mutation: MutationMaster PENDING → SUBMIT → ASSESSOR → PNO → MAYOR → update CustomerInfo
```

Financial year is **July–June**. Installments: Q1 Jul–Sep, Q2 Oct–Dec, Q3 Jan–Mar, Q4 Apr–Jun.
