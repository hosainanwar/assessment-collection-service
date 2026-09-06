# e-pourashava-api — Authentication & RBAC Design

> **Status:** Implemented
> **Scope:** `e-pourashava-api` (Spring Boot 4.1 / Java 17), with follow-on changes in `e-pourashava-ui`

---

## 1. Objective

Every request to the API must answer two independent questions before it touches data:

| Question | Name | Answered by | Enforced at |
|----------|------|-------------|-------------|
| *Who are you?* | Authentication | JWT issued at login | `JwtAuthenticationFilter` |
| *What action may you perform?* | Authorization | Roles → permissions | `@PreAuthorize` on service/controller methods |
| *Whose data may you touch?* | Tenant isolation | The user's pourashava | Hibernate filter + explicit ownership checks |

The second and third are separate concerns and must not be collapsed into one. A user holding `WORD:UPDATE` has passed authorization for *any* ward in the system; it is tenant isolation, not RBAC, that stops them editing Gazipur's wards. A system that implements only roles gives every pourashava admin write access to every other pourashava.

**Requirement:** every user belongs to exactly one pourashava. This is enforced as a `NOT NULL` foreign key, not a convention.

---

## 2. Current State

### 2.1 What exists

| Component | File | State |
|-----------|------|-------|
| JWT provider | `security/JwtTokenProvider.java` | Works; issues HS256 tokens with a `tenant_id` claim |
| JWT filter | `security/JwtAuthenticationFilter.java` | Works; populates `SecurityContext` and `TenantContext` |
| User details | `security/CustomUserDetailsService.java` | Maps `User.role` string → single `ROLE_x` authority |
| Tenant holder | `security/TenantContext.java` | `ThreadLocal<String>` holding a subdomain |
| Password encoder | `config/SecurityConfig.java` | `BCryptPasswordEncoder` bean — correct |
| Method security | `config/SecurityConfig.java` | `@EnableMethodSecurity` present but unused |

### 2.2 Gaps

1. **No endpoint is protected.** `SecurityConfig` ends with `.anyRequest().permitAll()`. The JWT filter runs and populates a security context that nothing subsequently checks. Every endpoint is effectively public.
2. **RBAC is a single string.** `User.role` is a free-text column with no `roles`/`permissions` tables and no `@PreAuthorize` anywhere in `controller/`.
3. **No relational link from user to pourashava.** `User.subdomain` is an unconstrained string; nothing guarantees it matches a row in `pourashavas`, and nothing guarantees it is set at all.
4. **No data-layer tenant enforcement.** Queries do not filter by tenant; `TenantContext` is populated but never read by the persistence layer.

### 2.3 Defects to fix as part of this work

These are live issues that the design depends on:

**(a) Login does not verify tenant membership.** `AuthService.login` takes `tenantId` from the request body, places it in `TenantContext`, and `JwtTokenProvider.generateToken` stamps it into the JWT — without ever checking the user belongs to that pourashava:

```java
public LoginResponseDto login(LoginRequestDto request) {
    TenantContext.setCurrentTenant(request.getTenantId());   // trusted blindly
    Authentication authentication = authenticationManager.authenticate(...);
    String accessToken = tokenProvider.generateToken(authentication);
```

A user of `sreepur` can log in with `tenantId: "gazipur"` and receive a token scoped to Gazipur. Once the tenant filter is switched on, this becomes the single most dangerous line in the codebase, because everything downstream will trust that claim.

**(b) Refresh silently drops the tenant.** `AuthService.refresh` calls `tokenProvider.generateToken(username)`, which reads `TenantContext.getCurrentTenant()` — null on a refresh request, since nothing populated it. Every refreshed access token carries `tenant_id: null`.

**(c) The seed admin has no role.** The `data.sql` insert omits the `role` column. `@Builder.Default` is a Java-level default and does not apply to raw SQL inserts, so the row stores `NULL` and `CustomUserDetailsService` constructs the authority `ROLE_null`.

---

## 3. Design Decisions

