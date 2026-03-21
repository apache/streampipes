# AGENTS Guide (Cypress E2E)

## Scope

Applies to everything under `ui/cypress/`.

## Inheritance

- Also follow `AGENTS.md` at repository root.
- Also follow `ui/AGENTS.md`.

## Primary Goal

Keep tests readable and maintainable by centralizing selectors and common flows in `support/` classes.

## Authoring Rules For New/Generated Tests

- Prefer existing helpers from `ui/cypress/support/utils/**` and `ui/cypress/support/builder/**`.
- Do not introduce new inline selector strings in spec files when a selector can be reused.
- If a new selector is needed, add it to a fitting support class first, then consume it from the spec.
- Keep specs focused on scenario intent and assertions, not low-level UI wiring.

## Selector Placement Rules

- Put `data-cy` element accessors in domain `*Btns` classes (for example `PipelineBtns`, `ConnectBtns`, `ChartBtns`).
- Put multi-step user flows in domain `*Utils` classes (for example `PipelineUtils`, `ConnectUtils`, `ChartUtils`).
- For dynamic selectors, use typed helper methods with parameters instead of string concatenation in specs.
- Reuse existing selector constants/patterns when already present (for example `SiteUtils` constants).
- Keep direct `cy.get(...)` in specs to a minimum; if reused, move it behind a support helper.

## Spec Structure

- Initialize test state with `cy.initStreamPipesTest()` unless a test explicitly requires different setup.
- Reuse builders for test objects (`AdapterBuilder`, `PipelineBuilder`, `PipelineElementBuilder`, ...).
- Avoid fixed `cy.wait(...)` where possible; prefer state-based waits/assertions via helpers.
- Keep each test independent: no inter-test dependencies.

## Refactoring Expectations

- When touching an existing spec with many inline selectors, opportunistically move touched selectors to the matching `*Btns`/`*Utils` class.
- Do not do broad cross-module rewrites; keep refactors scoped to the test area being changed.

## Naming And Organization

- Follow existing naming style:
  - UI accessors: verb/noun methods in `*Btns` returning Cypress chains.
  - Flows/assertions: descriptive methods in `*Utils`.
- Place new helpers in the closest domain folder under `support/utils/` (connect, pipeline, chart, ...).

## Validation

- For Cypress changes, run targeted specs when feasible (for example via smoke selection).
- If execution is not possible, document what was not run.
