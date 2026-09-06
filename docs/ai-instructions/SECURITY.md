# e-Pourashava Security Specification

> **Status:** Implementation spec for `e-pourashava-api` and `e-pourashava-ui`
> **Source of truth analyzed:** `/home/egen/ai/github/epourashava-main` (Laravel monolith)
> **Related:** [api/RBAC_DESIGN.md](api/RBAC_DESIGN.md)

This document records how the existing Laravel system authenticates and authorizes users, then defines the security model the new API and UI must implement. Controllers, menus, and services in the rewrite follow this catalogue — not ad-hoc role names.

---

## 1. What the Laravel app actually does

### 1.1 Three separate login worlds

`config/auth.php` defines three session guards and three user tables:

| Guard | User table / model | App | Tenant |
|-------|--------------------|-----|--------|
| `web` | `users` / `App\Models\User` | Assessment (`{subdomain}.host/assessment`) | User.subdomain + URL subdomain |
| `collection` | `collection_users` / `CollectionUser` | Collection (`{subdomain}.host/collection`) | User.subdomain + URL subdomain |
| `admin` | `admins` / `Admin` | Central admin (`/admin`) | None — national registry |

There is no JWT for the main UI. Auth is cookie/session + CSRF. Sanctum is in `composer.json` but unused; Passport is not installed. Spatie Laravel Permission stores roles and permissions, keyed by `guard_name`. Laravel Policies and Gates are unused (`AuthServiceProvider` is empty).

The rewrite collapses this into **one JWT API** and **one Angular UI**. Assessment, collection, and platform-admin become permission modules on the same user, not separate logins. A user may hold roles that grant assessment permissions, collection permissions, or both.

### 1.2 Tenancy

- Assessment and collection URLs are `{subdomain}.{app.short_url}`.
- `CheckSubdomain` aborts 403 if the subdomain is not a row in `pourashavas`.
- `EnsureAssessmentTenantIsActive` / `EnsureCollectionTenantIsActive` refuse access when `assessment_app_status` / collection status is not `active`.
- Login for assessment (`LoginController::authLogin`) requires `Auth::attempt(..., 'subdomain' => $subdomain)`. Exception: a user with role `administrator` (`User::AUTHORITY`) may log in without matching subdomain and then operate inside that pourashava.
- Users are listed with `User::where('subdomain', $subdomain)`.
- Roles created in a pourashava are stored with `roles.subdomain` (`RoleModel::create`, `RolesController`).

**Do not copy the administrator cross-tenant login hole or email-hardcoded menus** (the Laravel sidebar grants delete-data to three hardcoded emails). Super admin in the rewrite is a role, not an email list.

### 1.3 Authorization style

Almost every controller method starts with:

```php
if (is_null($this->user) || !$this->user->can('Word View')) { ... }
```

The sidebar uses the same strings (`$usr->can('Customer Create')`). Permission names are human sentences, grouped by `permissions.group_name`. There is no `@middleware('permission:...')` on routes — checks are manual and easy to forget.

The rewrite uses `@PreAuthorize("hasAuthority('WORD:READ')")` on every endpoint and `*hasPermission` on every menu item. Same idea, enforced by the framework.

### 1.4 Extra data scopes (beyond tenant)

- **Ward assignment** (`user_wise_word_assigns`): a user may be limited to specific wards.
- **Bank assignment** (collection): a collector may be limited to specific banks.
- **Feature flags** on the pourashava (`hasFeature($subdomain, 'direct_final_tax')`) hide modules even if the user has the permission.

Laravel treats **empty assignment as all wards** (`HelperController::getWordList`). That is the opposite of least privilege. The rewrite keeps that only for `SUPER_ADMIN` and `POURASHAVA_ADMIN`. Operators and collectors with no assignment rows see **no** ward-scoped rows until someone assigns wards.

The new API must keep tenant isolation as the first filter, then apply ward/bank assignment when those modules land.

### 1.5 Roles in Laravel vs roles in the rewrite

Laravel seeds `superadmin`, `admin`, `editor`, `user` for the `web` guard, then lets each pourashava **create its own roles** (`RolesController` + `RoleModel` with `subdomain`). Collection uses `administrator` on the `collection` guard (also the cross-tenant login bypass). Central admin uses `superadmin` on the `admin` guard. Workflow also hardcodes a `pno` role in tax-approval blades; assessor/mayor steps are permission-gated, not role-gated.

