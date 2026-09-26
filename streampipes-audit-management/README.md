<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

-->

# Audit recording — initial implementation

This increment implements pluggable typed event definitions, best-effort recording,
a dedicated Influx append adapter, adapter-creation and authentication instrumentation,
admin-only status and browsing endpoints, and a viewer under Settings → Audit Log.
Export, editable audit policies, retention/S3 archival and the remaining event catalog
are not implemented yet.

## Configuration

Auditing is disabled by default for existing installations. To enable recording:

```text
SP_AUDIT_ENABLED=true
SP_AUDIT_STORAGE_PROVIDER=influx
SP_AUDIT_QUEUE_CAPACITY=1024
SP_AUDIT_SHUTDOWN_TIMEOUT_SECONDS=10
SP_AUDIT_INFLUX_URL=http://influxdb:8086
SP_AUDIT_INFLUX_ORG=sp
SP_AUDIT_INFLUX_DATABASE=streampipes_audit
SP_AUDIT_INFLUX_TOKEN=<deployment secret with audit provisioning and read/write permissions>
```

These settings use the typed `Environments` accessors in `streampipes-commons`,
with defaults registered in `Envs`. Audit payload validation and Influx details
serialization use Jackson, omitting optional null properties.

When auditing is enabled, the audit worker checks storage at startup, even before
the first event. For the supported InfluxDB 2.x deployment it resolves the configured
organization, creates the named bucket if missing, and creates its default InfluxQL
`autogen` mapping if missing. The organization must already exist. Existing bucket
retention and mappings are not modified; a conflicting mapping fails initialization.
New buckets have no automatic expiration; retention management remains future work.
InfluxDB 2.6's structured 404 response for a missing named bucket is treated as
absence and triggers creation; authentication failures and other lookup errors do not.
Reads and writes use that same default mapping and explicit audit credentials.
The token needs organization/bucket/mapping read permissions, bucket/mapping creation
permissions when missing, and audit data read/write permissions.

Provisioning runs off the Core startup thread, with a five-second timeout per HTTP
request. Failure increments the recording failure count and leaves status degraded;
the next event retries initialization before writing. Recovery without further
events requires a restart. Bucket creation followed by a failed mapping request is
safe to retry; the existing bucket is reused. No provisioning occurs when disabled.
Use only one active core writer for this bucket, including during deployment.

The measurement is `audit_events`; its only tag is `event_type`. Fields are
`event_id`, `outcome`, `actor`, optional `resource_type` and `resource_id`, and optional JSON `details`.
Both resource fields are optional; resource-free events, such as login, pass null
as the resource ID and use an event definition without a resource type. IDs are stored unchanged, including any colons. REST uses `resourceType`
and `resourceId`. The former combined `resource` field is not read or written;
this development schema change requires recreating the audit bucket.
A monotonic nanosecond timestamp keeps events separate even within the same
millisecond. Startup watermark lookup is lazy and precision-preserving, and an
unavailable lookup prevents writes until a subsequent attempt succeeds.
When timestamp allocation adjusts the logical time, `details.recorded_at`
preserves the original timestamp. Details providers must not use this reserved key.

Publication validates and serializes a bounded snapshot, then enqueues without
waiting for storage. One dedicated worker owns synchronous storage appends. The
queue holds at most 1024 waiting events by default, plus one active event. Full or
closed queues reject the newest event and increment dropped/failure counters;
they never run writes on the publishing thread. UUID, actor and logical time are
captured before enqueueing, and later changes to caller-owned details cannot alter
the snapshot. Provider detail types must support Jackson serialization and
deserialization (records are recommended).

Shutdown stops acceptance and drains for at most the configured deadline (10 seconds
by default), then discards remaining queued events and interrupts the worker.
An active unacknowledged write is counted as unconfirmed/dropped even if the server
ultimately persisted it. The worker closes its store when it exits; Spring does
not independently close the store. An adapter must support interruption/bounded
I/O for prompt worker cleanup. The queue is in memory: process crashes can lose
accepted events, and publication never promises durable capture.

Each worker append acknowledges its HTTP write synchronously, with at most two attempts
using identical timestamp and payload. The initial adapter supports retries only
inside that append call; callers must not resubmit an exhausted or completed
append as a new call. Crash-spanning replay and UUID lookup/deduplication across
independent calls are future storage-contract work. Timeouts are five seconds
per HTTP write attempt; the watermark query uses five-second connection/read/write
timeouts. The first append can also require that query. Clock-rollback detection
compares the watermark with the current clock, not an older queued event time.
The reusable `InfluxWriteTransport` shares the existing Influx HTTP/auth helpers,
with explicit audit connection settings and without automatic
batching, redirects or connection retries. A separate audit provisioner uses the v2
bucket/DBRP APIs. Audit retry policy stays in the store.
Business actions retain their original outcome if recording fails. This is not
transactional or crash-proof capture.

