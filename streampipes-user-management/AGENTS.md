# AGENTS Guide (User Management)

## Scope
Applies to `streampipes-user-management/`.

## Module Intent
- Authentication, token handling, role/privilege resolution, and user principal composition.

## High-Risk Areas
- JWT/token creation and validation (`jwt/*`, `service/TokenService`).
- Password and secret handling (`authentication/*`, `util/PasswordUtil`, `encryption/*`).
- Default role/privilege definitions (`authorization/RoleManager`, `authorization/PrivilegeManager`).

## Best Practices
- Treat security changes as high-risk and preserve secure defaults.
- Never store or log raw secrets/tokens/passwords.
- Keep role/privilege updates consistent with existing permission model and setup flows.
- Preserve compatibility with storage-backed user/group/role resolution.

## Validation
- `mvn -pl streampipes-user-management -am test`
