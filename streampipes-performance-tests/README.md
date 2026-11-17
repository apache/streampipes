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

# StreamPipes performance tests

Configurable, isolated performance benchmarks for Apache StreamPipes: adapter
ingestion and latency, adapter and pipeline lifecycle, and dataset queries. The
[case catalog](streampipes_performance/test_cases.md) links to each case's workload,
parameters, and metrics. Products built on StreamPipes add their own cases and
targets as [plugins](#plugins).

See the [CLI guide](docs/cli.md) for test selection, parameter overrides, settings
precedence, Docker commands and HTML-only email delivery.

## Install and run

Requires Python 3.11.5+. The framework uses the StreamPipes Python client of this
repository; install both from the `streampipes-performance-tests` folder:

```sh
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -e ../streampipes-client-python -e '.[test]'

export STREAMPIPES_URL='http://localhost:8030'
export STREAMPIPES_USERNAME='admin@streampipes.apache.org'
export STREAMPIPES_PASSWORD='your-password'
# Required for adapter/pipeline readiness; use the address reachable from this machine.
export EXTENSIONS_URL='http://localhost:8044'
sp-performance validate all
sp-performance preview all
sp-performance run all
```

`sp-performance` is the installed command; `python -m streampipes_performance` is
equivalent. `validate` checks configuration; `preview` shows the execution plan and
`preview --json` includes every expanded scenario. Both work offline without credentials.
Runs log progress to stderr; use `--log-level DEBUG` for individual API requests or
`--log-level WARNING` for less output.

Run against an initialized, dedicated empty instance with the extensions required
by your cases: machine simulator, internal Data Lake sink and NATS adapter. The
runner manages test resources; the deployment must already be running.

## Configuration

Run configurations in [run_configs](run_configs/) contain all case parameters and
execution settings inline. Use `all.json` for every case once, `realistic.json` for
larger workloads, or a standalone case configuration. Configurations are found by
name in `./run_configs`, directories registered by plugins, and the configurations
shipped with this package.

```json
{
  "target": "streampipes",
  "output_directory": "../results/framework",
  "execution": {"repetitions": 1},
  "cases": [
    {
      "id": "dataset-query",
      "type": "streampipes.dataset_query",
      "parameters": {
        "number_of_datasets": [1, 10],
        "rows_per_dataset": [1000, 10000],
        "parallel_operations": [1, 4]
      }
    }
  ]
}
```

`target` names a registered target (`streampipes`, or one added by a plugin). Each
case has a unique `id` and a registered `type`. Top-level parameter lists define
alternatives; their Cartesian product creates scenarios (eight in this example).
Scalars and objects are fixed values; wrap a literal list in `{"value": [...]}`.
Omitted parameters use case defaults, and unknown parameters are rejected.

`execution` controls repetitions, timing, cleanup, and optional pass/fail thresholds;
individual cases can override it. Workload details such as query presets and upload
batch sizes live in the case implementation. See each case's README and `case.py`
for its parameters and measurement behavior. Adapter ingestion and latency
`transformation` parameters accept objects containing `label` and `script`; the run
configs own all scripts and report labels.

Infrastructure connections use environment variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `STREAMPIPES_URL` | Required | HTTP(S) instance origin |
| `STREAMPIPES_USERNAME` | Required | User for temporary API-key creation |
| `STREAMPIPES_PASSWORD` | Required | User password |
| `NATS_URL` | `nats://localhost:4222` | Input broker reachable by the runner |
| `OUTPUT_NATS_URL` | `NATS_URL` | Output broker reachable by the runner |
| `ADAPTER_NATS_URL` | `nats://nats:4222` | Broker reachable by adapters |
| `STREAM_NATS_URL` | `nats://nats:4222` | Broker reachable by plugin-registered streams |
| `INFLUX_TOKEN` | Unset | Enable direct InfluxDB uploads |
| `INFLUX_URL` | `http://localhost:8086` | InfluxDB origin reachable by the runner |
| `INFLUX_ORG` / `INFLUX_BUCKET` | `sp` / `sp` | Backend's InfluxDB organization and bucket |
| `INFLUXDB_METRICS_URL` | Unset | Prometheus endpoint for storage settling after uploads |

Dataset cases prefer direct InfluxDB uploads when `INFLUX_TOKEN` is set; missing
tokens or failed writes fall back to StreamPipes uploads. Set `INFLUXDB_METRICS_URL`
(for example `http://localhost:8086/metrics`) to wait for the cache to flush and
compactions to settle before querying. Without it, queries start immediately and may
mostly hit cached data. Only anonymous NATS URLs are supported.

## Isolation and cleanup

The runner requires an empty instance, creates a temporary API key, and cleans up
after each scenario, including failures and graceful interruptions (Ctrl+C or
SIGTERM). It revokes the key at the end of the run. A cleanup failure stops the run.

The `streampipes` target defaults to instance reset; a target may default to
individual resource deletion instead. Reset requires an admin and
`SP_RESET_ENDPOINT_ENABLED=true` and deletes instance resources and other users. Use a
dedicated test instance. Forced termination or an unreachable backend can leave
resources or a `performance-tests-*` API key behind; remove leftovers before rerunning.

## Results

Every run writes a manifest, per-scenario JSON with raw samples, metrics, errors and
cleanup status, `summary.csv`, and a self-contained `report.html`. Output paths are
relative to the run configuration's directory; the example above writes to
`results/framework/<run-id>/` next to `run_configs/`. Set `PERFORMANCE_OUTPUT_DIRECTORY`
or `--output-directory` to write elsewhere. Failed and interrupted runs retain their
available results.

Open `report.html` in a browser to inspect case summaries, configuration comparisons,
latency plots, query timings, phases, and case documentation. Rebuild it with:

```sh
sp-performance report results/framework/<run-id>
```

[tools/merge_runs.py](tools/merge_runs.py) merges reruns of failed scenarios into an
original run.

## Plugins

A plugin is a Python package that declares an entry point in the group
`streampipes_performance.plugins`. The entry point names a module; importing it
registers everything the plugin adds:

```toml
[project.entry-points."streampipes_performance.plugins"]
myproduct = "myproduct_performance.plugin"
```

```python
from pathlib import Path

from streampipes_performance.plugins import register_config_directory, register_operation_label
from streampipes_performance.targets import Target, register_target

register_target(Target("myproduct", "My product", extends="streampipes",
                       default_cleanup="delete", supports_reset=False))
register_operation_label("job", "Job")          # "job.start" -> "Job start time"
register_config_directory(Path(__file__).parent / "run_configs")

from myproduct_performance.cases.job_latency import case  # noqa: E402,F401  (uses @register)
```

- **Cases** subclass `streampipes_performance.cases.Case`, decorate the class with
  `@register("myproduct.job_latency")`, set `targets = ("myproduct",)` and list the
  readiness `services` they need (`extensions`, `nats`, `influxdb_metrics`).
- **Targets** with `extends="streampipes"` also run every StreamPipes case. Subclass
  `Target` and override `apis(client, scenarios)` to add resource APIs keyed by kind;
  the runner includes them in the empty-instance check and in delete cleanup. An API
  with `stop_result` is stopped and deleted before pipelines; an API with
  `owns(identifier, prefix)` can claim resources whose names lack the scenario prefix.
- **Tests** can use the in-memory fakes in `streampipes_performance.testing`
  (`Server`, `Clock`, `Broker`).

`sp-performance tests list` shows the cases of every installed plugin.

## Development

To add a built-in case, define a class inheriting directly from `Case`, its
`Parameters` model, documentation, and workload steps in `case.py`. Add a case README
and standalone run configuration, then list the module in `BUILTIN_MODULES` in
[streampipes_performance/plugins.py](streampipes_performance/plugins.py).

Run the offline tests:

```sh
python -m pytest
```

Build a runner image from the repository root, which includes the Python client:

```sh
docker build -f streampipes-performance-tests/Dockerfile -t streampipes-performance:local .
```
