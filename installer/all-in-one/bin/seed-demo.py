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

The scenario covers three sites of a fictional company:

* Riverside Water Works: an intake pump station (three pumps), the main
  pressure line (two sensors) and a clear-water storage tank.
* Hillside Reservoir: a reservoir with two level sensors.
* Powertrain Components Plant: three CNC machining centres, two screwing
  stations on an assembly line and an end-of-line leak test rig.

Every machine is a machine data simulator adapter whose ``persist`` option
also creates the "Persist ..." pipeline; the CNC adapter additionally runs a
JavaScript transformation that enriches the raw events. On top of that,
analytics pipelines detect overheating pumps, worn tools, NOK joints and
failed tests, smooth signals and record tank overflow events. Charts,
dashboards, sites, labels and assets tie it together. Everything goes
through the public REST API of the core.

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
CHANGED_VALUE_APP_ID = "org.apache.streampipes.processors.transformation.jvm.changed-value"
REQUIRED_EXTENSIONS = (SIMULATOR_APP_ID, DATASET_SINK_APP_ID, NUMERICAL_FILTER_APP_ID,
                       BOOLEAN_FILTER_APP_ID, MOVING_AVERAGE_APP_ID, CHANGED_VALUE_APP_ID)

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
    "cnc": {**COMMON_SCHEMA, "spindle_speed": prop("Spindle speed"), "spindle_load": prop("Spindle load"),
            "spindle_temperature": prop("Spindle temperature"), "tool_wear": prop("Tool wear"),
            "vibration": prop("Vibration"), "spindle_power_kw": prop("Spindle power"),
            "tool_wear_class": prop("Tool wear class", semantic=None)},
    "assembly": {**COMMON_SCHEMA, "torque": prop("Torque"), "torque_angle": prop("Torque angle"),
                 "cycle_time": prop("Cycle time"), "cycle_progress": prop("Cycle progress")},
    "quality": {**COMMON_SCHEMA, "test_pressure": prop("Test pressure"), "leak_rate": prop("Leak rate"),
                "test_duration": prop("Test duration")},
}

# Script-based transformation of the CNC adapter: the raw simulator events are enriched
# in the adapter before they reach any pipeline (StreamPipes runs it with GraalJS).
CNC_TRANSFORM_SCRIPT = """function transform(event, out, ctx) {
  // the machining centres have an 18 kW spindle: derive the electrical power from the load
  event.spindle_power_kw = Math.round(event.spindle_load * 18) / 100;
  // classify the tool wear so dashboards and filters can work with a state instead of a number
  event.tool_wear_class = event.tool_wear >= 70 ? 'REPLACE' : (event.tool_wear >= 40 ? 'WARNING' : 'OK');
  out.collect(event);
}
"""

# ---------------------------------------------------------------------------
# Scenario
# ---------------------------------------------------------------------------
SITES = {
    "riverside": {"label": "Riverside Water Works", "latitude": 49.0134, "longitude": 8.4044, "zoom": 13,
                  "areas": ["Intake", "Treatment", "Storage"]},
    "hillside": {"label": "Hillside Reservoir", "latitude": 49.0521, "longitude": 8.4701, "zoom": 13,
                 "areas": ["Reservoir"]},
    "powertrain": {"label": "Powertrain Components Plant", "latitude": 49.1247, "longitude": 8.5983, "zoom": 13,
                   "areas": ["Machining", "Assembly", "Quality"]},
}

LABELS = {
    "demo": {"label": "Demo", "description": "Created by the all-in-one demo seed", "color": "#1b1464"},
    "critical": {"label": "Critical asset", "description": "Outage stops production or supply", "color": "#f44336"},
    "automotive": {"label": "Automotive", "description": "Gearbox housing line for an automotive customer",
                   "color": "#433bff"},
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
    "cnc_machining": {"name": "CNC Machining Centres", "mode": "cnc", "sensors": 3, "dataset": "cnc_machining",
                      "description": "Three CNC machining centres milling gearbox housings (simulated), "
                                     "enriched by a JavaScript transformation",
                      "script": CNC_TRANSFORM_SCRIPT},
    "assembly_stations": {"name": "Assembly Screwing Stations", "mode": "assembly", "sensors": 2,
                          "dataset": "assembly_stations",
                          "description": "Two screwing stations on the gearbox assembly line (simulated)"},
    "eol_leak_test": {"name": "End-of-Line Leak Test", "mode": "quality", "sensors": 1, "dataset": "eol_leak_test",
                      "description": "Leak test rig at the end of the assembly line (simulated)"},
}

