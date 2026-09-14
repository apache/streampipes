# AGENTS Guide (Service Core)

## Scope

Applies to `streampipes-service-core/`. Root `AGENTS.md` applies as well; build and
validation commands live there.

## Module intent

- Backend entrypoint and runtime wiring (`StreamPipesCoreApplication`).
- Security setup: `WebSecurityConfig`, the token filter, OAuth handlers. The list of
  unauthenticated endpoints is `UnauthenticatedInterfaces`, applied by `WebSecurityConfig`.
- Startup tasks, migrations and schedulers. Orchestration only — domain behaviour belongs
  in the `*-management` modules.

## High-risk areas

- Authentication flow and the unauthenticated-endpoint list.
- Migration registration and execution (`migrations/AvailableMigrations`, `MigrationsHandler`).
- Scheduled side effects (data lake retention, certificate jobs).

## Recipe: add a migration

1. Create the class under `migrations/v<release>/` (for example `migrations/v099/`)
   implementing `migrations.Migration`: `shouldExecute()`, `executeMigration()`,
   `getDescription()`.
2. `shouldExecute()` must make the migration **idempotent** — it runs on every start.
   Detect the already-migrated state and return `false`.
3. Take the storages you need through the constructor; `AvailableMigrations` obtains them
   from `SpResourceManager` (see its constructor for the available `I*Storage` fields).
4. Register it by **appending it to the end** of the list in
   `AvailableMigrations.getAvailableMigrations()`. Never insert between existing entries:
   migrations run in list order and installations may already be past earlier ones.
5. Treat partial failure explicitly — log and rethrow rather than leaving a half-migrated
   state silently.

## Rules

- Preserve startup behaviour for both OAuth and non-OAuth deployments.
- Do not put long-running or blocking work on startup thread paths.
