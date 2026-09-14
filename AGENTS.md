# AGENTS Guide (Repository Root)

## Scope

Applies to the whole repository. A deeper `AGENTS.md` adds to this file; it never
replaces it. Before editing inside a directory that has its own guide, read that guide.
Not every agent harness loads nested guides automatically, so the index at the end of this
file is the map.

## Toolchain (read this before running anything)

- **Java:** CI builds with **JDK 25** (Temurin). The compiler runs with `<release>17</release>`
  (`pom.xml`), so the **language level is Java 17** — do not use syntax newer than 17.
  `.java-version` pins 17 for local tooling; the `maven.compiler.source/target=25` properties
  in `pom.xml` are overridden by the `release` flag and can be ignored.
- **Maven** 3.8+. **Node** 22 with npm for `ui/`. **Docker + Compose** for the service stack.
- **Python client:** Poetry — see `streampipes-client-python/AGENTS.md`.
- **Go client:** Go 1.21 — see `streampipes-client-go/AGENTS.md`.
- **Main branch is `dev`.** Pull requests target `dev`.

## Build and validate

Fast loop for a backend change:

```bash
mvn -pl <module> -am test        # -am also builds upstream modules; add -o when offline
mvn -pl <module> -am -DskipTests package   # compile + checkstyle only
```

Before opening a pull request, run what CI runs (`.github/workflows/pr-validation.yml`):

```bash
mvn clean verify                 # from the repository root
cd ui && npm run format && npm run lint && npm run i18n:check
```

Why the root `verify` matters: checkstyle runs in the `validate` phase and is therefore
covered by `-pl <module> test`, but the Apache RAT **license-header check runs only in the
`verify` phase and only from the root pom** (`inherited=false`). A module-level `test`
will pass on a file that is missing its license header; CI will not.

Full backend build: `mvn clean package`. Full UI build: `cd ui && npm install && npm run build`.

## Run the stack locally

- `.devcontainer/` starts CouchDB, the message broker, the time-series store and the other
  third-party services with Docker Compose (`.devcontainer/README.md`). It is the reference
  for required services and environment variables even outside VS Code.
- `.vscode/launch.json` has working run configurations: **Debug Core**, **Debug UI**
  (UI on port 8082 with proxy), **Debug IIoT Extensions**, **Open Cypress**.
  `.vscode/tasks.json` has the matching `dev: ...` build tasks.
- `docker-compose.yml` at the root starts a complete stack from images (no volumes).

## Code standards

- **License header** on every source file (Java, TypeScript, SCSS, HTML, Markdown, YAML,
  shell). Markdown uses an HTML comment. Exempt by RAT configuration: `**/AGENTS.md`,
  `**/CLAUDE.md`, `.claude/**`, `*.json`, `*.txt` (full list in `pom.xml`, `apache-rat-plugin`).
- **Java style and import order:** enforced by `tools/maven/checkstyle.xml` with the header
  template `tools/maven/checkstyle-header.txt`. Eclipse formatter settings that match
  checkstyle: `tools/maven/streampipes-code-formatter.xml`. Import order is
  `org.apache.streampipes` → third-party → `jakarta` → `javax` → `java` → `scala` → static.
  (Spotless is configured in `pom.xml` but skipped; checkstyle is the gate.)
- Prefer constructor-injected, typed APIs over loosely typed maps when extending domain logic.
- Keep backward compatibility for public APIs, serialized models and storage contracts
  unless the task explicitly changes them.
- **Commit messages** follow the history: `feat(#1234): ...`, `fix(#1234): ...`,
  `test(#1234): ...`, `chore(deps): ...`, `ci: ...`, `docs: ...`. Reference the issue when
  one exists. The PR template is `.github/PULL_REQUEST_TEMPLATE.md`.

## Monorepo rules

- Keep changes scoped to the requested modules; no opportunistic cross-module refactors.
- Do not edit generated code by hand (`ui/projects/streampipes/platform-services/src/lib/model/gen/`,
  anything under `target/`). Regenerate it — see the owning module's guide.

## Architecture boundaries

- `streampipes-service-core`: bootstrapping, security configuration, migrations, scheduling.
  Orchestration only — no domain logic.
- `streampipes-rest`: HTTP resource layer. Thin: parse, authorize, delegate, map the response.
- `streampipes-*-management` (pipeline, resource, user, connect, storage, extensions,
  data-explorer): business and domain logic. New behaviour goes here.
- `streampipes-storage-api` / `streampipes-storage-couchdb`: persistence contracts and the
  CouchDB implementation.
- `streampipes-model`, `streampipes-model-client`, `streampipes-model-shared`: the domain
  model. It is persisted to CouchDB and generated into TypeScript for the UI, so field
  changes are compatibility-sensitive on both sides.
- `streampipes-extensions/`: adapters, processors and sinks, built on `streampipes-sdk`,
  `streampipes-extensions-api` and the `streampipes-wrapper-*` runtimes.
- `ui/`: Angular application plus the `@streampipes/platform-services` and
  `@streampipes/shared-ui` libraries.

## Security

`THREAT_MODEL.md` defines the trust boundaries (REST front door, adapter ingestion, extension
runtime) and what is the operator's responsibility rather than the engine's. Read it when a
change touches authentication, permissions, adapters or extension loading. `SECURITY.md` says
how vulnerabilities are reported. The full procedure for **triaging a suspected
vulnerability** lives in `.claude/skills/security-triage/SKILL.md`; it is deliberately not
inlined here so that it does not load on unrelated tasks.

## Guide index

| Directory | Guide |
| --- | --- |
| `streampipes-service-core/` | `AGENTS.md` — startup, security config, adding a migration |
| `streampipes-rest/` | `AGENTS.md` — adding an endpoint, authorization annotations |
| `streampipes-pipeline-management/` | `AGENTS.md` — matching, verification, graph, execution |
| `streampipes-resource-management/` | `AGENTS.md` — resource lifecycle, permissions coupling |
| `streampipes-user-management/` | `AGENTS.md` — tokens, passwords, roles |
| `streampipes-storage-couchdb/` | `AGENTS.md` — DAOs, views, serializers |
| `streampipes-extensions/` | `AGENTS.md` — adding an adapter, processor or sink |
| `streampipes-client-python/` | `AGENTS.md` — Poetry toolchain and gates |
| `streampipes-client-go/` | `AGENTS.md` — Go toolchain and gates |
| `ui/` | `AGENTS.md` — design sources of truth, build, page composition |
| `ui/cypress/` | `AGENTS.md` — E2E authoring rules |
| `ui/projects/streampipes/platform-services/` | `AGENTS.md` — API client library, generated models |
| `ui/projects/streampipes/shared-ui/` | `AGENTS.md` — shared component library |
