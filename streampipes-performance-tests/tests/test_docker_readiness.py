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

"""The Docker runner must gate benchmarks on readiness and preserve CLI status."""

from types import SimpleNamespace
from unittest.mock import MagicMock

import pytest


from streampipes_performance import readiness as runner
from streampipes_performance.settings import resolve_settings


def scenarios(kind="streampipes.adapter_latency"):
    return [SimpleNamespace(type=kind)]

@pytest.fixture
def environment(monkeypatch):
    for name, value in {
        "STREAMPIPES_URL": "http://backend:8030",
        "STREAMPIPES_USERNAME": "admin@example.org",
        "STREAMPIPES_PASSWORD": "secret",
        "EXTENSIONS_URL": "http://extensions:8044",
        "INFLUXDB_METRICS_URL": "http://influxdb:8086/metrics",
        "NATS_URL": "nats://nats:4222",
    }.items():
        monkeypatch.setenv(name, value)


def response(data):
    result = MagicMock()
    result.json.return_value = data
    return result


def test_unconfigured_backend_prevents_login(environment):
    session = MagicMock()
    session.get.return_value = response({"configured": False})
    with pytest.raises(runner.NotReady, match="backend setup"):
        runner.check_ready(session, lambda: 5, resolve_settings(),
                           runner.required_services(scenarios() + scenarios("streampipes.dataset_query"), resolve_settings()))
    session.post.assert_not_called()


@pytest.mark.parametrize("metrics_setting", ["enabled", "empty", "unset"])
def test_ready_checks_extensions_metrics_and_broker(environment, monkeypatch, metrics_setting):
    if metrics_setting == "empty":
        monkeypatch.setenv("INFLUXDB_METRICS_URL", "")
    elif metrics_setting == "unset":
        monkeypatch.delenv("INFLUXDB_METRICS_URL")
    session = MagicMock()
    session.get.side_effect = [
        response({"configured": True}),
        response({"adapterInstanceStates": {}, "runningPipelineElementInstanceIds": []}),
        response(None),
    ]
    session.post.return_value = response({"accessToken": "jwt"})
    connect = MagicMock()
    connect.return_value.__enter__.return_value.makefile.return_value.__enter__.return_value.readline.return_value = (
        b'INFO {"server_id":"test"}\r\n'
    )
    monkeypatch.setattr(runner.socket, "create_connection", connect)
    runner.check_ready(session, lambda: 5, resolve_settings(),
                           runner.required_services(scenarios() + scenarios("streampipes.dataset_query"), resolve_settings()))
    assert session.get.call_args_list[1].kwargs["headers"] == {"Authorization": "Bearer jwt"}
    if metrics_setting == "enabled":
        assert session.get.call_args_list[2].args == ("http://influxdb:8086/metrics",)
    else:
        assert session.get.call_count == 2
    connect.assert_called_once_with(("nats", 4222), timeout=5)


def test_readiness_deadline_prevents_run(environment, monkeypatch):
    now = [0.0]
    monkeypatch.setattr(runner.time, "monotonic", lambda: now[0])
    monkeypatch.setattr(runner.time, "sleep", lambda seconds: now.__setitem__(0, now[0] + seconds))
    check = MagicMock(side_effect=runner.NotReady("IIoT extensions"))
    monkeypatch.setattr(runner, "check_ready", check)
    with pytest.raises(TimeoutError, match="IIoT extensions"):
        runner.wait_ready(resolve_settings(overrides={"ready_timeout": 7}), scenarios())
    assert now[0] == 7


def test_invalid_config_exits_without_waiting(tmp_path, monkeypatch):
    from streampipes_performance.cli import main
    path = tmp_path / "bad.json"
    path.write_text('{}')
    wait = MagicMock()
    monkeypatch.setattr(runner, "wait_ready", wait)
    assert main(["run", str(path)]) == 2
    wait.assert_not_called()


def test_dataset_readiness_does_not_check_extensions_or_nats(environment, monkeypatch):
    settings = resolve_settings()
    session = MagicMock()
    session.get.side_effect = [response({"configured": True}), response(None)]
    session.post.return_value = response({"accessToken": "jwt"})
    connect = MagicMock()
    monkeypatch.setattr(runner.socket, "create_connection", connect)
    services = runner.required_services(scenarios("streampipes.dataset_query"), settings)
    assert services == {"backend", "influxdb_metrics"}
    runner.check_ready(session, lambda: 5, settings, services)
    connect.assert_not_called()
    assert session.get.call_count == 2
