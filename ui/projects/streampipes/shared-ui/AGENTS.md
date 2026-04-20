# AGENTS Guide (UI Shared Components)

## Scope

Applies to `ui/projects/streampipes/shared-ui/`.

## Module Intent

- Reusable, cross-feature UI components/dialogs/services.
- Public surface is exported via `ui/projects/streampipes/shared-ui/src/public-api.ts`.

## Best Practices

- Keep components generic and reusable; avoid feature-specific business logic.
- Reuse existing shared primitives (`sp-table`, `sp-form-field`, `basic-*`, dialog base components) before adding new ones.
- Preserve UX and visual consistency with `ui/STYLEGUIDE.md`.
- Keep component APIs translation-friendly and accessibility-aware.
- Treat `src/public-api.ts` exports as a compatibility boundary.

## Validation

- `ng build @streampipes/shared-ui`
- `ng test @streampipes/shared-ui`
