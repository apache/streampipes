# AGENTS Guide (UI Shared Components)

## Scope

Applies to `ui/projects/streampipes/shared-ui/`. `ui/AGENTS.md` and the root guide apply
as well.

## Module intent

Reusable, cross-feature components, dialogs and services (`sp-basic-view`, `sp-page-header`,
`sp-page-nav-tabs`, `sp-split-section`, `sp-form-field`, `sp-table`, `sp-label`,
`sp-alert-banner`, dialog base classes). Public surface: `src/public-api.ts`.

## Rules

- Components here are generic: no feature-specific business logic, no feature imports.
- Visual behaviour follows `DESIGN.md` (normative) and is documented for consumers in
  `ui/STYLEGUIDE.md`; a new page-level pattern gets a styleguide entry.
- Consume semantic CSS variables from the theme; never embed default brand values.
- Inputs and outputs must be translation-friendly (accept keys or translated strings, never
  hard-coded English) and accessible (labels, `aria-*`, keyboard operation).
- Treat `src/public-api.ts` as a compatibility boundary: export new components there;
  do not remove or rename exports without updating every consumer.

## Validate

```bash
ng build @streampipes/shared-ui && ng test @streampipes/shared-ui
npm run build-libraries      # so the app picks up the change
```