The rewrite already decided (**RBAC D1**): roles are **global** and assignable to users of any pourashava. Pourashava staff do not invent a private role catalogue. Super admin (or anyone with `ROLE:CREATE`) creates a role once; every pourashava can assign it.

That is an intentional improvement. Permission *codes* stay shared. Role *definitions* stay shared. Isolation stays on `pourashava_id`.

---

## 2. Target model (API + UI)

Unchanged from [RBAC_DESIGN.md](api/RBAC_DESIGN.md):

| Question | Mechanism |
|----------|-----------|
| Who are you? | JWT (`Authorization: Bearer`) issued at `/auth/login` after password **and** tenant check |
| What may you do? | Permissions on the role; `@PreAuthorize` / `*hasPermission` |
| Whose data? | `users.pourashava_id` NOT NULL; Hibernate tenant filter; write-time `TenantGuard` |

Additional rules taken from the Laravel app:

1. Login tenant must match the user's pourashava (already implemented). Super admin logs in against the `demo` pourashava and bypasses the tenant filter — they do **not** impersonate another subdomain at login.
2. A pourashava may be **suspended** (`assessment_app_status` / collection status). When those columns exist, refuse tokens and API calls for that module with 403 `SERVICE_SUSPENDED`.
3. Ward/bank assignment is a second filter on holding, assessment, and collection queries — not a permission.
4. Feature flags hide UI and reject API calls for optional modules (`DIRECT_FINAL_TAX`, etc.).
5. No email-based privilege. No permission-less “hidden” screens.

---

## 3. Seeded roles (rewrite)

| Code | Maps from Laravel | Intent |
|------|-------------------|--------|
| `SUPER_ADMIN` | `admin` guard `superadmin` + web `administrator` bypass | Platform: pourashavas, system roles, all data |
| `POURASHAVA_ADMIN` | Typical pourashava `admin` / custom “এডমিন” role | Full municipality ops except `ROLE:*` and `POURASHAVA:CREATE/DELETE` |
| `OPERATOR` | Typical data-entry / editor | Create/update holdings, wards, assessments; no user/role admin; no delete where Laravel separated it |
| `VIEWER` | `user` / report-only | Read + reports |
| `ASSESSOR` | Approval UI (permission, not a seeded role) | Add when tax/mutation approval lands |
| `PNO` | Hardcoded `pno` role in tax-approval blades | Add when tax/mutation approval lands |
| `MAYOR` | `Mutation Approve By Mayor` | Add when mutation approval lands |
| `COLLECTOR` | Collection operators | Add when collection lands |

Pourashava admins assign these (and any later global roles) via `USER:ASSIGN_ROLE`. They cannot grant `SUPER_ADMIN` unless they already hold every permission on that role (subset rule, already implemented).

---

## 4. Permission catalogue

Laravel names stay in the “Legacy” column so migration and training stay traceable. New code **only** uses `MODULE:ACTION`.

### 4.1 Already on the API (implement now / already present)

| Module | Actions | Legacy (web guard) | UI menu |
|--------|---------|--------------------|---------|
| `DIVISION` | READ, CREATE, UPDATE, DELETE | Central admin pourashava setup (division pickers) | বিভাগ |
| `DISTRICT` | READ, CREATE, UPDATE, DELETE | Central admin | জেলা |
| `POURASHAVA` | READ, CREATE, UPDATE, DELETE | `pourashava view/create/update/delete` (admin guard) | পৌরসভা |
| `POUROSHOVA_INFO` | READ, CREATE, UPDATE, DELETE | `Pouroshava View/Edit`, `Pourashava info Setup`, `Sign Delete` | পৌরসভা তথ্য |
| `WORD` | READ, CREATE, UPDATE, DELETE | `Word View/Create/Edit/Delete` | ওয়ার্ড |
| `PARA` | READ, CREATE, UPDATE, DELETE | `Para/Block/Road View/Create/Edit/Delete` | পাড়া |
| `USER` | READ, CREATE, UPDATE, DELETE, ASSIGN_ROLE | `User View/Create/Edit/Delete` | ব্যবহারকারী |
| `ROLE` | READ, CREATE, UPDATE, DELETE | `Role View/Create/Edit/Delete` | রোল |