| # | Decision | Rationale |
|---|----------|-----------|
| D1 | Roles are **global/common**, not per-pourashava | A single catalogue of roles assignable to users of any pourashava. No `pourashava_id` on `roles`. Keeps role management centralised and avoids a per-tenant role-management UI. |
| D2 | `SUPER_ADMIN` is allocated to a **demo pourashava** | Keeps `users.pourashava_id` strictly `NOT NULL` with no nullable special case. The `SUPER_ADMIN` role bypasses the tenant filter, so its home pourashava is administrative bookkeeping only. |
| D3 | Users hold **multiple roles** (`user_roles` join table) | Costs nothing now; avoids a painful migration when a user must be both operator and approver. |
| D4 | Endpoints are annotated with **permissions**, not role names | Adding or restructuring a role never requires editing a controller. |
| D5 | Permissions are **resolved per request and cached in Redis**, not embedded in the JWT | Keeps tokens small and, critically, makes permission changes take effect immediately instead of waiting for token expiry. |
| D6 | Tenant discriminator is **`pourashava_id` (FK)**, replacing the `subdomain` string | A subdomain is mutable, unconstrained, and duplicated across five tables. An FK is enforced by the database and indexes cleanly. |
| D7 | Security is **deny-by-default** | `.anyRequest().authenticated()` with an explicit public allowlist. A new endpoint is protected the moment it is written. |

---

## 4. Data Model

### 4.1 Entity relationships

```
┌────────────────┐        ┌──────────────────┐        ┌────────────────┐
│  pourashavas   │        │   user_roles     │        │     roles      │
├────────────────┤        ├──────────────────┤        ├────────────────┤
│ id (PK)        │        │ user_id (FK)     │───┐    │ id (PK)        │
│ subdomain (UK) │        │ role_id (FK)     │───┼───>│ code (UK)      │
│ bn_name        │        └──────────────────┘   │    │ name_bn        │
│ en_name        │                 ▲             │    │ name_en        │
│ division_id    │                 │             │    │ is_system      │
│ district_id    │                 │             │    │ status         │
└────────┬───────┘                 │             │    └───────┬────────┘
         │                         │             │            │
         │ 1                       │             │            │ 1
         │                         │             │            │
         │ N                       │             │            │ N
┌────────▼───────┐                 │             │    ┌───────▼────────────┐
│     users      │─────────────────┘             │    │  role_permissions  │
├────────────────┤                               │    ├────────────────────┤
│ id (PK)        │                               │    │ role_id (FK)       │
│ username (UK)  │                               │    │ permission_id (FK) │
│ email (UK)     │                               │    └───────┬────────────┘
│ password       │                               │            │
│ pourashava_id  │  NOT NULL ────────────────────┘            │ N
│ status         │                                            │
└────────────────┘                                    ┌───────▼────────┐
                                                      │  permissions   │
                                                      ├────────────────┤
                                                      │ id (PK)        │
                                                      │ code (UK)      │
                                                      │ module         │
                                                      │ action         │
                                                      │ description    │
                                                      └────────────────┘
```

### 4.2 Schema

Following the naming conventions in `ARCHITECTURE.md` §5.1 (`snake_case` plural tables, `idx_`/`uk_`/`fk_` prefixes):

```sql
CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL,
    name_bn     VARCHAR(100) NOT NULL,
    name_en     VARCHAR(100) NOT NULL,
    description TEXT,
    is_system   BOOLEAN      NOT NULL DEFAULT FALSE,
    status      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_roles_code UNIQUE (code)
);

CREATE TABLE permissions (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(100) NOT NULL,   -- e.g. 'WORD:UPDATE'
    module      VARCHAR(50)  NOT NULL,   -- e.g. 'WORD'
    action      VARCHAR(50)  NOT NULL,   -- e.g. 'UPDATE'
    description TEXT,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_permissions_code UNIQUE (code)
);

CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role       FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

ALTER TABLE users ADD COLUMN pourashava_id BIGINT;
-- backfill from users.subdomain, then:
ALTER TABLE users ALTER COLUMN pourashava_id SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT fk_users_pourashava
    FOREIGN KEY (pourashava_id) REFERENCES pourashavas(id);
CREATE INDEX idx_users_pourashava_id ON users(pourashava_id);
```

