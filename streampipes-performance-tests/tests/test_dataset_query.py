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

"""Dataset setup, query validation, concurrency, and failure cleanup."""

import threading
from types import SimpleNamespace
from unittest.mock import Mock

import pytest
from test_cases import Server
from test_framework import Clock

from streampipes_performance.cases.streampipes.dataset_query.case import DatasetQuery, HEADERS, QUERIES, Parameters, rows
from streampipes_performance.config import Cleanup, Timing
from streampipes_performance.metrics import operation_metrics
from streampipes_performance.runtime import Context, EvaluationError


@pytest.fixture
def environment(monkeypatch):
    monkeypatch.delenv("INFLUX_TOKEN", raising=False)
    server = Server()
    server.client_config = object()
    stored = {}

    def create(payload):
        name = payload["measureName"]
        identifier = f"id-{name}"
        server.datasets[identifier] = {**payload, "elementId": identifier}
        stored[name] = []
        return server.datasets[identifier]

    server.datasetApi.create.side_effect = create
    server.datasetApi.upload.side_effect = lambda name, headers, batch: stored[name].extend(batch)
    def count(identifier):
        metadata = server.datasets[identifier]
        # Match the backend: select the first non-dimension schema property
        # for COUNT. Timestamp is stored as time, never as a countable field.
        field = next(prop["runtimeName"] for prop in metadata["eventSchema"]["eventProperties"]
                     if prop["propertyScope"] != "DIMENSION_PROPERTY")
        if field == "timestamp":
            return 0
        return sum(row[HEADERS.index(field)] is not None for row in stored[metadata["measureName"]])

    server.datasetApi.count.side_effect = count
    server.datasetApi.get_by_name.side_effect = lambda name: server.datasets[f"id-{name}"]
    result = SimpleNamespace(query_status="OK", all_data_series=[SimpleNamespace(rows=[[1]])])
    server.dataLakeMeasureApi.get.side_effect = None
    server.dataLakeMeasureApi.get.return_value = result
    clients = []

    def client_factory(config):
        assert config is server.client_config
        client = SimpleNamespace(dataLakeMeasureApi=Mock(), datasetApi=Mock(), request_session=Mock())
        client.dataLakeMeasureApi.get.return_value = result
        clients.append(client)
        return client

    monkeypatch.setattr("streampipes_performance.cases.streampipes.dataset_query.case.StreamPipesClient", client_factory)
    context = Context(server, Timing(), "test-query", clock=Clock(), cleanup_config=Cleanup(strategy="delete"))
    return context, stored, clients


def test_setup_batches_schema_and_cleanup(environment):
    context, stored, clients = environment
    case = DatasetQuery({'number_of_datasets': 2, 'rows_per_dataset': 1001, 'parallel_operations': 2})
    case.setup(context)
    assert [len(batch.args[2]) for batch in context.client.datasetApi.upload.call_args_list] == [1000, 1] * 2
    assert all(len(events) == 1001 for events in stored.values())
    schema = context.client.datasetApi.create.call_args.args[0]["eventSchema"]["eventProperties"]
    assert {prop["runtimeName"] for prop in schema} == set(HEADERS) - {"timestamp"}
    assert context.client.datasetApi.create.call_args.args[0]["timestampField"] == "s0::timestamp"
    assert next(prop for prop in schema if prop["runtimeName"] == "sensorId")["propertyScope"] == "DIMENSION_PROPERTY"
    assert len(context.resources) == 2
    assert len(clients) == 2
    assert case.cleanup(context)
    assert not context.client.datasets
    assert all(client.request_session.close.call_count == 1 for client in clients)


