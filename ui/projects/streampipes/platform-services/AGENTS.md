# AGENTS Guide (UI Platform Services)

## Scope

Applies to `ui/projects/streampipes/platform-services/`.

## Module Intent

- API client and platform-level model/query/service layer for the UI.
- Public surface is exported via `ui/projects/streampipes/platform-services/src/public-api.ts`.

## Best Practices

- Keep services transport-focused (HTTP/query/model mapping), not feature-UI specific.
- Treat exports in `src/public-api.ts` as compatibility-sensitive.
- Keep generated model files in `src/lib/model/gen/` stable; avoid manual edits unless regeneration is part of the task.
- Prefer typed request/response objects and explicit model mapping over `any`.

## Validation

- `ng build @streampipes/platform-services`
- `ng test @streampipes/platform-services`
