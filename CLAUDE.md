# Claude Code guide

@AGENTS.md

## Nested guides

Every directory with its own `AGENTS.md` also has a one-line `CLAUDE.md` containing
`@AGENTS.md`, so the module guide loads automatically when you work inside that directory.
The "Guide index" at the end of `AGENTS.md` lists them. If a task spans a module you have
not touched yet, read its `AGENTS.md` before editing — the recipes (how to add a migration,
an endpoint, an extension) and module-specific rules live there, not here.

## Skills

- `security-triage` — use when the task is to assess, reproduce or report a suspected
  vulnerability. It loads `THREAT_MODEL.md` and the triage procedure. Do not load it for
  ordinary feature or bug work.

## Working in this repository

- Run `mvn -pl <module> -am test` for the fast loop and `mvn clean verify` from the root
  before declaring backend work done; only the root `verify` runs the license-header check.
- For `ui/` changes run `npm run format && npm run lint && npm run i18n:check` in `ui/`.
- Do not commit; leave changes in the working tree unless asked.