def test_generator_and_query_options():
    assert set(QUERIES) == {"latest", "projection", "time_range", "filtered", "aggregate"}
    events = list(rows(49, 51))
    assert events == list(rows(49, 51))
    assert events[1][HEADERS.index("timestamp")] - events[0][HEADERS.index("timestamp")] == 1000
    assert events[0][HEADERS.index("sensor_fault_flags")] is False
    assert events[1][HEADERS.index("sensor_fault_flags")] is True
    from streampipes.endpoint.api.data_lake_measure import MeasurementGetQueryConfig
    for row_count in (1, 2, 10000):
        for options in QUERIES.values():
            parameters = Parameters(rows_per_dataset=row_count)
            query_config = MeasurementGetQueryConfig.model_validate(options(parameters))
            assert query_config.limit is None
            assert "limit=" not in query_config.build_query_string()
        window = QUERIES["time_range"](parameters)
        assert window["start_date"] < window["end_date"]
    assert QUERIES["latest"](Parameters()) == {"limit": None, "order": "DESC"}


def test_complete_dataset_workflows_without_workers(environment, monkeypatch):
    monkeypatch.setattr("streampipes_performance.cases.streampipes.dataset_query.case.ITERATIONS", 2)
    context, _, clients = environment
    case = DatasetQuery({'number_of_datasets': 2, 'rows_per_dataset': 2, 'parallel_operations': 1})
    case.setup(context)
    assert not clients
    assert not context.client.dataLakeMeasureApi.get.called
    context.client.dataLakeMeasureApi.get.reset_mock()
    case.run(context)
    calls = context.client.dataLakeMeasureApi.get.call_args_list
    expected = [(name, options(case.parameters)) for name in context.datasets
                for _ in range(2) for options in QUERIES.values()]
    assert [(call.args[0], call.kwargs) for call in calls] == expected
    assert [result["query"] for result in case.results] == list(QUERIES)
    assert case.results[0]["query"] == "latest"
    assert case.results[0]["concurrency"] == 1
    assert case.cleanup(context)


def test_query_size_sensor_count_and_user_alternatives_expand_to_independent_scenarios():
    from streampipes_performance.config import RunConfig, expand

    run_config = RunConfig.model_validate({"target": "streampipes", "cases": [{
        "id": "queries", "type": "streampipes.dataset_query", "parameters": {
            "parallel_operations": [1, 3], "rows_per_dataset": [100, 1000], "number_of_datasets": [1, 2],
            "sensor_count": [10, 100],
        },
    }]})
    scenarios = expand(run_config)
    assert len(scenarios) == 16
    assert {(s.parameters["number_of_datasets"], s.parameters["parallel_operations"], s.parameters["rows_per_dataset"],
             s.parameters["sensor_count"]) for s in scenarios} == {
        (count, users, rows, sensors) for count in [1, 2] for users in [1, 3]
        for rows in [100, 1000] for sensors in [10, 100]
    }


def test_parallel_requests_overlap_and_use_independent_clients(environment, monkeypatch):
    monkeypatch.setattr("streampipes_performance.cases.streampipes.dataset_query.case.ITERATIONS", 2)
    mode = "parallel_many"
    context, _, clients = environment
    case = DatasetQuery({'number_of_datasets': 2, 'rows_per_dataset': 2, 'parallel_operations': 2})
    case.setup(context)
    barrier = threading.Barrier(2)

    def query(*args, **kwargs):
        barrier.wait(timeout=3)  # Fails if the supposed parallel requests are serialized.
        return SimpleNamespace(query_status="OK", all_data_series=[SimpleNamespace(rows=[[1]])])

    for client in clients:
        client.dataLakeMeasureApi.get.reset_mock()
        client.dataLakeMeasureApi.get.side_effect = query
    context.phase = "measurement"
    case.run(context)
    workloads = case.prepare_results(context)["query_workloads"]
    assert [workload["query"] for workload in workloads] == list(QUERIES)
    assert workloads[0]["mode"] == mode
    assert all(workload["completed"] == 4 and workload["errors"] == 0 for workload in workloads)
    for i, client in enumerate(clients):
        names = [call.args[0] for call in client.dataLakeMeasureApi.get.call_args_list]
        assert names == [context.datasets[i]] * (2 * len(QUERIES))
    operations = operation_metrics(context.samples)["operations"]
    assert "Query time" in operations
    assert not any(mode in name for name in operations)
    measured = [stats for key, stats in operations.items() if key == "Query time"]
    assert len(measured) == 1
    assert all(stats["count"] == 4 * len(QUERIES) and stats["errors"] == 0 for stats in measured)
    assert case.cleanup(context)