`roles.is_system` marks the four seeded roles so the API can refuse to let anyone delete or rename them. The `module` and `action` columns on `permissions` are redundant with `code` but make it trivial to render a grouped permission matrix in the admin UI without string-splitting.

### 4.3 Retiring `User.role`

The existing `role VARCHAR` column is dropped after `user_roles` is backfilled from it. Keep it in place through one release if you want a rollback path, but do not read from it once `user_roles` is live — two sources of truth for authorization is how privilege-escalation bugs happen.

---

## 5. Role & Permission Catalogue

### 5.1 Modules

Derived from the entities that exist today. Each module gets `READ`, `CREATE`, `UPDATE`, `DELETE` unless noted.

| Module | Backing entity | Tenant scope |
|--------|----------------|--------------|
| `USER` | `User` | Tenant-scoped (+ `USER:ASSIGN_ROLE`) |
| `ROLE` | `Role` | Global — super admin only |
| `POURASHAVA` | `Pourashava` | Global registry |
| `POUROSHOVA_INFO` | `PouroshovaInfo` | Tenant-scoped |
| `DIVISION` | `Division` | Global reference data |
| `DISTRICT` | `District` | Global reference data |
| `WORD` | `Word` (ward) | Tenant-scoped |
| `PARA` | `Para` | Tenant-scoped |

That is 33 permission rows (8 modules × 4 actions, plus `USER:ASSIGN_ROLE`).

**Tenant scope** here describes the data, not the permission. `WORD:READ` grants the ability to read wards; the tenant filter independently restricts *which* wards. Global reference data (`divisions`, `districts`) is readable by everyone and writable only by super admin — it is national data, identical for all pourashavas.

### 5.2 Seeded roles

Global and assignable to a user of any pourashava (decision D1):

| Code | Bengali | Intent | Permissions |
|------|---------|--------|-------------|
| `SUPER_ADMIN` | সুপার অ্যাডমিন | Platform operator; creates pourashavas and their first admin | All permissions; bypasses the tenant filter |
| `POURASHAVA_ADMIN` | পৌরসভা অ্যাডমিন | Runs one municipality | Full CRUD on `USER`, `POUROSHOVA_INFO`, `WORD`, `PARA`; `READ` on the rest; **not** `ROLE:*`, **not** `POURASHAVA:CREATE/DELETE` |
| `OPERATOR` | অপারেটর | Day-to-day data entry | `CREATE`/`UPDATE`/`READ` on `WORD`, `PARA`; `READ` elsewhere; no `DELETE`, no `USER:*` |
| `VIEWER` | পর্যবেক্ষক | Read-only / reporting | `READ` on all modules |

Migration from the current free-text values: `SUPER_ADMIN` → `SUPER_ADMIN`, `ADMIN` → `POURASHAVA_ADMIN`, `USER` and `NULL` → `VIEWER`. Mapping unknown/null to the least-privileged role is deliberate; the alternative silently grants access.

### 5.3 The super admin and the demo pourashava (D2)

A seeded pourashava (`subdomain: 'demo'`) exists solely as the home of platform-level accounts, so `users.pourashava_id` can stay `NOT NULL` with no nullable branch in the model.

The tenant filter is skipped entirely when the principal holds `SUPER_ADMIN`. Two consequences worth stating explicitly:

- The demo pourashava must be excluded from pourashava listings, reports, and dropdowns presented to normal users.
- Bypassing the filter is a role check, *not* a check on `pourashava_id = demo`. Never grant tenant bypass based on which pourashava a user belongs to — that would make the demo pourashava a privilege-escalation target for any account placed in it.

---

## 6. Token Design

### 6.1 Claims

