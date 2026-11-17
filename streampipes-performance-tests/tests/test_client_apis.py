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

"""Contracts for metadata, lifecycle results and bounded requests."""

from unittest.mock import Mock, patch

import pytest
from requests import HTTPError, Response
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.model.compact import CompactAdapter, CompactPipeline

from streampipes_performance.clients.streampipes import StreamPipesClient, StreamPipesClientConfig


@pytest.fixture
def client():
    with patch("streampipes.client.client.StreamPipesClient._get_server_version", return_value="1.0"):
        return StreamPipesClient(
            StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
                request_timeout=2,
            )
        )


def test_dataset_metadata_routes(client):
    client.request_json = Mock(return_value={"resources": [{"elementId": "d"}]})
    assert client.datasetApi.all() == [{"elementId": "d"}]
    client.request_json.assert_called_with("GET", "api/v4/datalake/measure/summary")
    client.datasetApi.get_by_name("a/b")
    client.request_json.assert_called_with("GET", "api/v4/datalake/measure/byName/a%2Fb")
    client.datasetApi.delete("a/b")
    client.request_json.assert_called_with("DELETE", "api/v4/datalake/measure/a%2Fb")


@pytest.mark.parametrize(
    "kind,verb,path",
    [
        ("adapter", "POST", "api/v2/connect/master/adapters"),
        ("pipeline", "GET", "api/v2/pipelines"),
    ],
)
def test_lifecycle_result_routes(client, kind, verb, path):
    client.request_json = Mock(return_value={"success": False})
    api = getattr(client, kind + "Api")
    assert api.start_result("a/b") == {"success": False}
    client.request_json.assert_called_with(verb, path + "/a%2Fb/start")
    api.stop_result("a/b", force_stop=True)
    client.request_json.assert_called_with(verb, path + "/a%2Fb/stop", params={"forceStop": "true"})
    api.delete_result("a/b")
    client.request_json.assert_called_with("DELETE", path + "/a%2Fb")


def test_compact_results_and_details(client):
    client.request_json = Mock(return_value={"success": True})
    client.adapterApi.create_result(CompactAdapter(name="test", app_id="simulator"))
    assert client.request_json.call_args.args == ("POST", "api/v2/connect/compact-adapters")
    client.pipelineApi.create_result(CompactPipeline(name="test", pipeline_elements=[]))
    assert client.request_json.call_args.args == ("POST", "api/v2/compact-pipelines")
    client.pipelineApi.get_details("a/b")
    client.request_json.assert_called_with("GET", "api/v2/pipelines/a%2Fb")


def test_request_timeout_and_empty_response(client):
    response = Response()
    response.status_code = 204
    response._content = b""
    with patch.object(client.request_session, "send", return_value=response) as send:
        assert client.request_json("DELETE", "api/resource") is None
        assert send.call_args.kwargs["timeout"] == 2
        client.request_json("GET", "api/resource", timeout=5)
        assert send.call_args.kwargs["timeout"] == 5
        response.status_code = 200
        response._content = b'{"success": true}'
        assert client.request_json("POST", "api/resource") == {"success": True}
        response.status_code = 404
        with pytest.raises(HTTPError):
            client.request_json("GET", "api/resource")
    with pytest.raises(ValueError):
        client.request_json("GET", "https://another-host/")
    with pytest.raises(ValueError):
        client.request_json("GET", "api/../outside")


def test_invalid_timeout():
    with pytest.raises(ValueError):
        StreamPipesClient(
            StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="u", api_key="k"),
                host_address="localhost",
                request_timeout=0,
            )
        )


def test_measurement_identifier_encoding(client):
    from streampipes_performance.clients.streampipes import ApacheMeasurements

    with patch.object(ApacheMeasurements, "get") as get:
        client.dataLakeMeasureApi.get("a/b% c", limit=10)
        get.assert_called_once_with("a%2Fb%25%20c", limit=10)
    with patch.object(ApacheMeasurements, "storeDataToMeasurement") as store:
        frame = object()
        client.dataLakeMeasureApi.storeDataToMeasurement("a/b", frame)
        store.assert_called_once_with("a%2Fb", frame, False, 10000)


def test_timeout_applies_during_initialization_and_header_refresh():
    observed = []

    def version(client):
        observed.append(client.request_session.request.keywords["timeout"])
        return "1.0"

    with patch("streampipes.client.client.StreamPipesClient._get_server_version", version):
        client = StreamPipesClient(
            StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
                request_timeout=3,
            )
        )
    assert observed == [3]
    request = client.request_session.request
    client.refresh_headers()
    assert client.request_session.request is request
    assert client.request_session.headers["X-API-Key"] == "key"
    client.request_session.close()


def test_dataset_create_count_upload_routes(client):
    client.request_json = Mock(return_value={"elementId": "server-id"})
    payload = {"measureName": "test"}
    assert client.datasetApi.create(payload) == {"elementId": "server-id"}
    client.request_json.assert_called_with("POST", "api/v4/datalake/measure", json=payload)
    client.datasetApi.count("a/b")
    client.request_json.assert_called_with("GET", "api/v4/datalake/measure/a%2Fb/count")
    client.datasetApi.upload("a/b", ["timestamp", "value"], [[1, 2]])
    client.request_json.assert_called_with(
        "POST", "api/v4/datalake/measurements/a%2Fb",
        params={"ignoreSchemaMismatch": "false"},
        json={"allDataSeries": [{"headers": ["timestamp", "value"], "rows": [[1, 2]], "total": 1}]},
    )
