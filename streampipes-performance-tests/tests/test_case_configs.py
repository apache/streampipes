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

"""Inline run configurations and implementation-owned documentation."""

import inspect
import json
from pathlib import Path

import pytest

from streampipes_performance.cases import REGISTRY
from streampipes_performance.config import Model, RunConfig, expand, expand_values, load

ROOT = Path(__file__).parents[1]


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value))
    return path


def check_case_packages(kinds, config_directory, cleanup):
    """Every case owns its documentation and README and has a standalone run configuration."""
    standalone = {}
    for path in config_directory.glob("*.json"):
        config, scenarios = load(path)
        assert scenarios
        if len(config.cases) == 1:
            standalone[config.cases[0].type] = config
    assert set(kinds) <= set(standalone)
    for kind in kinds:
        implementation = REGISTRY[kind]
        module_path = Path(inspect.getfile(implementation))
        assert module_path.name == "case.py"
        assert module_path.parent.name == kind.split(".")[1]
        assert not list(module_path.parent.glob("*.json"))
        assert implementation.Parameters.__module__ == implementation.__module__
        assert implementation.Parameters.__bases__ == (Model,)
        docs = implementation.documentation
        assert docs.description and docs.metrics
        assert set(docs.parameter_descriptions) == set(implementation.Parameters.model_fields)
        assert module_path.with_name("README.md").is_file()
        config = standalone[kind]
        assert set(config.cases[0].parameters) == set(implementation.Parameters.model_fields)
        scenarios = expand(config)
        assert all(s.documentation == docs for s in scenarios)
        assert all(s.cleanup.strategy == cleanup for s in scenarios)


def test_each_implementation_owns_documentation_and_has_a_standalone_run():
    kinds = [kind for kind in REGISTRY if kind.startswith("streampipes.")]
    assert len(kinds) == 5
    check_case_packages(kinds, ROOT / "run_configs", "reset")


def test_inline_parameters_are_independent_between_runs(tmp_path):
    case = {"id": "base", "type": "streampipes.adapter_ingestion", "parameters": {
        "number_of_adapters": [2, 3], "wait_time_ms": 100, "transformation": None,
    }}
    path = write(tmp_path / "custom.json", {"target": "streampipes", "cases": [case]})
    default_path = write(tmp_path / "default.json", {"target": "streampipes", "cases": [{
        "id": "base", "type": "streampipes.adapter_ingestion",
    }]})
    config, scenarios = load(path)
    assert len(scenarios) == 2
    assert {s.parameters["number_of_adapters"] for s in scenarios} == {2, 3}
    assert all(s.parameters["wait_time_ms"] == 100 for s in scenarios)
    assert all(s.parameters["transformation"] is None for s in scenarios)
    assert load(default_path)[1][0].parameters["number_of_adapters"] == 1
    case["parameters"]["number_of_adapters"] = 5
    write(path, {"target": "streampipes", "cases": [case]})
    assert load(path)[1][0].parameters["number_of_adapters"] == 5
    assert load(default_path)[1][0].parameters["number_of_adapters"] == 1


@pytest.mark.parametrize("parameters", [
    {"number_of_adapters": [2, 0]}, {"number_of_adapters": []}, {"unknown": 1}, {"number_of_adapters": True},
])
def test_invalid_inline_parameters(tmp_path, parameters):
    path = write(tmp_path / "run.json", {"target": "streampipes", "cases": [{
        "id": "base", "type": "streampipes.adapter_ingestion", "parameters": parameters,
    }]})
    with pytest.raises(ValueError):
        load(path)


@pytest.mark.parametrize("cases", [
    [], ["case.json"], [{"config": "case.json", "parameters": {"number_of_adapters": 2}}],
    [{"extends": "case.json", "id": "x", "type": "streampipes.adapter_ingestion"}],
    [{"id": "x", "type": "streampipes.adapter_ingestion", "description": "external docs"}],
    [{"id": "x", "type": "streampipes.adapter_ingestion", "parameter_descriptions": {}}],
    [{"id": "x", "type": "streampipes.adapter_ingestion", "metrics": {}}],
    [{"id": "x", "type": "streampipes.adapter_ingestion"}] * 2,
])
def test_run_configs_reject_references_inheritance_and_documentation(tmp_path, cases):
    path = write(tmp_path / "run.json", {"target": "streampipes", "cases": cases})
    with pytest.raises(ValueError):
        load(path)


