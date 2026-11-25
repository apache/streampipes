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

"""Unified workload alternatives and execution defaults."""

import pytest

from streampipes_performance.config import RunConfig, expand, expand_values


def scenarios(parameters=None, execution=None, defaults=None):
    return expand(RunConfig.model_validate({
        "target": "streampipes",
        "execution": defaults or {},
        "cases": [{
            "id": "example",
            "type": "streampipes.adapter_ingestion",
            "parameters": parameters or {},
            "execution": execution or {},
        }],
    }))


def test_workload_and_execution_cross_product():
    result = scenarios(
        {"number_of_adapters": [1, 2], "wait_time_ms": [100, 1000]},
        {"cleanup": [{"strategy": "delete"}, {"strategy": "reset"}], "repetitions": 2},
    )
    assert len(result) == 16
    assert len({s.id for s in result}) == 16
    assert {(s.parameters["number_of_adapters"], s.parameters["wait_time_ms"], s.cleanup.strategy, s.repetition)
            for s in result} == {
        (count, interval, strategy, repetition)
        for count in [1, 2] for interval in [100, 1000]
        for strategy in ["delete", "reset"] for repetition in [1, 2]
    }


def test_object_alternatives_preserve_nested_lists():
    objects = [{"rules": [1, 2]}, {"rules": []}]
    assert [p["options"] for p in expand_values({"options": objects})] == objects
    assert list(expand_values({"options": {"value": {"values": []}}}))[0]["options"] == {"values": []}


def test_transformation_object_alternatives():
    scripts = [None, {"label": "My script", "script": "function transform(event, out, ctx) { out.collect(event); }"}]
    assert [s.parameters["transformation"] for s in scenarios({"transformation": scripts})] == scripts
    assert scenarios({"transformation": scripts[-1]})[0].parameters["transformation"] == scripts[-1]


def test_literal_lists_and_explicit_alternatives(monkeypatch):
    from streampipes_performance.cases import REGISTRY
    from streampipes_performance.cases.streampipes.adapter_ingestion.case import Parameters as IngestionParameters

    class ListParameters(IngestionParameters):
        labels: list[str]

    monkeypatch.setattr(REGISTRY["streampipes.adapter_ingestion"], "Parameters", ListParameters)
    assert scenarios({"labels": {"value": []}})[0].parameters["labels"] == []
    assert [s.parameters["labels"] for s in scenarios({"labels": {"values": [["a"], ["b", "c"]]}})] == [["a"], ["b", "c"]]


@pytest.mark.parametrize("parameters,execution", [
    ({"number_of_adapters": []}, {}),
    ({"number_of_adapters": {"values": []}}, {}),
    ({"number_of_adapters": {"values": 1}}, {}),
    ({"number_of_adapters": [1, 0]}, {}),
    ({}, {"cleanup": []}),
    ({}, {"timing": [{"stable_seconds": 40}]}),
    ({}, {"repetitions": [1, 2]}),
])
def test_invalid_alternatives(parameters, execution):
    with pytest.raises(ValueError):
        scenarios(parameters, execution)


def test_execution_inheritance_and_complete_object_override():
    defaults = {
        "timing": {"duration_seconds": 50},
        "cleanup": {"strategy": "reset", "reset_timeout_seconds": 900},
        "thresholds": {"max_error_rate": 0.1},
        "repetitions": 2,
    }
    result = scenarios(execution={"cleanup": {"strategy": "delete"}, "timing": None}, defaults=defaults)
    assert len(result) == 2
    assert result[0].timing.duration_seconds == 50
    assert result[0].cleanup.reset_timeout_seconds == 600
    assert result[0].thresholds.max_error_rate == 0.1
    result = scenarios(execution={"timing": [{"duration_seconds": 1}, {"duration_seconds": 2}]})
    assert [s.timing.duration_seconds for s in result] == [1, 2]


@pytest.mark.parametrize("old", ["matrix", "timing", "cleanup", "repetitions", "thresholds"])
def test_old_top_level_case_fields_rejected(old):
    with pytest.raises(ValueError):
        RunConfig.model_validate({"target": "streampipes", "cases": [
            {"id": "x", "type": "streampipes.adapter_ingestion", old: {}}
        ]})
