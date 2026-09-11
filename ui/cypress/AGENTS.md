# AGENTS Guide (Cypress E2E)

## Scope

Applies to everything under `ui/cypress/`. `ui/AGENTS.md` and the root guide apply as well.
`ui/cypress/README.md` covers running the suite, test-run scheduling and fixture generation;
this file covers how to write tests.

## Primary goal

Keep specs readable by centralising selectors and flows in `support/` classes.

## Running

- `npm run test-cypress-open` against a UI on port 8082 (**Open Cypress** in
  `.vscode/launch.json`).
- Name a spec `*.smoke.spec.ts` to run it on every pull request; `*.spec.ts` runs nightly.
  Prefer the smoke suffix for a test that guards a user-facing flow.

## Authoring rules

- Use the helpers in `support/utils/**` (grouped by domain: `connect`, `pipeline`, `chart`,
  `dashboard`, `dataset`, `asset`, `configuration`, `user`, ...) and the builders in
  `support/builder/**` (`AdapterBuilder`, `PipelineBuilder`, `PipelineElementBuilder`,
  `UserBuilder`, ...).
- No new inline selector strings in specs when a selector can be reused. If a new selector
  is needed, add it to the fitting support class first, then consume it from the spec.
- Specs express scenario intent and assertions, not UI wiring.

## Selector placement

- `data-cy` accessors go in the domain `*Btns` classes (`PipelineBtns`, `ConnectBtns`,
  `ChartBtns`, ...), as verb/noun methods returning Cypress chains.
- Multi-step user flows go in the domain `*Utils` classes (`PipelineUtils`, `ConnectUtils`,
  `ChartUtils`, ...).
- Dynamic selectors use typed helper methods with parameters, not string concatenation in specs.
- Keep direct `cy.get(...)` in specs to a minimum; if it is reused, move it behind a helper.
- Do not probe UI state with `cy.get('body')` / `$body.find(...)` conditionals.
- If a reliable element is hard to target, add a `data-cy` in the Angular template and
  expose it through the matching `*Btns` helper.

## Spec structure

- Start with `cy.initStreamPipesTest()` unless the test needs a different setup; it resets
  the system so every test starts clean.
- Each test sets up its own data with the builders; no dependencies between tests.
- Avoid fixed `cy.wait(...)`; prefer state-based waits and assertions through helpers.

## Interaction style

- Simple, deterministic steps that mirror user flows.
- Explicit navigation and toolbar actions to leave pages clean, not conditional dialog handling.
- No `force: true` clicks unless there is no deterministic path.
- When overlays or dialogs block interaction repeatedly, fix the flow with stable `data-cy`
  hooks in the UI, not DOM-probing workarounds in the spec.

## Refactoring

- When touching a spec with many inline selectors, move the touched selectors into the
  matching `*Btns` / `*Utils` class. Keep the refactor scoped to that test area.

## Validation

- Run the affected specs (smoke selection where feasible). If they cannot be run, say so in
  the pull request.
