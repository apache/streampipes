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

"""Progress logging behavior without contacting a deployment."""

import logging
from unittest.mock import Mock

import pytest
from requests import Timeout
from test_cases import Server
from test_framework import Clock, client, run_config

from streampipes_performance.config import Timing, expand
from streampipes_performance.logging import console_logging
from streampipes_performance.runner import execute_run_config
from streampipes_performance.runtime import Context


@pytest.mark.parametrize("log_level", ["INFO", "ERROR"])
def test_cli_run_logs_exception_details(tmp_path, monkeypatch, capsys, log_level):
    from streampipes_performance.cli import main

    config = tmp_path / "run_config.json"
    monkeypatch.setattr("sys.argv", ["sp-performance", "run", str(config), "--log-level", log_level])
    monkeypatch.setattr("streampipes_performance.cli.load", Mock(side_effect=ValueError("Unknown case type: example")))

    assert main() == 2
    output = capsys.readouterr()
    assert not output.out
    assert "Could not run: ValueError: Unknown case type: example" in output.err
    assert "Traceback (most recent call last)" not in output.err


def test_cli_run_logs_validation_fields(tmp_path, monkeypatch, capsys):
    from streampipes_performance.cli import main

    config = tmp_path / "run_config.json"
    config.write_text('{"target":"invalid","cases":[]}')
    monkeypatch.setattr("sys.argv", ["sp-performance", "run", str(config)])

    assert main() == 2
    output = capsys.readouterr()
    assert not output.out
    assert "ValidationError" in output.err
    assert "target" in output.err
    assert "Unknown target 'invalid'; available: " in output.err
    assert "Traceback (most recent call last)" not in output.err


def test_console_logging_verbosity_stderr_and_restoration(capsys):
    logger = logging.getLogger("streampipes_performance")
    http_logger = logging.getLogger("streampipes.endpoint")
    previous = logger.level, logger.propagate, list(logger.handlers)
    with console_logging():
        logger.info("Scenario started")
        logger.debug("Read suppressed")
        http_logger.info("HTTP chatter suppressed")
    output = capsys.readouterr()
    assert "INFO Scenario started" in output.err
    assert "suppressed" not in output.err
    assert not output.out
    assert (logger.level, logger.propagate, logger.handlers) == previous
    with console_logging("DEBUG"):
        logger.debug("Query details")
    assert "DEBUG Query details" in capsys.readouterr().err


def test_api_timing_logs_exclude_payloads_and_exception_text(caplog):
    caplog.set_level(logging.DEBUG, logger="streampipes_performance")
    clock = Clock()
    context = Context(client(), Timing(), "perf", clock=clock, scenario_id="case-1")

    def create(payload):
        clock.sleep(0.25)
        return {"success": True}

    context.call("ai.create", create, {"code": "SECRET_WORKLOAD_CODE"})
    assert "[case-1] ai.create completed in 250.0 ms" in caplog.text
    assert context.samples[0]["duration_ms"] == 250
    with pytest.raises(Timeout):
        context.call("dataset.query", Mock(side_effect=Timeout("SECRET_API_KEY")), "dataset-1")
    assert "dataset.query (dataset-1) failed" in caplog.text
    assert "Timeout" in caplog.text
    assert "SECRET" not in caplog.text


@pytest.mark.parametrize("strategy", ["delete", "reset"])
def test_run_logs_progress_metrics_cleanup_and_result_paths(tmp_path, caplog, strategy):
    caplog.set_level(logging.INFO, logger="streampipes_performance")
    server = Server()
    if strategy == "reset":

        def reset(*args, **kwargs):
            server.adapters.clear()
            server.pipelines.clear()
            server.datasets.clear()
            return {"success": True}

        server.request_json = reset
    config = run_config(execution={'cleanup': {"strategy": strategy}, 'timing': {
            "warmup_seconds": 0,
            "duration_seconds": 2,
            "stable_seconds": 1,
            "drain_timeout_seconds": 3,
        }})
    directory, passed = execute_run_config(config, expand(config), server, tmp_path, Clock())
    assert passed
    for text in [
        "Starting performance run",
        "Checking that the instance is empty",
        "Running case 1/1",
        "resources=1, interval=1000 ms",
        "Starting setup",
        "adapter.create",
        "Warming up",
        "Measuring for 2.0 s",
        "Querying measurement",
        "Collection complete",
        "events=2/2.0",
        f"Cleanup using {strategy}",
        "cleanup completed in",
        "Case passed",
        "Wrote JSON result",
        "Wrote CSV summary",
        str(directory),
    ]:
        assert text in caplog.text
    assert caplog.text.count("Querying measurement") == 1
    assert "dataset.query" not in caplog.text  # Polling details are DEBUG only.
    if strategy == "reset":
        assert "Resetting instance" in caplog.text
        assert "instance.reset completed" in caplog.text