@pytest.mark.parametrize("status,series", [("TOO_MUCH_DATA", [[1]]), ("OK", [])])
def test_invalid_query_results_fail_samples_and_workload(environment, status, series, monkeypatch):
    monkeypatch.setattr("streampipes_performance.cases.streampipes.dataset_query.case.ITERATIONS", 2)
    context, _, _ = environment
    case = DatasetQuery({'number_of_datasets': 1, 'rows_per_dataset': 1})
    case.setup(context)
    context.client.dataLakeMeasureApi.get.return_value = SimpleNamespace(
        query_status=status, all_data_series=[SimpleNamespace(rows=series)]
    )
    context.phase = "measurement"
    with pytest.raises(EvaluationError, match="dataset_queries_failed"):
        case.run(context)
    assert all(result["errors"] == 2 for result in case.results)
    assert all(not sample["success"] for sample in context.samples if sample["phase"] == "measurement")
    assert case.cleanup(context)


def test_upload_failure_still_cleans_created_dataset(environment):
    context, _, _ = environment
    case = DatasetQuery({'rows_per_dataset': 1})
    context.client.datasetApi.upload.side_effect = RuntimeError("upload failed")
    with pytest.raises(RuntimeError):
        case.setup(context)
    assert len(context.resources) == 1
    assert case.cleanup(context)


def test_readiness_requires_all_uploaded_rows(environment):
    context, _, _ = environment
    case = DatasetQuery({'number_of_datasets': 1, 'rows_per_dataset': 2})
    context.client.datasetApi.count.side_effect = lambda _: 1
    with pytest.raises(EvaluationError, match="dataset_readiness_timeout: expected_rows=2, observed_rows=1"):
        case.setup(context)
    assert case.cleanup(context)


@pytest.mark.parametrize("parameters", [{"concurrency": 0}, {"rows_per_dataset": 0}, {"batch_size": 0},
                                        {"query": []}, {"query": "unknown"},
                                        {"parallel_operations": 0}, {"parallel_operations": 1.5},
                                        {"concurrency": "unknown"}])
def test_invalid_parameters(parameters):
    with pytest.raises(ValueError):
        DatasetQuery(parameters)


def test_lost_creation_response_reconciles_dataset(environment):
    context, _, _ = environment
    case = DatasetQuery({'rows_per_dataset': 1})
    create = context.client.datasetApi.create.side_effect

    def lost_response(payload):
        create(payload)
        raise TimeoutError()

    context.client.datasetApi.create.side_effect = lost_response
    with pytest.raises(TimeoutError):
        case.setup(context)
    assert context.datasets
    assert case.cleanup(context)
    assert not context.client.datasets


def test_runner_saves_query_metrics_and_cleanup(environment, tmp_path):
    import json

    from streampipes_performance.config import RunConfig, expand
    from streampipes_performance.runner import execute_run_config

    context, _, _ = environment
    run_config = RunConfig.model_validate({
        "target": "streampipes", "output_directory": str(tmp_path),
        "execution": {"cleanup": {"strategy": "delete"}},
        "cases": [{"id": "query", "type": "streampipes.dataset_query", "parameters": {
            "number_of_datasets": 1, "rows_per_dataset": 2, "parallel_operations": 1,
        }}],
    })
    directory, success = execute_run_config(run_config, expand(run_config), context.client, clock=context.clock)
    assert success
    result = json.loads((directory / "query-m1-r1.json").read_text())
    assert result["cleanup"]["status"] == "completed"
    assert "Query time" in result["metrics"]["operations"]
    assert all(not key.startswith(("setup.", "measurement.")) for key in result["metrics"]["operations"])
    import csv

    with (directory / "summary.csv").open() as summary:
        names = {row["metric"] for row in csv.DictReader(summary)}
    assert {f"Query time · {stat} (ms)" for stat in ("mean", "min", "max")} <= names
    assert not any(stat in name for name in names for stat in ("p50", "p95", "p99"))
    assert not any(name.startswith("operations.measurement.") for name in names)
    assert [workload["query"] for workload in result["metrics"]["query_workloads"]] == list(QUERIES)
    assert all(resource["deleted"] for resource in result["resources"])


