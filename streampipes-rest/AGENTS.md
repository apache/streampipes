# AGENTS Guide (REST API)

## Scope
Applies to `streampipes-rest/`.

## Module Intent
- HTTP API/resource layer (`impl`, `impl/admin`, `impl/connect`, `impl/datalake`, `impl/runtime`).
- Security-related endpoint behavior (`rest/security`).

## Best Practices
- Keep resources thin: request parsing, auth/permission checks, delegation, response mapping.
- Keep business/domain logic in management modules.
- Preserve endpoint contracts and semantics unless explicitly requested.
- Apply consistent error handling and status codes; avoid silent behavioral changes.
- Preserve permission evaluation behavior (`SpPermissionEvaluator`) for protected operations.

## Validation
- `mvn -pl streampipes-rest -am test`
