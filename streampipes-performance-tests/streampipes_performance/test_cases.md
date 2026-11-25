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

# Performance test cases

The five built-in cases below each own a `case.py` and `README.md`, with a standalone run configuration in `run_configs/`. Plugins document their own cases. Open `case.py` to see the complete parameter model, descriptions, named workload presets, fixed constants, and ordered setup and measurement steps. Shared helpers contain generic operations; schemas, generation rules and query selection belong to the cases. Configurations use the existing English parameter names; only these workload parameters are accepted. Arrays select alternatives. Timing, cleanup and repetitions are execution settings in the run configuration.

| Case | Parameters | Primary metric |
| --- | --- | --- |
| [streampipes.adapter_latency](cases/streampipes/adapter_latency/README.md) | `number_of_adapters`, `events_per_resource`, `publish_interval_ms`, `transformation` | processing_latency_ms |
| [streampipes.adapter_ingestion](cases/streampipes/adapter_ingestion/README.md) | `number_of_adapters`, `wait_time_ms`, `transformation` | datasets[].event_gaps_ms |
| [streampipes.adapter_start_stop](cases/streampipes/adapter_start_stop/README.md) | `number_of_adapters`, `wait_time_ms`, `cycles` | operations.Adapter start time |
| [streampipes.dataset_query](cases/streampipes/dataset_query/README.md) | `number_of_datasets`, `rows_per_dataset`, `sensor_count`, `parallel_operations` | operations.Query time |
| [streampipes.pipeline_start_stop](cases/streampipes/pipeline_start_stop/README.md) | `number_of_pipelines`, `cycles`, `wait_time_ms` | operations.Pipeline start time |

Resource counts use `number_of_adapters`, `number_of_pipelines` or `number_of_datasets`, according to the case. `parallel_operations` means concurrent users. Rates retain the existing interval parameters: events/second per resource = `1000 / wait_time_ms` (simulators) or `1000 / publish_interval_ms` (NATS). `events_per_resource` is an input count per adapter.

Script source and report labels live in the run configurations as `label`/`script` objects. Adapter ingestion and latency `transformation` accepts an object or `null` (disabled). There are no built-in script defaults or script-text lookups. The supplied configs demonstrate pass-through, arithmetic conversion and CPU work.

Broker addresses are set with `NATS_URL`, `OUTPUT_NATS_URL` and `ADAPTER_NATS_URL`. They are deployment settings rather than case parameters.

Each dataset configuration runs every named preset in its `case.py` `QUERIES` collection. Dataset presets cover latest rows, projection, time ranges, filtering and aggregation. Every query is listed directly in one `QUERIES` dictionary; add an explicit entry for each request and range without changing parameters or the runner. Each preset gets its own warmup and result summary.