```json
{
  "sub": "admin",
  "userId": 1,
  "pourashavaId": 1,
  "subdomain": "sreepur",
  "roles": ["POURASHAVA_ADMIN"],
  "iat": 1735660800,
  "exp": 1735664400
}
```

Identity and tenancy live in the token. **Permissions deliberately do not** (decision D5):

- A user with several roles can carry 30+ permission codes, inflating every request header.
- Permissions baked into a token cannot be revoked. Tighten a role's permissions and every already-issued token keeps the old access until it expires.

Instead, `JwtAuthenticationFilter` resolves permissions per request and caches them in Redis keyed by user id. `RedisConfig` already declares a cache named `roles` with a one-hour TTL that is currently unused — this is what it was intended for. Evict the key whenever a user's role assignments change or a role's permission set changes.

`pourashavaId` is written from the **database record** at login, never from the request payload. `LoginRequestDto.tenantId` becomes a value to *validate against* the user's actual pourashava, not a value to trust. This is the fix for defect (a).

### 6.2 Lifetimes

`ARCHITECTURE.md` §7.2 specifies a 15-minute access token; `application.yml` currently ships 24 hours (`JWT_EXPIRATION: 86400000`). Since permissions are resolved server-side per request, a longer access token is no longer a revocation hazard for authorization — but it still is for *deactivation*. Recommend aligning with the documented 15 minutes and relying on the 7-day refresh token, which is already configured.

Refresh must reload the user and re-derive `pourashavaId` and roles from the database rather than copying claims forward, which fixes defect (b) and ensures a deactivated user cannot refresh their way into a valid session.

---

## 7. Enforcement

### 7.1 Request lifecycle

```
   HTTP request
        │
        ▼
  ┌───────────────────────────────────────────────┐
  │ JwtAuthenticationFilter                       │
  │  1. extract + validate Bearer token           │
  │  2. load roles from claims                    │
  │  3. resolve permissions (Redis, else DB)      │
  │  4. build UserPrincipal                       │
  │     { userId, username, pourashavaId,         │
  │       subdomain, authorities[] }              │
  │  5. TenantContext.set(pourashavaId)           │
  └───────────────────┬───────────────────────────┘
                      ▼
  ┌───────────────────────────────────────────────┐
  │ SecurityFilterChain                           │
  │   permitAll: /auth/**, /health, swagger       │
  │   anyRequest().authenticated()   ← 401        │
  └───────────────────┬───────────────────────────┘
                      ▼
  ┌───────────────────────────────────────────────┐
  │ @PreAuthorize("hasAuthority('WORD:UPDATE')")  │
  │                                  ← 403        │
  └───────────────────┬───────────────────────────┘
                      ▼
  ┌───────────────────────────────────────────────┐
  │ Service layer                                 │
  │   Hibernate @Filter auto-applies              │
  │     WHERE pourashava_id = :tenantId           │
  │   + explicit ownership check on writes        │
  └───────────────────┬───────────────────────────┘
                      ▼
                  Response
        │
        ▼ finally
   TenantContext.clear()
```

The `TenantContext.clear()` already sits in a `finally` block in the existing filter. That is correct and must stay — with pooled request threads, a leaked `ThreadLocal` means the next request on that thread inherits the previous user's tenant.

### 7.2 Authorization layer

