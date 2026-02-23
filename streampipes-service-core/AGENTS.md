# AGENTS Guide (Service Core)

## Scope
Applies to `streampipes-service-core/`.

## Module Intent
- Backend entrypoint and runtime wiring (`StreamPipesCoreApplication`).
- Security setup (`WebSecurityConfig`, token filter, OAuth handlers).
- Startup tasks, migrations, and schedulers.

## High-Risk Areas
- Authentication flow and unauthenticated endpoint configuration.
- Migration registration/execution (`migrations/AvailableMigrations`, `MigrationsHandler`).
- Scheduled side effects (for example data lake and certificate jobs).

## Best Practices
- Keep this module orchestration-focused; domain behavior belongs in management modules.
- New migrations must be idempotent and explicitly registered.
- In `migrations/AvailableMigrations`, always append new migrations at the end of the list.
- Do not insert new migrations between existing entries to avoid conflicting migration order.
- Preserve existing startup behavior for OAuth and non-OAuth deployments.
- Avoid introducing long-running/blocking work on startup thread paths.

## Validation
- `mvn -pl streampipes-service-core -am test`
