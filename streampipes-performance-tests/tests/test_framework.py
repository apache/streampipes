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

"""Offline framework behavior and failure-path tests."""

import json
from types import SimpleNamespace
from unittest.mock import Mock

import pytest
from pydantic import ValidationError
from requests import HTTPError, Response, Timeout

from streampipes_performance.cases.base import REGISTRY, Case
from streampipes_performance.config import CaseDocumentation, Cleanup, RunConfig, Timing, expand
from streampipes_performance.measurement import collect_pages, collect_window
from streampipes_performance.metrics import summarize, threshold_failures
from streampipes_performance.runner import execute_run_config
from streampipes_performance.runtime import Context, checked
from streampipes_performance.testing import Clock


def run_config(**kwargs):
    # Most runner fixtures exercise individual resource deletion.
    execution = {"cleanup": {"strategy": "delete"}, **kwargs.pop("execution", {})}
    kwargs["execution"] = execution
    return RunConfig.model_validate(
        {
            "target": "streampipes",
            "cases": [{"id": "ingest", "type": "streampipes.adapter_ingestion"}],
            **kwargs,
        }
    )


def write_run_config(path, config):
    """Write an inline run configuration fixture."""
    path.write_text(config.model_dump_json())


def client():
    result = Mock(server_version="1.0")
    result.adapterApi.all.return_value = []
    result.pipelineApi.all.return_value = []
    result.datasetApi.all.return_value = []
    return result


@pytest.mark.parametrize(
    "case",
    [
        {"id": "x", "type": "unknown"},
        {"id": "x", "type": "vendor.unregistered_case"},
        {"id": "x", "type": "streampipes.adapter_ingestion", "parameters": {"number_of_adapters": 0}},
        {"id": "x", "type": "streampipes.adapter_ingestion", "parameters": {"number_of_adapters": True}},
        {"id": "x", "type": "streampipes.adapter_ingestion", "parameters": {"surprise": 1}},
        {"id": "x", "type": "streampipes.adapter_ingestion", "parameters": {'number_of_adapters': []}},
        {"id": "x", "type": "streampipes.adapter_start_stop", "parameters": {"cycles": 0}},
        {
            "id": "x",
            "type": "streampipes.adapter_ingestion",
            "parameters": {"number_of_adapters": 1},
            "matrix": {"number_of_adapters": [2]},
        },
    ],
)
def test_invalid_scenarios(case):
    with pytest.raises(ValueError):
        expand(run_config(cases=[case]))


def test_parameter_alternatives_and_repetitions():
    config = run_config(cases=[
            {"id": "x", "type": "streampipes.adapter_ingestion", "parameters": {'number_of_adapters': [1, 5], 'wait_time_ms': [100, 1000]}}
        ], execution={'repetitions': 2})
    scenarios = expand(config)
    assert len(scenarios) == 8
    assert len({s.id for s in scenarios}) == 8
    assert scenarios[0].parameters["number_of_adapters"] == 1
    assert scenarios[-1].parameters["wait_time_ms"] == 1000
    assert scenarios == expand(config)


def test_duplicate_id_and_secrets_rejected():
    case = {"id": "x", "type": "streampipes.adapter_ingestion"}
    with pytest.raises(ValueError, match="Duplicate"):
        expand(run_config(cases=[case, case]))
    with pytest.raises(ValidationError):
        run_config(connection={"api_key": "secret"})
    with pytest.raises(ValidationError):
        run_config(connection={"base_url": "https://user:password@localhost"})


@pytest.mark.parametrize("values,expected", [
    ([], {"count": 0, "min": None, "mean": None, "max": None}),
    ([0], {"count": 1, "min": 0, "mean": 0, "max": 0}),
    ([30, 0, 6], {"count": 3, "min": 0, "mean": 12, "max": 30}),
    ([1, 1, 1], {"count": 3, "min": 1, "mean": 1, "max": 1}),
    ([float("nan"), float("inf"), -float("inf"), 2],
     {"count": 1, "min": 2, "mean": 2, "max": 2}),
])
def test_summary_contains_only_mean_min_max_and_count(values, expected):
    assert summarize(iter(values)) == expected