# Analytics pipelines on top of the adapter streams: stream -> processor(s) -> dataset.
# "processor" is a single processor, "processors" a chain; both take compact configurations.
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
    "tool_wear_warnings": {
        "name": "Tool Wear Warnings", "adapter": "cnc_machining", "dataset": "cnc_tool_wear_warnings",
        "description": "Emits one event whenever a tool enters the WARNING or REPLACE class (derived by the adapter script)",
        "processors": [
            {"id": CHANGED_VALUE_APP_ID, "configuration": [{"compare": "s0::tool_wear_class"}]},
            {"id": NUMERICAL_FILTER_APP_ID, "configuration": [
                {"number-mapping": "s0::tool_wear"}, {"operation": ">="}, {"value": 40}]},
        ],
    },
    "vibration_smoothing": {
        "name": "Spindle Vibration Smoothing", "adapter": "cnc_machining", "dataset": "cnc_vibration_smoothed",
        "description": "Adds a moving average over the last 20 vibration readings",
        "processor": {"id": MOVING_AVERAGE_APP_ID, "configuration": [
            {"number": "s0::vibration"}, {"n": 20}, {"method": "mean"}]},
    },
    "nok_joints": {
        "name": "NOK Joint Detection", "adapter": "assembly_stations", "dataset": "assembly_nok_joints",
        "description": "Emits one event per joint tightened outside the torque specification",
        "processors": [
            {"id": CHANGED_VALUE_APP_ID, "configuration": [{"compare": "s0::parts_nok"}]},
            {"id": BOOLEAN_FILTER_APP_ID, "configuration": [{"boolean-mapping": "s0::screw_ok"}, {"value": "False"}]},
        ],
    },
    "failed_tests": {
        "name": "Failed Leak Tests", "adapter": "eol_leak_test", "dataset": "eol_failed_tests",
        "description": "Emits one event per part that failed the end-of-line leak test",
        "processors": [
            {"id": CHANGED_VALUE_APP_ID, "configuration": [{"compare": "s0::parts_failed"}]},
            {"id": BOOLEAN_FILTER_APP_ID, "configuration": [{"boolean-mapping": "s0::passed"}, {"value": "False"}]},
        ],
    },
}

# Display names of the fields as shown in legends, table columns, value cards and axes.
FIELD_LABELS = {
    "timestamp": "Time", "sensorId": "Sensor", "mass_flow": "Mass flow", "volume_flow": "Volume flow",
    "temperature": "Temperature", "density": "Density", "sensor_fault_flags": "Sensor fault",
    "pressure": "Pressure", "level": "Level", "overflow": "Overflow", "filterResult": "Moving average",
    "program_id": "Program", "machine_state": "Machine state", "spindle_speed": "Spindle speed",
    "spindle_load": "Spindle load", "feed_rate": "Feed rate", "spindle_temperature": "Spindle temperature",
    "tool_wear": "Tool wear", "coolant_flow": "Coolant flow", "vibration": "Vibration",
    "parts_completed": "Parts completed", "spindle_power_kw": "Spindle power", "tool_wear_class": "Tool wear class",
    "station_state": "Station state", "part_id": "Part", "cycle_progress": "Cycle progress", "torque": "Torque",
    "torque_angle": "Torque angle", "cycle_time": "Cycle time", "screw_ok": "Joint OK", "parts_ok": "Parts OK",
    "parts_nok": "Parts NOK", "test_state": "Test state", "test_pressure": "Test pressure", "leak_rate": "Leak rate",
    "test_duration": "Test duration", "passed": "Passed", "test_result": "Test result",
    "parts_tested": "Parts tested", "parts_failed": "Parts failed", "change_detected": "Change detected",
}
AGGREGATION_LABELS = {"MEAN": "Average", "MAX": "Maximum", "MIN": "Minimum", "SUM": "Sum", "COUNT": "Count",
                      "MODE": "Most frequent"}


def field_label(runtime_name, aggregation=None):
    label = FIELD_LABELS.get(runtime_name, runtime_name.replace("_", " ").capitalize())
    return f"{AGGREGATION_LABELS.get(aggregation, aggregation)} {label.lower()}" if aggregation else label


