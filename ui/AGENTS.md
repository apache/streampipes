# AGENTS Guide (UI)

## Scope

Applies to everything under `ui/` unless overridden by deeper `AGENTS.md` files.

## Source of Truth

- Follow:
  - `ui/STYLEGUIDE.md`
  - `ui/eslint.config.mjs`
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

## Styling and CSS

- Use Angular Flex-Layout directives for layout where appropriate (`fxLayout`, `fxFlex`, `fxLayoutAlign`, `fxLayoutGap`) instead of adding custom CSS layout wrappers.
- Prefer spacing utilities and spacing tokens from `ui/src/scss/sp/_spacing.scss` over ad hoc margins and paddings.
- Prefer typography utilities and typography tokens from `ui/src/scss/sp/_typography.scss` over component-local font sizes or line heights.
- Check shared variables in `ui/src/scss/sp/_variables.scss` before introducing new component-local CSS variables, colors, or repeated size values.
- Prefer `rem`, CSS variables, existing utility classes, and calculated values based on spacing or typography tokens over raw pixel values.
- Pixel values are acceptable for borders, icons, canvas/SVG coordinates, third-party component constraints, and cases where Angular Material or browser APIs require pixels.
- Avoid inline `style` attributes unless the value is dynamic or cannot reasonably be expressed with classes, Flex-Layout directives, or existing utilities.
- Before adding a new CSS class, check whether an existing utility class, shared component, or styleguide pattern already covers the use case.

## Change Safety

- Preserve routing and guard behavior in `ui/src/app/_guards`.
- Keep service contract compatibility with `@streampipes/platform-services` APIs.
- Prefer incremental UI changes over broad structural rewrites.