def test_metrics_and_missing_thresholds():
    config = run_config(
        cases=[
            {"id": "x", "type": "streampipes.adapter_ingestion", "execution": {'thresholds': {
                    "min_throughput_ratio": 0.9,
                    "max_error_rate": 0.1,
                    "max_operation_mean_ms": 20,
                }}}
        ]
    )
    thresholds = expand(config)[0].thresholds
    assert len(threshold_failures(thresholds, {"operations": {"setup.api.create": {"mean": 25}}})) == 3
    assert "operation mean unavailable" in threshold_failures(thresholds, {"operations": {}})
    assert threshold_failures(thresholds, {
        "operations": {"Query time": summarize([0, 40])},
        "error_rate": 0,
        "throughput_ratio": 1,
    }) == []


@pytest.mark.parametrize("body", [{"success": False}, {"successful": False}])
def test_application_failure(body):
    with pytest.raises(RuntimeError):
        checked(body)
    assert checked({"successful": True, "warning": True})["warning"]


def test_timeout_recorded_and_not_retried():
    context = Context(client(), Timing(), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    operation = Mock(side_effect=Timeout("secret-key"))
    with pytest.raises(Timeout):
        context.call("create", operation)
    operation.assert_called_once()
    assert context.samples[0]["error"] == {"type": "Timeout"}
    assert not context.samples[0]["success"]


def test_readiness_deadline():
    clock = Clock()
    context = Context(client(), Timing(readiness_timeout_seconds=2), "perf", clock=clock, cleanup_config=Cleanup(strategy="delete"))
    with pytest.raises(TimeoutError):
        context.wait("running", lambda: False)
    assert clock.now == 2
    assert context.samples[-1]["duration_ms"] == 2000


@pytest.mark.parametrize("failure", ["setup", "measurement", "preparation", "interrupt", None])
def test_runner_always_prepares_and_cleans(tmp_path, monkeypatch, failure):
    events = []

    class Example(Case):
        def setup(self, context):
            events.append("setup")
            if failure == "setup":
                raise Timeout("private-token")

        def run(self, context):
            events.append("measurement")
            if failure == "measurement":
                raise ValueError("private-token")
            if failure == "interrupt":
                raise KeyboardInterrupt

        def prepare_results(self, context):
            events.append("preparation")
            if failure == "preparation":
                raise ValueError
            return {"observed_events": 5}

        def cleanup(self, context):
            events.append("cleanup")
            # A partial result already exists before cleanup.
            assert list(tmp_path.glob("*/*.json"))
            return True

    monkeypatch.setitem(REGISTRY, "example", Example)
    documentation = {
        "description": "Example workload",
        "parameter_descriptions": {"number_of_adapters": "Number of adapters"},
        "metrics": {"observed_events": "Events received"},
    }
    Example.documentation = CaseDocumentation(**documentation)
    config = run_config(cases=[{"id": "x", "type": "example"}])
    directory, passed = execute_run_config(config, expand(config), client(), tmp_path, Clock())
    assert events[-2:] == ["preparation", "cleanup"]
    assert passed == (failure is None)
    result = json.loads((directory / "x-m1-r1.json").read_text())
    assert result["scenario"]["documentation"] == documentation
    manifest = json.loads((directory / "manifest.json").read_text())
    assert manifest["planned_scenarios"][0]["documentation"] == documentation
    assert result["cleanup"]["status"] == "completed"
    assert "private-token" not in json.dumps(result)
    assert (directory / "summary.csv").exists()
    assert not list(directory.glob("*.tmp"))


def test_cleanup_failure_stops_run_config(tmp_path, monkeypatch):
    class Example(Case):
        def setup(self, context):
            pass

        def run(self, context):
            pass

        def cleanup(self, context):
            return False

    monkeypatch.setitem(REGISTRY, "example", Example)
    config = run_config(cases=[{"id": "x", "type": "example"}], execution={'repetitions': 2})
    directory, passed = execute_run_config(config, expand(config), client(), tmp_path, Clock())
    assert not passed
    manifest = json.loads((directory / "manifest.json").read_text())
    assert len(manifest["results"]) == 1


def test_dirty_instance_does_not_create(tmp_path):
    api = client()
    api.adapterApi.all.return_value = [{"elementId": "unowned"}]
    config = run_config()
    directory, passed = execute_run_config(config, expand(config), api, tmp_path, Clock())
    assert not passed
    api.adapterApi.create_result.assert_not_called()
    assert json.loads((directory / "manifest.json").read_text())["results"] == []


def test_partial_creation_reconciles_and_cleanup_order():
    api = client()
    events = []
    resources = {
        "adapter": [{"elementId": "a", "name": "perf-a"}],
        "pipeline": [{"elementId": "p", "name": "perf-p"}],
        "dataset": [{"elementId": "d", "measureName": "perf-d"}],
    }
    for kind, endpoint in [
        ("adapter", api.adapterApi),
        ("pipeline", api.pipelineApi),
        ("dataset", api.datasetApi),
    ]:
        endpoint.all.side_effect = lambda kind=kind: list(resources[kind])
        endpoint.stop_result.side_effect = lambda *args, kind=kind, **kw: events.append(f"stop:{kind}")

        def delete(identifier, kind=kind):
            events.append(f"delete:{kind}")
            resources[kind].clear()

        (endpoint.delete if kind == "dataset" else endpoint.delete_result).side_effect = delete
    context = Context(api, Timing(), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    assert context.cleanup()
    assert events == ["stop:adapter", "stop:pipeline", "delete:pipeline", "delete:adapter", "delete:dataset"]
    assert all(resource.deleted for resource in context.resources)


def query(rows, status="OK"):
    return SimpleNamespace(
        query_status=status,
        headers=["time"],
        all_data_series=[SimpleNamespace(rows=[[value] for value in rows])],
    )


def test_pagination_does_not_truncate():
    api = client()
    api.dataLakeMeasureApi.get.side_effect = [query([10, 20]), query([30]), query([10, 20])]
    context = Context(api, Timing(), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    timestamps, _, complete = collect_pages(context, "data", 0, 100, 2, 3, 20)
    assert timestamps == [10, 20, 30] and complete
    assert api.dataLakeMeasureApi.get.call_args.kwargs["offset"] == 2
    assert collect_pages(context, "data", 0, 100, 2, 1, 20)[2] is False


def test_server_truncation_flag():
    api = client()
    api.dataLakeMeasureApi.get.return_value = query([10], "TOO_MUCH_DATA")
    context = Context(api, Timing(), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    assert collect_pages(context, "data", 0, 100, 2, 3, 20)[2] is False


def test_drain_waits_for_stable_snapshot():
    api = client()
    api.datasetApi.get_by_name.return_value = {"elementId": "dataset", "measureName": "data"}
    api.dataLakeMeasureApi.get.side_effect = [
        query([100]),
        query([100, 200]),
        query([100, 200]),
        query([100, 200]),
    ]
    context = Context(api, Timing(stable_seconds=1, drain_timeout_seconds=5), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    result = collect_window(context, ["data"], 0, 1000, wait_time_ms=1000, page_size=1000, max_pages=1000)
    assert result["observed_events"] == 2
    assert result["collection_complete"]
    assert result["datasets"][0]["event_gaps_ms"]["mean"] == 100
    assert "event_gap_measurements" not in result["datasets"][0]


def test_missing_dataset_is_incomplete():
    api = client()
    response = Response()
    response.status_code = 404
    api.datasetApi.get_by_name.side_effect = HTTPError(response=response)
    context = Context(api, Timing(stable_seconds=1, drain_timeout_seconds=2), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    result = collect_window(context, ["data"], 0, 1000, wait_time_ms=1000, page_size=1000, max_pages=1000)
    assert not result["collection_complete"]


def test_iso_timestamps():
    from streampipes_performance.measurement import timestamp_ms

    assert timestamp_ms("2023-11-14T22:13:20Z") == 1700000000000
    assert timestamp_ms("1000") == 1000
    with pytest.raises(ValueError):
        timestamp_ms(float("nan"))


def test_readiness_retries_not_found():
    response = Response()
    response.status_code = 404
    predicate = Mock(side_effect=[HTTPError(response=response), True])
    context = Context(client(), Timing(), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    context.wait("created", predicate)
    assert predicate.call_count == 2


def test_cleanup_continues_after_error():
    api = client()
    api.adapterApi.all.return_value = [{"elementId": "a", "name": "perf-a"}]
    api.pipelineApi.all.side_effect = [[{"elementId": "p", "name": "perf-p"}], [], [], []]
    api.adapterApi.stop_result.side_effect = Timeout()
    api.adapterApi.delete_result.side_effect = lambda *args: setattr(api.adapterApi.all, "return_value", [])
    context = Context(api, Timing(), "perf", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    assert not context.cleanup()
    api.adapterApi.delete_result.assert_called_once_with("a")
    assert context.cleanup_errors


def test_continue_after_case_failure_when_clean(tmp_path, monkeypatch):
    count = []

    class Example(Case):
        def setup(self, context):
            count.append(1)
            if len(count) == 1:
                raise RuntimeError

        def run(self, context):
            pass

        def cleanup(self, context):
            return True

    monkeypatch.setitem(REGISTRY, "example", Example)
    config = run_config(cases=[{"id": "x", "type": "example"}], execution={'repetitions': 2})
    directory, passed = execute_run_config(config, expand(config), client(), tmp_path, Clock())
    assert not passed and len(count) == 2
    assert json.loads((directory / "x-m1-r2.json").read_text())["status"] == "passed"


def test_all_example_configs():
    from pathlib import Path

    from streampipes_performance.config import load

    configs = list((Path(__file__).parents[1] / "run_configs").glob("*.json"))
    assert len(configs) >= 7
    for path in configs:
        assert load(path)[1]


@pytest.mark.parametrize("command", ["validate", "preview"])
def test_cli_offline(command, tmp_path, monkeypatch, capsys):
    from streampipes_performance.cli import main

    config = tmp_path / "run_config.json"
    write_run_config(config, run_config())
    monkeypatch.delenv("STREAMPIPES_URL", raising=False)
    monkeypatch.setattr("sys.argv", ["sp-performance", command, str(config)])
    monkeypatch.setattr("requests.Session.request", Mock(side_effect=AssertionError("Network request")))
    assert main() == 0
    assert capsys.readouterr().out


def test_cli_rejects_invalid_config(tmp_path, monkeypatch):
    from streampipes_performance.cli import main

    config = tmp_path / "run_config.json"
    config.write_text('{"target":"streampipes","cases":[]}')
    monkeypatch.setattr("sys.argv", ["sp-performance", "validate", str(config)])
    assert main() == 2


@pytest.mark.parametrize("url_env", ["STREAMPIPES_URL", "CUSTOM_STREAMPIPES_URL"])
def test_cli_runs_with_env_credentials(tmp_path, monkeypatch, url_env):
    from test_cases import Server

    from streampipes_performance.cli import main

    server = Server()
    monkeypatch.setattr("streampipes_performance.cases.streampipes.adapter_start_stop.case.INTERVAL_SECONDS", 0)
    server.request_session = Mock()
    config = run_config(cases=[
            {
                "id": "lifecycle",
                "type": "streampipes.adapter_start_stop",
                "parameters": {"cycles": 1},
            }
        ], execution={'timing': {"warmup_seconds": 0, "duration_seconds": 0.001}})
    config.connection.url_env = url_env
    path = tmp_path / "run_config.json"
    write_run_config(path, config)
    monkeypatch.setattr("sys.argv", ["sp-performance", "run", str(path), "--no-wait-ready"])
    monkeypatch.setenv(url_env, "https://perf.example:8443")
    monkeypatch.setenv("STREAMPIPES_USERNAME", "user")
    monkeypatch.setenv("STREAMPIPES_PASSWORD", "password-not-in-reports")
    auth_session = Mock()
    auth_session.request.side_effect = [
        Mock(json=Mock(return_value=data)) for data in [
            {"accessToken": "initial-jwt"},
            {"rawToken": "secret-not-in-reports", "tokenId": "run-token"},
            {"accessToken": "fresh-jwt"},
            {"principalId": "user-id", "userApiTokens": [{"tokenId": "run-token"}]},
            {"success": True},
        ]
    ]
    auth_factory = Mock()
    auth_factory.return_value.__enter__ = Mock(return_value=auth_session)
    auth_factory.return_value.__exit__ = Mock(return_value=False)
    monkeypatch.setattr("streampipes_performance.auth.requests.Session", auth_factory)
    factory = Mock(return_value=server)
    monkeypatch.setattr("streampipes_performance.clients.streampipes.StreamPipesClient", factory)
    assert main() == 0
    client_config = factory.call_args.args[0]
    assert client_config.host_address == "perf.example"
    assert client_config.port == 8443
    assert client_config.https_disabled is False
    server.request_session.close.assert_called_once()
    assert auth_session.request.call_args.kwargs["json"]["userApiTokens"] == []
    assert "secret-not-in-reports" not in "".join(p.read_text() for p in tmp_path.rglob("*.json"))
    assert "password-not-in-reports" not in "".join(p.read_text() for p in tmp_path.rglob("*.json"))


def test_cleanup_configuration_defaults_and_override():
    config = run_config(cases=[
            {"id": "inherited", "type": "streampipes.adapter_ingestion"},
            {"id": "override", "type": "streampipes.adapter_ingestion", "execution": {'cleanup': {"strategy": "delete"}}},
        ], execution={'cleanup': {"strategy": "reset", "reset_timeout_seconds": 900}})
    scenarios = expand(config)
    assert scenarios[0].cleanup.strategy == "reset"
    assert scenarios[0].cleanup.reset_timeout_seconds == 900
    assert scenarios[1].cleanup.strategy == "delete"
    assert expand(run_config(execution={"cleanup": {}}))[0].cleanup.strategy == "reset"


@pytest.mark.parametrize(
    "cleanup",
    [
        {"strategy": "unknown"},
        {"reset_timeout_seconds": 0},
        {"reset_timeout_seconds": -1},
        {"reset_timeout_seconds": float("inf")},
    ],
)
def test_invalid_cleanup_configuration(cleanup):
    with pytest.raises(ValidationError):
        run_config(execution={'cleanup': cleanup})


def test_adapter_run_config_uses_reset_cleanup():
    from pathlib import Path
    from streampipes_performance.config import load

    _, scenarios = load(Path(__file__).parents[1] / "run_configs/adapter-ingestion.json")
    assert len(scenarios) == 3
    assert all(s.cleanup.strategy == "reset" for s in scenarios)


def test_reset_is_measured_and_verified_without_resource_deletion():
    from streampipes_performance.config import Cleanup

    api = client()
    clock = Clock()

    def reset(*args, **kwargs):
        clock.sleep(3)
        return {"success": True}

    api.request_json.side_effect = reset
    context = Context(
        api,
        Timing(),
        "perf",
        clock=clock,
        cleanup_config=Cleanup(strategy="reset", reset_timeout_seconds=123),
    )
    context.phase = "cleanup"
    context.track("adapter", "a", "perf-a")
    assert context.cleanup()
    api.request_json.assert_called_once_with("POST", "api/v2/reset", timeout=123)
    api.adapterApi.delete_result.assert_not_called()
    api.adapterApi.stop_result.assert_not_called()
    api.pipelineApi.delete_result.assert_not_called()
    assert context.resources[0].deleted
    assert context.samples[0]["operation"] == "instance.reset"
    assert context.samples[0]["duration_ms"] == 3000
    assert context.samples[1]["operation"] == "cleanup.empty"


@pytest.mark.parametrize("failure", ["timeout", "http", "body", "leftovers"])
def test_reset_failure_does_not_fallback(failure):
    from streampipes_performance.config import Cleanup

    api = client()
    api.request_json.return_value = {"success": True}
    if failure == "timeout":
        api.request_json.side_effect = Timeout()
    elif failure == "http":
        response = Response()
        response.status_code = 404
        api.request_json.side_effect = HTTPError(response=response)
    elif failure == "body":
        api.request_json.return_value = {"success": False}
    else:
        api.adapterApi.all.return_value = [{"elementId": "remaining"}]
    context = Context(
        api,
        Timing(readiness_timeout_seconds=1),
        "perf",
        clock=Clock(),
        cleanup_config=Cleanup(strategy="reset"),
    )
    context.track("adapter", "remaining", "perf-remaining")
    assert not context.cleanup()
    assert not context.resources[0].deleted
    api.request_json.assert_called_once()
    api.adapterApi.delete_result.assert_not_called()
    api.adapterApi.stop_result.assert_not_called()
    assert context.cleanup_errors[0]["operation"] == (
        "verify_empty" if failure == "leftovers" else "instance.reset"
    )


@pytest.mark.parametrize("interrupt", [False, True])
def test_runner_resets_after_partial_setup_and_saves_strategy(tmp_path, monkeypatch, interrupt):
    class Example(Case):
        def setup(self, context):
            context.track("adapter", "a", "perf-a")
            if interrupt:
                raise KeyboardInterrupt
            raise RuntimeError

        def run(self, context):
            raise AssertionError("Setup did not succeed")

    monkeypatch.setitem(REGISTRY, "example", Example)
    api = client()

    def reset(*args, **kwargs):
        partial = list(tmp_path.glob("*/x-m1-r1.json"))
        assert len(partial) == 1
        assert json.loads(partial[0].read_text())["cleanup"]["status"] == "pending"
        return {"success": True}

    api.request_json.side_effect = reset
    config = run_config(cases=[{"id": "x", "type": "example"}], execution={'cleanup': {"strategy": "reset"}})
    directory, passed = execute_run_config(config, expand(config), api, tmp_path, Clock())
    assert not passed
    result = json.loads((directory / "x-m1-r1.json").read_text())
    assert result["cleanup"] == {"status": "completed", "strategy": "reset", "errors": []}
    assert result["scenario"]["cleanup"]["strategy"] == "reset"
    assert "Instance reset time (cleanup)" in result["metrics"]
    import csv

    with (directory / "summary.csv").open() as summary:
        assert {row["cleanup_strategy"] for row in csv.DictReader(summary)} == {"reset"}


def test_failed_reset_stops_remaining_scenarios(tmp_path, monkeypatch):
    class Example(Case):
        def setup(self, context):
            pass

        def run(self, context):
            pass

    monkeypatch.setitem(REGISTRY, "example", Example)
    api = client()
    api.request_json.side_effect = Timeout()
    config = run_config(cases=[{"id": "x", "type": "example"}], execution={'cleanup': {"strategy": "reset"}, 'repetitions': 2})
    directory, passed = execute_run_config(config, expand(config), api, tmp_path, Clock())
    assert not passed
    assert len(json.loads((directory / "manifest.json").read_text())["results"]) == 1
    api.request_json.assert_called_once()


def test_reset_is_not_called_when_instance_is_dirty(tmp_path):
    api = client()
    api.adapterApi.all.return_value = [{"elementId": "unowned"}]
    config = run_config(execution={'cleanup': {"strategy": "reset"}})
    _, passed = execute_run_config(config, expand(config), api, tmp_path, Clock())
    assert not passed
    api.request_json.assert_not_called()


def test_reset_rejected_for_targets_without_reset():
    from streampipes_performance.targets import Target

    api = client()
    context = Context(api, Timing(), "perf", clock=Clock(), target=Target("vendor", "Vendor", supports_reset=False))
    with pytest.raises(ValueError, match="Vendor"):
        context.reset_cleanup()
    api.request_json.assert_not_called()


def test_extra_apis_join_inventory_and_cleanup_order():
    api = client()
    order = []
    running = Mock(spec=["all", "stop_result", "delete_result"])
    static = Mock(spec=["all", "delete_result", "owns"])
    running.all.return_value = [{"_id": "j", "name": "perf-j"}]
    static.all.return_value = [{"_id": "vendor-perf-x", "name": "other"}]
    static.owns.side_effect = lambda identifier, prefix: identifier == f"vendor-{prefix}-x"
    running.stop_result.side_effect = lambda identifier, force_stop=False: order.append(("stop", identifier))

    def delete_running(identifier):
        order.append(("delete", identifier))
        running.all.return_value = []

    running.delete_result.side_effect = delete_running

    def delete_static(identifier):
        order.append(("delete", identifier))
        static.all.return_value = []

    static.delete_result.side_effect = delete_static
    api.pipelineApi.stop_result.side_effect = lambda identifier, force_stop=False: order.append(("stop", identifier))
    api.pipelineApi.all.return_value = [{"pipelineId": "p", "name": "perf-p"}]

    def delete_pipeline(identifier):
        order.append(("delete", identifier))
        api.pipelineApi.all.return_value = []

    api.pipelineApi.delete_result.side_effect = delete_pipeline
    api.streamApi.all.return_value = []
    context = Context(api, Timing(readiness_timeout_seconds=1), "perf", clock=Clock(),
                      cleanup_config=Cleanup(strategy="delete"), extra_apis={"job": running, "setting": static})
    context.track("pipeline", "p", "perf-p")
    context.track("job", "j", "perf-j")
    assert context.cleanup(), context.cleanup_errors
    assert ("delete", "vendor-perf-x") in order
    assert order.index(("stop", "j")) < order.index(("stop", "p"))
    assert order.index(("delete", "j")) < order.index(("delete", "p")) < order.index(("delete", "vendor-perf-x"))


@pytest.mark.parametrize("delete_succeeds", [True, False])
def test_stream_cleanup_ignores_unrelated_registry_entries(delete_succeeds):
    api = client()
    registered = {
        "input": {"elementId": "input", "name": "renamed-input"},
        "lost": {"elementId": "lost", "name": "perf-lost"},
        "installed": {"elementId": "installed", "name": "installed-service-stream"},
    }
    api.streamApi.all.side_effect = lambda: list(registered.values())

    def delete(identifier):
        if delete_succeeds:
            registered.pop(identifier)
        return {"success": True}

    api.streamApi.delete_result.side_effect = delete
    context = Context(
        api, Timing(readiness_timeout_seconds=1), "perf", clock=Clock(),
        cleanup_config=Cleanup(strategy="delete"),
    )
    context.track("stream", "input", "perf-input")
    assert context.cleanup() is delete_succeeds
    assert [call.args[0] for call in api.streamApi.delete_result.call_args_list] == ["input", "lost"]
    assert "installed" in registered
    if delete_succeeds:
        assert set(registered) == {"installed"}
        assert all(resource.deleted for resource in context.resources)
    else:
        assert context.cleanup_errors[-1]["operation"] == "verify_empty"
        assert all(not resource.deleted for resource in context.resources)


@pytest.mark.parametrize("failure", [None, "measurement", "interrupt", "cleanup"])
def test_report_is_created_once_after_final_results_and_cleanup(tmp_path, monkeypatch, failure):
    from streampipes_performance.report.run_report import write_report

    events = []

    class Example(Case):
        def setup(self, context):
            pass

        def run(self, context):
            events.append("measurement")
            if failure == "measurement":
                raise ValueError("failed query")
            if failure == "interrupt":
                raise KeyboardInterrupt

        def cleanup(self, context):
            events.append("cleanup")
            return failure != "cleanup"

    def report(directory):
        events.append("report")
        manifest = json.loads((directory / "manifest.json").read_text())
        assert manifest["status"] == ("passed" if failure is None else "interrupted" if failure == "interrupt" else "failed")
        assert len(manifest["results"]) == (1 if failure in {"interrupt", "cleanup"} else 2)
        for entry in manifest["results"]:
            result = json.loads((directory / entry["file"]).read_text())
            assert result["status"] != "running"
            assert result["cleanup"]["status"] != "pending"
        assert (directory / "summary.csv").is_file()
        return write_report(directory)

    monkeypatch.setitem(REGISTRY, "example", Example)
    monkeypatch.setattr("streampipes_performance.runner.write_report", report)
    config = run_config(cases=[{"id": "x", "type": "example"}], execution={"repetitions": 2})
    directory, passed = execute_run_config(config, expand(config), client(), tmp_path, Clock())
    assert passed == (failure is None)
    assert events[-2:] == ["cleanup", "report"]
    assert events.count("report") == 1
    html = (directory / "report.html").read_text()
    embedded = html.split('<script id="report-data" type="application/json">')[1].split("</script>")[0]
    assert json.loads(embedded)["run"]["status"] == ("passed" if passed else "interrupted" if failure == "interrupt" else "failed")


def test_interruption_during_instance_check_still_writes_interrupted_report(tmp_path, monkeypatch):
    monkeypatch.setattr(Context, "assert_empty", Mock(side_effect=KeyboardInterrupt))
    config = run_config()
    directory, passed = execute_run_config(config, expand(config), client(), tmp_path, Clock())
    assert not passed
    assert json.loads((directory / "manifest.json").read_text())["status"] == "interrupted"
    assert (directory / "report.html").is_file()