# Charts: type, title, dataset and type-specific options. Query options shared by all types:
#   group_by: True        split the series per sensor (dimension sensorId)
#   aggregate: {...}      aggregated query (time interval + function per field) instead of raw events
# Time-series options: fields, display (per field: lines | area | bar | normal_markers), axis (per field:
# left | right), zoom (slider below the chart). Single-value charts (gauge, value-card, indicator,
# progress-bar, traffic-light, status) read the latest event.
CHARTS = {
    # --- Riverside Water Works
    "pump_flow": {"type": "time-series", "title": "Intake pump flow", "dataset": "intake_pumps",
                  "fields": ["mass_flow"], "group_by": True},
    "pump_temperature": {"type": "time-series", "title": "Intake pump temperature", "dataset": "intake_pumps",
                         "fields": ["temperature"], "group_by": True, "display": {"temperature": "area"}, "zoom": True},
    "pump_temperature_heatmap": {"type": "heatmap", "title": "Pump temperature heatmap", "dataset": "intake_pumps",
                                 "field": "temperature", "group_by": True, "min": 40, "max": 100},
    "pump_temperature_gauge": {"type": "gauge", "title": "Latest pump temperature", "dataset": "intake_pumps",
                               "field": "temperature", "min": 0, "max": 120, "low": 60, "high": 80},
    "pump_temperature_light": {"type": "traffic-light", "title": "Pump temperature alarm", "dataset": "intake_pumps",
                               "field": "temperature", "threshold": 80, "warning": 10},
    "pump_fault_pie": {"type": "pie", "title": "Pump fault flags (share of events)", "dataset": "intake_pumps",
                       "field": "sensor_fault_flags", "colors": {"false": "#39B54A", "true": "#f44336"}},
    "pump_faults": {"type": "status-heatmap", "title": "Pump fault flags over time", "dataset": "intake_pumps",
                    "field": "sensor_fault_flags"},
    "pump_overheating": {"type": "table", "title": "Pump overheating events", "dataset": "pump_overheating",
                         "columns": ["timestamp", "sensorId", "temperature", "mass_flow"]},
    "pressure": {"type": "time-series", "title": "Main line pressure", "dataset": "main_line_pressure",
                 "fields": ["pressure"], "group_by": True},
    "pressure_smoothed": {"type": "time-series", "title": "Main line pressure: raw vs. smoothed",
                          "dataset": "main_line_pressure_smoothed", "fields": ["pressure", "filterResult"],
                          "group_by": True, "display": {"pressure": "normal_markers", "filterResult": "lines"}},
    "pressure_histogram": {"type": "histogram", "title": "Pressure distribution", "dataset": "main_line_pressure",
                           "field": "pressure", "bins": 20},
    "tank_level": {"type": "time-series", "title": "Clear water tank level", "dataset": "clear_water_tank",
                   "fields": ["level"], "display": {"level": "area"}},
    "tank_latest": {"type": "value-card", "title": "Clear water tank (latest)", "dataset": "clear_water_tank",
                    "fields": ["level", "overflow"]},
    "tank_status": {"type": "status", "title": "Tank overflow protection", "dataset": "clear_water_tank",
                    "field": "overflow", "green_when": False},
    "tank_overflow": {"type": "table", "title": "Tank overflow events", "dataset": "tank_overflow_events",
                      "columns": ["timestamp", "sensorId", "level"]},
    "reservoir_level": {"type": "time-series", "title": "Reservoir level", "dataset": "reservoir_level",
                        "fields": ["level"], "group_by": True, "display": {"level": "area"}},
    # --- Powertrain Components Plant: machining
    "spindle_speed": {"type": "time-series", "title": "Spindle speed per machine", "dataset": "cnc_machining",
                      "fields": ["spindle_speed"], "group_by": True},
    "spindle_load_wear": {"type": "time-series", "title": "Spindle load (area) and tool wear (right axis)",
                          "dataset": "cnc_machining", "fields": ["spindle_load", "tool_wear"], "group_by": True,
                          "display": {"spindle_load": "area"}, "axis": {"tool_wear": "right"}, "zoom": True},
    "spindle_load_per_minute": {"type": "time-series", "title": "Average spindle load per minute (aggregated)",
                                "dataset": "cnc_machining", "fields": ["spindle_load"], "group_by": True,
                                "display": {"spindle_load": "bar"},
                                "aggregate": {"unit": "m", "value": 1, "functions": {"spindle_load": "MEAN"}}},
    "spindle_power": {"type": "time-series", "title": "Spindle power (derived by script)", "dataset": "cnc_machining",
                      "fields": ["spindle_power_kw"], "group_by": True, "display": {"spindle_power_kw": "area"}},
    "spindle_temperature_gauge": {"type": "gauge", "title": "Latest spindle temperature", "dataset": "cnc_machining",
                                  "field": "spindle_temperature", "min": 0, "max": 100, "low": 55, "high": 70},
    "spindle_temperature_histogram": {"type": "histogram", "title": "Spindle temperature distribution",
                                      "dataset": "cnc_machining", "field": "spindle_temperature", "bins": 20},
    "load_vs_vibration": {"type": "scatter", "title": "Spindle load vs. vibration", "dataset": "cnc_machining",
                          "x": "spindle_load", "y": "vibration"},
    "machine_states": {"type": "status-heatmap", "title": "Machine states", "dataset": "cnc_machining",
                       "field": "machine_state",
                       "colors": {"RUNNING": "#39B54A", "LOADING": "#6699D4", "TOOL_CHANGE": "#FD8B00",
                                  "ALARM": "#f44336"}},
    "machine_state_pie": {"type": "pie", "title": "Machine state share", "dataset": "cnc_machining",
                          "field": "machine_state",
                          "colors": {"RUNNING": "#39B54A", "LOADING": "#6699D4", "TOOL_CHANGE": "#FD8B00",
                                     "ALARM": "#f44336"}},
    "parts_completed": {"type": "indicator", "title": "Parts completed",
                        "dataset": "cnc_machining", "field": "parts_completed"},
    "tool_wear_warnings": {"type": "table", "title": "Tool wear warnings", "dataset": "cnc_tool_wear_warnings",
                           "columns": ["sensorId", "tool_wear_class", "tool_wear", "parts_completed", "program_id"]},
    "vibration_smoothed": {"type": "time-series", "title": "Spindle vibration: raw vs. smoothed",
                           "dataset": "cnc_vibration_smoothed", "fields": ["vibration", "filterResult"],
                           "group_by": True, "display": {"vibration": "normal_markers", "filterResult": "lines"}},
    # --- Powertrain Components Plant: assembly and quality
    "torque": {"type": "time-series", "title": "Torque per station", "dataset": "assembly_stations",
               "fields": ["torque", "torque_angle"], "group_by": True, "axis": {"torque_angle": "right"}},
    "torque_histogram": {"type": "histogram", "title": "Torque distribution", "dataset": "assembly_stations",
                         "field": "torque", "bins": 20},
    "torque_density": {"type": "density", "title": "Torque vs. angle density", "dataset": "assembly_stations",
                       "x": "torque", "y": "torque_angle"},
    "station_status": {"type": "value-card", "title": "Station status (latest)", "dataset": "assembly_stations",
                       "fields": ["station_state", "part_id", "cycle_progress", "parts_ok", "parts_nok"]},
    "nok_indicator": {"type": "indicator", "title": "NOK joints (counter)", "dataset": "assembly_stations",
                      "field": "parts_nok"},
    "nok_joints": {"type": "table", "title": "NOK joints", "dataset": "assembly_nok_joints",
                   "columns": ["sensorId", "part_id", "torque", "torque_angle", "cycle_time"]},
    "leak_test_pressure": {"type": "time-series", "title": "Leak test pressure and leak rate",
                           "dataset": "eol_leak_test", "fields": ["test_pressure", "leak_rate"],
                           "display": {"test_pressure": "area"}, "axis": {"leak_rate": "right"}},
    "leak_test_counters": {"type": "value-card", "title": "Leak test (latest)", "dataset": "eol_leak_test",
                           "fields": ["test_state", "part_id", "test_result", "parts_tested", "parts_failed"]},
    "leak_test_progress": {"type": "progress-bar", "title": "Shift target: 60 tests", "dataset": "eol_leak_test",
                           "field": "parts_tested", "target": 60},
    "leak_test_status": {"type": "status", "title": "Last leak test passed", "dataset": "eol_leak_test",
                         "field": "passed", "green_when": True},
    "test_result_pie": {"type": "pie", "title": "Leak test results", "dataset": "eol_leak_test",
                        "field": "test_result", "colors": {"PASS": "#39B54A", "FAIL": "#f44336"}},
    "failed_tests": {"type": "table", "title": "Failed leak tests", "dataset": "eol_failed_tests",
                     "columns": ["part_id", "leak_rate", "test_pressure", "test_duration"]},
}

