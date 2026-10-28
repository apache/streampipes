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

The seed builds a fictional company with three sites: a water works, its reservoir and an automotive
supplier plant. Every machine is a *machine data simulator* adapter that emits one event per second;
the simulator alternates between a normal and an alarm phase every 30 seconds (a worn pump, a broken
tool, a bad fastener batch), which is what the alarm pipelines pick up.

| Site | Asset | Simulated machine (adapter) | Dataset |
| --- | --- | --- | --- |
| Riverside Water Works, area Intake | Intake Pump Station with pumps P-101 to P-103 | *Intake Pumps*: flow, temperature, density of three pumps | `intake_pumps` |
| Riverside Water Works, area Treatment | Main Pressure Line | *Main Line Pressure*: two pressure sensors | `main_line_pressure` |
| Riverside Water Works, area Storage | Clear Water Tank T-200 | *Clear Water Tank*: level and overflow flag | `clear_water_tank` |
| Hillside Reservoir, area Reservoir | Hillside Reservoir Basin | *Reservoir Level*: two level sensors | `reservoir_level` |
| Powertrain Components Plant, area Machining | Machining Cell 1 with CNC-01 to CNC-03 | *CNC Machining Centres*: spindle, tool wear, states of three machines, enriched by a JavaScript transformation | `cnc_machining` |
| Powertrain Components Plant, area Assembly | Assembly Line 1 with two screwing stations | *Assembly Screwing Stations*: torque, angle, cycle, OK/NOK | `assembly_stations` |
| Powertrain Components Plant, area Quality | End-of-Line Test Rig | *End-of-Line Leak Test*: pressure, leak rate, pass/fail | `eol_leak_test` |

The CNC adapter shows the script-based transformation of StreamPipes Connect: a small JavaScript
function in the adapter derives `spindle_power_kw` from the spindle load and classifies the tool
wear (`tool_wear_class`: OK, WARNING, REPLACE) before the events reach any pipeline. Open the
adapter under **Adapters** to see and edit the script.

| Where to click | What you find |
| --- | --- |
| **Home** | Counters, the asset table and a map with both sites (toggle *Map*) |
| **Dashboards** | Water works: *Plant Overview*, *Intake Pump Station*, *Water Storage*. Automotive plant: *Powertrain Plant Overview*, *CNC Machining*, *Assembly and Quality*. Plus *Shop Floor Display*, a dashboard in slide view for a screen on the shop floor. Each overview starts with a KPI row (indicator, progress bar towards a shift target, traffic light, status lamp), continues with trends and ends with the event tables |
| **Charts** | 40 charts covering most chart types: time series with line, area, bar and scatter display types, a second axis and a zoom slider; an aggregated query (average spindle load per minute); gauge, indicator, progress bar, traffic light, status lamp and value cards for the latest values; histogram, pie, scatter, density and time-series heatmap for distributions; status heatmaps and tables |
| **Pipelines** | Seven *Persist ...* pipelines plus *Pump Overheating Detection* (numerical filter), *Pressure Smoothing* and *Spindle Vibration Smoothing* (moving averages), *Tank Overflow Events* (boolean filter), and *Tool Wear Warnings*, *NOK Joint Detection*, *Failed Leak Tests* (changed-value detection on the counters, then a filter, so each event is recorded once) |
| **Datasets** | The seven machine datasets and the seven datasets written by the analytics pipelines |
| **Assets** | Seven assets with sub-assets, site, area, labels (*Demo*, *Critical asset*, *Automotive*) and links to adapters, data streams, pipelines, datasets, charts and dashboards |
| **Adapters** | The seven simulated machines, already running |

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
| StreamPipes core | `streampipes-core.jar` built by Maven, exploded into layers like the `backend` image | 8030 |
| IIoT extensions | `streampipes-extensions-all-iiot.jar` built by Maven, exploded like the `extensions-all-iiot` image | 8090 |
| UI | the production build of `ui/` served by nginx | **80** (the only exposed port) |
| Demo seed | `bin/seed-demo.py`, runs once per start and builds the scenario above | - |

The InfluxDB and NATS images are the ones `../compose/docker-compose.yml` uses; CouchDB is 3.4.x because
the 3.3.1 image is built on a Debian release that no longer receives packages. The StreamPipes parts are
the same artifacts the component images are built from, so the demo always matches the commit it was
built from. `supervisord`
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

The `Dockerfile` takes the StreamPipes artifacts from three named build contexts (`core`,
`extensions`, `ui`), so it never depends on published images. Build the artifacts first, from the
repository root:

```bash
mvn -pl streampipes-service-core,streampipes-extensions/streampipes-extensions-all-iiot -am -DskipTests package
(cd ui && npm ci && npm run build)
```

Then build the image with the helper script, which checks that the artifacts exist and tags the image
with the project version (pass another tag as the first argument if you like):

```bash
installer/all-in-one/build.sh
```

Without the script, the equivalent command is:

```bash
docker build \
  --build-context core=streampipes-service-core/target \
  --build-context extensions=streampipes-extensions/streampipes-extensions-all-iiot/target \
  --build-context ui=ui/dist/streampipes/ui/browser \
  -t apachestreampipes/all-in-one:0.99.0-SNAPSHOT installer/all-in-one
```

Named build contexts need BuildKit (Docker 23 or newer, or `docker buildx build`). Build arguments:
`COUCHDB_VERSION`, `INFLUXDB_VERSION` and `NATS_VERSION`. The CI workflow builds the image the same
way, right after the Maven and npm builds of the same commit.

## Troubleshooting

- `docker logs <container>` shows the output of all processes; `docker exec <container> supervisorctl status`
  lists their state.
- The UI stays on the progress screen for longer than two minutes: check the log for the core
  (`StreamPipesCoreApplication`) and make sure Docker has enough memory.
- Dashboards are missing: the seed waits for every dataset to have a schema before it creates the charts, so
  look for `[demo-seed]` lines in the log. `[demo-seed] Demo is ready` means everything was created.
- Event tables such as *Tool wear warnings*, *NOK joints* or *Failed leak tests* stay empty for a while: they only
  receive events when the condition occurs (a tool wears out after a few minutes, NOK joints and failed tests
  happen rarely outside the alarm phases).
- The map on the home page stays grey: it loads tiles from openstreetmap.org, which needs internet access.
