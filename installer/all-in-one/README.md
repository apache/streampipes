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
# StreamPipes All-in-One - Try it in 60 seconds

A single Docker image that runs the complete Apache StreamPipes platform in one container and
seeds a demo scenario on first start, so you see live machine data without connecting any hardware.

**Current version:** 0.99.0-SNAPSHOT

> **NOTE**: This image is made for trying StreamPipes out on a laptop, demos and workshops. It
> runs every service in one container and keeps its data in the container. For anything you want
> to keep, use the [Compose installer](../compose) or the [Kubernetes chart](../k8s).

## Run it

```bash
docker run --rm -p 80:80 apachestreampipes/all-in-one:0.99.0-SNAPSHOT
```

1. Open [http://localhost](http://localhost). While the services boot (about a minute) the UI shows a
   progress screen that moves on by itself.
2. Sign in with **`admin@streampipes.apache.org`** and password **`admin`**.
3. Click **Dashboards** in the left menu and open **Plant Overview**. The charts refresh every five seconds.

## The demo scenario

The seed builds a small water works with two sites. Every machine is a *machine data simulator*
adapter that emits one event per second; the simulator alternates between a normal and an alarm
phase every 30 seconds, which is what the alarm pipelines pick up.

| Site | Asset | Simulated machine (adapter) | Dataset |
| --- | --- | --- | --- |
| Riverside Water Works, area Intake | Intake Pump Station with pumps P-101 to P-103 | *Intake Pumps*: flow, temperature, density of three pumps | `intake_pumps` |
| Riverside Water Works, area Treatment | Main Pressure Line | *Main Line Pressure*: two pressure sensors | `main_line_pressure` |
| Riverside Water Works, area Storage | Clear Water Tank T-200 | *Clear Water Tank*: level and overflow flag | `clear_water_tank` |
| Hillside Reservoir, area Reservoir | Hillside Reservoir Basin | *Reservoir Level*: two level sensors | `reservoir_level` |

| Where to click | What you find |
| --- | --- |
| **Home** | Counters, the asset table and a map with both sites (toggle *Map*) |
| **Dashboards** | *Plant Overview*, *Intake Pump Station* and *Water Storage* |
| **Charts** | 11 charts: time series per machine, a temperature gauge, a tank value card, a fault-flag heatmap and two event tables |
| **Pipelines** | Four *Persist ...* pipelines plus *Pump Overheating Detection* (numerical filter, temperature > 80), *Pressure Smoothing* (moving average) and *Tank Overflow Events* (boolean filter) |
| **Datasets** | The four machine datasets and `pump_overheating`, `main_line_pressure_smoothed`, `tank_overflow_events` |
| **Assets** | The four assets with their site, area, labels (*Demo*, *Critical asset*) and links to adapters, data streams, pipelines, datasets, charts and dashboards |
| **Adapters** | The four simulated machines, already running |

Everything else works as in a regular installation: add further adapters, build pipelines in the
editor or explore the data. Stop the container with `Ctrl+C` (or `docker stop`). With `--rm` all
data is discarded; drop `--rm` to keep the container and start it again later with `docker start`.

## Requirements

- Docker (Docker Desktop on macOS and Windows). The image is built for `linux/amd64` and `linux/arm64`.
- Roughly 4 GB of memory available to Docker; the container runs two JVMs plus three databases.
- Port 80 free on the host, or map another one: `-p 8080:80` and open `http://localhost:8080`.

## What is inside

| Process | Origin | Port (inside the container) |
| --- | --- | --- |
| CouchDB | `couchdb:3.4.3`, the base image | 5984 |
| InfluxDB | `influxdb:2.6` binaries and setup entrypoint | 8086 |
| NATS | `nats` server binary, token authentication enabled | 4222 |
| StreamPipes core | `apachestreampipes/backend` | 8030 |
| IIoT extensions | `apachestreampipes/extensions-all-iiot` | 8090 |
| UI | `apachestreampipes/ui` static files served by nginx | **80** (the only exposed port) |
| Demo seed | `bin/seed-demo.py`, runs once per start and builds the scenario above | - |

The StreamPipes, InfluxDB and NATS images are the ones `../compose/docker-compose.yml` uses; CouchDB is
3.4.x because the 3.3.1 image is built on a Debian release that no longer receives packages. `supervisord`
starts and watches the processes (`supervisord.conf`); the start scripts in `bin/` wait for their
dependencies. All services talk to each other over `localhost`, which is why the StreamPipes
environment variables (`SP_COUCHDB_HOST`, `SP_TS_STORAGE_HOST`, `SP_NATS_HOST`, `SP_CORE_HOST`,
`SP_HOST`) are preset to `localhost` in the `Dockerfile`.

### Credentials

The initial administrator is `admin@streampipes.apache.org` / `admin`. Override it on the first start:

```bash
docker run --rm -p 80:80 -e SP_INITIAL_ADMIN_PASSWORD=change-me apachestreampipes/all-in-one:0.99.0-SNAPSHOT
```

The secrets the services share (service account, JWT signing, CouchDB and InfluxDB credentials,
encryption passcode, NATS token) are generated randomly on the first start of a container and stored in
`/var/lib/streampipes/secrets.env` inside the container, so a stopped container can be started
again. The same `SP_*` variables as in the Compose installer (`SP_SERVICE_SECRET`, `SP_JWT_SECRET`,
`SP_COUCHDB_PASSWORD`, `SP_TS_STORAGE_TOKEN`, `SP_INFLUXDB_ADMIN_PASSWORD`,
`SP_ENCRYPTION_PASSCODE`, `SP_NATS_TOKEN`) can be passed with `-e` to use your own values; this only has an effect
on the first start.

### Options

| Variable | Default | Effect |
| --- | --- | --- |
| `SP_DEMO_SEED` | `true` | Set to `false` to start a clean platform without the demo scenario |
| `SP_INITIAL_ADMIN_EMAIL` / `SP_INITIAL_ADMIN_PASSWORD` | `admin@streampipes.apache.org` / `admin` | Initial administrator |
| `SP_BACKEND_JAVA_OPTS` / `SP_EXTENSIONS_JAVA_OPTS` | `-XX:MaxRAMPercentage=30` | JVM options of the core and the extensions service |

The seed is idempotent: it reuses adapters, pipelines, charts, dashboards, sites, labels and assets
that already exist (matched by name), so restarting a container does not create duplicates. The
scenario is defined at the top of `bin/seed-demo.py` and easy to adapt. Its log lines are prefixed with `[demo-seed]`.

## Build the image yourself

The `Dockerfile` copies the published StreamPipes images for the given version, so building it does
not require a Maven or npm build:

```bash
docker build -t apachestreampipes/all-in-one:0.99.0-SNAPSHOT installer/all-in-one
```

Build arguments: `SP_VERSION` (default `0.99.0-SNAPSHOT`), `SP_DOCKER_REGISTRY` (default
`apachestreampipes`), `COUCHDB_VERSION`, `INFLUXDB_VERSION` and `NATS_VERSION`. To bundle images you
built locally, tag them as `<registry>/backend:<version>`, `<registry>/extensions-all-iiot:<version>`
and `<registry>/ui:<version>` and pass matching build arguments.

## Troubleshooting

- `docker logs <container>` shows the output of all processes; `docker exec <container> supervisorctl status`
  lists their state.
- The UI stays on the progress screen for longer than two minutes: check the log for the core
  (`StreamPipesCoreApplication`) and make sure Docker has enough memory.
- Dashboards are missing: the seed waits for every dataset to receive its first events before it creates
  the charts, so look for `[demo-seed]` lines in the log. `[demo-seed] Demo is ready` means everything was created.
- The map on the home page stays grey: it loads tiles from openstreetmap.org, which needs internet access.
