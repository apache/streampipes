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
from collections import namedtuple
from unittest import TestCase
from unittest.mock import MagicMock, call, patch

from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.endpoint.api import DataLakeMeasureEndpoint


class TestStreamPipesClient(TestCase):
    def test_client_init(self):
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

    def test_client_create(self):
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

    @patch("builtins.print")
    @patch("streampipes.endpoint.endpoint.APIEndpoint._make_request", autospec=True)
    def test_client_describe(self, make_request: MagicMock, mocked_print: MagicMock):
        def simulate_response(*args, **kwargs):
            Response = namedtuple("Response", ["text"])
            if "measurements" in kwargs["url"]:
                return Response(
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
                return Response(
                    json.dumps(
                        [{"elementId": "test-stream", "name": "test", "eventGrounding": {"transportProtocols": []}}]
                    )
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

        mocked_print.assert_has_calls(
            calls=[
                call(
                    "\nHi there!\nYou are connected to a StreamPipes instance running at https://localhost:443.\n"
                    "The following StreamPipes resources are available with this client:\n"
                    "1x DataLakeMeasures\n1x DataStreams"
                ),
            ],
            any_order=True,
        )
