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

from unittest import TestCase

from streampipes_client.client import StreamPipesClient
from streampipes_client.client.client_config import StreamPipesClientConfig
from streampipes_client.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes_client.endpoint import DataLakeMeasureEndpoint


class TestStreamPipesClient(TestCase):
    def test_client_init(self):
        result = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
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
        self.assertEqual(result.base_api_path, "https://localhost:80/streampipes-backend/")

    def test_client_create(self):
        result = StreamPipesClient.create(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
                https_disabled=True,
                port=500,
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
        self.assertEqual(result.base_api_path, "http://localhost:500/streampipes-backend/")
