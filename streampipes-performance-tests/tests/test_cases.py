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

"""Exercise each built-in workload against an in-memory API contract."""

import json
from unittest.mock import Mock, call

import pytest

from streampipes_performance.cases import REGISTRY, Case
from streampipes_performance.config import Cleanup, RunConfig, Timing, expand
from streampipes_performance.runner import execute_run_config
from streampipes_performance.runtime import Context
from streampipes_performance.testing import Clock, Server


@pytest.mark.parametrize(
    "kind",
    [
        "streampipes.adapter_ingestion",
        "streampipes.adapter_start_stop",
        "streampipes.pipeline_start_stop",
    ],
)
@pytest.mark.parametrize("count", [1, 2])
def test_built_in_lifecycle(tmp_path, kind, count):
    config = RunConfig.model_validate(
        {
            "target": "streampipes",
            "cases": [{"id": "case", "type": kind, "parameters": {"number_of_pipelines" if "pipeline" in kind else "number_of_adapters": count}}],
            "execution": {
                "cleanup": {"strategy": "delete"},
                "timing": {
                    "warmup_seconds": 0,
                    "duration_seconds": 2,
                    "stable_seconds": 1,
                    "drain_timeout_seconds": 3,
                },
            },
        }
    )
    server = Server()
    directory, passed = execute_run_config(config, expand(config), server, tmp_path, Clock())
    result = json.loads((directory / "case-m1-r1.json").read_text())
    assert passed, result
    assert server.empty()
    assert set(result["phase_durations_ms"]) == {"setup", "measurement", "preparation", "cleanup"}
    if kind == "streampipes.adapter_start_stop":
        for call in server.adapterApi.create_result.call_args_list:
            assert call.args[0].model_dump(by_alias=True)["transformationConfig"] == {
                "scriptActive": False, "script": "",
            }
    if "ingestion" in kind:
        assert result["metrics"]["observed_events"] == 2 * count
        assert result["metrics"]["throughput_ratio"] == 1


@pytest.mark.parametrize("implementation", REGISTRY.values(), ids=list(REGISTRY.keys()))
def test_cases_inherit_only_from_base_case(implementation):
    assert implementation.__bases__ == (Case,)


@pytest.mark.parametrize("resource_kind", ["adapter", "pipeline"])
def test_lifecycle_cycles_operate_on_selected_resources(resource_kind):
    context = Context(
        Server(), Timing(), "cycles", clock=Clock(), cleanup_config=Cleanup(strategy="delete")
    )
    case = REGISTRY[f"streampipes.{resource_kind}_start_stop"]({
        f"number_of_{resource_kind}s": 2, "cycles": 2,
    })
    case.setup(context)
    identifiers = [resource.identifier for resource in context.resources if resource.kind == resource_kind]
    context.lifecycle = Mock(wraps=context.lifecycle)
    start = context.clock.time()

    case.run(context)

    assert context.lifecycle.call_args_list == [
        call(resource_kind, identifier, running, False)
        for _ in range(2)
        for running in (False, True)
        for identifier in identifiers
    ]
    assert context.clock.time() - start == 4
    assert case.cleanup(context)


@pytest.mark.parametrize("kind", ["streampipes.adapter_ingestion"])
def test_ingestion_setup_failure_preserves_cleanup_and_incomplete_results(kind):
    server = Server()
    context = Context(
        server, Timing(), "partial",
        clock=Clock(), cleanup_config=Cleanup(strategy="delete"),
    )
    case = REGISTRY[kind]({"number_of_adapters": 2})

    def create_adapter(payload):
        result = server.create_adapter(payload)
        if len(server.adapters) == 2:
            raise TimeoutError("creation response lost")
        return result

    server.adapterApi.create_result.side_effect = create_adapter
    with pytest.raises(TimeoutError):
        case.setup(context)

    assert len(server.adapters) == 2
    assert case.prepare_results(context) == {"collection_complete": False}
    assert case.cleanup(context)
    assert server.empty()
