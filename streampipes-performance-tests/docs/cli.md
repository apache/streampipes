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

# Command-line usage

`sp-performance` is the main command; `python -m streampipes_performance` is
equivalent, and plugins may install their own alias (for example `sp-performance`).
Local execution and the Docker image use the same CLI.

## Discover and select workloads

```sh
sp-performance tests list
sp-performance tests show dataset-query
sp-performance configs list
sp-performance run --help
sp-performance run medium --case dataset-query --help
sp-performance preview medium --case dataset-query
sp-performance validate run_configs/medium.json
```

Configuration arguments accept a supplied name or a JSON path. Name lookup checks
`./run_configs`, directories registered by installed plugins, the source checkout,
then the installed configuration directory. The first match wins, so a plugin
configuration replaces a built-in one of the same name; use an explicit path when needed.
`tests show` accepts a full registered type or its short hyphenated name.

`--case` selects a case **ID from the configuration**, not its registered type.
Repeat it to select several cases. `--set CASE_ID.PARAMETER=JSON` replaces a workload
parameter before validation and scenario expansion:

```sh
sp-performance run medium \
  --case dataset-query \
  --set 'dataset-query.number_of_datasets=5' \
  --set 'dataset-query.sensor_count=[10,100,1000]'
```

Arrays define alternatives as before. Nested objects remain atomic. Overrides do
not edit the source file; effective values are saved in scenario results. Unknown
IDs, unknown parameters and invalid values fail before contacting the stack.
Edit timing, repetitions, thresholds, scripts and other complex workload objects
in the run configuration.

`preview` prints selected cases, descriptions, parameter alternatives, query names
and repetitions, scenario counts, cleanup, readiness checks, output location and
email recipients. `preview --json` emits the complete plan and expanded scenarios.
`run CONFIG --help` adds that configuration's plan to the command help. Discovery,
help, validation and preview do not contact the stack, read credential files, or
send mail. Report resends read only the SMTP secret. Secret values
are omitted from plans and saved deployment settings. `run` prints its plan before
starting without an interactive confirmation.

## Deployment settings and precedence

Use flags, environment variables, or a JSON settings file:

```sh
sp-performance run dataset-queries \
  --settings settings/local.example.json \
  --url http://localhost:8082 \
  --password-file /path/to/backend-password \
  --output-directory ./results
```

The settings file is a flat JSON object. Its field names match flag names with
hyphens replaced by underscores. All fields are optional.

Precedence, highest first:

1. Explicit CLI flags.
2. Environment variables.
3. The file selected by `--settings` (or `PERFORMANCE_SETTINGS`).
4. Existing run-config connection/output values.
5. Framework defaults.

Existing `connection.url_env`, `username_env`, and `password_env` selectors remain
supported. They select the environment variables used for that run; explicit CLI
values still win. A connection may also contain direct `url`, `username`, and
`password` values. `output_directory` in run configs remains supported.

Relative output paths are resolved against the run config when defined there,
against the settings file when defined there, and against the current working
directory for CLI/environment overrides. The CLI does not automatically read a
`.env` file; export values or use `--settings`. Docker Compose reads its `.env` file.

| CLI / JSON field | Environment variable |
| --- | --- |
| `--url` / `url` | `STREAMPIPES_URL` |
| `--username` / `username` | `STREAMPIPES_USERNAME` |
| `--password` / `password` | `STREAMPIPES_PASSWORD` |
| `--nats-url` / `nats_url` | `NATS_URL` |
| `--output-nats-url` / `output_nats_url` | `OUTPUT_NATS_URL` |
| `--adapter-nats-url` / `adapter_nats_url` | `ADAPTER_NATS_URL` |
| `--stream-nats-url` / `stream_nats_url` | `STREAM_NATS_URL` |
| `--extensions-url` / `extensions_url` | `EXTENSIONS_URL` |
| `--influx-url` / `influx_url` | `INFLUX_URL` |
| `--influx-org` / `influx_org` | `INFLUX_ORG` |
| `--influx-bucket` / `influx_bucket` | `INFLUX_BUCKET` |
| `--influx-token` / `influx_token` | `INFLUX_TOKEN` |
| `--influxdb-metrics-url` / `influxdb_metrics_url` | `INFLUXDB_METRICS_URL` |
| `--ready-timeout` / `ready_timeout` | `PERFORMANCE_READY_TIMEOUT` |
| `--wait-ready` / `wait_ready` | `PERFORMANCE_WAIT_READY` |
| `--output-directory` / `output_directory` | `PERFORMANCE_OUTPUT_DIRECTORY` |
| `--log-level` / `log_level` | `PERFORMANCE_LOG_LEVEL` |