def test_hundred_rows_are_ready_with_backend_count_contract(environment, caplog):
    context, stored, _ = environment
    case = DatasetQuery({'number_of_datasets': 1, 'rows_per_dataset': 100})
    with caplog.at_level("INFO", logger="streampipes_performance.runtime"):
        case.setup(context)
    assert len(stored[context.datasets[0]]) == 100
    assert context.client.datasetApi.count.call_count == 1
    assert "uploaded 100% (100/100 rows)" in caplog.text
    assert "persisted 100/100 rows" in caplog.text
    # Demonstrate why the old payload produced zero despite a successful upload.
    metadata = context.client.datasets[context.resources[0].identifier]
    metadata["eventSchema"]["eventProperties"].insert(
        0, {"runtimeName": "timestamp", "propertyScope": "HEADER_PROPERTY"}
    )
    assert context.client.datasetApi.count(context.resources[0].identifier) == 0
    assert case.cleanup(context)


def test_readiness_waits_for_count_to_catch_up(environment):
    context, _, _ = environment
    case = DatasetQuery({'number_of_datasets': 1, 'rows_per_dataset': 100})
    context.client.datasetApi.count.side_effect = [0, 50, 100]
    case.setup(context)
    assert context.client.datasetApi.count.call_count == 3
    assert case.cleanup(context)


def test_partial_worker_creation_closes_existing_session(environment, monkeypatch):
    context, _, clients = environment
    from streampipes_performance.cases.streampipes.dataset_query import case as module

    factory = module.StreamPipesClient

    def fail_second(config):
        if clients:
            raise OSError("session creation failed")
        return factory(config)

    monkeypatch.setattr(module, "StreamPipesClient", fail_second)
    case = DatasetQuery({"rows_per_dataset": 2, "parallel_operations": 2})
    with pytest.raises(OSError, match="session creation failed"):
        case.setup(context)
    assert case.cleanup(context)
    assert not context.client.datasets
    clients[0].request_session.close.assert_called_once()


def test_default_query_count_is_ten_per_dataset(environment):
    context, _, _ = environment
    case = DatasetQuery({"rows_per_dataset": 2, "number_of_datasets": 2})
    case.setup(context)
    context.client.dataLakeMeasureApi.get.reset_mock()
    case.run(context)
    assert all(workload["requests"] == workload["completed"] == 20 for workload in case.results)
    assert context.client.dataLakeMeasureApi.get.call_count == 20 * len(QUERIES)
    assert case.cleanup(context)


def test_new_query_is_measured_without_runner_changes(environment, monkeypatch):
    context, _, _ = environment
    monkeypatch.setitem(QUERIES, "custom_limit", lambda p: {"limit": 7, "order": "DESC"})
    case = DatasetQuery({"number_of_datasets": 1, "rows_per_dataset": 2})
    case.setup(context)
    assert not context.client.dataLakeMeasureApi.get.called
    context.phase = "measurement"
    case.run(context)
    custom = case.results[-1]
    assert custom["query"] == "custom_limit"
    assert custom["completed"] == custom["query_time_ms"]["count"] == 10
    assert len([s for s in context.samples if s["operation"] == "dataset.query.custom_limit"]) == 10
    assert case.cleanup(context)


def test_failed_query_does_not_skip_later_queries(environment):
    context, _, _ = environment
    case = DatasetQuery({"number_of_datasets": 1, "rows_per_dataset": 2})
    case.setup(context)

    def query(name, **options):
        if "filter" in options:
            raise ValueError("filtered query failed")
        return SimpleNamespace(query_status="OK", all_data_series=[SimpleNamespace(rows=[[1]])])

    context.client.dataLakeMeasureApi.get.side_effect = query
    context.phase = "measurement"
    with pytest.raises(EvaluationError, match="dataset_queries_failed"):
        case.run(context)
    assert [result["query"] for result in case.results] == list(QUERIES)
    assert next(result for result in case.results if result["query"] == "filtered")["errors"] == 10
    assert case.results[-1]["query"] == "aggregate"
    assert case.results[-1]["completed"] == 10
    assert case.cleanup(context)


