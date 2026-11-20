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

"""SMTP delivery attaches only HTML and cannot replace benchmark results."""

import json
import smtplib
from contextlib import contextmanager
from unittest.mock import MagicMock, Mock

import pytest

from streampipes_performance.config import RunConfig, expand
from streampipes_performance.notification import send_report
from streampipes_performance.settings import RuntimeSettings


def settings(**overrides):
    return RuntimeSettings(url="http://backend", username="user", password="backend-secret", wait_ready=False,
                           email_to=["recipient@example.org"], email_from="sender@example.org", smtp_host="mail",
                           smtp_username="mail-user", smtp_password="mail-secret", **overrides)


def result_directory(tmp_path, status="passed"):
    (tmp_path / "report.html").write_text("<!doctype html><title>Report</title>")
    (tmp_path / "manifest.json").write_text(json.dumps({"run_id": "run-123", "status": status, "results": []}))
    (tmp_path / "summary.csv").write_text("DO NOT SEND")
    (tmp_path / "scenario.json").write_text('{"private": true}')
    return tmp_path


@pytest.mark.parametrize("security", ["starttls", "ssl", "none"])
def test_send_attaches_only_report_and_records_delivery(tmp_path, monkeypatch, security):
    directory = result_directory(tmp_path)
    transport = MagicMock()
    smtp = transport.return_value.__enter__.return_value
    smtp.send_message.return_value = {}
    monkeypatch.setattr("streampipes_performance.notification.smtplib.SMTP", transport)
    monkeypatch.setattr("streampipes_performance.notification.smtplib.SMTP_SSL", transport)
    assert send_report(directory, settings(smtp_security=security))
    message = smtp.send_message.call_args.args[0]
    attachments = list(message.iter_attachments())
    assert len(attachments) == 1 and attachments[0].get_filename() == "report.html"
    assert attachments[0].get_content_type() == "text/html"
    assert attachments[0].get_payload(decode=True) == (directory / "report.html").read_bytes()
    assert "run-123" in message["Subject"] and "passed" in message["Subject"]
    assert "DO NOT SEND" not in message.as_string()
    assert "mail-secret" not in message.as_string() and "backend-secret" not in message.as_string()
    assert smtp.starttls.call_count == (security == "starttls")
    smtp.login.assert_called_once_with("mail-user", "mail-secret")
    record = json.loads((directory / "email-delivery.json").read_text())
    assert record["status"] == "sent"
    assert "secret" not in json.dumps(record)


@pytest.mark.parametrize("refused", [False, True])
def test_failure_preserves_report_and_benchmark_status(tmp_path, monkeypatch, refused):
    directory = result_directory(tmp_path)
    transport = MagicMock()
    smtp = transport.return_value.__enter__.return_value
    if refused:
        smtp.send_message.return_value = {"recipient@example.org": (550, b"hidden-secret")}
    else:
        smtp.send_message.side_effect = smtplib.SMTPException("hidden-secret")
    monkeypatch.setattr("streampipes_performance.notification.smtplib.SMTP", transport)
    before = (directory / "report.html").read_bytes()
    assert not send_report(directory, settings())
    assert (directory / "report.html").read_bytes() == before
    assert json.loads((directory / "manifest.json").read_text())["status"] == "passed"
    record = (directory / "email-delivery.json").read_text()
    assert '"failed"' in record and "hidden-secret" not in record


@pytest.mark.parametrize("status,delivered,expected", [
    ("passed", True, 0), ("passed", False, 3), ("failed", True, 1), ("failed", False, 1),
    ("interrupted", True, 130), ("interrupted", False, 130),
])
def test_delivery_occurs_after_cleanup_and_key_revocation(tmp_path, monkeypatch, status, delivered, expected):
    from streampipes_performance.cli import execute

    directory = result_directory(tmp_path, status)
    events = []
    @contextmanager
    def key(*args):
        events.append("key-created")
        try:
            yield "key"
        finally:
            events.append("key-revoked")
    monkeypatch.setattr("streampipes_performance.cli.temporary_api_key", key)
    client = Mock()
    client.request_session.close.side_effect = lambda: events.append("session-closed")
    monkeypatch.setattr("streampipes_performance.clients.streampipes.StreamPipesClient", Mock(return_value=client))
    def run(*args, **kwargs):
        events.append("cleanup-complete")
        assert kwargs["runtime_settings"].password.get_secret_value() == "backend-secret"
        return directory, status == "passed"
    monkeypatch.setattr("streampipes_performance.runner.execute_run_config", run)
    def email(*args):
        assert events == ["key-created", "cleanup-complete", "session-closed", "key-revoked"]
        events.append("email")
        return delivered
    monkeypatch.setattr("streampipes_performance.notification.send_report", email)
    config = RunConfig(target="streampipes", cases=[{"id": "test", "type": "streampipes.dataset_query"}])
    assert execute(config, expand(config), settings()) == expected
    assert events[-1] == "email"


