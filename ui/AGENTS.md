# AGENTS Guide (UI)

## Scope

Applies to everything under `ui/`. Root `AGENTS.md` applies as well. Deeper guides:
`ui/cypress/AGENTS.md`, `ui/projects/streampipes/platform-services/AGENTS.md`,
`ui/projects/streampipes/shared-ui/AGENTS.md`.

## Design sources of truth (in this order)

1. `PRODUCT.md` (repository root) — who the users are, brand personality, anti-references,
   accessibility target. The _why_ behind every rule below.
2. `DESIGN.md` (repository root) — **normative** for colour roles, typography scale, spacing,
   radius, elevation and the named rules (Theme Boundary, Operational Signal, One Page
   Identity, Flat Working Plane). Read it before styling anything.
3. `ui/STYLEGUIDE.md` — concrete component usage and copy-paste markup.
4. `ui/eslint.config.mjs`, `ui/.prettierrc.json`, `ui/angular.json` — mechanical rules.

Where `DESIGN.md` and `STYLEGUIDE.md` disagree, `DESIGN.md` wins and the styleguide is the
file to fix. Token values live in `ui/deployment/theme/` (copied to `ui/src/scss/custom-theme/`
by `deployment/prebuild.js`) and `ui/src/scss/sp/`; consume them through CSS variables, never
by copying hex values.

## Build and validate

```bash
npm run build-libraries      # rebuild @streampipes/* libs after changing them; the app consumes the built copy
ng test app                  # unit tests for the app (npm test also rebuilds the libraries first — slow)
ng test @streampipes/shared-ui
npm run format:fix && npm run lint:fix
npm run i18n:check           # extract + validate translations; CI fails on drift
npm run build                # production build, includes i18n:validate
```

CI (`pr-validation.yml`) runs `npm run i18n:check`, `npm run format`, `npm run lint` and the
production build. Run the UI with the **Debug UI** configuration in `.vscode/launch.json`
(port 8082, proxied to the core) or `npm start`.

## Page composition

Page-level views are `sp-basic-view` (with `hideNavbar`) → `sp-page-header` → optional
`sp-page-nav-tabs` → content. `sp-basic-nav-tabs` is legacy: do not introduce it, and
replace it when migrating a view. Markup is in `STYLEGUIDE.md` ("Basic Layouts"); the rules
are in `DESIGN.md` ("Layout").

## Rules not covered by DESIGN.md

- Every user-facing string goes through `| translate` and lives in
  `ui/deployment/i18n/{en,de,pl}.json`. Add keys via `npm run i18n:check`, which extracts
  and formats the files; do not hand-order them.
- Layout with the `@ngbracket/ngx-layout` directives already used across the app
  (`fxLayout`, `fxFlex`, `fxLayoutAlign`, `fxLayoutGap`) rather than new CSS layout wrappers.
  (This is the maintained fork of Angular Flex-Layout; do not add `@angular/flex-layout`.)
- Prefer `rem`, CSS variables, existing utility classes and token-derived values over raw
  pixels. Pixels are fine for borders, icons, canvas/SVG coordinates and where Angular
  Material or browser APIs require them.
- No inline `style` attributes unless the value is dynamic.
- Before adding a CSS class or a component, check `shared-ui`, the utility classes in
  `ui/src/scss/sp/` and the styleguide for an existing one.
- Feature logic lives in `ui/src/app`; reusable building blocks in the library projects.

## Change safety

- Preserve routing and guard behaviour in `ui/src/app/_guards`.
- `@streampipes/platform-services` exports are a compatibility boundary (see its guide).
- Prefer incremental changes over structural rewrites of a feature area.
