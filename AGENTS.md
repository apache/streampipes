# AGENTS Guide (Repository Root)

## Scope
Applies to the whole repository unless a deeper `AGENTS.md` overrides it.

## Monorepo Rules
- Keep changes scoped to requested modules; avoid opportunistic cross-module refactors.
- Preserve Apache license headers in source files.
- Keep backward compatibility for public APIs, serialized models, and storage contracts unless explicitly requested.

## Java Standards
- Follow style and import order from:
  - `tools/maven/checkstyle.xml`
  - `tools/maven/checkstyle-header.txt`
- Prefer constructor/typed APIs over loosely typed maps when extending domain logic.

## Architecture Boundaries
- `streampipes-service-core`: bootstrapping, security, migrations, scheduling.
- `streampipes-rest`: HTTP/resource layer.
- `*-management` modules: business/domain logic.
- `streampipes-storage-*`: persistence abstractions/implementations.
- Avoid moving business logic into controllers/resources.

## Validation
- For backend changes, run targeted module checks first:
  - `mvn -pl <module> -am test`
- Run only additional modules affected by dependency edges.

## UI
- UI-specific rules live in `ui/AGENTS.md` and deeper UI-level files.

## Security

You are helping a security researcher find and report vulnerabilities in
Apache StreamPipes. Before drafting any report or reaching any conclusion,
complete these steps.

### Step 1 — Read the threat model
Read **[THREAT_MODEL.md](THREAT_MODEL.md)**: the trust boundaries (the REST
front door, the external-data ingestion boundary at the adapters, the
extension runtime), the adversaries in and out of scope, and what StreamPipes
upholds versus what it leaves to the operator.

### Step 2 — Read the security policy
Read **[SECURITY.md](SECURITY.md)** for how to report (`security@streampipes.apache.org`).

### Key scoping facts (see THREAT_MODEL.md)
- The **`streampipes-rest`** HTTP/REST layer is the primary untrusted boundary;
  the broker, datastore, and extension-runtime services are assumed to run
  inside an operator-controlled perimeter.
- **Installed extensions (custom adapters / processors / sinks) are
  code-execution by design**, not a sandbox.
- An **adapter ingesting data from an external source** is the intended
  function; source trust and the handling guarantee for hostile ingested data
  are spelled out in THREAT_MODEL.md (and are an open §11 question).
- Transport security (TLS), network isolation, and extension vetting are
  **operator** responsibilities, not engine invariants.

### Step 3 — Route the finding
Route the finding to exactly one disposition in **THREAT_MODEL.md §10**
(VALID, or one of the `OUT-OF-MODEL` / `BY-DESIGN` dispositions) and cite the
section that justifies the call. This model is **v0** — open questions for the
PMC are in §11.
