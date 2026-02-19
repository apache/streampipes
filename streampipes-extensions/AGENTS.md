# AGENTS Guide (Extensions Aggregator)

## Scope
Applies to `streampipes-extensions/` and all its submodules unless overridden.

## Module Intent
- Parent for adapters/connectors/processors/sinks and extension bundle modules.

## Best Practices
- Keep changes narrowly scoped to the affected extension submodule.
- Avoid broad multi-extension edits unless explicitly requested.
- Preserve extension metadata/resources (`documentation.md`, `strings.*`, icons) for changed elements.
- Keep migration classes and tests aligned when changing extension model versions.
- Preserve bundle startup/packaging behavior in `streampipes-extensions-all-*` modules.

## Build Notes
- Parent uses `streampipes-maven-plugin`; avoid changing plugin behavior unless explicitly required.

## Validation
- Run tests/build from changed submodule(s), for example:
  - `mvn -pl streampipes-extensions/<submodule> -am test`