def test_cli_can_send_existing_html_using_environment(tmp_path, monkeypatch):
    from streampipes_performance.cli import main
    directory = result_directory(tmp_path)
    for key, value in {"PERFORMANCE_EMAIL_TO": "recipient@example.org", "PERFORMANCE_EMAIL_FROM": "sender@example.org",
                       "PERFORMANCE_SMTP_HOST": "mail"}.items():
        monkeypatch.setenv(key, value)
    sender = Mock(return_value=True)
    monkeypatch.setattr("streampipes_performance.notification.send_report", sender)
    assert main(["report", "send", str(directory)]) == 0
    assert sender.call_args.args[1].email_to == ["recipient@example.org"]


def test_no_mail_without_opt_in_and_invalid_email_settings_fail_before_run(monkeypatch):
    from streampipes_performance.cli import execute
    config = RunConfig(target="streampipes", cases=[{"id": "test", "type": "streampipes.dataset_query"}])
    invalid = settings().model_copy(update={"smtp_host": None})
    runner = Mock()
    monkeypatch.setattr("streampipes_performance.runner.execute_run_config", runner)
    with pytest.raises(ValueError, match="smtp_host"):
        execute(config, expand(config), invalid)
    runner.assert_not_called()


def test_smtp_is_never_called_without_recipients(tmp_path, monkeypatch):
    from streampipes_performance.cli import execute
    directory = result_directory(tmp_path)
    @contextmanager
    def key(*args):
        yield "key"
    monkeypatch.setattr("streampipes_performance.cli.temporary_api_key", key)
    monkeypatch.setattr("streampipes_performance.clients.streampipes.StreamPipesClient", Mock(return_value=Mock()))
    monkeypatch.setattr("streampipes_performance.runner.execute_run_config", Mock(return_value=(directory, True)))
    sender = Mock()
    monkeypatch.setattr("streampipes_performance.notification.send_report", sender)
    config = RunConfig(target="streampipes", cases=[{"id": "test", "type": "streampipes.dataset_query"}])
    assert execute(config, expand(config), settings().model_copy(update={"email_to": []})) == 0
    sender.assert_not_called()


def test_revocation_failure_updates_report_before_delivery(tmp_path, monkeypatch):
    from streampipes_performance.cli import execute
    directory = result_directory(tmp_path)
    events = []
    @contextmanager
    def key(*args):
        try:
            yield "key"
        finally:
            events.append("revocation-failed")
            raise RuntimeError("revocation failed")
    monkeypatch.setattr("streampipes_performance.cli.temporary_api_key", key)
    monkeypatch.setattr("streampipes_performance.clients.streampipes.StreamPipesClient", Mock(return_value=Mock()))
    monkeypatch.setattr("streampipes_performance.runner.execute_run_config", Mock(return_value=(directory, True)))
    def report(path):
        manifest = json.loads((path / "manifest.json").read_text())
        assert manifest["status"] == "failed" and manifest["finalization_failed"]
        events.append("report-updated")
    def email(*args):
        assert events == ["revocation-failed", "report-updated"]
        return True
    monkeypatch.setattr("streampipes_performance.report.run_report.write_report", report)
    monkeypatch.setattr("streampipes_performance.notification.send_report", email)
    config = RunConfig(target="streampipes", cases=[{"id": "test", "type": "streampipes.dataset_query"}])
    assert execute(config, expand(config), settings()) == 2


def test_report_send_does_not_require_backend_secret_file(tmp_path, monkeypatch):
    from streampipes_performance.cli import main
    result_directory(tmp_path)
    sender = Mock(return_value=True)
    monkeypatch.setattr("streampipes_performance.notification.send_report", sender)
    assert main(["report", "send", str(tmp_path), "--to", "recipient@example.org",
                 "--smtp-host", "mail", "--email-from", "sender@example.org",
                 "--password-file", str(tmp_path / "missing-backend")]) == 0
    assert sender.call_args.args[1].password is None