def test_query_timing_summaries_are_independent(environment):
    context, _, _ = environment
    case = DatasetQuery({"number_of_datasets": 1, "rows_per_dataset": 2})
    case.setup(context)

    def query(name, **options):
        context.clock.sleep(0.005 if "aggregation_function" in options else 0.001)
        return SimpleNamespace(query_status="OK", all_data_series=[SimpleNamespace(rows=[[1]])])

    context.client.dataLakeMeasureApi.get.side_effect = query
    context.phase = "measurement"
    case.run(context)
    for workload in case.results:
        assert workload["query_time_ms"]["count"] == 10
        expected = 5 if workload["query"] == "aggregate" else 1
        assert workload["query_time_ms"]["mean"] == pytest.approx(expected)
    stats = operation_metrics(context.samples)["operations"]["Query time"]
    assert stats["count"] == 50
    assert stats["mean"] == pytest.approx(1.8)
    assert case.cleanup(context)


@pytest.mark.parametrize("users", [1, 10])
def test_setup_does_not_issue_query_or_client_warmup(environment, users):
    context, _, clients = environment
    case = DatasetQuery({"number_of_datasets": 2, "rows_per_dataset": 2, "parallel_operations": users})
    case.setup(context)
    assert not context.client.dataLakeMeasureApi.get.called
    for client in clients:
        assert not client.datasetApi.get_by_name.called
        assert not client.dataLakeMeasureApi.get.called
    assert not any("warmup" in s["operation"] for s in context.samples)
    assert "Query time" not in operation_metrics(context.samples)["operations"]
    assert case.cleanup(context)


def test_first_query_delay_is_measured(environment):
    context, _, _ = environment
    case = DatasetQuery({"number_of_datasets": 1, "rows_per_dataset": 2})
    case.setup(context)
    calls = 0

    def query(*args, **kwargs):
        nonlocal calls
        context.clock.sleep(0.5 if calls == 0 else 0.001)
        calls += 1
        return SimpleNamespace(query_status="OK", all_data_series=[SimpleNamespace(rows=[[1]])])

    context.client.dataLakeMeasureApi.get.side_effect = query
    context.phase = "measurement"
    case.run(context)
    samples = [s for s in context.samples if s["operation"] == "dataset.query.latest"]
    assert samples[0]["duration_ms"] == pytest.approx(500)
    assert samples[0]["phase"] == "measurement"
    assert case.results[0]["query_time_ms"]["max"] == pytest.approx(500)
    assert case.results[0]["query_time_ms"]["mean"] == pytest.approx(50.9)
    assert case.cleanup(context)


@pytest.mark.parametrize("users", [2, 10])
def test_dataset_workflows_keep_thread_affinity_and_cap_users(environment, monkeypatch, users):
    context, _, clients = environment
    monkeypatch.setattr("streampipes_performance.cases.streampipes.dataset_query.case.ITERATIONS", 2)
    case = DatasetQuery({"number_of_datasets": 3, "rows_per_dataset": 2, "parallel_operations": users})
    case.setup(context)
    active = min(users, 3)
    assert len(clients) == active
    barrier = threading.Barrier(active)
    records = []
    started = set()
    lock = threading.Lock()

    def query(client, dataset, name):
        thread = threading.get_ident()
        with lock:
            first = thread not in started
            started.add(thread)
            records.append((dataset, name, thread, id(client)))
        if first:
            barrier.wait(timeout=3)
        return 1

    monkeypatch.setattr(case, "query", query)
    case.run(context)
    assert len(started) == active
    for dataset in context.datasets:
        calls = [record for record in records if record[0] == dataset]
        assert [record[1] for record in calls] == list(QUERIES) * 2
        assert len({record[2:] for record in calls}) == 1
    assert all(result["requests"] == result["completed"] == 6 for result in case.results)
    assert all(result["concurrency"] == active for result in case.results)
    assert case.cleanup(context)


