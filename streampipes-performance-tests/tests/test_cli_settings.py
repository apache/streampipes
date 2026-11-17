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

"""Offline selection, precedence, output paths and credential-safe runtime settings."""

import json
from pathlib import Path
from unittest.mock import Mock

import pytest

from streampipes_performance.cli import main
from streampipes_performance.config import Connection
from streampipes_performance.settings import ENVIRONMENT, SECRET_FIELDS, resolve_settings


@pytest.fixture(autouse=True)
def clean_environment(monkeypatch):
    for name in (*ENVIRONMENT.values(), "PERFORMANCE_SETTINGS"):
        monkeypatch.delenv(name, raising=False)
        monkeypatch.delenv(name + "_FILE", raising=False)


def config_file(tmp_path):
    path = tmp_path / "run.json"
    path.write_text(json.dumps({"target": "streampipes", "output_directory": "saved", "cases": [
        {"id": "dataset", "type": "streampipes.dataset_query", "parameters": {"sensor_count": [10, 100]}},
        {"id": "adapter", "type": "streampipes.adapter_start_stop"},
    ]}))
    return path


def test_settings_precedence_and_legacy_env_selectors(tmp_path):
    path = tmp_path / "settings.json"
    path.write_text(json.dumps({"url": "http://file", "username": "file-user", "password": "file-secret",
                               "output_directory": "file-results", "smtp_host": "file-smtp"}))
    connection = Connection(url="http://config", username="config-user", password="config-secret",
                            url_env="CUSTOM_URL", password_env="CUSTOM_PASSWORD")
    args = dict(connection=connection, config_path=tmp_path / "run.json", output_directory="run-results",
                settings_path=path, environ={"CUSTOM_URL": "http://env", "CUSTOM_PASSWORD": "env-secret",
                                             "PERFORMANCE_SMTP_HOST": "env-smtp"})
    settings = resolve_settings(**args)
    assert settings.url == "http://env" and settings.username == "file-user"
    assert settings.password.get_secret_value() == "env-secret"
    assert settings.output_directory == str(tmp_path / "file-results")
    assert settings.smtp_host == "env-smtp"
    settings = resolve_settings(**args, overrides={"url": "http://cli", "password": "cli-secret",
                                                   "output_directory": str(tmp_path / "cli-results")})
    assert settings.url == "http://cli" and settings.password.get_secret_value() == "cli-secret"
    assert settings.output_directory == str(tmp_path / "cli-results")
    fallback = resolve_settings(connection=connection, config_path=tmp_path / "run.json",
                                output_directory="run-results", environ={})
    assert fallback.url == "http://config" and fallback.password.get_secret_value() == "config-secret"
    assert fallback.output_directory == str(tmp_path / "run-results")


@pytest.mark.parametrize("field", SECRET_FIELDS)
def test_secret_file_overrides_and_relative_paths(tmp_path, field):
    (tmp_path / "secret").write_text("file-secret\n")
    path = tmp_path / "settings.json"
    path.write_text(json.dumps({field + "_file": "secret"}))
    settings = resolve_settings(settings_path=path, environ={})
    assert getattr(settings, field).get_secret_value() == "file-secret"
    assert "file-secret" not in json.dumps(settings.redacted())
    path.write_text(json.dumps({field + "_file": "missing"}))
    settings = resolve_settings(settings_path=path, overrides={field: "cli-secret"}, environ={})
    assert getattr(settings, field).get_secret_value() == "cli-secret"


def test_email_environment_and_cli_precedence():
    settings = resolve_settings(environ={
        "PERFORMANCE_EMAIL_TO": "one@example.org, two@example.org", "PERFORMANCE_EMAIL_FROM": "sender@example.org",
        "PERFORMANCE_SMTP_HOST": "mail", "PERFORMANCE_SMTP_PORT": "465", "PERFORMANCE_SMTP_SECURITY": "ssl",
        "PERFORMANCE_SMTP_USERNAME": "user", "PERFORMANCE_SMTP_PASSWORD": "secret",
    }, overrides={"smtp_port": "587", "smtp_security": "starttls"})
    settings.require_email()
    assert settings.email_to == ["one@example.org", "two@example.org"]
    assert settings.smtp_port == 587 and settings.smtp_security == "starttls"
    assert "secret" not in json.dumps(settings.redacted())


def test_preview_selects_and_expands_overrides_without_network(tmp_path, monkeypatch, capsys):
    path = config_file(tmp_path)
    wait = Mock(side_effect=AssertionError("network must not be used"))
    monkeypatch.setattr("streampipes_performance.readiness.wait_ready", wait)
    assert main(["preview", str(path), "--case", "dataset", "--set", "dataset.sensor_count=[5,50,500]",
                 "--set", "dataset.number_of_datasets=4", "--json"]) == 0
    plan = json.loads(capsys.readouterr().out)
    assert plan["scenario_count"] == 3
    assert plan["required_services"] == ["backend"]
    assert plan["settings"]["output_directory"] == str(tmp_path / "saved")
    assert {s["parameters"]["sensor_count"] for s in plan["scenarios"]} == {5, 50, 500}
    assert all(s["parameters"]["number_of_datasets"] == 4 for s in plan["scenarios"])
    wait.assert_not_called()


