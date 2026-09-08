# Claude Code guide

@AGENTS.md

## Nested guides

Claude Code loads this file and the root `AGENTS.md` it imports. It does **not** automatically
load the nested `AGENTS.md` files. Before editing inside any directory listed in the
"Guide index" at the end of `AGENTS.md`, read that directory's `AGENTS.md` first — they
contain the recipes (how to add a migration, an endpoint, an extension) and the
module-specific rules.

## Skills

- `security-triage` — use when the task is to assess, reproduce or report a suspected
  vulnerability. It loads `THREAT_MODEL.md` and the triage procedure. Do not load it for
  ordinary feature or bug work.

## Working in this repository

- Run `mvn -pl <module> -am test` for the fast loop and `mvn clean verify` from the root
  before declaring backend work done; only the root `verify` runs the license-header check.
- For `ui/` changes run `npm run format && npm run lint && npm run i18n:check` in `ui/`.
- Do not commit; leave changes in the working tree unless asked.
