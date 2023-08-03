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
import json
from unittest import TestCase
from unittest.mock import patch, MagicMock, call


from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.endpoint.api import DataLakeMeasureEndpoint


class TestStreamPipesClient(TestCase):
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_client_init(self, server_version: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

        result = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
                https_disabled=True,
            )
        )

        expected_headers = {
            "X-API-User": "user",
            "X-API-Key": "key",
            "Application": "application/json",
        }
        result_headers = dict(result.request_session.headers)
        self.assertDictContainsSubset(
            subset=expected_headers,
            dictionary=result_headers,
        )
        self.assertTrue(isinstance(result.dataLakeMeasureApi, DataLakeMeasureEndpoint))
        self.assertEqual(result.base_api_path, "http://localhost:80/streampipes-backend/")

    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_client_create(self, server_version: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

        result = StreamPipesClient.create(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
                https_disabled=False,
                port=443,
            )
        )

        expected_headers = {
            "X-API-User": "user",
            "X-API-Key": "key",
            "Application": "application/json",
        }
        result_headers = dict(result.request_session.headers)
        self.assertDictContainsSubset(
            subset=expected_headers,
            dictionary=result_headers,
        )
        self.assertTrue(isinstance(result.dataLakeMeasureApi, DataLakeMeasureEndpoint))
        self.assertEqual(result.base_api_path, "https://localhost:443/streampipes-backend/")

    @patch("streampipes.client.client.logger", autospec=True)
    @patch("streampipes.endpoint.endpoint.APIEndpoint._make_request", autospec=True)
    def test_client_describe(self, make_request: MagicMock, mocked_logger: MagicMock):

        class MockResponse:
            def __init__(self, text):
                self.text = text

            def json(self):
                return json.loads(self.text)

        def simulate_response(*args, **kwargs):

            if "measurements" in kwargs["url"]:
                return MockResponse(
                    json.dumps(
                        [
                            {
                                "measureName": "test",
                                "timestampField": "time",
                                "pipelineIsRunning": False,
                                "schemaVersion": "0",
                            }
                        ]
                    )
                )
            if "streams" in kwargs["url"]:
                return MockResponse(
                    json.dumps(
                        [{"elementId": "test-stream", "name": "test", "eventGrounding": {"transportProtocols": []}}]
                    )
                )
            if "versions" in kwargs["url"]:
                return MockResponse(
                    json.dumps({"backendVersion": "SP-dev"})
                )

        make_request.side_effect = simulate_response
        StreamPipesClient.create(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
                https_disabled=False,
                port=443,
            )
        ).describe()

        mocked_logger.info.assert_has_calls(
            calls=[
                call(
                    "\nHi there!\nYou are connected to a StreamPipes instance running at https://localhost:443 with version SP-dev.\n"
                    "The following StreamPipes resources are available with this client:\n"
                    "1x DataLakeMeasures\n1x DataStreams"
                ),
            ],
            any_order=True,
        )