`GET /api/v2/admin/audit/status` requires `ROLE_ADMIN`. It reports enablement,
last-write availability, failure count, queue depth, write-failure and dropped-event
counts, a degraded flag and the last successful write. Queue depth excludes the
active write. Dropped events keep degraded status set for this process lifetime;
availability can recover after a successful write. Last-success time is updated
only after acknowledgement. Availability
is based on write attempts, not a background health probe. Enabled but missing
connection settings remains unavailable and records failures; correct configuration
and restart. Unknown provider IDs fail startup. Storage exceptions are logged by
class only; payloads, tokens and raw storage errors are not logged.

## Browsing audit logs

Administrators can open **Settings → Audit Log** to filter by the last 24 hours,
7 days or 30 days, exact event type, actor ID and outcome. The table uses server
pagination (50 rows). Clicking a row (or pressing Enter/Space on it) opens a slide-in
details panel and loads JSON details on demand. The panel resolves the current actor
username, falling back to the recorded ID for deleted or unavailable accounts. This
is a current display label, not a historical username snapshot. Rows also offer the
existing feature-card preview when the audit `resourceType` matches a registered
card type. No asset assignment is required; preview activation does not open audit
details. Card availability is determined by the registry, not an existence check. Registered types
are suggestions; historical types can still be entered. Disabled recording,
loading, empty results, storage failures and unavailable details have separate states.
A toolbar action opens a separate slide-in recording-status panel with fresh counters
and a refresh action. Deployment settings remain environment variables.

All endpoints below require `ROLE_ADMIN`, including direct HTTP access:

| GET endpoint under `/api/v2/admin/audit` | Result |
| --- | --- |
| `/status` | Recording status and counters |
| `/event-types` | Sorted registered event type IDs |
| `/events` | Summary page and opaque `nextCursor` |
| `/events/{eventId}?locator=...` | One event and its JSON details |

List requests require ISO-8601 `from` (inclusive) and `to` (exclusive), spanning at
most 31 days. Optional filters are `eventType`, `actor`, `outcome`, `limit` (default
50, maximum 200), and `cursor`. Keep the same filters and time bounds when following
a cursor. Ordering is newest stored timestamp first. The list excludes JSON details.
`storedAt` preserves the physical timestamp's nanosecond precision; detail responses
also expose the original `recordedAt`, removing the internal reserved JSON key.

For this increment, detail lookup requires the opaque `locator` returned with each
summary as well as its UUID; UUID-only historical lookup remains future work.
The adapter verifies that the stored UUID matches the locator. Invalid queries or
cursors return 400, missing events return 404 and storage failures return 503.
Concurrent retention may remove an event between listing and opening its details.

`AuditEventReader` is independent of Influx. The Influx implementation uses the
shared query transport with explicit projection, bounded time predicates and
nanosecond keyset pagination. Reads do not acquire the append worker's lock.
Provider-specific cursors and locators must not be inspected by clients or reused
when changing storage providers. This is browsing, not snapshot-isolated export.

## Built-in event ownership

`streampipes-audit-api` contains reusable contracts only. The new
`streampipes-audit-events` module owns StreamPipes' `StandardAuditEvents`, typed
payloads, reason codes and small domain recorders. It depends only on the audit API.
Management and REST/authentication operations use these recorders; Core registers
the provider and wires the authentication recorder. Generic audit management and
Influx production code do not depend on StreamPipes' event catalog.

| Event | Resource type | Captured outcomes |
| --- | --- | --- |
| `sp.adapter.create` | `adapter` | Success, failure, partial completion |
| `sp.auth.login` | None | Success or authentication rejection |
| `sp.auth.logout` | None | Successful explicit logout with a resolved principal |

## Resource-free authentication events

A definition without its third constructor argument has no resource type:

```java
public static final AuditEventDefinition<AuthenticationDetails> AUTH_LOGIN =
    new AuditEventDefinition<>("sp.auth.login", AuthenticationDetails.class);
```

`AuthenticationAuditRecorder.loggedIn(actorId, AuthenticationMethod.PASSWORD)`
publishes it with a null resource ID. Both resource fields are omitted in storage.
Login details contain only `authMethod` (`PASSWORD` or `OAUTH2`). Logout has no
details because its endpoint does not identify the original authentication method.
See [login.json](examples/login.json) for an illustrative logical event.

Local login is recorded after token issuance and user update; OAuth login after
successful redirect. Authentication rejections record `DENIED` with actor `unknown`;
submitted identifiers, credentials, tokens and raw exception messages are excluded.
Infrastructure failures outside authentication rejection are not login events.
Explicit logout resolves its actor from a valid refresh cookie or authenticated
security context before clearing it. Expired/revoked cookies alone do not establish
an audit actor; existing token revocation behavior is preserved. Anonymous logout,
token refresh, token expiry and closing a browser do not emit login/logout events.

## Adapter creation

