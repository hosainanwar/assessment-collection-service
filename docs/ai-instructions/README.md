# AI instructions

Architecture and coding guidance for agents and developers. Application source stays in `e-pourashava-api` and `e-pourashava-ui`.

## API (`e-pourashava-api`)

| File | Purpose |
|------|---------|
| [ARCHITECTURE.md](api/ARCHITECTURE.md) | Backend architecture, packages, security, API conventions |
| [ARCHITECTURE_RULES.md](api/ARCHITECTURE_RULES.md) | Layering, CQRS, naming, transaction, and security rules |
| [CODE_WRITING_GUIDELINES.md](api/CODE_WRITING_GUIDELINES.md) | How to write and structure API code |
| [RBAC_DESIGN.md](api/RBAC_DESIGN.md) | Authentication, roles, permissions, tenant isolation |
| [AUTHENTICATION_ARCHITECTURE_TEMPLATE.md](api/AUTHENTICATION_ARCHITECTURE_TEMPLATE.md) | Historical OAuth2/JWT template (reference only) |

## Cross-cutting

| File | Purpose |
|------|---------|
| [SECURITY.md](SECURITY.md) | Security spec from `epourashava-main`, for API and UI implementation |
| [entities/](entities/README.md) | One file per entity: schema, then business logic |

## UI (`e-pourashava-ui`)

| File | Purpose |
|------|---------|
| [ARCHITECTURE.md](ui/ARCHITECTURE.md) | Angular project structure, modules, API layer |