def test_query_rates_use_complete_workflow_duration(environment, monkeypatch):
    context, _, _ = environment
    monkeypatch.setattr("streampipes_performance.cases.streampipes.dataset_query.case.ITERATIONS", 2)
    case = DatasetQuery({"number_of_datasets": 2, "rows_per_dataset": 2})
    case.setup(context)

    def query(client, dataset, name):
        context.clock.sleep(0.005 if name == "aggregate" else 0.001)
        return 1

    monkeypatch.setattr(case, "query", query)
    case.run(context)
    for result in case.results:
        assert result["duration_scope"] == "asset_workflows"
        assert result["duration_seconds"] == pytest.approx(0.036)
        assert result["queries_per_second"] == pytest.approx(4 / 0.036)
        assert result["query_time_ms"]["mean"] == pytest.approx(5 if result["query"] == "aggregate" else 1)
    assert case.cleanup(context)


def test_saved_dataset_request_examples_match_options_without_extra_queries(environment):
    context, _, _ = environment
    case = DatasetQuery({"number_of_datasets": 1, "rows_per_dataset": 2})
    case.setup(context)
    case.run(context)
    count = context.client.dataLakeMeasureApi.get.call_count
    examples = case.prepare_results(context)["query_requests"]
    assert context.client.dataLakeMeasureApi.get.call_count == count
    assert [example["query"] for example in examples] == list(QUERIES)
    assert all("client.dataLakeMeasureApi.get(" in example["source"] for example in examples)
    assert all(repr(context.datasets[0]) in example["source"] for example in examples)
    assert "columns=['temperature', 'mass_flow']" in examples[1]["source"]
    assert "start_date=" in examples[2]["source"]
    assert "aggregation_function='MEAN'" in examples[4]["source"]
    assert case.cleanup(context)


def test_setup_prefers_direct_influx_upload(environment, monkeypatch):
    context, stored, _ = environment
    monkeypatch.setenv('INFLUX_TOKEN', 'test-token')
    direct = Mock()
    direct.upload.side_effect = lambda name, headers, batch: stored[name].extend(batch)
    factory = Mock(return_value=direct)
    monkeypatch.setattr('streampipes_performance.cases.common.InfluxUploader', factory)
    DatasetQuery({'number_of_datasets': 1, 'rows_per_dataset': 1001}).setup(context)
    assert factory.call_args.args[1] == ('sensorId',)
    assert [len(call.args[2]) for call in direct.upload.call_args_list] == [1000, 1]
    context.client.datasetApi.upload.assert_not_called()
    assert len(stored['test-query-dataset-0']) == 1001


@pytest.mark.parametrize("sensor_count", [1, 7, 1005, 3000])
@pytest.mark.parametrize("direct_upload", [False, True])
def test_sensor_cardinality_reaches_uploads_across_batches(environment, monkeypatch, sensor_count, direct_upload):
    from collections import Counter

    context, stored, _ = environment
    if direct_upload:
        monkeypatch.setenv("INFLUX_TOKEN", "test-token")
        uploader = Mock()
        uploader.upload.side_effect = lambda name, headers, batch: stored[name].extend(batch)
        monkeypatch.setattr("streampipes_performance.cases.common.InfluxUploader", Mock(return_value=uploader))
    case = DatasetQuery({"number_of_datasets": 2, "rows_per_dataset": 2003, "sensor_count": sensor_count})
    case.setup(context)
    sensor_index = HEADERS.index("sensorId")
    for events in stored.values():
        ids = [row[sensor_index] for row in events]
        assert ids == [f"flowrate{i % sensor_count:02d}" for i in range(2003)]
        assert len(set(ids)) == min(sensor_count, 2003)
        counts = Counter(ids)
        assert max(counts.values()) - min(counts.values()) <= 1
        assert counts["flowrate00"] == (2003 + sensor_count - 1) // sensor_count
    assert case.cleanup(context)


@pytest.mark.parametrize("sensor_count", [0, -1, 1.5, True, "10", None])
def test_sensor_count_must_be_a_positive_integer(sensor_count):
    with pytest.raises(ValueError, match="sensor_count"):
        DatasetQuery({"sensor_count": sensor_count})
