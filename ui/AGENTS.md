# AGENTS Guide (UI)

## Scope

Applies to everything under `ui/` unless overridden by deeper `AGENTS.md` files.

## Source of Truth

- Follow:
  - `ui/STYLEGUIDE.md`
  - `ui/.eslintrc.json`
  - `ui/.prettierrc.json`
  - `ui/angular.json`

## Build and Validation

- Standard checks:
  - `npm run lint`
  - `npm run format`
  - `npm test`
- For full app packaging, use:
  - `npm run build`

## UI Best Practices

- Use shared design-system components before creating new equivalents (`sp-basic-view`, `sp-form-field`, `sp-table`, alert/banner components).
- Keep user-facing strings translatable (`| translate`).
- Keep button usage consistent (`mat-flat-button` per style guide).
- Keep feature logic in `ui/src/app`; keep shared/reusable building blocks in library projects.

## Change Safety

- Preserve routing and guard behavior in `ui/src/app/_guards`.
- Keep service contract compatibility with `@streampipes/platform-services` APIs.
- Prefer incremental UI changes over broad structural rewrites.