`AdapterMasterManagement.addAdapter` uses its constructor-injected
`AdapterAuditRecorder` to publish `sp.adapter.create` after
adapter and data-stream/permission work. Success, thrown failures and observed
partial completion produce one outcome record. A rejected stream installation
is recorded as partial without changing the existing REST behavior. Both regular
and compact REST creation routes use this manager. Generic persistence updates,
adapter start/stop and document saves do not emit additional audit events.

The recorder owns event selection and safe payload construction. The adapter
operation owns outcome decisions, including whether a failure happened after
persistence. Construction sites explicitly pass a recorder backed by the configured
`AuditService`; disabled deployments pass the existing no-op service, never null.
The resource-manager audit dependency and recorder constructor reject null wiring.

```java
adapterAudit.created(principalSid, adapterId, streamId, outcome,
    AdapterCreationReason.STREAM_CREATION_FAILED);
```

Actor fallback is centralized in `DefaultAuditService`: null and blank actors become
`unknown`. Explicit principal IDs and system actors are preserved. Background jobs
should provide an explicit identity such as `system:recovery`; the worker does not
read a thread-local security context. Actor, timestamp and details are captured
before enqueueing.

Details contain only the generated stream ID and an optional `AdapterCreationReason`.
Its enum names retain the existing serialized reason codes. Future domains should
provide small recorders of their own, reusing the generic publication API, with safe
typed details and reasons. Keep outcome decisions at business-operation boundaries;
do not emit events from generic document saves or duplicate them in REST layers.
No adapter configuration, credential, raw exception or submitted payload is copied.
The sample [adapter-created.json](examples/adapter-created.json) is illustrative
logical JSON, not data automatically inserted at startup or a current query response.

Downstream applications register an `AuditEventProvider` bean and publish their
own typed `AuditDetails` through `AuditService`. Providers explicitly control safe
payload types; registering a type does not automatically instrument business code.
Each Core application explicitly imports `AuditConfiguration` in its bootstrap.
`ExtensionServiceRequestConfiguration` receives `AuditService` through injection
and does not enable auditing itself. Legacy direct
`SpResourceManager` constructors remain audit-disabled; production Spring wiring
passes the configured service explicitly.

## Validation

```sh
mvn -pl streampipes-service-core -am test \
  -Dtest=DefaultAuditServiceTest,InfluxAuditEventReaderTest,InfluxAuditDatabaseProvisionerTest,InfluxAuditEventStoreTest,InfluxQueryTransportTest,InfluxWriteTransportTest,AdapterMasterManagementTest,AuditConfigurationTest,AuthenticationAuditTest,OAuth2AuditTest,RefreshTokenAuditActorTest \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Tests cover startup provisioning, existing bucket preservation, partial provisioning
recovery, permission/conflict failures, registration, downstream definitions, payload bounds, outage isolation,
queue overflow, mutable-payload isolation, bounded shutdown, HTTP timeouts and
retry identity, nanosecond watermark preservation, delayed events versus clock
rollback, adapter outcomes and admin
status and query access, stable filtered pagination, exact timestamps, lazy details,
invalid cursors and storage errors versus missing records. Nine UI tests cover paging, dialog opening, resource preview registration/identity, username resolution/fallback,
request cancellation, disabled recording and error recovery. HTTP tests use an isolated local test server, not a live Influx instance.
The production Influx version/default mapping still needs an integration smoke test.

## Registering Bytefabrik events

Import `AuditConfiguration` and a Bytefabrik provider configuration directly from
`BytefabrikCoreApplication`. The provider configuration registers a bean:

```java
@Configuration
public class BytefabrikAuditConfiguration {
  public record NotebookCreatedDetails(String name) implements AuditDetails { }

  public static final AuditEventDefinition<NotebookCreatedDetails> NOTEBOOK_CREATE =
      new AuditEventDefinition<>("bytefabrik.ai-notebook.create", NotebookCreatedDetails.class, "ai-notebook");

  @Bean
  public AuditEventProvider bytefabrikAuditEvents() {
    return () -> List.of(NOTEBOOK_CREATE);
  }
}
```

The definition declares the resource type once; callers only supply its ID.
The two-argument definition constructor declares a resource-free event, such as login.
Supplying an ID for such a definition is rejected by best-effort recording.
A resource-associated definition can still omit the ID when it is not known.

The domain module should own event definitions/details; the Core configuration
only registers its provider. The compact example keeps both together for clarity.
Inject `AuditService` into the notebook management service and publish once after
the logical creation succeeds:

```java
auditService.record(BytefabrikAuditConfiguration.NOTEBOOK_CREATE,
    AuditOutcome.SUCCEEDED, actorId, notebookId,
    new BytefabrikAuditConfiguration.NotebookCreatedDetails(safeNotebookName));
```

`AuditConfiguration` collects all `AuditEventProvider` beans, including StreamPipes'
standard provider. Duplicate IDs fail initialization; publishing an unregistered
definition is rejected and reported. Registering an event does not automatically
instrument domain operations. Use only explicitly safe details, with Jackson
round-trip support, and publish from the operation boundary rather than generic
persistence hooks. Provider code depends on `streampipes-audit-api`, not Influx.