# Dashboards: name, description, widgets as (chart key, x, y, cols, rows) on an 8-column grid, and an
# optional view mode ("grid" or "slide"). The story: a KPI row on top, trends in the middle, events at the
# bottom; overview dashboards per site, detail dashboards per area, and a slide show for a shop-floor screen.
DASHBOARDS = {
    "overview": {"name": "Plant Overview", "description": "Key figures of the water works at a glance", "widgets": [
        ("pump_temperature_light", 0, 0, 2, 3), ("tank_status", 2, 0, 2, 3), ("pump_temperature_gauge", 4, 0, 2, 3),
        ("tank_latest", 6, 0, 2, 3),
        ("pump_temperature_heatmap", 0, 3, 4, 3), ("tank_level", 4, 3, 4, 3),
        ("pressure", 0, 6, 4, 3), ("reservoir_level", 4, 6, 4, 3),
        ("pump_overheating", 0, 9, 8, 3)]},
    "pumps": {"name": "Intake Pump Station", "description": "Flow, temperature and alarms of the three intake pumps",
              "widgets": [("pump_temperature_light", 0, 0, 2, 3), ("pump_temperature_gauge", 2, 0, 2, 3),
                          ("pump_fault_pie", 4, 0, 4, 3),
                          ("pump_flow", 0, 3, 4, 3), ("pump_temperature", 4, 3, 4, 3),
                          ("pump_faults", 0, 6, 8, 3),
                          ("pump_overheating", 0, 9, 8, 3)]},
    "storage": {"name": "Water Storage", "description": "Clear water tank, reservoir and the pressure line",
                "widgets": [("tank_latest", 0, 0, 2, 3), ("tank_status", 2, 0, 2, 3), ("tank_level", 4, 0, 4, 3),
                            ("reservoir_level", 0, 3, 4, 3), ("pressure_smoothed", 4, 3, 4, 3),
                            ("pressure_histogram", 0, 6, 4, 3), ("tank_overflow", 4, 6, 4, 3)]},
    "powertrain_overview": {"name": "Powertrain Plant Overview",
                            "description": "Machining, assembly and end-of-line test at a glance",
                            "widgets": [("parts_completed", 0, 0, 2, 3), ("nok_indicator", 2, 0, 2, 3),
                                        ("leak_test_progress", 4, 0, 2, 3), ("leak_test_status", 6, 0, 2, 3),
                                        ("spindle_load_wear", 0, 3, 4, 3), ("torque", 4, 3, 4, 3),
                                        ("machine_states", 0, 6, 4, 3), ("test_result_pie", 4, 6, 4, 3),
                                        ("failed_tests", 0, 9, 8, 3)]},
    "cnc": {"name": "CNC Machining", "description": "Spindles, tool wear and alarms of the three machining centres",
            "widgets": [("spindle_temperature_gauge", 0, 0, 2, 3), ("parts_completed", 2, 0, 2, 3),
                        ("machine_state_pie", 4, 0, 4, 3),
                        ("spindle_speed", 0, 3, 4, 3), ("spindle_load_wear", 4, 3, 4, 3),
                        ("spindle_power", 0, 6, 4, 3), ("vibration_smoothed", 4, 6, 4, 3),
                        ("spindle_load_per_minute", 0, 9, 4, 3), ("load_vs_vibration", 4, 9, 4, 3),
                        ("spindle_temperature_histogram", 0, 12, 4, 3), ("machine_states", 4, 12, 4, 3),
                        ("tool_wear_warnings", 0, 15, 8, 3)]},
    "assembly_quality": {"name": "Assembly and Quality",
                         "description": "Screwing stations and the end-of-line leak test",
                         "widgets": [("station_status", 0, 0, 2, 3), ("nok_indicator", 2, 0, 2, 3),
                                     ("leak_test_progress", 4, 0, 2, 3), ("leak_test_status", 6, 0, 2, 3),
                                     ("torque", 0, 3, 4, 3), ("torque_histogram", 4, 3, 4, 3),
                                     ("torque_density", 0, 6, 4, 3), ("leak_test_pressure", 4, 6, 4, 3),
                                     ("test_result_pie", 0, 9, 4, 3), ("leak_test_counters", 4, 9, 2, 3),
                                     ("nok_joints", 0, 12, 4, 3), ("failed_tests", 4, 12, 4, 3)]},
    "shop_floor": {"name": "Shop Floor Display", "description": "Slide show for a screen on the shop floor",
                   "view": "slide",
                   "widgets": [("spindle_load_wear", 0, 0, 8, 6), ("machine_states", 0, 6, 8, 6),
                               ("test_result_pie", 0, 12, 8, 6), ("pump_temperature_heatmap", 0, 18, 8, 6)]},
}

