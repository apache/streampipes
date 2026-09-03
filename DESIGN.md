---
name: "Apache StreamPipes"
description: "A calm control-room system for clear and dependable industrial data work."
colors:
  deep-signal-indigo: "#1b1464"
  deep-signal-indigo-dark: "#83a3de"
  operational-green: "#39b54a"
  operational-green-dark: "#7ac98a"
  telemetry-blue: "#0062a0"
  canvas: "#fafafa"
  surface: "#ffffff"
  surface-high: "#eeeeee"
  surface-highest: "#e0e0e0"
  ink: "#1a1a1a"
  ink-muted: "#5e5e5e"
  night-canvas: "#121212"
  night-surface: "#1e1e1e"
  night-ink: "#e0e0e0"
  info: "#2563eb"
  success: "#16a34a"
  warning: "#f59e0b"
  error: "#dc2626"
  data-view: "#60a5fa"
  dashboard: "#4f81bd"
  adapter: "#b48c5f"
  data-source: "#eab308"
  pipeline: "#4ab69b"
  measurement: "#38b2ac"
  file: "#a855f7"
  processor: "#009688"
  sink: "#3f51b5"
typography:
  display:
    fontFamily: "Roboto, Arial, sans-serif"
    fontSize: "clamp(1.7rem, 1.55rem + 0.55vw, 2.1rem)"
    fontWeight: 700
    lineHeight: 1.2
  headline:
    fontFamily: "Roboto, Arial, sans-serif"
    fontSize: "clamp(1.35rem, 1.28rem + 0.38vw, 1.7rem)"
    fontWeight: 600
    lineHeight: 1.2
  title:
    fontFamily: "Roboto, Arial, sans-serif"
    fontSize: "clamp(1.22rem, 1.14rem + 0.3vw, 1.4rem)"
    fontWeight: 600
    lineHeight: 1.2
  body:
    fontFamily: "Roboto-Regular, Arial, sans-serif"
    fontSize: "clamp(0.96rem, 0.92rem + 0.16vw, 1.05rem)"
    fontWeight: 400
    lineHeight: 1.4
  label:
    fontFamily: "Roboto-Regular, Arial, sans-serif"
    fontSize: "clamp(0.83rem, 0.79rem + 0.12vw, 0.9rem)"
    fontWeight: 500
    lineHeight: 1.4
    letterSpacing: "-0.01em"
rounded:
  xs: "0.25rem"
  sm: "0.5rem"
  md: "0.75rem"
  lg: "1rem"
  pill: "999px"
spacing:
  2xs: "clamp(0.15rem, 0.12rem + 0.15vw, 0.25rem)"
  xs: "clamp(0.25rem, 0.22rem + 0.2vw, 0.4rem)"
  sm: "clamp(0.4rem, 0.35rem + 0.25vw, 0.6rem)"
  md: "clamp(0.6rem, 0.5rem + 0.35vw, 0.9rem)"
  lg: "clamp(0.9rem, 0.75rem + 0.4vw, 1.2rem)"
  xl: "clamp(1.2rem, 1rem + 0.55vw, 1.6rem)"
  2xl: "clamp(1.6rem, 1.3rem + 0.7vw, 2.2rem)"
components:
  button-primary:
    backgroundColor: "{colors.deep-signal-indigo}"
    textColor: "#cccccc"
    typography: "{typography.label}"
    rounded: "{rounded.sm}"
    padding: "0 1.3rem"
    height: "2.5rem"
  button-secondary:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.deep-signal-indigo}"
    typography: "{typography.label}"
    rounded: "{rounded.sm}"
    padding: "0 1.3rem"
    height: "2.5rem"
  input-outlined:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    typography: "{typography.body}"
    rounded: "{rounded.sm}"
    padding: "0.5rem 1rem"
    height: "2.5rem"
  status-label:
    backgroundColor: "{colors.surface-high}"
    textColor: "{colors.ink}"
    typography: "{typography.label}"
    rounded: "{rounded.pill}"
    padding: "0.2rem 0.55rem"
---

# Design System: Apache StreamPipes

## 1. Overview

**Creative North Star: "The Calm Control Room"**

StreamPipes should feel like a calm, well-organized control room: technically
capable, operationally trustworthy, and immediately legible. Its visual system
uses restrained surfaces, compact controls, clear hierarchy, and semantic
color so industrial domain experts can work confidently with complex data and
stateful workflows.

The visual identity is deliberately subordinate to the task. Beauty comes from
alignment, rhythm, consistent interaction states, and an uncluttered reading
order—not from decoration. The interface should never feel like a flashy
consumer product, a dense legacy enterprise application, or a collection of
unfamiliar custom controls.

