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

"""Per-run authentication lifecycle and failure handling."""

import logging
from unittest.mock import MagicMock, Mock

import pytest
import requests

from streampipes_performance.auth import temporary_api_key
from streampipes_performance.config import Connection


@pytest.fixture
def session(monkeypatch):
    session = MagicMock()
    session.__enter__.return_value = session
    monkeypatch.setattr("streampipes_performance.auth.requests.Session", Mock(return_value=session))
    return session


def responses(session, *data):
    session.request.side_effect = [Mock(json=Mock(return_value=item)) for item in data]


def lifecycle(session, token=None, result=None):
    responses(
        session,
        {"accessToken": "initial-jwt"},
        token if token is not None else {"rawToken": "api-secret", "tokenId": "run-token"},
        {"accessToken": "fresh-jwt"},
        {
            "principalId": "user/id", "fullName": "Keep this name", "roles": ["ROLE_ADMIN"],
            "userApiTokens": [{"tokenId": "existing"}, {"tokenId": "run-token"}, {"tokenId": "other-run"}],
        },
        result if result is not None else {"success": True},
    )


@pytest.mark.parametrize("failure", [None, RuntimeError, KeyboardInterrupt])
def test_key_revoked_on_success_failure_and_interruption(session, failure, caplog):
    lifecycle(session)
    with caplog.at_level(logging.INFO):
        if failure:
            with pytest.raises(failure):
                with temporary_api_key("https://perf.example:8443/", "a+b@example.org", "password-secret", 12) as key:
                    assert key == "api-secret"
                    raise failure("run interrupted")
        else:
            with temporary_api_key("https://perf.example:8443/", "a+b@example.org", "password-secret", 12) as key:
                assert key == "api-secret"
                assert session.request.call_count == 2
    calls = session.request.call_args_list
    assert calls[0].args == ("POST", "https://perf.example:8443/streampipes-backend/api/v2/auth/login")
    assert calls[0].kwargs["json"] == {"username": "a+b@example.org", "password": "password-secret"}
    assert calls[1].args[1].endswith("/users/a%2Bb%40example.org/tokens")
    assert calls[1].kwargs["json"]["tokenName"].startswith("performance-tests-")
    assert calls[1].kwargs["headers"] == {"Authorization": "Bearer initial-jwt"}
    assert calls[2].args == calls[0].args
    assert calls[3].kwargs["headers"] == {"Authorization": "Bearer fresh-jwt"}
    assert calls[4].args == ("PUT", "https://perf.example:8443/streampipes-backend/api/v2/users/user/user%2Fid")
    assert calls[4].kwargs["json"] == {
        "principalId": "user/id", "fullName": "Keep this name", "roles": ["ROLE_ADMIN"],
        "userApiTokens": [{"tokenId": "existing"}, {"tokenId": "other-run"}],
    }
    assert all(call.kwargs["timeout"] == 12 for call in calls)
    assert "api-secret" not in caplog.text
    assert "password-secret" not in caplog.text
    session.__exit__.assert_called_once()


def test_each_run_gets_a_unique_key_name(session):
    names = []
    for _ in range(2):
        lifecycle(session)
        with temporary_api_key("http://localhost", "user", "password", 30):
            names.append(session.request.call_args.kwargs["json"]["tokenName"])
    assert names[0] != names[1]


@pytest.mark.parametrize("failure_step", [0, 1, 2, 3, 4])
def test_authentication_errors_hide_secrets_and_close_session(session, failure_step, caplog):
    lifecycle(session)
    replies = list(session.request.side_effect)
    replies[failure_step] = requests.ConnectionError("password-secret api-secret")
    session.request.side_effect = replies
    with pytest.raises(RuntimeError, match="StreamPipes") as error:
        with temporary_api_key("http://localhost", "user", "password-secret", 30):
            pass
    assert "secret" not in str(error.value)
    assert error.value.__suppress_context__
    assert "secret" not in caplog.text
    session.__exit__.assert_called_once()