`POUROSHOVA_INFO:UPDATE` covers logo/sign upload. Add `POUROSHOVA_INFO:DELETE_SIGN` only if sign-delete must be split from general edit (Laravel did split it as `Sign Delete`).

### 4.2 Assessment — add when those screens are ported

Grouped the way the Laravel sidebar groups them.

| Module | Actions | Legacy | UI (Bengali) |
|--------|---------|--------|--------------|
| `TAX_YEAR` | READ, CREATE, UPDATE, DELETE | Tax Year View/Create/Edit/Delete | করবর্ষ সেটআপ |
| `TAX_RATE` | READ, UPDATE, SETUP | Tax Rate View/Edit, Tax Rate Setup | ট্যাক্স সেটআপ |
| `WORD_ASSIGN` | READ, CREATE, UPDATE, DELETE | Word Assign View/Create/Edit/Delete | ওয়ার্ড বরাদ্দ |
| `SMS_SETUP` | READ, UPDATE | `sms setup` | এসএমএস সেটআপ |
| `HOLDING` | READ, CREATE, UPDATE, DELETE, CHANGE_STATUS, CREATE_NEW, UPDATE_ADDRESS | Customer View/Create/Edit/Delete, Customer Status Change, New Customer Info Create, Customer Address Edit | হোল্ডিং এর তথ্য |
| `ASSESSMENT` | READ, CREATE, UPDATE, DELETE | Kor Nirdharon View/Create/Edit/Delete | কর নির্ধারণ |
| `TAX_APPROVAL` | READ, UPDATE | approval view, approval status update | কর অনুমোদন / অননুমোদিত |
| `FINAL_ASSESSMENT` | READ, CREATE, UPDATE, DELETE, DIRECT, NEW_YEAR | Churanto Kor Nirdharon *, Direct Churanto, New Final Assessment Create | চূড়ান্ত কর |
| `EXTENSION` | READ, UPDATE | Bordhito View/Edit | বর্ধিত |
| `HOLDING_CHANGE` | READ, UPDATE | Holding Change View/Edit | পরিবর্তন তথ্য প্রবেশ |
| `MUTATION` | READ, CREATE, UPDATE, DELETE, SUBMIT, APPROVE_ASSESSOR, APPROVE_PNO, APPROVE_MAYOR, REJECT | Mutation * | মালিকানা পরিবর্তন |
| `MUTATION_SEPARATION` | READ, CREATE, UPDATE, DELETE | Mutation Separation * | হোল্ডিং পৃথকীকরণ |
| `MUTATION_AMALGAMATION` | READ, CREATE, UPDATE, DELETE | Mutation Amalgamation * | হোল্ডিং একত্রিতকরণ |
| `MUTATION_APPORTIONMENT` | READ, CREATE, UPDATE, DELETE | Mutation Apportionment * | |
| `MUTATION_CONVERSION` | READ, CREATE, UPDATE, DELETE | Mutation Conversion * | |
| `MUTATION_OCCUPANCY` | READ, CREATE, UPDATE, DELETE | Mutation Change in Occupancy * | |
| `SMS` | SEND, READ_REPORT, SPECIAL | SMS Send | এস এম এস |
| `REPORT` | READ, CHO, SO, UO, UO_HTML, NOTICE, CHURANTO_UO, CHURANTO_PREVIEW, CHURANTO_CHO | Report View, Cho/So/UO/UO HTML/Notice/Churanto * Report | রিপোর্ট |
| `SPECIAL_EDIT` | UPDATE | Final Assessment and Aday Entry Special Edit | চূড়ান্ত কর ও বকেয়া এডিট |
| `ENTRY_REPORT` | READ | Entry Report Preview | এন্ট্রি রিপোর্ট |
| `ACTIVITY_LOG` | READ | (gated by User View / Role View) | লগ রিপোর্ট |
| `CACHE` | CLEAR | (gated by User View / Role View) | ক্যাশ পরিষ্কার |

### 4.3 Collection — add when collection is ported

