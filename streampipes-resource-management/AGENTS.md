# AGENTS Guide (Resource Management)

## Scope

Applies to `streampipes-resource-management/`. Root `AGENTS.md` applies as well; build and
validation commands live there.

## Module intent

Cross-resource orchestration and lifecycle: permissions, users, adapters, pipelines,
notifications, secret handling. `SpResourceManager` is the entry point other modules use.

## High-risk areas

- Permission creation and deletion is coupled to entity CRUD in the resource managers.
  Deleting an entity without its permissions orphans them; creating one without permissions
  makes it invisible to its owner.
- User onboarding, account activation and password recovery (`UserResourceManager`).
- Secret encryption and decryption handlers (`secret/*`).

## Rules

- Keep the side effects of an operation together: storage write, permission update,
  notification — in that order, and consistently across managers.
- Delete and update operations should stay idempotent where the existing manager already is.
- Mail-triggering flows must fail without aborting the surrounding operation.