`--password-file`, `--influx-token-file`, and `--smtp-password-file` read secrets
from files. JSON uses `password_file`, `influx_token_file`, and `smtp_password_file`;
file paths in JSON are relative to the settings file. Environment equivalents
append `_FILE` to the corresponding secret variable. A CLI secret overrides an
environment/file secret. Within the environment, `_FILE` takes precedence over
the value variable. Supplying both forms in one CLI or JSON source is an error.
Use files or environment variables to keep secrets out of shell command history.

Readiness is enabled for runs by default with a 600-second deadline. It checks
backend setup and login, then the services each selected case declares: extensions
for adapter/pipeline workloads, NATS for latency workloads, and configured storage
metrics for dataset workloads. For extension checks provide `extensions_url`;
dataset-only runs do not need it. `--no-wait-ready` skips these checks. Workload readiness, such as
waiting for uploaded rows and resource startup, still runs normally.

## Email only the HTML report

SMTP settings support CLI, settings-file, and environment configuration equally:

| CLI / JSON field | Environment variable | Default |
| --- | --- | --- |
| `--email-to` / `email_to` | `PERFORMANCE_EMAIL_TO` | No recipients; disabled |
| `--email-from` / `email_from` | `PERFORMANCE_EMAIL_FROM` | Required for delivery |
| `--smtp-host` / `smtp_host` | `PERFORMANCE_SMTP_HOST` | Required for delivery |
| `--smtp-port` / `smtp_port` | `PERFORMANCE_SMTP_PORT` | `587` |
| `--smtp-username` / `smtp_username` | `PERFORMANCE_SMTP_USERNAME` | No authentication |
| `--smtp-password` / `smtp_password` | `PERFORMANCE_SMTP_PASSWORD` | Required with username |
| `--smtp-security` / `smtp_security` | `PERFORMANCE_SMTP_SECURITY` | `starttls` |
| `--smtp-timeout` / `smtp_timeout` | `PERFORMANCE_SMTP_TIMEOUT` | `30` seconds per operation |

Security modes are `starttls`, `ssl` (normally port 465), and `none` for an
unencrypted relay. TLS verifies the server certificate. Recipients use plain
email addresses: repeat `--email-to`, use a JSON array, or a comma-separated
environment value. `--to` is an alias. `--no-email` disables configured recipients.

```sh
export PERFORMANCE_SMTP_HOST=smtp.example.org
export PERFORMANCE_SMTP_USERNAME=benchmark
export PERFORMANCE_SMTP_PASSWORD_FILE=/path/to/smtp-password
export PERFORMANCE_EMAIL_FROM=benchmark@example.org
export PERFORMANCE_EMAIL_TO=results@example.org
sp-performance run medium --settings settings/local.example.json
```

Or put non-secret mail settings into a settings file:

```json
{
  "smtp_host": "smtp.example.org",
  "smtp_port": 587,
  "smtp_security": "starttls",
  "smtp_username": "benchmark",
  "smtp_password_file": "smtp-password",
  "email_from": "benchmark@example.org",
  "email_to": ["results@example.org"]
}
```

Recipient configuration explicitly enables delivery. The message has a short text
summary and exactly one attachment, `report.html`; JSON, CSV, logs and other files
are never attached. The HTML retains the details already embedded by the report
generator. Mail is attempted after result persistence, resource cleanup and
API-key revocation have completed or reported failure. Passed, failed and graceful
interruption reports are eligible. Preflight failures without a report send no
mail. A forced process kill cannot send a report.

Delivery status is written separately to `email-delivery.json`, including partial
recipient rejection. A mail failure leaves the report and benchmark outcome
intact. No automatic send retries occur; retry explicitly without rerunning tests:

```sh
sp-performance report results/framework/RUN_ID
sp-performance report send results/framework/RUN_ID --to results@example.org
```

The second command sends the existing HTML and does not rebuild or execute tests.
A retry sends to all configured recipients again, including anyone who may have
received an earlier partially accepted message.

Exit codes: `0` success; `1` benchmark/cleanup failure; `2` configuration, startup,
execution infrastructure, report-building or finalization failure; `3` email
failure after an otherwise successful run (or `report send`); `130` graceful
interruption. A mail failure does not replace an existing benchmark failure code.