| Module | Actions | Legacy (`collection` guard) |
|--------|---------|-----------------------------|
| `COLLECTION_DASHBOARD` | READ | collection.dashboard_view / collection Dashboard View |
| `COLLECTION_USER` | READ, CREATE, UPDATE, DELETE | collection.user_* |
| `COLLECTION_ROLE` | READ, CREATE, UPDATE, DELETE | collection.role_* (rewrite: reuse `ROLE:*` if same role table) |
| `COLLECTION_INFO` | READ, UPDATE, DELETE_SIGN | collection.basic_info, Basic Sign Delete |
| `TAX_COLLECTION` | READ, CREATE, UPDATE, DELETE | Tax Collection * |
| `TAX_BILL` | READ, CREATE, UPDATE, DELETE | Tax Bill * |
| `BILL_POSTING` | READ, CREATE, UPDATE, DELETE | Bill Posting * |
| `BANK` | READ, CREATE, UPDATE, DELETE | Bank Information * |
| `BILL_TYPE` | READ, CREATE, UPDATE, DELETE | Bill Type * |
| `BANK_ASSIGN` | READ, CREATE, UPDATE, DELETE | User Wise Bank Assign * |
| `COLLECTION_WORD_ASSIGN` | READ, CREATE, UPDATE, DELETE | Collection User Wise Word Assign * |
| `COLLECTION_REPORT` | BILL, BLOCK_BILL, HOLDING, BLOCK, WORD, REGISTER, DAILY, NOT_COLLECTED | Reports + Not Collected Report |
| `COLLECTION_ALL_INFO` | READ | Collection All Information |
| `FY_PROCESS` | READ, UPDATE | Hardcoded emails on “বার্ষিক প্রক্রিয়া” — replace with this permission |

### 4.4 Platform admin (Laravel `admin` guard)

| Module | Actions | Legacy |
|--------|---------|--------|
| `ADMIN_DASHBOARD` | READ | dashboard view |
| `PLATFORM_ADMIN` | READ, CREATE, UPDATE, DELETE | admin view/create/update/delete |
| `USER_TRANSFER` | READ, CREATE | user transfer view/create |
| `PLATFORM_REPORT` | PROPOSED_TAX, FINAL_TAX | proposed tax report, final tax report |
| `FEATURE` | READ, UPDATE | feature read/update (sidebar; often DB-only) |
| `VENDOR` | READ, CREATE, UPDATE, DELETE | vendor index/create/update/delete |
| `NOTICE` | READ, CREATE, UPDATE, DELETE | Admin notices |
| `PERMISSION` | READ, CREATE, UPDATE, DELETE | `permission_create/update/delete` — rewrite: `ROLE:*` owns this; no public `/permissions` |

**Do not port district-name permissions** (Laravel seeds `ঢাকা`, `গাজীপুর`, … as permission names and barely enforces them). They do not scale. Platform users limited to some districts get an `admin_district_scopes` table (user_id, district_id), checked in pourashava list queries — same pattern as ward assignment.

Laravel permission **names are inconsistent** (Title Case `Tax Collection View`, snake_case `collection.dashboard_view`, lowercase `dashboard view`). Sidebar strings do not always match seeders, so menus can stay hidden. The rewrite has one spelling: `MODULE:ACTION` in `PermissionCodes`, seed data, `@PreAuthorize`, and `*hasPermission`.

---

## 5. API implementation rules