The palette is deployment-configurable by design. Default brand values live in
`ui/deployment/theme/` and are copied into `ui/src/scss/custom-theme/` by the
prebuild script; operators may replace them through `THEME_VARIABLES` and
`THEME_COLORS`. Components must consume semantic CSS variables rather than
embedding the default hex values documented here.

**Key Characteristics:**

- Restrained, information-first product surfaces in light and dark modes.
- Compact but readable density suited to dashboards, editors, forms, and tables.
- Deep indigo and operational green defaults, with semantic state and data colors.
- Familiar Angular Material interaction patterns refined through shared tokens.
- Responsive structure, visible focus, reduced-motion support, and translatable copy.

## 2. Colors

The default palette pairs grounded indigo with clear operational green and a
telemetry-blue tertiary, set against neutral tonal layers. These are default
role values, not tenant-locked brand colors.

### Primary

- **Deep Signal Indigo** (`#1b1464`): Default primary action, focus, selection,
  step, and identity color. Use through `--color-primary` or Material system
  roles so operator themes remain effective.
- **Deep Signal Indigo—Dark** (`#83a3de`): Accessible primary role on dark
  surfaces, exposed as `--color-primary-dark`.

### Secondary

- **Operational Green** (`#39b54a`): Default navigation and complementary
  brand role. It communicates active, connected operation without replacing
  the distinct success token.
- **Operational Green—Dark** (`#7ac98a`): Dark-mode counterpart for the
  secondary role.

### Tertiary

- **Telemetry Blue** (`#0062a0`): Material tertiary role for secondary emphasis
  and data-oriented accents when primary and semantic colors are already occupied.

### Neutral

- **Canvas** (`#fafafa`): Default page background and low surface layer.
- **Surface** (`#ffffff`): Primary content, input, table, and dialog surface.
- **Surface High** (`#eeeeee`) and **Surface Highest** (`#e0e0e0`): Dividers,
  grouped regions, inactive controls, and structural layering.
- **Ink** (`#1a1a1a`): Default light-mode foreground.
- **Muted Ink** (`#5e5e5e`): Supporting copy that still needs readable contrast.
- **Night Canvas** (`#121212`), **Night Surface** (`#1e1e1e`), and **Night Ink**
  (`#e0e0e0`): Dark-mode background, surface, and foreground foundations.
- **Info** (`#2563eb`), **Success** (`#16a34a`), **Warning** (`#f59e0b`), and
  **Error** (`#dc2626`): Dedicated semantic roles. Always pair them with text,
  iconography, or shape; never communicate state through hue alone.
- **Data roles:** Data View (`#60a5fa`), Dashboard (`#4f81bd`), Adapter
  (`#b48c5f`), Data Source (`#eab308`), Pipeline (`#4ab69b`), Measurement
  (`#38b2ac`), File (`#a855f7`), Processor (`#009688`), and Sink (`#3f51b5`).
  These distinguish domain objects and must not be repurposed as generic state colors.

### Named Rules

**The Theme Boundary Rule.** Default values belong in `ui/deployment/theme/`.
Feature and shared-component styles use semantic CSS variables; do not edit the
generated `ui/src/scss/custom-theme/` output as the source of truth or hard-code
default brand hex values in components.

**The Operational Signal Rule.** Primary color marks action and selection;
semantic colors communicate state; domain colors identify object types. Do not
collapse these three vocabularies into one ambiguous accent system.

## 3. Typography

**Display Font:** Roboto (with Arial and sans-serif fallback)  
**Body Font:** Roboto Regular (with Arial and sans-serif fallback)  
**Label/Mono Font:** Roboto Regular for labels; the configured editor font for code

**Character:** A single pragmatic sans-serif family keeps dense product screens
coherent and familiar. Weight, size, spacing, and surface hierarchy—not a
decorative font pairing—create emphasis.

### Hierarchy

- **Display** (700, `--font-size-3xl`, 1.2): Page-level headings and the largest
  in-product titles; avoid marketing-scale typography in working screens.
- **Headline** (600, `--font-size-2xl`, 1.2): Primary page headers and major panels.
- **Title** (600, `--font-size-xl`, 1.2): Section titles and prominent card headers.
- **Body** (400, `--font-size-md`, 1.4; 1.6 for prose): Instructions, descriptions,
  and primary content. Keep explanatory prose within roughly 65–75 characters.
- **Label** (500, `--font-size-sm`, `-0.01em`): Buttons, form labels, table controls,
  and compact UI. Uppercase is reserved for real categorical labels, not every heading.

### Named Rules

**The One Working Voice Rule.** Use the shared Roboto scale and weight tokens
across labels, data, navigation, and headings. Do not introduce display fonts or
component-local type scales into product workflows.

## 4. Elevation

The system is tonal and structural by default. Most hierarchy comes from
surface-container steps, one-pixel dividers, and selected-state tints. Shadows
are reserved for content that genuinely floats above the working plane—dialogs,
menus, popovers, and persistent navigation—not ordinary cards or buttons.

