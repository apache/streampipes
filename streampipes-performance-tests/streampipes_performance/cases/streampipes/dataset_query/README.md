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

# Dataset query

Populate deterministic machine datasets, then measure latest-row, projection, time-range, filtered and aggregate queries for every configuration.

Configure this case in [`run_configs/dataset-queries.json`](../../../../run_configs/dataset-queries.json). Arrays specify alternatives; their Cartesian product produces independent scenarios. Only the parameters below are accepted. The run configuration controls timeouts, cleanup and repetitions.

## Parameters

- `number_of_datasets`: Number of datasets.
- `rows_per_dataset`: Total rows in each dataset.
- `sensor_count`: Positive integer specifying how many `sensorId` values to cycle through per dataset; defaults to 10. Actual distinct values are `min(sensor_count, rows_per_dataset)`.
- `parallel_operations`: Maximum concurrent dataset workflows, each with its own HTTP session. All presets and repetitions of a dataset stay on one thread; active users are capped at the dataset count.

To compare sensor cardinality at the same dataset size, set alternatives such as
`"sensor_count": [10, 100, 1000]` in the run config. Each becomes a separate scenario.
IDs cycle deterministically from `flowrate00` through `flowrate{sensor_count - 1}`
(with at least two digits), continuing across upload batches. `sensorId` remains a
dimension and an InfluxDB tag. This changes the number of distinct values in one
dimension, not the number of dimension columns. The filtered query still selects
`flowrate00`, so increasing `sensor_count` also reduces its matching row count.

## Metrics

- `operations.Query time`: Validated request duration across all named query presets (ms): count, min, mean, max and errors. Per-query summaries are stored in query_workloads.
- `query_workloads`: Completed requests, errors, returned rows, elapsed seconds and successful queries per second. Each dataset stays with one worker, which runs every preset in declaration order for ten repetitions before moving to its next dataset. Each preset issues exactly ten queries per dataset, without a row limit. Presets are latest rows, projection, time range, filtered rows and aggregation. Each passes `limit=None` to suppress the Python client's default 1000-row cap; filtering, time ranges and aggregation still determine which rows are returned.

Uploads prefer the shared [`influx.py`](../../influx.py) uploader when
`INFLUX_TOKEN` is configured. `INFLUX_URL` defaults to `http://localhost:8086`;
`INFLUX_ORG` and `INFLUX_BUCKET` default to `sp`. Missing tokens or a failed
write switch to StreamPipes uploads for the failed and remaining batches.
Dataset metadata creation and row-count validation still use StreamPipes.

All created resources are tracked for cleanup, including partial setup failures.

## Code layout

[`case.py`](case.py) declares all accepted parameters, documentation, workload presets and fixed constants, then shows the ordered setup, measurement and result preparation steps. Shared helpers handle technical operations; this case owns its schemas, generated data and workload choices.

## Query presets

Every configuration runs all entries in `QUERIES` in [`case.py`](case.py). Add a named options factory to that collection to extend the workload; no runner or config changes are required. Each query has separate request/error counters, elapsed time and `query_time_ms` statistics in `query_workloads`. Raw samples retain its name. The `Query time` metric summarizes all measured presets together. Failed queries do not skip later presets.

## First requests

No query or client warmup requests are issued. Every asset or dataset workflow starts directly in measurement, so first-request delays are included in raw samples and query timing statistics.

## Workflow timing

Presets overlap across assets or datasets. In `query_workloads`, `duration_seconds` is the common wall-clock duration of the complete workflow (`duration_scope: asset_workflows`); `queries_per_second` is each preset’s successful requests divided by that duration. Summing these rates gives total workflow throughput. `query_time_ms` remains the independent latency summary for each preset. Failures are recorded without skipping later presets or assets.
