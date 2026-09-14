# AGENTS Guide (Extensions Aggregator)

## Scope

Applies to `streampipes-extensions/` and all submodules. Root `AGENTS.md` applies as well.

## Module intent

- Parent for adapters (`streampipes-connect-adapters*`, `streampipes-connectors-*`),
  processors (`streampipes-processors-*`), sinks (`streampipes-sinks-*`) and the runnable
  bundles (`streampipes-extensions-all-jvm`, `streampipes-extensions-all-iiot`,
  `streampipes-extensions-iiot-minimal`).
- The parent pom uses `streampipes-maven-plugin`; do not change plugin behaviour unless the
  task requires it.

## Recipe: add a processor, sink or adapter

1. Implement the element in the fitting submodule (or a new one following the naming
   above). Processors and sinks implement the `IStreamPipesPipelineElement` family from
   `streampipes-extensions-api`; adapters extend `StreamPipesAdapter`.
2. Register it in the submodule's `*ExtensionModuleExport` (implements
   `IExtensionModuleExport`): `pipelineElements()` for processors and sinks, `adapters()`
   for adapters, `migrators()` for model migrators. Example:
   `streampipes-processors-filters-jvm/.../FilterExtensionModuleExport.java`.
3. Add the element's resources under `src/main/resources/<element app id>/`:
   `documentation.md`, `documentation.de.md`, `icon.png`, `strings.en`, `strings.de`.
   These are shipped to users and rendered in the UI; keep them in sync with the element's
   parameters.
4. A new **module** must also be registered in each bundle it belongs to, for example
   `streampipes-extensions-all-jvm/.../AllExtensionsInit.provideServiceDefinition()`
   (`registerModules(...)`). A new element inside an existing module needs no bundle change.
5. If you bump an element's model version, ship the matching `IModelMigrator` in
   `migrators()` and a test for it.
6. Validate with `mvn -pl streampipes-extensions/<submodule> -am test`.

## Rules

- Keep changes scoped to the affected submodule; no multi-extension sweeps unless asked.
- Preserve existing element resources when changing an element.
- Preserve bundle startup and packaging behaviour in the `streampipes-extensions-all-*` modules.