1. **Deny by default.** `SecurityConfig` already requires authentication except `/auth/login`, `/auth/refresh`, `/health`, swagger, H2. Laravel left helper AJAX, UO/CHA report APIs, SMS send, trade-license, and most of `routes/api.php` unauthenticated — **do not port that**. A public bill-lookup page is allowed only as an explicit, documented exception with no extra PII.
2. **Every mutating or listing endpoint** has `@PreAuthorize("hasAuthority('MODULE:ACTION')")`. No “authenticated is enough” for business data. No controller-only checks without the annotation.
3. **Permission strings** live in `PermissionCodes`. Seed rows must match exactly.
4. **Tenant filter** on every pourashava-owned table. Super admin skips the filter.
5. **Ownership on writes** via `TenantGuard.assertSameTenant`.
6. **Role assignment subset rule** stays (already in `RoleService.assertCanAssign`).
7. **When adding a Laravel screen**, add the module’s permission rows in a Flyway seed, grant them to `SUPER_ADMIN` and the appropriate seeded roles, then annotate the new controller. Do not invent a second naming scheme.
8. **Suspended pourashava:** if `assessment_app_status != active`, assessment controllers return 403. Same for collection.
9. **Ward assignment:** if the user has rows in `user_wise_word_assigns`, holding/assessment queries must `AND word_id IN (...)`. `SUPER_ADMIN` and `POURASHAVA_ADMIN` with no rows see all wards in tenant. Operator/collector with no rows see none.
10. **Audit:** prefer a real activity log (Laravel has `LogsModelActivity` / activity log report) over email-gated delete screens.
11. **No maintenance endpoints** (`cache:clear`, queue, artisan wrappers) on the public API. Laravel put some of these inside the assessment auth group.

---

## 6. UI implementation rules

1. Register `httpTokenInterceptor` and `authGuard` (already done).
2. Every sidebar item and button that Laravel gated with `$usr->can(...)` uses `*hasPermission="'MODULE:ACTION'"`.
3. Hide is not security — the API must reject the same permission.
4. Login sends `tenantId` (subdomain). Infer from hostname when not localhost.
5. After login, persist `roles` and `permissions`. Super admin is `roles.includes('SUPER_ADMIN')`, not a hardcoded email.
6. 401 → logout and `/login`. 403 → stay on page, show “অনুমতি নেই”.
7. Feature-flagged items (`DIRECT_FINAL_TAX`) require **both** the flag on the current pourashava **and** the permission.
8. Do not ship Laravel email allowlists (“তথ্য মুছে ফেলা”, collection “বার্ষিক প্রক্রিয়া” for `anwarhosain@collection.com` / `ass.nazmul@gmail.com`). Dangerous tools get a permission (`HOLDING:PURGE`, `FY_PROCESS:*`) on `SUPER_ADMIN` only.

Menu mapping for what exists today:

| Route | Permission |
|-------|------------|
| `/divisions` | `DIVISION:READ` |
| `/districts` | `DISTRICT:READ` |
| `/pourashavas` | `POURASHAVA:READ` |
| `/pouroshova-infos` | `POUROSHOVA_INFO:READ` |
| `/words` | `WORD:READ` |
| `/paras` | `PARA:READ` |
| `/users` | `USER:READ` |
| `/roles` | `ROLE:READ` |
| Create buttons | corresponding `:CREATE` |
| Edit / delete | `:UPDATE` / `:DELETE` |

---

## 7. Default grants (seeded roles)

Apply when seeding new permission rows. Existing API seed already follows this pattern.

| Role | Gets |
|------|------|
| `SUPER_ADMIN` | All codes |
| `POURASHAVA_ADMIN` | All assessment + user + pourashava-info + word/para + reports; **not** `ROLE:*`, **not** `POURASHAVA:CREATE/DELETE`, **not** `DIVISION/DISTRICT` writes |
| `OPERATOR` | CREATE/UPDATE/READ on holdings, assessment, word, para, mutation drafts; READ on setup and reports; no DELETE, no USER/ROLE, no approval/mayor steps |
| `VIEWER` | Every `:READ` and report READ action |

Approval steps (`TAX_APPROVAL:UPDATE`, `MUTATION:APPROVE_*`) stay off OPERATOR and VIEWER unless a pourashava-specific global role is created later (e.g. `ASSESSOR`, `MAYOR`).

Suggested extra seeded roles when mutation/approval land (still global):

| Code | Permissions |
|------|-------------|
| `ASSESSOR` | Assessment + `TAX_APPROVAL:*` + `MUTATION:APPROVE_ASSESSOR` |
| `PNO` | `TAX_APPROVAL:*` + `MUTATION:APPROVE_PNO` (cannot re-approve own step) |
| `MAYOR` | READ + `MUTATION:APPROVE_MAYOR` + `TAX_APPROVAL:READ` |
| `COLLECTOR` | Collection create/update + own ward/bank scope |

---

## 8. Implementation phases

