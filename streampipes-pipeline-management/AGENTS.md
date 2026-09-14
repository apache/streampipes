# AGENTS Guide (Pipeline Management)

## Scope

Applies to `streampipes-pipeline-management/`. Root `AGENTS.md` applies as well; build and
validation commands live there.

## Module intent

Core pipeline domain behaviour: element matching, pipeline verification, graph construction,
execution orchestration, pipeline-element migrations, setup tasks, permission handling.

## High-risk areas

- Verification and matching (`manager/verification`, `manager/matching`, `manager/matching/v2`).
  Keep these deterministic and side-effect free.
- Graph construction and output schemas (`manager/data/PipelineGraph*`). Output schema
  behaviour is user-visible in the pipeline editor; keep it backward compatible.
- Execution tasks and status updates (`manager/execution`).
- Pipeline-element migrations (`manager/migration`). Surface partial failures; do not leave
  a pipeline in an unknown migration state.

## Rules

- Creating, updating and deleting pipelines must keep the permission and ownership
  behaviour that already exists; permissions are created and removed alongside the entity.