def test_revocation_failure_is_not_silently_accepted(session):
    lifecycle(session, result={"success": False, "notifications": ["api-secret"]})
    with pytest.raises(RuntimeError, match="API key revocation failed"):
        with temporary_api_key("http://localhost", "user", "password", 30):
            pass


def test_malformed_key_response_still_revokes_known_token(session):
    lifecycle(session, token={"tokenId": "run-token"})
    with pytest.raises(RuntimeError, match="did not return an API key"):
        with temporary_api_key("http://localhost", "user", "password", 30):
            pytest.fail("Run must not start without a key")
    assert session.request.call_count == 5


@pytest.mark.parametrize("data", [{}, {"success": False}, {"accessToken": ""}])
def test_invalid_login_never_creates_a_key(session, data):
    responses(session, data)
    with pytest.raises(RuntimeError):
        with temporary_api_key("http://localhost", "user", "password", 30):
            pytest.fail("Run must not start without login")
    assert session.request.call_count == 1


@pytest.mark.parametrize("failure_point", ["client", "run", "interrupt"])
def test_cli_revokes_key_when_client_or_run_fails(session, monkeypatch, failure_point):
    import signal
    from types import SimpleNamespace

    from streampipes_performance.cli import main
    from streampipes_performance.config import Execution

    lifecycle(session)
    config = SimpleNamespace(connection=Connection(password_env="CUSTOM_PASSWORD"), execution=Execution(),
                             output_directory="results", cases=[], target="streampipes")
    monkeypatch.setattr("streampipes_performance.cli.load", Mock(return_value=(config, [])))
    monkeypatch.setattr("sys.argv", ["sp-performance", "run", "config.json", "--no-wait-ready"])
    monkeypatch.setenv("STREAMPIPES_URL", "http://localhost:8082")
    monkeypatch.setenv("STREAMPIPES_USERNAME", "user")
    monkeypatch.setenv("CUSTOM_PASSWORD", "password-secret")
    client = Mock()
    factory = Mock(return_value=client)
    if failure_point == "client":
        factory.side_effect = RuntimeError("client failed")
    monkeypatch.setattr("streampipes_performance.clients.streampipes.StreamPipesClient", factory)
    error = KeyboardInterrupt() if failure_point == "interrupt" else RuntimeError("run failed")
    monkeypatch.setattr("streampipes_performance.runner.execute_run_config", Mock(side_effect=error))
    original_handler = signal.getsignal(signal.SIGTERM)
    if failure_point == "interrupt":
        assert main() == 130
    else:
        assert main() == 2
    assert session.request.call_count == 5
    assert signal.getsignal(signal.SIGTERM) == original_handler
    if failure_point != "client":
        client.request_session.close.assert_called_once()


def test_cli_returns_failure_when_revocation_fails(session, monkeypatch, caplog):
    from types import SimpleNamespace

    from streampipes_performance.cli import main
    from streampipes_performance.config import Execution

    lifecycle(session, result={"success": False})
    config = SimpleNamespace(connection=Connection(), execution=Execution(), output_directory="results", cases=[], target="streampipes")
    monkeypatch.setattr("streampipes_performance.cli.load", Mock(return_value=(config, [])))
    monkeypatch.setattr("sys.argv", ["sp-performance", "run", "config.json", "--no-wait-ready"])
    monkeypatch.setenv("STREAMPIPES_URL", "http://localhost")
    monkeypatch.setenv("STREAMPIPES_USERNAME", "user")
    monkeypatch.setenv("STREAMPIPES_PASSWORD", "password-secret")
    monkeypatch.setattr("streampipes_performance.clients.streampipes.StreamPipesClient", Mock(return_value=Mock()))
    monkeypatch.setattr("streampipes_performance.runner.execute_run_config", Mock(return_value=("results", True)))
    assert main() == 2
