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

# Security Policy

## Reporting a Vulnerability

Please report suspected security vulnerabilities in Apache StreamPipes
**privately** to the StreamPipes security list at
`security@streampipes.apache.org`, following the [Apache Software Foundation security process](https://www.apache.org/security/).
Do **not** open public GitHub issues or pull requests for security reports — a
private report lets the issue be investigated and fixed before disclosure.

## Threat Model
A threat model for Apache StreamPipes is maintained in
[THREAT_MODEL.md](THREAT_MODEL.md). It describes the trust boundaries (the REST
front door, the external-data ingestion boundary at the adapters, the
extension runtime), the adversaries in and out of scope, the security
properties StreamPipes upholds given its deployment assumptions versus those
left to the operator (transport security, network isolation, extension
vetting, source trust), and the recurring non-findings. Triagers of scanner,
fuzzer, or AI-generated findings should route each through `THREAT_MODEL.md`
§10.

This threat model is **v0**. See `THREAT_MODEL.md` for its current status and
maintenance notes.
