# AGENTS Guide (Resource Management)

## Scope
Applies to `streampipes-resource-management/`.

## Module Intent
- Cross-resource orchestration and lifecycle management (permissions, users, adapters, pipelines, notifications, secret handling).

## High-Risk Areas
- Permission creation/deletion coupling in CRUD/resource managers.
- User onboarding/account activation/password recovery flows (`UserResourceManager`).
- Secret encryption/decryption handlers (`secret/*`).

## Best Practices
- Keep side effects explicit and consistent (storage + permission + notification updates).
- Preserve idempotency for delete/update operations where possible.
- Avoid orphaning permissions/assets when deleting entities.
- Keep mail-triggering flows resilient and error-aware.

## Validation
- `mvn -pl streampipes-resource-management -am test`