Replace the blanket `permitAll`:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/login", "/auth/refresh").permitAll()
    .requestMatchers("/health", "/actuator/health").permitAll()
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .anyRequest().authenticated()
)
```

Then annotate by permission (decision D4):

```java
@PreAuthorize("hasAuthority('WORD:CREATE')")
@PostMapping
public ResponseEntity<ApiResponse<WordResponseDto>> create(@Valid @RequestBody WordRequestDto request) { ... }
```

`@EnableMethodSecurity` is already enabled, so these take effect as soon as authorities carry permission codes.

Swagger must stay public in dev/qa but should be closed in prod — worth a profile-conditional matcher rather than a permanent allowlist entry.

### 7.3 Tenant isolation layer

Two mechanisms, used together:

**Hibernate filter** on tenant-scoped entities, enabled once per request from `TenantContext`:

```java
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "pourashavaId", type = Long.class))
@Filter(name = "tenantFilter", condition = "pourashava_id = :pourashavaId")
```

This appends the predicate to every `SELECT`, including lazy-loaded collections, so a forgotten `WHERE` clause cannot leak data. Applies to `User`, `PouroshovaInfo`, `Word`, `Para`. Not applied to `Division`, `District` (national reference data) or `Pourashava` (the tenant registry itself, which needs its own read rules).

**Explicit ownership checks** on write paths and fetch-by-id. The filter makes a cross-tenant id return "not found" — correct behaviour, but it should be a deliberate assertion rather than an accident of configuration, and writes need it regardless.

Do **not** rely on hand-written `AND pourashava_id = ?` in each query. That approach fails the first time someone forgets, and the failure is silent.

### 7.4 Two checks that RBAC alone will not cover

- **Role escalation on user creation.** A `POURASHAVA_ADMIN` holds `USER:CREATE` and `USER:ASSIGN_ROLE`. Without an extra rule, they can create a user and assign `SUPER_ADMIN`. Rule: a user may only assign roles whose permission set is a subset of their own.
- **Cross-tenant user creation.** `POURASHAVA_ADMIN` must not choose an arbitrary `pourashava_id` when creating a user; it is forced to their own. Only `SUPER_ADMIN` may specify it freely.

---

## 8. Components

Following the layer rules in `ARCHITECTURE_RULES.md` (CQRS split under `service/`, validators in `service/validator/`, constructor injection via `@RequiredArgsConstructor`).

### 8.1 New

| Path | Purpose |
|------|---------|
| `entity/Role.java` | Role entity, `@ManyToMany` to `Permission` |
| `entity/Permission.java` | Permission entity |
| `repository/RoleRepository.java` | + `findByCode` |
| `repository/PermissionRepository.java` | + `findPermissionCodesByUserId` |
| `security/UserPrincipal.java` | `UserDetails` carrying `userId`, `pourashavaId`, `subdomain` |
| `security/PermissionResolver.java` | Loads and Redis-caches a user's permission codes |
| `security/CurrentUserProvider.java` | Static accessor for the principal (named in `ARCHITECTURE_RULES.md` §3) |
| `config/TenantFilterAspect.java` | Enables the Hibernate filter per request |
| `controller/RoleController.java` | Role CRUD + permission assignment |
| `service/RoleService.java` | With `service/validator/RoleValidatorService.java` |
| `db/migration/V2__*.sql` … | Real Flyway migrations |

### 8.2 Modified

| Path | Change |
|------|--------|
| `config/SecurityConfig.java` | Deny-by-default matcher chain |
| `security/JwtTokenProvider.java` | New claims; remove reliance on `TenantContext` at issue time |
| `security/JwtAuthenticationFilter.java` | Build `UserPrincipal`; resolve permissions; set tenant from token |
| `security/CustomUserDetailsService.java` | Return `UserPrincipal` with permission authorities |
| `security/TenantContext.java` | Hold `Long pourashavaId` instead of a subdomain string |
| `service/AuthService.java` | Validate tenant membership at login; reload user on refresh |
| `entity/User.java` | `@ManyToOne Pourashava`; `@ManyToMany` roles; drop `role` string |
| `controller/*.java` | Add `@PreAuthorize` |
| `resources/data.sql` | Seed roles, permissions, demo pourashava; fix the admin row |

---

## 9. Implementation Plan

Each phase leaves the application in a working state.

| Phase | Work | Why here |
|-------|------|----------|
| **0** | Fix defects (a), (b), (c) from §2.3 | Live vulnerabilities, independent of RBAC. Fix before building on top. |
| **1** | Real Flyway migrations | `V1__baseline.sql` is a `SELECT 1;` placeholder and Flyway is disabled in dev while Hibernate runs `create-drop`. Get schema management working *before* adding tables with foreign keys, or the RBAC schema exists only on developer laptops. |
| **2** | `users.pourashava_id`, backfilled from `subdomain`; FK + `NOT NULL`; demo pourashava seeded | Satisfies "every user must have a pourashava" as a database constraint. |
| **3** | `roles`, `permissions`, `role_permissions`, `user_roles`; seed the catalogue; backfill `user_roles` from `User.role` | Schema and data, no behaviour change yet. |
| **4** | `UserPrincipal`, `PermissionResolver` with Redis caching, new JWT claims | Authorities now carry permissions; still nothing is denied. |
| **5** | Close `SecurityConfig`; add `@PreAuthorize` module by module | **First breaking change.** Every client must send a valid token. |
| **6** | Hibernate tenant filter + ownership checks on writes | Cross-tenant access closed. |
| **7** | Role-management API (`RoleController`) | Admin UI can manage assignments. |
| **8** | UI wiring | See §10. |

Phase 5 is where existing integrations break. Everything before it is additive.

### 9.1 A note on `ddl-auto`

Dev currently runs `create-drop` with `data.sql`; qa/prod run `validate` with Flyway against a stub baseline. This means the dev schema is defined by JPA annotations and the prod schema by nothing at all. Phase 1 should generate a real baseline from the current entities and switch dev to Flyway too, so all environments share one schema definition.

---

## 10. UI Impact (`e-pourashava-ui`)

The Angular app has auth scaffolding that is written but not wired:

| Item | File | State |
|------|------|-------|
| `authGuard` | `common/guard/auth.guard.ts` | Exists; **not applied** in `app.routes.ts` |
| `httpTokenInterceptor` | `common/interceptor/` | Exists; **not registered** in `app.config.ts` |
| Login payload | `LoginComp.ts` | Does not send `tenantId`, which the API marks `@NotBlank` |

Required: register the interceptor, apply the guard to all non-login routes, add a permission-aware structural directive (`*hasPermission="'WORD:CREATE'"`) for hiding controls, and handle 401 (refresh, then redirect to login) and 403 (show a forbidden message) distinctly.

**UI permission checks are cosmetic.** Every one must be duplicated server-side; hiding a button prevents no requests.

Unrelated but adjacent: `proxy.conf.json` targets `localhost:8080` while the API defaults to `9080`.

---

## 11. Testing

`AssessmentCollectionApplicationTests.contextLoads()` is the only test in the project. Authorization logic is exactly the kind of code that needs tests, because failures are silent — an over-permissive rule produces no error, just a leak.

| Layer | Coverage |
|-------|----------|
| Unit | Permission resolution per role; the role-subset rule for assignment |
| Slice (`@WebMvcTest` + `spring-security-test`) | Each endpoint returns 401 anonymous, 403 under-privileged, 200 authorised |
| Integration | User A of pourashava 1 cannot read/update/delete a record of pourashava 2 — one test per tenant-scoped entity |
| Integration | `SUPER_ADMIN` sees across pourashavas; the demo pourashava is hidden from normal listings |
| Regression | Login with a mismatched `tenantId` is rejected (defect a); refreshed tokens retain `pourashavaId` (defect b) |

`spring-security-test` is already on the classpath.

---

## 12. Open Items

1. **Audit trail** — should role assignments and permission changes be recorded? `ARCHITECTURE.md` mentions an `@AuditLog` annotation that does not exist yet.
2. **Token revocation** — a Redis denylist on logout, or accept that access tokens stay valid until expiry (acceptable at a 15-minute lifetime).
3. **Password policy** — no complexity rules, expiry, or lockout after failed attempts currently exist.
4. **Subdomain-based tenant hinting** — should the login page infer the pourashava from the browser's hostname rather than a form field?
5. **Assessment/collection modules** — the permission catalogue covers only today's entities. When those modules land, they add rows to `permissions` and grants to `role_permissions`; no structural change.

---

*Document Version: 1.0 — Proposed*
*Last Updated: 2026-09-02*
