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

## Error contract

Every error response of the API is an RFC 9457 problem detail (`application/problem+json`)
with the HTTP status that describes the failure. `SpRestProblemHandler` in
`streampipes-rest-shared` is the only place that builds error bodies; it also runs in the
extensions services.

- Resources never return an error body themselves: no `ResponseEntity.status(...).body(...)`,
  no 200 with an `ErrorMessage`, no `SpLogMessage` or plain string bodies. Throw
  `SpProblemException` (`notFound`, `forbidden`, `badRequest`, `validationFailed`,
  `conflict`, `of(status, detail)`) and let the handler answer. `statusMessage` /
  `constructErrorMessage` in `AbstractRestResource` and `SpMessageException`,
  `SpNotificationException`, `BadRequestException` are deprecated; they still map to a
  problem detail but new code does not use them.
- Status codes: unknown id → 404; authenticated but not permitted → 403 (an empty 401 is
  reserved for failed authentication); rejected payload → 400 with `errors[]`; conflicting
  state (adapter still used by pipelines, duplicate id) → 409; an accepted operation that an
  extension service could not complete → 500 `operation-failed`; no extension service
  registered → 503. `AdapterException`, `SpLogMessageException`,
  `NoServiceEndpointsAvailableException`, `IllegalArgumentException` (400),
  `NoSuchElementException` (404), `AccessDeniedException` (403) and any other exception
  (500) are mapped by the handler, so a resource only catches what it turns into a better
  status or message.
- Body: `type` is a stable URN `urn:streampipes:problem:<slug>` (`ProblemType`), `title`
  the generic sentence of the type, `detail` the specific sentence a user or an LLM reads,
  `instance` the request path, `errors[]` entries of `{detail, pointer}` for individual
  rejected inputs. Problems raised from an `SpLogMessage` keep `level`, `cause` and
  `fullStackTrace` as extension members so the UI's exception viewers work unchanged.
  Extension members never reuse the reserved names.
- Success responses are unchanged: a 2xx never carries `success: false`.
  `PipelineOperationStatus` (pipeline start/stop) is an operation report, not an error
  envelope; its `success` flag describes the pipeline elements.
- A `@ControllerAdvice` that needs to answer before the shared handler (which ends with a
  catch-all for `Exception`) must declare a higher precedence, for example
  `@Order(Ordered.LOWEST_PRECEDENCE - 100)` as `ConnectRestExceptionHandler` does.
- Tests: `SpRestProblemHandlerTest` in `streampipes-rest-shared` pins the mapping; a
  resource test asserts the thrown `SpProblemException` and its `getStatus()`.
- Clients: the UI reads bodies through `problemDetailMessage` / `problemErrors` /
  `problemTypeOf` from `@streampipes/platform-services`; the Java client surfaces `detail`
  in `SpHttpErrorStatusCode`'s message; the Python and Go clients append the server
  message to their error.

## Rules

- Preserve endpoint paths, verbs, status codes and payload shapes unless the task changes
  them; the UI, the Java/Python/Go clients and users' scripts depend on them.
- Apply the error contract above; no silent behaviour changes.
