<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
-->

# Adapter latency

Measure correlated NATS → adapter → NATS latency for each configured transformation.

Configure this case in [`run_configs/adapter-latency.json`](../../../../run_configs/adapter-latency.json). Arrays specify alternatives; their Cartesian product produces independent scenarios. Only the parameters below are accepted. The run configuration controls timeouts, cleanup and repetitions.

## Parameters

- `number_of_adapters`: Number of adapters.
- `events_per_resource`: Number of measured input events per adapter; setup probes and ten warmup events are excluded.
- `publish_interval_ms`: Interval between publishing rounds in milliseconds. Events/second per adapter = 1000 / publish_interval_ms.
- `transformation`: Object with `label` and `script` (JavaScript source). Omitted or `null` disables scripting. Each input must produce one output preserving `timestamp` and, for latency, `eventId`.

## Script configuration

The run configuration owns the script and its report label. Supply one object or
an array of objects to compare alternatives:

```json
"transformation": {
  "label": "My pass-through",
  "script": "function transform(event, out, ctx) { out.collect(event); }"
}
```

Script strings from older configs must be wrapped in a `label`/`script` object.
An empty script is rejected; use `null` to disable scripting.

## Metrics

- `processing_latency_ms`: NATS input to correlated output elapsed time on the benchmark clock: count, min, mean and max (ms).
- `missing_events`: Expected outputs that were not received; a nonzero count fails the case.
- `throughput_ratio`: Observed / expected outputs. Target: 1.0.

All created resources are tracked for cleanup, including partial setup failures.

## Code layout

[`case.py`](case.py) declares all accepted parameters, documentation and fixed constants, then shows the ordered setup, measurement and result preparation steps. Shared helpers handle technical operations; this case owns its schemas, generated data and workload choices.
