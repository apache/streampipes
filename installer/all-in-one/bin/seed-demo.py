#!/usr/bin/env python3
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Seeds the all-in-one demo scenario once the StreamPipes core is ready.

The scenario is a small water works with two sites:

* Riverside Water Works: an intake pump station (three pumps), the main
  pressure line (two sensors) and a clear-water storage tank.
* Hillside Reservoir: a reservoir with two level sensors.

Every machine is a machine data simulator adapter whose ``persist`` option
also creates the "Persist ..." pipeline. On top of that, three analytics
pipelines detect overheating pumps, smooth the pressure signal and record
tank overflow events. Charts, dashboards, sites, labels and assets tie it
together. Everything goes through the public REST API of the core.

The script is idempotent: resources that already exist (matched by name) are
reused, so it runs on every container start. Set SP_DEMO_SEED=false to skip
it. Only the Python standard library is used.
"""

import json
import os
import secrets
import sys
import time
import urllib.error
import urllib.request

BACKEND_URL = os.environ.get("SP_SEED_BACKEND_URL", "http://localhost:8030/streampipes-backend")
ADMIN_EMAIL = os.environ.get("SP_INITIAL_ADMIN_EMAIL", "admin@streampipes.apache.org")
ADMIN_PASSWORD = os.environ.get("SP_INITIAL_ADMIN_PASSWORD", "admin")
SEED_ENABLED = os.environ.get("SP_DEMO_SEED", "true").strip().lower() not in ("false", "0", "no")

READY_TIMEOUT_SECONDS = int(os.environ.get("SP_SEED_TIMEOUT_SECONDS", "900"))
POLL_INTERVAL_SECONDS = 3
RECOVERY_GRACE_SECONDS = 60

# ---------------------------------------------------------------------------
# Extension ids
# ---------------------------------------------------------------------------
SIMULATOR_APP_ID = "org.apache.streampipes.connect.iiot.adapters.simulator.machine"
DATASET_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.dataset"
NUMERICAL_FILTER_APP_ID = "org.apache.streampipes.processors.filters.jvm.numericalfilter"
BOOLEAN_FILTER_APP_ID = "org.apache.streampipes.processors.filters.jvm.processor.booleanfilter"
MOVING_AVERAGE_APP_ID = "org.apache.streampipes.processors.filters.jvm.movingaverage"
REQUIRED_EXTENSIONS = (SIMULATOR_APP_ID, DATASET_SINK_APP_ID, NUMERICAL_FILTER_APP_ID,
                       BOOLEAN_FILTER_APP_ID, MOVING_AVERAGE_APP_ID)

XSD = "http://www.w3.org/2001/XMLSchema#"
NUMERIC_TYPES = {XSD + "integer", XSD + "long", XSD + "float", XSD + "double", "http://schema.org/Number"}
BOOLEAN_TYPE = XSD + "boolean"
PRESET_COLORS = ["#39B54A", "#1B1464", "#f44336", "#FFEB3B", "#000000", "#433BFF", "#FF00E4", "#FD8B00"]
TIMESTAMP_FIELD = "timestamp"
SENSOR_ID_FIELD = "sensorId"


def prop(label, scope="measurement", semantic="http://schema.org/Number"):
    return {"label": label, "description": "", "propertyScope": scope, "semanticType": semantic,
            "additionalMetadata": {}}


# Metadata the sample-data schema guess cannot know: the timestamp (needed by the
# persist pipeline) and the sensor id as dimension (becomes a tag in the storage).
COMMON_SCHEMA = {
    TIMESTAMP_FIELD: prop("Timestamp", "header", "http://schema.org/DateTime"),
    SENSOR_ID_FIELD: prop("Sensor ID", "dimension", None),
}
SIMULATOR_SCHEMAS = {
    "flowrate": {**COMMON_SCHEMA, "mass_flow": prop("Mass flow"), "volume_flow": prop("Volume flow"),
                 "temperature": prop("Temperature"), "density": prop("Density")},
    "pressure": {**COMMON_SCHEMA, "pressure": prop("Pressure")},
    "waterlevel": {**COMMON_SCHEMA, "level": prop("Level")},
}

# ---------------------------------------------------------------------------
# Scenario
# ---------------------------------------------------------------------------
SITES = {
    "riverside": {"label": "Riverside Water Works", "latitude": 49.0134, "longitude": 8.4044, "zoom": 13,
                  "areas": ["Intake", "Treatment", "Storage"]},
    "hillside": {"label": "Hillside Reservoir", "latitude": 49.0521, "longitude": 8.4701, "zoom": 13,
                 "areas": ["Reservoir"]},
}

LABELS = {
    "demo": {"label": "Demo", "description": "Created by the all-in-one demo seed", "color": "#1b1464"},
    "critical": {"label": "Critical asset", "description": "Outage stops water supply", "color": "#f44336"},
}

# key -> adapter definition; the persist pipeline is named "Persist <name>"
ADAPTERS = {
    "intake_pumps": {"name": "Intake Pumps", "mode": "flowrate", "sensors": 3, "dataset": "intake_pumps",
                     "description": "Three intake pumps reporting flow, temperature and density (simulated)"},
    "main_line_pressure": {"name": "Main Line Pressure", "mode": "pressure", "sensors": 2,
                           "dataset": "main_line_pressure",
                           "description": "Two pressure sensors on the main treatment line (simulated)"},
    "clear_water_tank": {"name": "Clear Water Tank", "mode": "waterlevel", "sensors": 1,
                         "dataset": "clear_water_tank",
                         "description": "Level sensor of the clear-water storage tank (simulated)"},
    "reservoir_level": {"name": "Reservoir Level", "mode": "waterlevel", "sensors": 2, "dataset": "reservoir_level",
                        "description": "Two level sensors in the hillside reservoir (simulated)"},
}

# Analytics pipelines on top of the adapter streams: stream -> processor -> dataset
PIPELINES = {
    "pump_overheating": {
        "name": "Pump Overheating Detection", "adapter": "intake_pumps", "dataset": "pump_overheating",
        "description": "Keeps pump events whose temperature exceeds 80 degrees",
        "processor": {"id": NUMERICAL_FILTER_APP_ID, "configuration": [
            {"number-mapping": "s0::temperature"}, {"operation": ">"}, {"value": 80}]},
    },
    "pressure_smoothing": {
        "name": "Pressure Smoothing", "adapter": "main_line_pressure", "dataset": "main_line_pressure_smoothed",
        "description": "Adds a moving average over the last 10 pressure readings",
        "processor": {"id": MOVING_AVERAGE_APP_ID, "configuration": [
            {"number": "s0::pressure"}, {"n": 10}, {"method": "mean"}]},
    },
    "tank_overflow": {
        "name": "Tank Overflow Events", "adapter": "clear_water_tank", "dataset": "tank_overflow_events",
        "description": "Records every event in which the tank reports an overflow",
        "processor": {"id": BOOLEAN_FILTER_APP_ID, "configuration": [
            {"boolean-mapping": "s0::overflow"}, {"value": "True"}]},
    },
}

# Charts: type, title, dataset and type-specific options
CHARTS = {
    "pump_flow": {"type": "time-series", "title": "Intake pump flow", "dataset": "intake_pumps",
                  "fields": ["mass_flow"], "group_by": True},
    "pump_temperature": {"type": "time-series", "title": "Intake pump temperature", "dataset": "intake_pumps",
                         "fields": ["temperature"], "group_by": True},
    "pump_temperature_gauge": {"type": "gauge", "title": "Latest pump temperature", "dataset": "intake_pumps",
                               "field": "temperature", "min": 0, "max": 120, "low": 60, "high": 80},
    "pump_faults": {"type": "status-heatmap", "title": "Pump fault flags", "dataset": "intake_pumps",
                    "field": "sensor_fault_flags"},
    "pump_overheating": {"type": "table", "title": "Pump overheating events", "dataset": "pump_overheating",
                         "columns": ["timestamp", "sensorId", "temperature", "mass_flow"]},
    "pressure": {"type": "time-series", "title": "Main line pressure", "dataset": "main_line_pressure",
                 "fields": ["pressure"], "group_by": True},
    "pressure_smoothed": {"type": "time-series", "title": "Main line pressure (smoothed)",
                          "dataset": "main_line_pressure_smoothed", "fields": ["pressure", "filterResult"],
                          "group_by": True},
    "tank_level": {"type": "time-series", "title": "Clear water tank level", "dataset": "clear_water_tank",
                   "fields": ["level"]},
    "tank_latest": {"type": "value-card", "title": "Clear water tank (latest)", "dataset": "clear_water_tank",
                    "fields": ["level", "overflow"]},
    "tank_overflow": {"type": "table", "title": "Tank overflow events", "dataset": "tank_overflow_events",
                      "columns": ["timestamp", "sensorId", "level"]},
    "reservoir_level": {"type": "time-series", "title": "Reservoir level", "dataset": "reservoir_level",
                        "fields": ["level"], "group_by": True},
}

# Dashboards: name, description and widgets as (chart key, x, y, cols, rows) on an 8-column grid
DASHBOARDS = {
    "overview": {"name": "Plant Overview", "description": "Key figures of both sites at a glance", "widgets": [
        ("pump_temperature_gauge", 0, 0, 2, 3), ("tank_latest", 2, 0, 2, 3), ("pump_flow", 4, 0, 4, 3),
        ("pressure", 0, 3, 4, 3), ("reservoir_level", 4, 3, 4, 3), ("pump_overheating", 0, 6, 8, 3)]},
    "pumps": {"name": "Intake Pump Station", "description": "Flow, temperature and alarms of the three intake pumps",
              "widgets": [("pump_flow", 0, 0, 4, 3), ("pump_temperature", 4, 0, 4, 3),
                          ("pump_faults", 0, 3, 4, 3), ("pump_temperature_gauge", 4, 3, 2, 3),
                          ("pump_overheating", 0, 6, 8, 3)]},
    "storage": {"name": "Water Storage", "description": "Clear water tank, reservoir and the pressure line",
                "widgets": [("tank_latest", 0, 0, 2, 3), ("tank_level", 2, 0, 6, 3),
                            ("reservoir_level", 0, 3, 4, 3), ("pressure_smoothed", 4, 3, 4, 3),
                            ("tank_overflow", 0, 6, 8, 3)]},
}

# Assets: top-level assets with their site, links and sub-assets
ASSETS = [
    {"name": "Intake Pump Station", "description": "Three centrifugal pumps lifting raw water from the river",
     "isa95": "PRODUCTION_UNIT", "site": "riverside", "area": "Intake", "labels": ["demo", "critical"],
     "adapters": ["intake_pumps"], "pipelines": ["pump_overheating"],
     "charts": ["pump_flow", "pump_temperature", "pump_temperature_gauge", "pump_faults", "pump_overheating"],
     "dashboards": ["pumps"],
     "sub_assets": [{"name": f"Pump P-10{i}", "description": f"Intake pump {i} (sensor flowrate0{i})",
                     "isa95": "UNIT", "labels": ["demo"]} for i in (1, 2, 3)]},
    {"name": "Main Pressure Line", "description": "Pressurised line from the pump station to the treatment stage",
     "isa95": "PRODUCTION_LINE", "site": "riverside", "area": "Treatment", "labels": ["demo"],
     "adapters": ["main_line_pressure"], "pipelines": ["pressure_smoothing"],
     "charts": ["pressure", "pressure_smoothed"], "dashboards": []},
    {"name": "Clear Water Tank T-200", "description": "Storage tank for treated water before distribution",
     "isa95": "STORAGE_UNIT", "site": "riverside", "area": "Storage", "labels": ["demo", "critical"],
     "adapters": ["clear_water_tank"], "pipelines": ["tank_overflow"],
     "charts": ["tank_level", "tank_latest", "tank_overflow"], "dashboards": ["storage"]},
    {"name": "Hillside Reservoir Basin", "description": "Open reservoir feeding the works by gravity",
     "isa95": "STORAGE_ZONE", "site": "hillside", "area": "Reservoir", "labels": ["demo"],
     "adapters": ["reservoir_level"], "pipelines": [], "charts": ["reservoir_level"], "dashboards": ["overview"]},
]

LOCATION_CONFIG = {"locationEnabled": True, "mapLayerType": "TILE",
                   "tileServerUrl": "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
                   "attributionText": "&copy; OpenStreetMap contributors"}


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
def log(message):
    print(f"[demo-seed] {message}", flush=True)


def now_ms():
    return int(time.time() * 1000)


class Api:
    def __init__(self, base_url):
        self.base_url = base_url
        self.token = None

    def request(self, method, path, body=None):
        headers = {"Accept": "application/json"}
        data = None
        if body is not None:
            data = json.dumps(body).encode("utf-8")
            headers["Content-Type"] = "application/json"
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        request = urllib.request.Request(self.base_url + path, data=data, method=method, headers=headers)
        with urllib.request.urlopen(request, timeout=60) as response:
            raw = response.read()
        return json.loads(raw) if raw else None

    def get(self, path):
        return self.request("GET", path)

    def post(self, path, body):
        return self.request("POST", path, body)

    def put(self, path, body):
        return self.request("PUT", path, body)


def wait_until(description, probe, timeout=READY_TIMEOUT_SECONDS):
    """Calls probe() until it returns a truthy value; failures count as 'not yet'."""
    deadline = time.monotonic() + timeout
    last_error = None
    while time.monotonic() < deadline:
        try:
            result = probe()
            if result:
                return result
        except (urllib.error.URLError, OSError, ValueError, KeyError) as error:
            last_error = error
        time.sleep(POLL_INTERVAL_SECONDS)
    raise TimeoutError(f"gave up waiting for {description} (last error: {last_error})")


def core_is_configured(api):
    return api.get("/api/v2/setup/configured").get("configured") is True


def login(api):
    response = api.post("/api/v2/auth/login", {"username": ADMIN_EMAIL, "password": ADMIN_PASSWORD, "rememberMe": False})
    api.token = response["accessToken"]
    return True


def required_extension_items(api):
    items = {item["appId"]: item for item in api.get("/api/v2/extension-items")}
    found = [items[app_id] for app_id in REQUIRED_EXTENSIONS if app_id in items]
    return found if len(found) == len(REQUIRED_EXTENSIONS) else None


def ensure_extensions_installed(api):
    wait_until("the IIoT extensions service to register", lambda: required_extension_items(api))
    log("Extensions service is registered")
    # The core installs all extensions during its initial setup; give it a moment before stepping in.
    try:
        wait_until("the initial extension installation",
                   lambda: all(item["installed"] for item in required_extension_items(api)), timeout=90)
    except TimeoutError:
        for item in required_extension_items(api):
            if not item["installed"]:
                log(f"Installing extension {item['appId']}")
                api.post("/api/v2/extension-installation",
                         {"appId": item["appId"], "serviceTagPrefix": item["serviceTagPrefix"], "publicElement": True})
        wait_until("the extension installation",
                   lambda: all(item["installed"] for item in required_extension_items(api)), timeout=120)
    # "available" means the extensions service that provides them is up, which starting an adapter or pipeline needs.
    wait_until("the extensions service to become available",
               lambda: all(item["installed"] and item["available"] for item in required_extension_items(api)))
    log("Required extensions are installed and available")


# ---------------------------------------------------------------------------
# Adapters and pipelines
# ---------------------------------------------------------------------------
def find_by_name(items, name):
    return next((item for item in items if item.get("name") == name), None)


def find_adapter(api, name):
    return find_by_name(api.get("/api/v2/connect/master/adapters"), name)


def find_pipeline(api, name):
    return find_by_name(api.get("/api/v2/pipelines"), name)


def ensure_adapter(api, definition):
    name = definition["name"]
    adapter = find_adapter(api, name)
    if adapter is None:
        log(f"Creating adapter '{name}' and its persist pipeline")
        response = api.post("/api/v2/connect/compact-adapters", {
            "name": name,
            "description": definition["description"],
            "appId": SIMULATOR_APP_ID,
            "configuration": [
                {"wait-time-ms": "1000"},
                {"numberOfSensors": str(definition["sensors"])},
                {"selected-simulator-option": definition["mode"]},
            ],
            "schema": SIMULATOR_SCHEMAS[definition["mode"]],
            "createOptions": {"persist": True, "start": True, "persistDatasetName": definition["dataset"]},
        })
        if response and response.get("success") is False:
            raise RuntimeError(f"adapter creation failed: {response}")
        adapter = wait_until(f"adapter '{name}' to be stored", lambda: find_adapter(api, name), timeout=60)
    else:
        log(f"Adapter '{name}' already exists")
        if not adapter.get("running"):
            # After a restart the core recovers adapters and pipelines itself; only step in if it does not.
            try:
                wait_until(f"the core to recover adapter '{name}'",
                           lambda: find_adapter(api, name).get("running"), timeout=RECOVERY_GRACE_SECONDS)
            except TimeoutError:
                log(f"Starting adapter '{name}'")
                api.post(f"/api/v2/connect/master/adapters/{adapter['elementId']}/start", {})
    return adapter


def persist_pipeline_elements(adapter, dataset):
    return [
        {"type": "stream", "ref": "stream1", "id": adapter["correspondingDataStreamElementId"]},
        dataset_sink_element("stream1", dataset),
    ]


def dataset_sink_element(connected_to, dataset):
    return {"type": "sink", "ref": "lake", "id": DATASET_SINK_APP_ID, "connectedTo": [connected_to],
            "configuration": [
                {"schema_update": "Update schema"},
                {"ignore_duplicates": False},
                {"db_measurement": dataset},
                {"timestamp_mapping": f"s0::{TIMESTAMP_FIELD}"},
                {"dimensions_selection": [SENSOR_ID_FIELD]},
            ]}


def ensure_pipeline(api, pipeline_id, name, description, elements):
    pipeline = find_pipeline(api, name)
    if pipeline is None:
        log(f"Creating pipeline '{name}'")
        response = api.post("/api/v2/compact-pipelines", {
            "id": pipeline_id, "name": name, "description": description,
            "pipelineElements": elements, "createOptions": {"start": True},
        })
        if isinstance(response, dict) and response.get("success") is False:
            raise RuntimeError(f"pipeline '{name}' failed: {response}")
        pipeline = wait_until(f"pipeline '{name}' to be stored", lambda: find_pipeline(api, name), timeout=60)
    elif not pipeline.get("running"):
        try:
            wait_until(f"the core to restart pipeline '{name}'",
                       lambda: find_pipeline(api, name).get("running"), timeout=RECOVERY_GRACE_SECONDS)
            log(f"Pipeline '{name}' was restarted by the core")
        except TimeoutError:
            log(f"Starting pipeline '{name}'")
            api.get(f"/api/v2/pipelines/{pipeline['elementId']}/start")
    else:
        log(f"Pipeline '{name}' is running")
    return pipeline


def ensure_machines(api):
    """Creates adapters, their persist pipelines and the analytics pipelines. Returns the ids by key."""
    adapters, pipelines = {}, {}
    for key, definition in ADAPTERS.items():
        adapters[key] = ensure_adapter(api, definition)
    for key, definition in ADAPTERS.items():
        # The persist option normally creates this pipeline; recreate it if it is missing.
        pipelines[f"persist_{key}"] = ensure_pipeline(
            api, f"demo-persist-{key.replace('_', '-')}", f"Persist {definition['name']}",
            f"Stores the events of {definition['name']} in the time-series storage",
            persist_pipeline_elements(adapters[key], definition["dataset"]))
    for key, definition in PIPELINES.items():
        processor = definition["processor"]
        elements = [
            {"type": "stream", "ref": "stream1", "id": adapters[definition["adapter"]]["correspondingDataStreamElementId"]},
            {"type": "processor", "ref": "processor", "id": processor["id"], "connectedTo": ["stream1"],
             "configuration": processor["configuration"]},
            dataset_sink_element("processor", definition["dataset"]),
        ]
        pipelines[key] = ensure_pipeline(api, f"demo-{key.replace('_', '-')}", definition["name"],
                                         definition["description"], elements)
    return adapters, pipelines


# ---------------------------------------------------------------------------
# Datasets and charts
# ---------------------------------------------------------------------------
def dataset_metadata(api, name):
    measure = api.get(f"/api/v4/datalake/measure/byName/{name}")
    properties = (measure or {}).get("eventSchema", {}).get("eventProperties") or []
    return measure if properties else None


def wait_for_datasets(api):
    names = [a["dataset"] for a in ADAPTERS.values()] + [p["dataset"] for p in PIPELINES.values()]
    datasets = {}
    for name in names:
        datasets[name] = wait_until(f"dataset '{name}' to receive its schema", lambda: dataset_metadata(api, name),
                                    timeout=300)
    log(f"All {len(datasets)} datasets have a schema")
    return datasets


def is_numeric(p):
    return p.get("runtimeType") in NUMERIC_TYPES


def is_boolean(p):
    return p.get("runtimeType") == BOOLEAN_TYPE


def make_field(measure, p):
    field = {
        "runtimeName": p["runtimeName"], "measure": measure["measureName"], "fullDbName": p["runtimeName"],
        "sourceIndex": 0,
        "fieldCharacteristics": {
            "dimension": p.get("propertyScope") == "DIMENSION_PROPERTY", "numeric": is_numeric(p),
            "binary": is_boolean(p), "semanticTypes": [p.get("semanticType")],
        },
    }
    if p.get("measurementUnit"):
        field["measurementUnitResourceId"] = p["measurementUnit"]
    return field


def properties_of(measure):
    return sorted(measure["eventSchema"]["eventProperties"], key=lambda p: p["runtimeName"])


def fields_of(measure, names=None):
    return [make_field(measure, p) for p in properties_of(measure) if names is None or p["runtimeName"] in names]


def field_key(field):
    return field["fullDbName"] + str(field["sourceIndex"])


def last_15_minutes():
    now = now_ms()
    return {"startTime": now - 15 * 60 * 1000, "endTime": now, "dynamicSelection": -1,
            "timeSelectionId": "last-15-minutes"}


def source_config(measure, group_by=False, latest_only=False):
    query = {
        "selectedFilters": [], "resultLabelOverrides": {},
        "fields": [{"runtimeName": p["runtimeName"], "selected": True, "numeric": is_numeric(p),
                    "aggregations": ["MEAN" if is_numeric(p) else "MODE"]} for p in properties_of(measure)],
        "groupBy": [{"runtimeName": SENSOR_ID_FIELD, "selected": True, "numeric": False, "aggregations": ["MODE"]}]
        if group_by else [],
        "aggregationTimeUnit": "d", "aggregationValue": 1, "fill": "none",
    }
    if latest_only:
        query.update({"order": "DESC", "limit": 1, "page": 1})
    return {"measureName": measure["measureName"], "measure": measure, "queryType": "raw", "queryConfig": query}


def chart_model(definition, measure, visualization, group_by=False, latest_only=False):
    now = now_ms()
    visualization.setdefault("configurationValid", True)
    appearance = {"widgetTitle": definition["title"], "backgroundColor": "var(--color-bg-0)",
                  "textColor": "var(--color-default-text)"}
    if definition["type"] in ("gauge", "value-card", "table"):
        appearance["numberFormat"] = {"decimals": 2}
    return {
        "@class": "org.apache.streampipes.model.datalake.DataExplorerWidgetModel",
        "widgetType": definition["type"] if definition["type"] != "time-series" else "time-series-chart",
        "baseAppearanceConfig": appearance,
        "dataConfig": {"ignoreMissingValues": False, "ignoreTooMuchDataWarning": True,
                       "sourceConfigs": [source_config(measure, group_by, latest_only)]},
        "visualizationConfig": visualization,
        "timeSettings": last_15_minutes(),
        "metadata": {"createdAtEpochMs": now, "lastModifiedEpochMs": now},
    }


def build_chart(definition, measure):
    kind = definition["type"]
    if kind == "time-series":
        plottable = [f for f in fields_of(measure) if f["fieldCharacteristics"]["numeric"] or f["fieldCharacteristics"]["binary"]]
        visualization = {
            "selectedTimeSeriesChartProperties": [f for f in plottable if f["runtimeName"] in definition["fields"]],
            "yKeys": [],
            "chosenColor": {field_key(f): PRESET_COLORS[i % len(PRESET_COLORS)] for i, f in enumerate(plottable)},
            "displayName": {field_key(f): f["fullDbName"] for f in plottable},
            "displayType": {field_key(f): "lines" for f in plottable},
            "chosenAxis": {field_key(f): "left" for f in plottable},
            "groupedColorMode": {field_key(f): "stable_palette" for f in plottable},
            "groupedColorMappings": {field_key(f): [] for f in plottable},
            "leftAxis": {"autoScaleActive": True, "axisMin": 0, "axisMax": 100},
            "rightAxis": {"autoScaleActive": True, "axisMin": 0, "axisMax": 100},
            "showSpike": True,
        }
        return chart_model(definition, measure, visualization, group_by=definition.get("group_by", False))
    if kind == "gauge":
        field = fields_of(measure, [definition["field"]])[0]
        visualization = {
            "selectedProperty": field, "displayName": field["runtimeName"],
            "min": definition["min"], "max": definition["max"], "startAngle": 225, "endAngle": -45,
            "splitNumber": 10, "showPointer": True, "enableThresholdColors": True,
            "thresholdLow": definition["low"], "thresholdHigh": definition["high"],
            "thresholdColorLow": "#91cc75", "thresholdColorMedium": "#fac858", "thresholdColorHigh": "#ee6666",
        }
        return chart_model(definition, measure, visualization, latest_only=True)
    if kind == "value-card":
        visualization = {"selectedFields": fields_of(measure, definition["fields"]), "title": "",
                         "description": "", "showTimestamp": True}
        return chart_model(definition, measure, visualization, latest_only=True)
    if kind == "table":
        visualization = {"selectedColumns": fields_of(measure, definition["columns"]), "highlightedColumns": [],
                         "highlightedColumnColors": {}, "pageSize": 20, "stickyHeaders": True, "searchValue": ""}
        return chart_model(definition, measure, visualization)
    if kind == "status-heatmap":
        field = fields_of(measure, [definition["field"]])[0]
        visualization = {"selectedProperty": field, "isSelectedPropertyBoolean": field["fieldCharacteristics"]["binary"],
                         "showCustomColorMappingStatusHeatmap": False, "colorMappingsStatusHeatmap": []}
        return chart_model(definition, measure, visualization)
    raise ValueError(f"unknown chart type {kind}")


def ensure_charts(api, datasets):
    existing = {(c.get("baseAppearanceConfig") or {}).get("widgetTitle"): c["elementId"]
                for c in api.get("/api/v3/datalake/dashboard/widgets")}
    charts = {}
    for key, definition in CHARTS.items():
        if definition["title"] in existing:
            charts[key] = existing[definition["title"]]
            continue
        log(f"Creating chart '{definition['title']}'")
        charts[key] = api.post("/api/v3/datalake/dashboard/widgets",
                               build_chart(definition, datasets[definition["dataset"]]))["elementId"]
    log(f"{len(charts)} charts available")
    return charts


def ensure_dashboards(api, charts):
    existing = {d.get("name"): d["elementId"] for d in api.get("/api/v3/datalake/dashboard")}
    dashboards = {}
    for key, definition in DASHBOARDS.items():
        if definition["name"] in existing:
            dashboards[key] = existing[definition["name"]]
            continue
        log(f"Creating dashboard '{definition['name']}'")
        now = now_ms()
        response = api.post("/api/v3/datalake/dashboard", {
            "name": definition["name"], "description": definition["description"],
            "dashboardGeneralSettings": {"chartOverrides": {"hideToolbox": False, "borderThickness": 0},
                                         "defaultViewMode": "grid", "globalTimeEnabled": True, "gridRowHeightPx": 90},
            "dashboardLiveSettings": {"label": "5 sec", "refreshModeActive": True, "refreshIntervalInSeconds": 5},
            "dashboardTimeSettings": last_15_minutes(),
            "widgets": [{"id": secrets.token_hex(5), "x": x, "y": y, "cols": cols, "rows": rows, "w": cols, "h": rows,
                         "dataViewElementId": charts[chart]} for chart, x, y, cols, rows in definition["widgets"]],
            "metadata": {"createdAtEpochMs": now, "lastModifiedEpochMs": now},
        })
        dashboards[key] = response["elementId"]
    log(f"{len(dashboards)} dashboards available")
    return dashboards


# ---------------------------------------------------------------------------
# Sites, labels and assets
# ---------------------------------------------------------------------------
def ensure_generic_documents(api, app_doc_name, definitions, match_key="label"):
    existing = {doc.get(match_key): doc["_id"] for doc in api.get(f"/api/v2/storage-generic/{app_doc_name}")}
    ids = {}
    for key, definition in definitions.items():
        if definition[match_key] in existing:
            ids[key] = existing[definition[match_key]]
        else:
            log(f"Creating {app_doc_name} document '{definition[match_key]}'")
            ids[key] = api.post(f"/api/v2/storage-generic/{app_doc_name}", definition)["_id"]
    return ids


def ensure_sites(api):
    documents = {key: {"appDocType": "asset-sites", "label": site["label"], "areas": site["areas"],
                       "location": {"coordinates": {"latitude": site["latitude"], "longitude": site["longitude"]},
                                    "zoom": site["zoom"]}} for key, site in SITES.items()}
    return ensure_generic_documents(api, "asset-sites", documents)


def ensure_labels(api):
    documents = {key: {"appDocType": "sp-labels", **label} for key, label in LABELS.items()}
    return ensure_generic_documents(api, "sp-labels", documents)


def ensure_location_config(api):
    config = api.get("/api/v2/admin/location-config")
    if not config.get("locationEnabled"):
        log("Enabling the site map on the home page")
        api.put("/api/v2/admin/location-config", LOCATION_CONFIG)


def asset_link(resource_id, link_type, label, navigation=True):
    return {"resourceId": resource_id, "linkType": link_type, "linkLabel": label, "queryHint": link_type,
            "editingDisabled": False, "navigationActive": navigation}


def asset_links(definition, ctx):
    links = []
    for key in definition.get("adapters", []):
        adapter = ctx["adapters"][key]
        links.append(asset_link(adapter["elementId"], "adapter", adapter["name"]))
        links.append(asset_link(adapter["correspondingDataStreamElementId"], "data-stream", adapter["name"], False))
        links.append(asset_link(ctx["pipelines"][f"persist_{key}"]["elementId"], "pipeline", f"Persist {adapter['name']}"))
        dataset = ctx["datasets"][ADAPTERS[key]["dataset"]]
        links.append(asset_link(dataset["elementId"], "dataset", dataset["measureName"]))
    for key in definition.get("pipelines", []):
        links.append(asset_link(ctx["pipelines"][key]["elementId"], "pipeline", PIPELINES[key]["name"]))
        dataset = ctx["datasets"][PIPELINES[key]["dataset"]]
        links.append(asset_link(dataset["elementId"], "dataset", dataset["measureName"]))
    for key in definition.get("charts", []):
        links.append(asset_link(ctx["charts"][key], "chart", CHARTS[key]["title"]))
    for key in definition.get("dashboards", []):
        links.append(asset_link(ctx["dashboards"][key], "dashboard", DASHBOARDS[key]["name"]))
    return links


def asset_node(definition, ctx):
    site = SITES[definition["site"]] if "site" in definition else None
    node = {
        "assetId": secrets.token_hex(4), "assetName": definition["name"],
        "assetDescription": definition["description"],
        "assetType": {"isa95AssetType": definition["isa95"]},
        "assetLinks": asset_links(definition, ctx),
        "assets": [asset_node(sub, ctx) for sub in definition.get("sub_assets", [])],
        "labelIds": [ctx["labels"][key] for key in definition.get("labels", [])],
        "additionalData": {},
    }
    if site:
        node["assetSite"] = {"siteId": ctx["sites"][definition["site"]], "area": definition["area"],
                             "hasExactLocation": False,
                             "location": {"coordinates": {"latitude": site["latitude"], "longitude": site["longitude"]},
                                          "zoom": site["zoom"]}}
    return node


def ensure_assets(api, ctx):
    existing = {asset.get("assetName") for asset in api.get("/api/v2/assets")}
    created = 0
    for definition in ASSETS:
        if definition["name"] in existing:
            continue
        log(f"Creating asset '{definition['name']}'")
        api.post("/api/v2/assets", {"appDocType": "asset-management", "removable": True, **asset_node(definition, ctx)})
        created += 1
    log(f"{len(ASSETS)} assets available ({created} created)")


# ---------------------------------------------------------------------------
def main():
    if not SEED_ENABLED:
        log("SP_DEMO_SEED is false, skipping the demo seed")
        return 0
    api = Api(BACKEND_URL)
    log("Waiting for the StreamPipes core to finish its initial setup ...")
    wait_until("the StreamPipes core", lambda: core_is_configured(api))
    wait_until("the administrator login", lambda: login(api))
    log("Core is ready")
    ensure_extensions_installed(api)
    adapters, pipelines = ensure_machines(api)
    datasets = wait_for_datasets(api)
    charts = ensure_charts(api, datasets)
    dashboards = ensure_dashboards(api, charts)
    ctx = {"adapters": adapters, "pipelines": pipelines, "datasets": datasets, "charts": charts,
           "dashboards": dashboards, "sites": ensure_sites(api), "labels": ensure_labels(api)}
    ensure_location_config(api)
    ensure_assets(api, ctx)
    log("Demo is ready: sign in and open 'Dashboards' (Plant Overview, Intake Pump Station, Water Storage), "
        "'Assets' for the plant structure or the home page for the site map.")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as error:  # noqa: BLE001 - report any failure in the container log
        log(f"ERROR: demo seed failed: {error}")
        sys.exit(1)
