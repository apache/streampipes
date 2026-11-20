#
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
#

"""The requested public parameter surface and executable sample programs."""

import json
import subprocess
from pathlib import Path

import pytest

from streampipes_performance.cases import REGISTRY
from script_configs import ADAPTER_TRANSFORMATIONS
from streampipes_performance.config import load

EXPECTED = {
    "streampipes.adapter_latency": {"number_of_adapters", "events_per_resource", "publish_interval_ms", "transformation"},
    "streampipes.adapter_ingestion": {"number_of_adapters", "wait_time_ms", "transformation"},
    "streampipes.adapter_start_stop": {"number_of_adapters", "wait_time_ms", "cycles"},
    "streampipes.pipeline_start_stop": {"number_of_pipelines", "wait_time_ms", "cycles"},
    "streampipes.dataset_query": {"number_of_datasets", "rows_per_dataset", "sensor_count", "parallel_operations"},
}


def test_catalog_exposes_exactly_requested_parameters():
    assert {kind for kind in REGISTRY if kind.startswith("streampipes.")} == set(EXPECTED)
    for kind, keys in EXPECTED.items():
        model = REGISTRY[kind].Parameters
        assert set(model.model_fields) == keys
        with pytest.raises(ValueError):
            model.model_validate({"unexpected": 1})
        for old in {
            "batch_size", "iterations", "warmup_events", "nats_url", "interval_seconds", "page_size",
            "upload_method", "influx", "query", "concurrency", "query_limit", "sensors", "interval_ms",
            "warmup_queries", "force_stop",
        }:
            with pytest.raises(ValueError):
                model.model_validate({old: 1})


@pytest.mark.parametrize("transformation", [None, ADAPTER_TRANSFORMATIONS[0]])
def test_adapter_start_stop_rejects_transformations(transformation):
    with pytest.raises(ValueError, match="transformation"):
        REGISTRY["streampipes.adapter_start_stop"]({"transformation": transformation})


def test_all_run_config_selects_every_case_and_samples():
    _, scenarios = load(Path(__file__).parents[1] / "run_configs/all.json")
    assert {s.type for s in scenarios} == set(EXPECTED)
    assert len(scenarios) == 12
    for kind in ("adapter_latency", "adapter_ingestion"):
        scripts = [s.parameters["transformation"] for s in scenarios if s.type == f"streampipes.{kind}"]
        assert scripts == ADAPTER_TRANSFORMATIONS * (2 if kind == "adapter_latency" else 1)


@pytest.mark.parametrize("kind", ["adapter_latency", "adapter_ingestion"])
@pytest.mark.parametrize("parameters,script,active", [
    ({}, "", False),
    ({"transformation": None}, "", False),
    ({"transformation": ADAPTER_TRANSFORMATIONS[0]}, ADAPTER_TRANSFORMATIONS[0]["script"], True),
])
def test_adapter_transformation_string_controls_payload(kind, parameters, script, active):
    from importlib import import_module
    module = import_module(f"streampipes_performance.cases.streampipes.{kind}.case")
    case = REGISTRY[f"streampipes.{kind}"](parameters)
    if kind == "adapter_latency":
        payload = module.nats_adapter("id", "name", "subject", case.parameters, case.adapter_nats_url)
    else:
        payload = module.adapter_payload("id", "name", case.parameters.wait_time_ms, case.parameters.transformation)
    assert payload.model_dump(by_alias=True)["transformationConfig"] == {
        "scriptActive": active, "script": script,
    }
    assert case.parameters.model_dump()["transformation"] == parameters.get("transformation")


@pytest.mark.parametrize("kind", ["adapter_latency", "adapter_ingestion"])
@pytest.mark.parametrize("transformation", [{"scriptActive": True, "script": "source"}, 42, False, "", "source", {"script": "source"}, {"label": "missing script"}, {"label": "", "script": "source"}, {"label": "empty", "script": ""}])
def test_adapter_transformation_rejects_invalid_objects(kind, transformation):
    with pytest.raises(ValueError):
        REGISTRY[f"streampipes.{kind}"]({"transformation": transformation})


def run_js(program, events):
    harness = '''
const vm = require('node:vm');
const fs = require('node:fs');
const {program, events} = JSON.parse(fs.readFileSync(0, 'utf8'));
const emitted = [];
let saved;
const scope = vm.createContext({
  out: {collect: event => emitted.push({...event})},
  ctx: {stateStore: () => ({load: fallback => saved || fallback, persist: state => {saved = state;}})},
});
vm.runInContext(program, scope, {timeout: 1000});
for (const event of events) {
  scope.event = event;
  vm.runInContext('transform(event, out, ctx)', scope, {timeout: 1000});
}
process.stdout.write(JSON.stringify({emitted, saved}));
'''
    result = subprocess.run(["node", "-e", harness], input=json.dumps({"program": program, "events": events}),
                            capture_output=True, text=True, check=True, timeout=10)
    return json.loads(result.stdout)


@pytest.mark.parametrize("field", ["value", "mass_flow"])
@pytest.mark.parametrize("sample", ADAPTER_TRANSFORMATIONS)
def test_adapter_samples_preserve_correlation_and_one_to_one_delivery(sample, field):
    events = [{"eventId": f"e{i}", "timestamp": 1000 + i, field: 42.5} for i in range(3)]
    output = run_js(sample["script"], events)["emitted"]
    assert len(output) == 3
    assert [(event["eventId"], event["timestamp"]) for event in output] == [(f"e{i}", 1000 + i) for i in range(3)]
    assert all(isinstance(event[field], (int, float)) for event in output)


@pytest.mark.parametrize("kind", ["adapter_latency", "adapter_ingestion"])
def test_custom_script_and_label_from_json_reach_adapter_payload(tmp_path, kind):
    from importlib import import_module

    custom = {"label": "Customer conversion", "script": "function transform(event, out, ctx) { event.value *= 7; out.collect(event); }"}
    path = tmp_path / "custom.json"
    path.write_text(json.dumps({"target": "streampipes", "cases": [{
        "id": "custom", "type": f"streampipes.{kind}", "parameters": {"transformation": custom},
    }]}))
    _, scenarios = load(path)
    case = REGISTRY[scenarios[0].type](scenarios[0].parameters)
    module = import_module(case.__module__)
    if kind == "adapter_latency":
        payload = module.nats_adapter("id", "name", "subject", case.parameters, case.adapter_nats_url)
    else:
        payload = module.adapter_payload("id", "name", case.parameters.wait_time_ms, case.parameters.transformation)
    assert scenarios[0].parameters["transformation"] == custom
    assert payload.model_dump(by_alias=True)["transformationConfig"] == {"scriptActive": True, "script": custom["script"]}
