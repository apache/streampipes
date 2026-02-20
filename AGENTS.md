# AGENTS Guide (Repository Root)

## Scope
Applies to the whole repository unless a deeper `AGENTS.md` overrides it.

## Monorepo Rules
- Keep changes scoped to requested modules; avoid opportunistic cross-module refactors.
- Preserve Apache license headers in source files.
- Keep backward compatibility for public APIs, serialized models, and storage contracts unless explicitly requested.

## Java Standards
- Follow style and import order from:
  - `tools/maven/checkstyle.xml`
  - `tools/maven/checkstyle-header.txt`
- Prefer constructor/typed APIs over loosely typed maps when extending domain logic.

## Architecture Boundaries
- `streampipes-service-core`: bootstrapping, security, migrations, scheduling.
- `streampipes-rest`: HTTP/resource layer.
- `*-management` modules: business/domain logic.
- `streampipes-storage-*`: persistence abstractions/implementations.
- Avoid moving business logic into controllers/resources.

## Validation
- For backend changes, run targeted module checks first:
  - `mvn -pl <module> -am test`
- Run only additional modules affected by dependency edges.

## UI
- UI-specific rules live in `ui/AGENTS.md` and deeper UI-level files.
