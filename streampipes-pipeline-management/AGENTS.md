# AGENTS Guide (Pipeline Management)

## Scope
Applies to `streampipes-pipeline-management/`.

## Module Intent
- Core pipeline domain behavior: matching, verification, graph construction, execution orchestration, migrations, setup tasks, permission handling.

## High-Risk Areas
- Verification/matching pipelines (`manager/verification`, `manager/matching`, `manager/matching/v2`).
- Graph logic (`manager/data/PipelineGraph*`).
- Execution task pipeline and status updates (`manager/execution`).
- Pipeline element migrations (`manager/migration`).

## Best Practices
- Keep verification deterministic and side-effect free where intended.
- Keep pipeline graph and output schema behavior backward compatible.
- Treat migration failures explicitly; do not hide partial migration states.
- Preserve permission and ownership behavior when creating/updating/deleting pipelines.

## Validation
- `mvn -pl streampipes-pipeline-management -am test`