def test_multiple_configurations_of_same_case_have_independent_parameters(tmp_path):
    path = write(tmp_path / "run.json", {"target": "streampipes", "cases": [
        {"id": "small", "type": "streampipes.adapter_ingestion", "parameters": {"number_of_adapters": 1}},
        {"id": "large", "type": "streampipes.adapter_ingestion", "parameters": {"number_of_adapters": 10}},
    ]})
    _, scenarios = load(path)
    assert [s.id for s in scenarios] == ["small-m1-r1", "large-m1-r1"]
    assert [s.parameters["number_of_adapters"] for s in scenarios] == [1, 10]
    assert scenarios[0].documentation == scenarios[1].documentation
    scenarios[0].documentation.parameter_descriptions["number_of_adapters"] = "Changed snapshot"
    assert scenarios[1].documentation == REGISTRY[scenarios[1].type].documentation


def test_product_restrictions_apply_to_inline_cases(tmp_path, monkeypatch):
    class VendorCase(REGISTRY["streampipes.adapter_ingestion"]):
        targets = ("vendor",)

    monkeypatch.setitem(REGISTRY, "vendor.case", VendorCase)
    path = write(tmp_path / "run.json", {"target": "streampipes", "cases": [{"id": "x", "type": "vendor.case"}]})
    with pytest.raises(ValueError, match="unavailable"):
        load(path)


def test_unknown_target_lists_registered_targets(tmp_path):
    path = write(tmp_path / "run.json", {"target": "unknown", "cases": [
        {"id": "x", "type": "streampipes.adapter_ingestion"}]})
    with pytest.raises(ValueError, match="Unknown target 'unknown'; available: .*streampipes"):
        load(path)


@pytest.mark.parametrize("execution", [{}, {"cleanup": {}}, {"cleanup": {"reset_timeout_seconds": 100}}])
def test_extending_target_inherits_cases_and_sets_cleanup_default(monkeypatch, execution):
    from streampipes_performance.targets import TARGETS, Target

    monkeypatch.setitem(TARGETS, "vendor", Target("vendor", "Vendor", extends="streampipes",
                                                   default_cleanup="delete", supports_reset=False))
    run_config = RunConfig(target="vendor", execution=execution, cases=[
        {"id": "adapter", "type": "streampipes.adapter_ingestion",
         "execution": {"cleanup": [{}, {"reset_timeout_seconds": 5}]}},
        {"id": "query", "type": "streampipes.dataset_query"},
    ])
    assert {s.cleanup.strategy for s in expand(run_config)} == {"delete"}
    with pytest.raises(ValueError, match="Reset cleanup is not supported for Vendor"):
        expand(RunConfig(target="vendor", execution={"cleanup": {"strategy": "reset"}}, cases=[
            {"id": "adapter", "type": "streampipes.adapter_ingestion"}]))


def test_realistic_run_config_owns_all_five_workloads():
    resolved, scenarios = load(ROOT / "run_configs/realistic.json")
    assert len(resolved.cases) == 5
    assert len(scenarios) == 35
    for case in resolved.cases:
        count_parameter = ("number_of_datasets" if case.type == "streampipes.dataset_query" else
                           "number_of_pipelines" if "pipeline" in case.type else "number_of_adapters")
        assert case.parameters[count_parameter] == (
            [5] if case.type == "streampipes.dataset_query" else [50, 100, 150])
    assert all(s.timing.duration_seconds == 120 for s in scenarios)


def test_script_objects_stay_atomic_during_expansion():
    for path in (ROOT / "run_configs").glob("*.json"):
        config, scenarios = load(path)
        for case in config.cases:
            for parameter in ("transformation", "code"):
                if parameter not in case.parameters:
                    continue
                alternatives = case.parameters[parameter]
                if not isinstance(alternatives, list):
                    alternatives = [alternatives]
                actual = [s.parameters[parameter] for s in scenarios if s.id.startswith(case.id + "-m")]
                assert all(value in alternatives for value in actual)
                assert all(value in actual for value in alternatives)