@pytest.mark.parametrize("arguments", [
    ["--case", "missing"], ["--case", "dataset", "--set", "adapter.cycles=3"],
    ["--set", "dataset.sensor_count=0"], ["--set", "dataset.unknown=1"], ["--set", "dataset.sensor_count=no"],
])
def test_invalid_selection_fails_before_readiness(tmp_path, monkeypatch, arguments):
    wait = Mock()
    monkeypatch.setattr("streampipes_performance.readiness.wait_ready", wait)
    assert main(["run", str(config_file(tmp_path)), *arguments]) == 2
    wait.assert_not_called()


def test_discovery_and_contextual_help_are_offline(tmp_path, capsys):
    assert main(["tests", "show", "dataset-query"]) == 0
    assert "sensor_count" in capsys.readouterr().out
    assert main(["configs", "list"]) == 0
    assert "medium" in capsys.readouterr().out
    assert main(["run", str(config_file(tmp_path)), "--case", "dataset", "--help"]) == 0
    output = capsys.readouterr().out
    assert "--set" in output and "sensor_count" in output and "Queries:" in output
    assert "adapter (" not in output


def test_cli_output_overrides_config_and_preview_never_prints_secrets(tmp_path, capsys, monkeypatch):
    monkeypatch.setenv("PERFORMANCE_EMAIL_TO", "recipient@example.org")
    assert main(["preview", str(config_file(tmp_path)), "--output-directory", str(tmp_path / "cli"),
                 "--password", "hidden", "--smtp-password", "hidden-mail", "--influx-token", "hidden-token",
                 "--no-email", "--json"]) == 0
    output = capsys.readouterr().out
    assert "hidden" not in output
    settings = json.loads(output)["settings"]
    assert settings["output_directory"] == str(tmp_path / "cli") and settings["email_to"] == []


def test_explicit_settings_reach_influx_without_mutating_environment(tmp_path, monkeypatch):
    from types import SimpleNamespace
    from streampipes_performance.cases.common import upload_rows
    from streampipes_performance.cases.streampipes.dataset_query.case import HEADERS, rows

    settings = resolve_settings(environ={}, overrides={"influx_url": "http://custom", "influx_token": "custom-secret"})
    factory = Mock()
    monkeypatch.setattr("streampipes_performance.cases.common.InfluxUploader", factory)
    context = SimpleNamespace(runtime_settings=settings, log=Mock(), call=lambda operation, fn, *a: fn(*a))
    upload_rows(context, "data", HEADERS, 1, 1000, rows, tag_fields=("sensorId",))
    config = factory.call_args.args[0]
    assert config.url == "http://custom" and config.token.get_secret_value() == "custom-secret"


def test_explicit_settings_reach_latency_case():
    from streampipes_performance.cases.streampipes.adapter_latency.case import AdapterLatency
    settings = resolve_settings(environ={}, overrides={"nats_url": "nats://input:4223",
        "output_nats_url": "nats://output:4224", "adapter_nats_url": "nats://adapter:4225"})
    adapter = AdapterLatency({}, runtime_settings=settings)
    assert adapter.latency.brokers.nats_url == settings.nats_url
    assert adapter.latency.brokers.output_nats_url == settings.output_nats_url
    assert adapter.adapter_nats_url == settings.adapter_nats_url


def test_runtime_settings_are_saved_without_any_secrets(tmp_path, monkeypatch):
    from streampipes_performance.cases import REGISTRY
    from streampipes_performance.cases.base import Case
    from streampipes_performance.config import RunConfig, expand
    from streampipes_performance.runner import execute_run_config
    from test_framework import client, Clock

    runtime = resolve_settings(environ={}, overrides={"password": "backend-secret", "smtp_password": "smtp-secret",
                                                    "influx_token": "token-secret", "url": "http://backend"})
    class Example(Case):
        def setup(self, context):
            assert context.runtime_settings is self.runtime_settings is runtime
        def run(self, context):
            pass
    monkeypatch.setitem(REGISTRY, "example", Example)
    config = RunConfig(target="streampipes", cases=[{"id": "test", "type": "example"}])
    directory, passed = execute_run_config(config, expand(config), client(), tmp_path, Clock(), runtime_settings=runtime)
    assert passed
    manifest = json.loads((directory / "manifest.json").read_text())
    assert manifest["settings"]["url"] == "http://backend"
    for path in directory.iterdir():
        if path.is_file():
            assert "backend-secret" not in path.read_text()
            assert "smtp-secret" not in path.read_text()
            assert "token-secret" not in path.read_text()


def test_plan_matches_exact_case_ids(tmp_path, capsys):
    path = tmp_path / "overlap.json"
    path.write_text(json.dumps({"target": "streampipes", "cases": [
        {"id": "data", "type": "streampipes.dataset_query", "parameters": {"sensor_count": 3}},
        {"id": "data-m2", "type": "streampipes.dataset_query", "parameters": {"sensor_count": 7}},
    ]}))
    assert main(["preview", str(path), "--json"]) == 0
    cases = json.loads(capsys.readouterr().out)["cases"]
    assert [case["scenarios"] for case in cases] == [1, 1]
    assert [case["parameters"]["sensor_count"] for case in cases] == [[3], [7]]


@pytest.mark.parametrize("command", ["preview", "validate"])
def test_offline_commands_do_not_read_credential_files(tmp_path, command):
    assert main([command, str(config_file(tmp_path)), "--password-file", str(tmp_path / "missing-backend"),
                 "--smtp-password-file", str(tmp_path / "missing-smtp")]) == 0