# Assets: top-level assets with their site, links and sub-assets
ASSETS = [
    {"name": "Intake Pump Station", "description": "Three centrifugal pumps lifting raw water from the river",
     "isa95": "PRODUCTION_UNIT", "site": "riverside", "area": "Intake", "labels": ["demo", "critical"],
     "adapters": ["intake_pumps"], "pipelines": ["pump_overheating"],
     "charts": ["pump_flow", "pump_temperature", "pump_temperature_heatmap", "pump_temperature_gauge",
                "pump_temperature_light", "pump_fault_pie", "pump_faults", "pump_overheating"],
     "dashboards": ["overview", "pumps", "shop_floor"],
     "sub_assets": [{"name": f"Pump P-10{i}", "description": f"Intake pump {i} (sensor flowrate0{i})",
                     "isa95": "UNIT", "labels": ["demo"]} for i in (1, 2, 3)]},
    {"name": "Main Pressure Line", "description": "Pressurised line from the pump station to the treatment stage",
     "isa95": "PRODUCTION_LINE", "site": "riverside", "area": "Treatment", "labels": ["demo"],
     "adapters": ["main_line_pressure"], "pipelines": ["pressure_smoothing"],
     "charts": ["pressure", "pressure_smoothed", "pressure_histogram"], "dashboards": ["overview", "storage"]},
    {"name": "Clear Water Tank T-200", "description": "Storage tank for treated water before distribution",
     "isa95": "STORAGE_UNIT", "site": "riverside", "area": "Storage", "labels": ["demo", "critical"],
     "adapters": ["clear_water_tank"], "pipelines": ["tank_overflow"],
     "charts": ["tank_level", "tank_latest", "tank_status", "tank_overflow"], "dashboards": ["overview", "storage"]},
    {"name": "Hillside Reservoir Basin", "description": "Open reservoir feeding the works by gravity",
     "isa95": "STORAGE_ZONE", "site": "hillside", "area": "Reservoir", "labels": ["demo"],
     "adapters": ["reservoir_level"], "pipelines": [], "charts": ["reservoir_level"],
     "dashboards": ["overview", "storage"]},
    {"name": "Machining Cell 1", "description": "Three CNC machining centres milling gearbox housings",
     "isa95": "PRODUCTION_UNIT", "site": "powertrain", "area": "Machining",
     "labels": ["demo", "automotive", "critical"],
     "adapters": ["cnc_machining"], "pipelines": ["tool_wear_warnings", "vibration_smoothing"],
     "charts": ["spindle_speed", "spindle_load_wear", "spindle_load_per_minute", "spindle_power",
                "spindle_temperature_gauge", "spindle_temperature_histogram", "load_vs_vibration", "machine_states",
                "machine_state_pie", "parts_completed", "tool_wear_warnings", "vibration_smoothed"],
     "dashboards": ["cnc", "powertrain_overview", "shop_floor"],
     "sub_assets": [{"name": f"CNC-0{i}", "description": f"Machining centre {i} (sensor cnc0{i})",
                     "isa95": "WORK_CELL", "labels": ["demo", "automotive"]} for i in (1, 2, 3)]},
    {"name": "Assembly Line 1", "description": "Gearbox assembly line with two screwing stations",
     "isa95": "PRODUCTION_LINE", "site": "powertrain", "area": "Assembly", "labels": ["demo", "automotive"],
     "adapters": ["assembly_stations"], "pipelines": ["nok_joints"],
     "charts": ["torque", "torque_histogram", "torque_density", "station_status", "nok_indicator", "nok_joints"],
     "dashboards": ["assembly_quality", "powertrain_overview"],
     "sub_assets": [{"name": f"Screwing Station {i}", "description": f"Screwing station {i} (sensor station0{i})",
                     "isa95": "WORK_CELL", "labels": ["demo", "automotive"]} for i in (1, 2)]},
    {"name": "End-of-Line Test Rig", "description": "Leak test of every assembled gearbox housing",
     "isa95": "WORK_CELL", "site": "powertrain", "area": "Quality", "labels": ["demo", "automotive"],
     "adapters": ["eol_leak_test"], "pipelines": ["failed_tests"],
     "charts": ["leak_test_pressure", "leak_test_counters", "leak_test_progress", "leak_test_status", "test_result_pie",
                "failed_tests"],
     "dashboards": ["assembly_quality", "powertrain_overview", "shop_floor"]},
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
        body = {
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
        }
        if definition.get("script"):
            body["transformationConfig"] = {"language": "javascript", "script": definition["script"],
                                            "scriptActive": True, "inputs": [], "outputs": []}
        response = api.post("/api/v2/connect/compact-adapters", body)
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
        processors = definition.get("processors") or [definition["processor"]]
        elements = [{"type": "stream", "ref": "stream1",
                     "id": adapters[definition["adapter"]]["correspondingDataStreamElementId"]}]
        previous = "stream1"
        for index, processor in enumerate(processors, start=1):
            ref = f"processor{index}"
            elements.append({"type": "processor", "ref": ref, "id": processor["id"], "connectedTo": [previous],
                             "configuration": processor["configuration"]})
            previous = ref
        elements.append(dataset_sink_element(previous, definition["dataset"]))
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


def group_by_config():
    return [{"runtimeName": SENSOR_ID_FIELD, "selected": True, "numeric": False, "aggregations": ["MODE"]}]


def source_config(measure, group_by=False, latest_only=False, aggregate=None, only_fields=None):
    """Query configuration of a chart: raw events (optionally the latest one only) or an aggregated query."""
    fields = []
    for p in properties_of(measure):
        selected = only_fields is None or p["runtimeName"] in only_fields
        default_function = "MEAN" if is_numeric(p) else "MODE"
        function = (aggregate or {}).get("functions", {}).get(p["runtimeName"], default_function)
        fields.append({"runtimeName": p["runtimeName"], "selected": selected, "numeric": is_numeric(p),
                       "aggregations": [function]})
    overrides = {}
    for f in fields:
        if f["selected"]:
            function = f["aggregations"][0] if aggregate else None
            name = f"{function.lower()}_{f['runtimeName']}" if function else f["runtimeName"]
            overrides[f"{name}:0"] = field_label(f["runtimeName"], function)
    query = {"selectedFilters": [], "resultLabelOverrides": overrides, "fields": fields,
             "groupBy": group_by_config() if group_by else [], "fill": "none",
             "aggregationTimeUnit": aggregate["unit"] if aggregate else "d",
             "aggregationValue": aggregate["value"] if aggregate else 1, "autoAggregate": False}
    if latest_only:
        query.update({"order": "DESC", "limit": 1, "page": 1})
    return {"measureName": measure["measureName"], "measure": measure,
            "queryType": "aggregated" if aggregate else "raw", "queryConfig": query}


def chart_model(definition, measure, visualization, group_by=False, latest_only=False, aggregate=None,
                only_fields=None, appearance_extra=None):
    now = now_ms()
    visualization.setdefault("configurationValid", True)
    appearance = {"widgetTitle": definition["title"], "backgroundColor": "var(--color-bg-0)",
                  "textColor": "var(--color-default-text)"}
    if definition["type"] in ("gauge", "value-card", "table", "indicator", "progress-bar", "histogram", "scatter",
                              "density"):
        appearance["numberFormat"] = {"decimals": 2}
    appearance.update(appearance_extra or {})
    return {
        "@class": "org.apache.streampipes.model.datalake.DataExplorerWidgetModel",
        "widgetType": WIDGET_TYPES[definition["type"]],
        "baseAppearanceConfig": appearance,
        "dataConfig": {"ignoreMissingValues": False, "ignoreTooMuchDataWarning": True,
                       "sourceConfigs": [source_config(measure, group_by, latest_only, aggregate, only_fields)]},
        "visualizationConfig": visualization,
        "timeSettings": last_15_minutes(),
        "metadata": {"createdAtEpochMs": now, "lastModifiedEpochMs": now},
    }


# chart type in CHARTS -> widget type id of the UI's chart registry
WIDGET_TYPES = {
    "time-series": "time-series-chart", "gauge": "gauge", "value-card": "value-card", "table": "table",
    "status-heatmap": "status-heatmap", "histogram": "histogram-chart", "pie": "pie-chart",
    "indicator": "indicator-chart", "progress-bar": "progress-bar", "traffic-light": "traffic-Light",
    "status": "status", "scatter": "scatter-chart", "density": "density-chart",
    "heatmap": "heatmap",
}


def aggregated_field(field, function):
    """The field as the UI sees it in an aggregated query: <function>_<name>."""
    return {**field, "aggregation": function, "fullDbName": f"{function.lower()}_{field['runtimeName']}"}


def color_mappings(colors):
    return [{"value": value, "label": value.replace("_", " ").title(), "color": color}
            for value, color in colors.items()]


def build_time_series(definition, measure):
    aggregate = definition.get("aggregate")
    fields = fields_of(measure, definition["fields"] if aggregate else None)
    if aggregate:
        fields = [aggregated_field(f, aggregate["functions"].get(f["runtimeName"], "MEAN")) for f in fields]
    plottable = [f for f in fields if f["fieldCharacteristics"]["numeric"] or f["fieldCharacteristics"]["binary"]]
    display = definition.get("display", {})
    axis = definition.get("axis", {})
    visualization = {
        "selectedTimeSeriesChartProperties": [f for f in plottable if f["runtimeName"] in definition["fields"]],
        "yKeys": [],
        "chosenColor": {field_key(f): PRESET_COLORS[i % len(PRESET_COLORS)] for i, f in enumerate(plottable)},
        "displayName": {field_key(f): field_label(f["runtimeName"], f.get("aggregation")) for f in plottable},
        "displayType": {field_key(f): display.get(f["runtimeName"], "lines") for f in plottable},
        "chosenAxis": {field_key(f): axis.get(f["runtimeName"], "left") for f in plottable},
        "groupedColorMode": {field_key(f): "stable_palette" for f in plottable},
        "groupedColorMappings": {field_key(f): [] for f in plottable},
        "leftAxis": {"autoScaleActive": True, "axisMin": 0, "axisMax": 100},
        "rightAxis": {"autoScaleActive": True, "axisMin": 0, "axisMax": 100},
        "showSpike": True,
    }
    appearance = {"chartAppearance": {"showLegend": True, "showToolbox": True, "showTooltip": True},
                  "dataZoom": {"show": bool(definition.get("zoom")), "type": "slider"}}
    return chart_model(definition, measure, visualization, group_by=definition.get("group_by", False),
                       aggregate=aggregate, only_fields=definition["fields"] if aggregate else None,
                       appearance_extra=appearance)


def build_chart(definition, measure):
    kind = definition["type"]
    if kind == "time-series":
        return build_time_series(definition, measure)
    field = fields_of(measure, [definition["field"]])[0] if "field" in definition else None
    if kind == "gauge":
        visualization = {
            "selectedProperty": field, "displayName": field_label(field["runtimeName"]),
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
    if kind == "indicator":
        visualization = {"valueField": field, "showDelta": False, "title": "",
                         "description": field_label(field["runtimeName"])}
        return chart_model(definition, measure, visualization, latest_only=True,
                           appearance_extra={"valueFontSize": 48})
    if kind == "progress-bar":
        visualization = {"currentValueField": field, "targetSource": "fixed", "targetValue": definition["target"],
                         "invertProgress": False, "clampProgress": True, "displayMode": "percent-and-value",
                         "showLabel": True, "title": "", "description": field_label(field["runtimeName"])}
        return chart_model(definition, measure, visualization, latest_only=True)
    if kind == "traffic-light":
        visualization = {"selectedFieldToObserve": field, "selectedUpperLimit": True,
                         "selectedThreshold": definition["threshold"], "selectedWarningRange": definition["warning"],
                         "selectedToShowValue": True}
        return chart_model(definition, measure, visualization, latest_only=True)
    if kind == "status":
        visualization = {"selectedDataType": "boolean", "selectedBooleanFieldToObserve": field,
                         "selectedInterval": 10, "showLastSeen": True,
                         "selectedMappingGreenTrue": definition.get("green_when", True)}
        return chart_model(definition, measure, visualization, latest_only=True)
    if kind == "table":
        visualization = {"selectedColumns": fields_of(measure, definition["columns"]), "highlightedColumns": [],
                         "highlightedColumnColors": {}, "pageSize": 20, "stickyHeaders": True, "searchValue": ""}
        return chart_model(definition, measure, visualization)
    if kind == "status-heatmap":
        colors = definition.get("colors", {})
        visualization = {"selectedProperty": field, "isSelectedPropertyBoolean": field["fieldCharacteristics"]["binary"],
                         "showCustomColorMappingStatusHeatmap": bool(colors),
                         "colorMappingsStatusHeatmap": color_mappings(colors)}
        return chart_model(definition, measure, visualization)
    if kind == "pie":
        colors = definition.get("colors", {})
        visualization = {"selectedProperty": field, "roundingValue": 0.1, "selectedRadius": 0, "startAngle": 90,
                         "clockwise": True, "minAngle": 0, "labelMode": "name_percent", "labelPosition": "outside",
                         "labelAlignTo": "edge", "avoidLabelOverlap": True, "showLabelLine": True,
                         "topNEnabled": False, "topN": 10, "othersLabel": "Others",
                         "isSelectedPropertyBoolean": field["fieldCharacteristics"]["binary"],
                         "showCustomColorMappingPieChart": bool(colors), "colorMappingsPieChart": color_mappings(colors)}
        return chart_model(definition, measure, visualization)
    if kind == "histogram":
        visualization = {"selectedProperty": field, "autoBin": False, "numberOfBins": definition.get("bins", 10),
                         "autoDomain": True, "domainMin": 0, "domainMax": 100}
        return chart_model(definition, measure, visualization)
    if kind in ("scatter", "density"):
        x, y = fields_of(measure, [definition["x"]])[0], fields_of(measure, [definition["y"]])[0]
        visualization = {"firstField": x, "secondField": y,
                         "labelX": field_label(x["runtimeName"]), "labelY": field_label(y["runtimeName"])}
        return chart_model(definition, measure, visualization)
    if kind == "heatmap":
        visualization = {"selectedHeatProperty": field, "showLabelsProperty": False,
                         "visualMapMin": definition["min"], "visualMapMax": definition["max"]}
        return chart_model(definition, measure, visualization, group_by=definition.get("group_by", False))
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
                                         "defaultViewMode": definition.get("view", "grid"), "globalTimeEnabled": True,
                                         "gridRowHeightPx": 90},
            "dashboardLiveSettings": {"label": "5 sec", "refreshModeActive": True, "refreshIntervalInSeconds": 5},
            "dashboardTimeSettings": last_15_minutes(),
            "widgets": [{"id": secrets.token_hex(5), "name": CHARTS[chart]["title"], "x": x, "y": y, "cols": cols,
                         "rows": rows, "w": cols, "h": rows, "dataViewElementId": charts[chart]}
                        for chart, x, y, cols, rows in definition["widgets"]],
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
    log("Demo is ready: sign in and open 'Dashboards' (Plant Overview, Powertrain Plant Overview, CNC Machining, "
        "Assembly and Quality, ...), 'Assets' for the plant structure or the home page for the site map.")
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except Exception as error:  # noqa: BLE001 - report any failure in the container log
        log(f"ERROR: demo seed failed: {error}")
        sys.exit(1)
