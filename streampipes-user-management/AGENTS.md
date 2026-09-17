# AGENTS Guide (User Management)

## Scope

Applies to `streampipes-user-management/`. Root `AGENTS.md` applies as well; build and
validation commands live there.

## Module intent

Authentication, token handling, role and privilege resolution, user principal composition.

## High-risk areas

- JWT creation and validation (`jwt/*`, `service/TokenService`).
- Password and secret handling (`authentication/*`, `util/PasswordUtil`, `encryption/*`).
- Default roles and privileges (`authorization/RoleManager`, `authorization/PrivilegeManager`).
  Changing defaults affects existing installations through the setup and migration flows in
  `streampipes-service-core`; a new privilege usually needs a migration there.

## Rules

- Never store or log raw secrets, tokens or passwords — not even at debug level.
- Preserve secure defaults; a change that relaxes a check needs an explicit reason in the
  pull request.
- Keep user, group and role resolution compatible with the storage-backed representation
  in `streampipes-storage-couchdb`.
