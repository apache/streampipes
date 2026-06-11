<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to you under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Apache StreamPipes — Threat Model (v0 draft)

> **Status:** v0 draft produced by the ASF Security team (Michael Scovetta
> rubric, run with Claude Opus) for the Apache StreamPipes PMC to review,
> correct, and own. Every non-trivial claim is provenance-tagged
> *(documented)* / *(maintainer)* / *(inferred)*; the *(inferred)* tags are
> hypotheses, each with a matching question in §14. Written against the `dev`
> branch; revise on a new public-facing surface (a new REST surface, adapter
> class, auth mechanism, or broker integration), not on internal refactors.

## §1 — Purpose and consumers

This document describes the **implicit security contract** between Apache
StreamPipes and its downstream operators: what StreamPipes assumes about its
environment, what it upholds, what it leaves to the operator, and which
"syntactically possible" misuses fall outside the intended design. It serves
the **integrator/operator** (which threats they own) and the **triager**
(classifying a scanner/AI/CVE-style finding as valid, out of model, or
disclaimed by design — cite the section).

## §2 — What StreamPipes is

Apache StreamPipes is an **open-source Industrial IoT data platform** for
connecting industrial data sources, building real-time streaming pipelines,
and exploring time-series data *(documented: README)*. In-scope component
families *(documented: root `AGENTS.md` architecture boundaries; roles
inferred — §14 Q1)*:

| Component | Role | Primary surface |
| --- | --- | --- |
| **`streampipes-rest`** | the HTTP/REST front door — auth, user/resource management, pipeline + adapter control | network (the primary untrusted boundary) *(inferred — §14 Q1)* |
| **`streampipes-service-core`** | bootstrapping, **security**, migrations, scheduling *(documented: AGENTS.md)* | in-process |
| **`*-management` modules** | business/domain logic (pipelines, adapters, data lake) | in-process |
| **Connect adapters (extensions)** | ingest data from **external** industrial sources (OPC-UA, MQTT, HTTP, files, DB, …) | the external-data ingestion boundary *(inferred — §14 Q2)* |
| **Pipeline elements (processors/sinks)** | run processing logic over the event streams; deployable as extensions | in-process / extension-runtime code *(inferred — §14 Q2)* |
| **Message broker + data lake** | event transport (Kafka/MQTT/NATS-class) and time-series persistence | intra-deployment infra *(inferred — §14 Q3)* |
| **UI** (`ui/`) | browser client | client trust domain |

StreamPipes is **not** a single hardened process: it is a multi-service
deployment that depends on a message broker, a time-series/metadata store, and
the external data sources its adapters connect to *(inferred — §14 Q3)*.

## §3 — Adversaries in and out of scope

**In scope** *(inferred — §14 Q4)*:

1. An **authenticated-but-limited UI/REST user** trying to act outside their
   role/permissions (read other users' pipelines, escalate, reach the host).
2. A **network adversary** between the browser/clients and the REST layer, or
   between services, where transport security is not configured.
3. A **malicious external data source** an adapter connects to, feeding
   hostile payloads into the ingestion path (see §7 for the boundary).

**Out of scope** *(inferred — §14 Q5)*:

4. **An operator with host / container / broker / datastore access.** Anyone
   controlling the deployment infrastructure is not an adversary StreamPipes
   defends against → `OUT-OF-MODEL: adversary-not-in-scope`.
5. **A trusted admin** performing an authorized action (installing an
   extension, registering an adapter, changing config). A new path to a
   privilege already held is `OUT-OF-MODEL: equivalent-harm`.
6. **Bugs in the infrastructure StreamPipes orchestrates** — the broker, the
   datastore, the JVM, an upstream OPC-UA/MQTT library. Report upstream →
   `OUT-OF-MODEL: unsupported-component`.

## §4 — Trust boundaries

- **Client (UI/API) → `streampipes-rest`** is the primary boundary: requests
  are authenticated and authorized against the user/role model before acting
  *(inferred — §14 Q6)*. Whether request bodies, pipeline/adapter definitions,
  and configuration are treated as untrusted here is load-bearing for triage.
- **Adapter → external data source** is a distinct boundary: the data an
  adapter ingests is **attacker-influenceable** in a way the REST surface is
  not, and the question is what StreamPipes guarantees about parsing/handling
  hostile ingested data versus what it passes through to downstream pipeline
  elements *(inferred — §14 Q2)*.
- **Inter-service / broker** boundaries are assumed to run inside an
  operator-controlled, network-isolated deployment perimeter *(inferred —
  §14 Q3)*.

## §5 — What StreamPipes upholds (given §3/§4 assumptions)

*(all inferred — §14 Q7)*

- **Authentication** of UI/REST sessions via the configured mechanism before
  privileged actions.
- **Authorization** of actions against the user/role/permission model, scoped
  to the principal's owned/permitted resources.
- **Memory safety on well-formed input** to the JVM's extent (StreamPipes is
  primarily Java).

## §6 — What StreamPipes leaves to the operator

*(inferred — §14 Q8)*

- **Transport security (TLS)** on the REST endpoint, the broker, and the
  datastore, and the security of those backing services themselves.
- **Network isolation** of the broker, datastore, and extension-runtime
  services from untrusted networks.