| Phase | Work | State |
|-------|------|-------|
| A | JWT, tenant FK, global RBAC, current entities, UI guard/interceptor | **Done** (see RBAC_DESIGN) |
| B | Align remaining UI buttons with `*hasPermission`; `/auth/me` to refresh permissions | Next |
| C | Port assessment setup (tax year/rate, word assign, SMS setup) with §4.2 codes | When those screens are built |
| D | Holdings, kor nirdharon, approval, churanto | |
| E | Mutation + approval chain | |
| F | Reports + SMS + activity log | |
| G | Collection modules + bank/ward assign | |
| H | Pourashava suspend flags + feature flags | |
| I | Drop any leftover Laravel habits (email allowlists, permitAll leftovers) | |

Each phase: Flyway permission seed → grant to roles → `@PreAuthorize` → UI `*hasPermission` → tests for 401/403/cross-tenant.

---

## 9. Gaps in the Laravel app (do not repeat)

- No route-level permission middleware; no Policies/Gates. Several routes have no `can()` at all (search “অনুসন্ধান করুন”, helper AJAX).
- `PermissionMakeController` (`/permissions`) has **no auth** — anyone can create/delete permissions.
- Most of `routes/api.php` is unauthenticated (wards, tax years, UO/CHA reports, `send-posting-sms`, trade license, vendors, notices).
- Collection bill lookup / print PDF (`PaymentPageController`) has no auth.
- Assessment helper routes (`/tax-years`, `/get-wards`, UO report list) sit **outside** the `auth` group.
- Superadmin / `administrator` (web and collection) can log into any subdomain.
- Empty ward assignment = all wards (least-privilege inversion).
- Roles listed with `id > 1` magic instead of `is_system`.
- User list had contradictory queries (filter by role, then overwrite with `where('subdomain')`).
- Privileged delete UI and annual-process menu are hardcoded emails (`InformationDeleteController`, collection sidebar).
- Three permission naming styles; sidebar strings often do not match seeders.
- Collection permission seeder sets `$rolesuperadmin = ''` (broken assign).
- Unique constraint on `(name, guard_name)` is commented out — duplicate permission names possible.
- `roles.subdomain` used in code; uniqueness migration commented out — schema/code drift.
- Admin password-reset provider points at `users`, not `admins`.
- `ApiHost` compares request host to the URL string `https://api.e-pourashava.com`.
- `DatabaseSeeder` has all seeders commented out — production RBAC is not reproducible from the repo.
- District-name admin permissions are seeded with little or no controller enforcement.

The rewrite treats these as bugs, not requirements.

**Rejected mappings** (an earlier Laravel survey suggested them; do not implement):

| Suggestion | Why rejected |
|------------|----------------|
| Three JWT/OAuth realms (`PLATFORM_ADMIN`, `TENANT_ASSESSMENT`, `TENANT_COLLECTION`) | One user table, one token, modules via permissions |
| Tenant-scoped roles (`roles.subdomain`) | Roles are global (RBAC D1); isolation is `pourashava_id` |

---

## 10. Mapping cheat-sheet

When a Laravel developer says a permission name, use this:

| They say | We implement |
|----------|----------------|
| `$user->can('Word View')` | `hasAuthority('WORD:READ')` / `*hasPermission="'WORD:READ'"` |
| `$user->can('Customer Create')` | `HOLDING:CREATE` |
| `$user->hasRole('superadmin')` | `principal.isSuperAdmin()` / `roles.includes('SUPER_ADMIN')` |
| `where('subdomain', $subdomain)` | Hibernate `tenantFilter` + `pourashava_id` |
| `Auth::attempt(..., subdomain)` | Login `tenantId` must equal user’s pourashava subdomain |
| New role per pourashava | Create **one** global role; assign it in every pourashava |
| District-name permission | `admin_district_scopes` row, not a permission code |

---

*Source: epourashava-main `config/auth.php`, Spatie config, `Kernel`, `CheckSubdomain` / tenant-active middleware, `User` / `CollectionUser` / `Admin` / `Pourashava`, `RoleModel`, login controllers, `RolesController`, `routes/{web,assessment,collection,api}.php`, three sidebars, seeders (`RolePermissionSeeder`, `AdminSitePermissionSeeder`, `MutationSeeder`, `CollectionAllFormSeeder`).*
*Last updated: 2026-09-06*
