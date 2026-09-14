# AGENTS Guide (UI Platform Services)

## Scope

Applies to `ui/projects/streampipes/platform-services/`. `ui/AGENTS.md` and the root guide
apply as well.

## Module intent

API client and platform-level model, query and service layer for the UI. Public surface:
`src/public-api.ts`.

## Generated models — do not edit by hand

`src/lib/model/gen/streampipes-model.ts` and `streampipes-model-client.ts` are generated
from the Java model by `typescript-generator-maven-plugin`. When the Java model changes,
regenerate from the repository root:

```bash
./create-client-model.sh     # runs the generator for streampipes-model and streampipes-model-client and copies the output here
```

Commit the regenerated files together with the Java change.

## Rules

- Services stay transport-focused: HTTP calls, query building, model mapping. Feature UI
  logic belongs in `ui/src/app`.
- Typed request and response objects; no `any` in public signatures.
- `src/public-api.ts` is a compatibility boundary: export new services there; do not remove
  or rename exports without updating every consumer.

## Validate

```bash
ng build @streampipes/platform-services && ng test @streampipes/platform-services
npm run build-libraries      # so the app picks up the change
```
