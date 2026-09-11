# AGENTS Guide (REST API)

## Scope

Applies to `streampipes-rest/`. Root `AGENTS.md` applies as well; build and validation
commands live there.

## Module intent

- HTTP resource layer under `rest/impl/` (`admin`, `connect`, `dashboard`, `datalake`,
  `pe`, `runtime`, and top-level resources). Security-related endpoints in `rest/security`.
- Resources are thin: request parsing, authorization, delegation to a `*-management`
  module, response mapping. Business logic does not live here.

## Recipe: add an endpoint

1. Put the class in the fitting `rest/impl/<area>/` package, annotated
   `@RestController` and `@RequestMapping("/api/v2/<path>")`, extending
   `AbstractAuthGuardedRestResource` (from `streampipes-rest-core-base`). For plain CRUD
   over a stored entity, extend `DefaultCRUDRestResource` / implement `CRUDResource` instead
   of re-implementing list/get/create/update/delete.
2. Guard every method with `@PreAuthorize`. Patterns used throughout the module:
   `"this.hasReadAuthority()"`, `"this.hasWriteAuthority()"`, and object-level
   `"this.hasWriteAuthority() and hasPermission(#pipelineId, 'WRITE')"`. Object-level
   checks are evaluated by `SpPermissionEvaluator`; do not bypass it.
3. Delegate to the management module and return with the base-class helpers
   (`ok(...)`, `badRequest(...)`, `notFound()`); do not build `ResponseEntity` by hand.
4. Add `@Operation(summary = ..., tags = {...})` so the endpoint appears in the OpenAPI spec.
5. If the UI needs it, add the client call in
   `ui/projects/streampipes/platform-services/` (see that guide). If the endpoint changes
   or adds model classes, regenerate the TypeScript models.

## Rules

- Preserve endpoint paths, verbs, status codes and payload shapes unless the task changes
  them; the UI, the Java/Python/Go clients and users' scripts depend on them.
- Apply the existing error-handling and status-code conventions; no silent behaviour changes.
