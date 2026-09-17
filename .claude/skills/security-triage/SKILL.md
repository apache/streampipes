---
name: security-triage
description: Assess, route and report a suspected security vulnerability in Apache StreamPipes against the project's threat model. Use when a task involves a potential vulnerability, an exploit claim, a security finding from a scanner, or a decision about whether something is in or out of the security model.
---

# Security triage for Apache StreamPipes

You are helping assess whether a reported or suspected issue is a security vulnerability
in Apache StreamPipes and how it should be routed. Complete these steps before drafting
any report or reaching any conclusion.

## Step 1 — Read the threat model

Read `THREAT_MODEL.md` at the repository root: the trust boundaries (the REST front door,
the external-data ingestion boundary at the adapters, the extension runtime), the
adversaries in and out of scope, and what StreamPipes upholds versus what it leaves to the
operator. The model is **v0**; cite sections by number.

## Step 2 — Read the security policy

Read `SECURITY.md` for how findings are reported (`security@apache.org`). Do not open a
public issue or pull request for a suspected vulnerability.

## Key scoping facts (details in THREAT_MODEL.md)

- The `streampipes-rest` HTTP layer is the primary control boundary; the external-data
  ingestion boundary is at the adapters. The broker, datastore and extension-runtime
  services are assumed to run inside an operator-controlled perimeter.
- **Installed extensions (custom adapters, processors, sinks) are code execution by
  design**, not a sandbox.
- An adapter ingesting data from an external source is the intended function; source trust
  and the handling guarantee for hostile ingested data are spelled out in the threat model.
- Transport security (TLS), network isolation and extension vetting are **operator**
  responsibilities, not engine invariants.

## Step 3 — Route the finding

Route the finding to exactly one disposition in THREAT_MODEL.md §10 — `VALID`, or one of
the `OUT-OF-MODEL` / `BY-DESIGN` dispositions — and cite the section that justifies the
call. State the affected component from the §2 table and the boundary it crosses.

## Output

A short triage note: disposition, justification with section citations, affected
component and boundary, and — only for `VALID` — the reporting route from `SECURITY.md`.
