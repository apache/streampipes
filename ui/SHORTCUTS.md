## Completly new feature

- Added edit and delete button in pipeline view
  - 3 dots -> edit pipeline -> forwards to edit mode
  - 3 dots -> delete pipeline -> pops up delete confirmation dialog

- Added forward and back button (+shortcut) in edit window of dashboard/charts/pipeline

## Implemented Shortcuts

- `Ctrl/Cmd + S` in chart edit view
  - Applies in `ChartViewComponent` when `editMode` is active.
  - Action: saves the current chart/data view.

- `E` in dashboard panel
  - Applies in `DashboardPanelComponent` when user has write privileges and is not already editing.
  - Action: enters dashboard edit mode.

- `Ctrl/Cmd + S` in dashboard panel
  - Applies in `DashboardPanelComponent` when `editMode` is active.
  - Action: persists dashboard changes.

- `Ctrl/Cmd + S` in pipeline assembly
  - Applies in `PipelineAssemblyComponent` when not readonly and pipeline model is present.
  - Action: opens pipeline save flow (`submit()`).

- `Delete` and `Backspace` in pipeline editor
  - Applies in `PipelineComponent` (non-readonly).
  - Action: deletes the currently hovered pipeline element.

- `Escape` in table widget filter dropdown
  - Applies in `TableWidgetComponent` when a column filter dropdown is open.
  - Action: closes the open filter dropdown.

- `Ctrl/Cmd + F` in table widget filter dropdown
  - Applies in `TableWidgetComponent` when a column filter dropdown is open.
  - Action: focuses/selects the filter search input instead of browser find.

- Dialog keyboard behavior
  - `Escape`: closes shared overlay dialogs via `DialogService` (unless `disableClose`).
  - `Enter`: confirms `ConfirmDialogComponent`.

## Shortcut Scope / Level

- **Central service level**: `KeyboardShortcutService` listens globally and dispatches to registered shortcut sets.
- **Component level**: each component opts in by registering shortcuts in its lifecycle.
- **Local widget level**: some shortcuts are handled directly with `@HostListener` inside a specific component.

## How To Keep Shortcuts in Future Components

When creating a new component, shortcuts are **not automatic** unless you wire them.

1. Inject `KeyboardShortcutService`.
2. Register shortcuts in init (`register('unique-id', [...])`).
3. Unregister in destroy (`shortcutReg?.unregister()`).
4. Guard actions by context (e.g., `editMode`, permissions, readonly).
5. Prefer component-specific handlers only when behavior is local to a widget.

## Key Files To Extend

- Primary entry point: `ui/projects/streampipes/shared-ui/src/lib/services/keyboard-shortcut.service.ts`
- Current registrations:
  - `ui/src/app/chart/components/chart-view/chart-view.component.ts`
  - `ui/src/app/dashboard/components/panel/dashboard-panel.component.ts`
  - `ui/src/app/editor/components/pipeline-assembly/pipeline-assembly.component.ts`
  - `ui/src/app/editor/components/pipeline/pipeline.component.ts`
- Local handlers:
  - `ui/src/app/chart-shared/components/charts/table/table-widget.component.ts`