- **Vetting the adapters/pipeline-elements installed.** Installing an
  extension is deploying code into the runtime (see §7).
- **Securing the external data sources** an adapter is pointed at, and the
  credentials configured for them.

## §7 — Properties StreamPipes does *not* uphold (by design)

*(inferred — §14 Q9)*

- **A sandbox around installed extensions** (custom adapters / processors /
  sinks). Deploying a pipeline element is deploying code that runs with the
  runtime's privileges — a feature, not a containment boundary.
  `BY-DESIGN: property-disclaimed`.
- **Protection against an operator who controls the deployment infra**
  (§3 item 4).
- **An assurance that arbitrary hostile data from an external source cannot
  affect a pipeline's behavior.** Adapters ingest whatever the source sends;
  whether StreamPipes claims any robustness guarantee on malformed/oversized
  ingested data, versus treating it as the operator's-source-trust problem, is
  the key §14 question (Q2).
- **Resource fairness as a hard guarantee** — a high-rate source or expensive
  pipeline can exhaust runtime resources; bounding it is an operator/config
  concern *(inferred — §14 Q10)*.

## §8 — Key configuration levers (load-bearing)

*(all inferred — §14 Q11; confirm names + defaults)*

| Lever | Why it matters |
| --- | --- |
| Authentication / initial-admin setup | decides whether the REST/UI front door is open and how the first admin is provisioned. |
| Role / permission model | decides whether non-admin users are bounded to their own resources. |
| TLS on REST + broker + datastore | decides whether sessions/events are on the wire in clear. |
| Extension installation policy | decides who may deploy adapter/processor code into the runtime. |

## §11a — Known non-findings (seed for scanner/AI triage)

Confirm and extend *(inferred — §14 Q12)*:

- **"A custom adapter / processor can run arbitrary code."** By design —
  installing an extension is an authorized code deployment, not a sandbox
  escape. `BY-DESIGN`.
- **"An adapter accepts data from an untrusted external source."** That is the
  intended function; in-model only if StreamPipes claims a parsing/handling
  guarantee the report violates (§14 Q2). Otherwise the source's
  trustworthiness is the operator's concern.
- **"The default/initial admin or an unauthenticated endpoint is reachable."**
  In-model only against the shipped default; an operator-loosened auth config
  is `OUT-OF-MODEL: non-default-build` (confirm the shipped default in §14).
- **Dependency-tail CVEs** (broker client, OPC-UA/MQTT library, a transitive
  JAR) from an SCA scanner — triage upstream unless StreamPipes' own code
  reaches the vulnerable path with untrusted input.

## §13 — Triage dispositions

A finding is **VALID** only when all hold: the violated property is one
StreamPipes claims (§5), the attacker is in scope (§3), and the affected code
is on an in-model surface (§2/§4) reached by untrusted input. Otherwise route
to one of: `OUT-OF-MODEL: adversary-not-in-scope` · `OUT-OF-MODEL:
equivalent-harm` · `OUT-OF-MODEL: unsupported-component` · `OUT-OF-MODEL:
non-default-build` · `BY-DESIGN: property-disclaimed`.

## §14 — Open questions for the StreamPipes PMC

Grouped in waves; answer inline. Each promotes an *(inferred)* tag to
*(maintainer)*.

**Wave 1 — scope & intended use**
1. Confirm the in-scope surface: `streampipes-rest` as the primary front door,
   with the broker/datastore/extension-runtime as intra-deployment trusted
   infra. Are the component roles in §2 right?
2. **The ingestion boundary** — when an adapter connects to a hostile external
   source, what (if anything) does StreamPipes guarantee about handling
   malformed/oversized/hostile ingested data, versus treating source-trust as
   the operator's problem? This is the highest-leverage question.
3. Confirm the assumed deployment: multi-service, behind an operator-controlled
   perimeter, with broker + datastore as trusted dependencies.
4. Is the in-scope adversary set "limited REST/UI user + network MITM +
   malicious external source"? Anything to add?
5. Confirm operators with host/broker/datastore access, and trusted admins
   doing authorized actions, are out of model.

**Wave 2 — boundaries, auth, extensions**
6. At the client→REST boundary, are request bodies, pipeline/adapter
   definitions, and config treated as untrusted and authorized per-resource?
7. What properties does StreamPipes claim to uphold given valid input
   (authn, per-resource authz, others)?
8. Confirm the operator-owned list in §6.
9. Are installed extensions (adapters/processors/sinks) code-execution by
   design (not a sandbox), per §7?

**Wave 3 — defaults & non-findings**
10. Is super-linear resource use / a hang on a high-rate source or pathological
    pipeline a bug, or the operator's config concern?
11. Confirm the real names + shipped defaults of the §8 levers — especially the
    initial-admin/auth setup and whether any endpoint is unauthenticated by
    default.
12. What do scanners/fuzzers/researchers most often report that you consider a
    non-finding? (Feeds §11a.)

**Wave 4 — meta**
13. StreamPipes has a root `AGENTS.md` but no `SECURITY.md`/`THREAT_MODEL.md`;
    this PR adds them and appends a `## Security` section to the existing
    `AGENTS.md` wiring `AGENTS.md → SECURITY.md → THREAT_MODEL.md`. Confirm this
    in-repo model is canonical, and who owns revisions.