### Shadow Vocabulary

- **Overlay** (`0 12px 32px rgba(0, 0, 0, 0.16)`): Dialogs, menus, and other
  temporary layers above the application surface.
- **Navigation depth** (`0 10px 30px rgba(0, 0, 0, 0.25)` plus an inset divider):
  The persistent sidebar boundary; do not repeat this treatment on content panels.
- **Keycap** (`0 2px 6px rgb(0 0 0 / 18%)`): Keyboard-shortcut hints only.

### Named Rules

**The Flat Working Plane Rule.** Content surfaces are flat at rest. Use tonal
layers or a border for structure; use a shadow only when the element is spatially
above adjacent content.

## 5. Components

Shared components are compact, dependable, and familiar. Prefer the existing
`sp-*` and Angular Material patterns before creating a feature-local equivalent.

### Buttons

- **Shape:** Soft rectangle using `--radius-sm` (`0.5rem`).
- **Primary:** `mat-flat-button` with the theme primary fill, medium-weight label,
  `-0.01em` tracking, and `1.3rem` horizontal padding.
- **Hover / Focus:** Hover and active states mix the current theme color rather
  than introduce a new hue. Keyboard focus uses the shared three-pixel focus ring.
- **Secondary / Warning:** Secondary actions use a subtle primary-tinted surface;
  destructive actions use the dedicated error role. Both stay flat at rest.

### Chips

- **Style:** `sp-label` supports solid, soft, and outline variants with semantic
  or domain-specific color input. Default labels use a pill shape (`999px`) and
  compact horizontal padding.
- **State:** Use text and icons alongside color. Badge shapes are slightly tighter
  and heavier than general-purpose pills.

### Cards / Containers

- **Corner Style:** Shared surfaces generally use `--radius-sm` to
  `--radius-md` (`0.5rem`–`0.75rem`); large rounding is not part of the system.
- **Background:** Use Material surface and surface-container roles.
- **Shadow Strategy:** Flat for ordinary content; follow the elevation vocabulary
  for true overlays.
- **Border:** One-pixel subtle borders are acceptable for structural separation.
- **Internal Padding:** Prefer the shared `--space-md` through `--space-xl` scale.

### Inputs / Fields

- **Style:** Outlined Angular Material fields on the surface color, `2.5rem` high,
  with `--radius-sm`. Wrap feature inputs in `sp-form-field`; do not use floating labels.
- **Focus:** Primary-colored outline plus shared focus semantics.
- **Error / Disabled:** Error uses the Material error role; disabled fields shift
  to a higher neutral surface and reduce saturation without disappearing.

### Navigation

The persistent sidebar expands from `62px` to `260px`, groups product areas,
and uses icons, labels, hover fills, and `aria-current` for orientation. Motion is
short (`120–220ms`) and state-driven. Preserve collapse behavior and ensure the
operator-configured navigation colors retain readable foregrounds.

### Tables

Use `sp-table` for paging, sorting, grouping, selection, and action menus. Headers
are semibold on the base surface; rows use subtle hover and selected-state fills.
Dense tabular information is appropriate when hierarchy and keyboard focus remain clear.

### Alerts

Use `sp-alert-banner` for info, success, warning, and error messages. Each alert
combines a tinted background, semantic foreground, one-pixel border, icon, title,
and optional description or action.

## 6. Do's and Don'ts

### Do:

- **Do** use shared spacing, typography, radius, motion, surface, and semantic tokens.
- **Do** treat `ui/deployment/theme/` as the source of default theme values and
  preserve operator overrides through the prebuild pipeline.
- **Do** use `sp-basic-view`, `sp-basic-header-title-component`, `sp-split-section`,
  `sp-form-field`, `sp-table`, and alert components where their patterns apply.
- **Do** keep controls keyboard-operable, focus-visible, translatable, and clear
  without color; target WCAG 2.2 AA.
- **Do** communicate loading, empty, success, and error states without removing
  the user's workflow context.

### Don't:

- **Don't** hard-code the default indigo or green in feature components; users
  can supply their own styles through `THEME_VARIABLES` and `THEME_COLORS`.
- **Don't** create flashy consumer-product styling, decorative complexity, dense
  legacy enterprise interfaces, or unfamiliar controls that obscure standard actions.
- **Don't** trade clarity, consistency, or useful information density for visual novelty.
- **Don't** pair a decorative one-pixel card border with a broad soft shadow;
  ordinary content belongs on the flat working plane.
- **Don't** use colored side stripes, gradient text, decorative glass cards,
  oversized rounding, or ornamental motion.
- **Don't** bypass shared components with one-off local versions of buttons,
  fields, tables, navigation, alerts, or loading indicators.
