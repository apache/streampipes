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
from unittest.mock import MagicMock, call, patch

from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.model.compact import CompactAdapter
from streampipes.model.container import Adapters
from streampipes.model.resource import AdapterSummary, DataStream


class TestAdapterEndpoint(TestCase):
    def setUp(self):
        session_patcher = patch("streampipes.client.client.Session", autospec=True)
        server_version_patcher = patch(
            "streampipes.client.client.StreamPipesClient._get_server_version",
            autospec=True,
        )
        self.addCleanup(session_patcher.stop)
        self.addCleanup(server_version_patcher.stop)

        session = session_patcher.start()
        server_version_patcher.start()
        self.request_session = MagicMock()
        session.return_value = self.request_session
        self.client = StreamPipesClient(
            StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )
        self.base_url = "https://localhost:80/streampipes-backend/api/v2/connect/master/adapters"
        self.summary = {
            "elementId": "adapter-1",
            "correspondingDataStreamElementId": "stream-1",
            "name": "Adapter",
            "description": "Description",
            "running": False,
            "createdAt": 123,
            "appId": "org.example.adapter",
            "includedAssets": [],
            "icon": None,
        }

    def test_summary_routes(self):
        response_payload = {"resources": [self.summary], "totalCount": 1}
        self.request_session.get.return_value.text = json.dumps(response_payload)

        adapters = self.client.adapterApi.all()

        self.assertIsInstance(adapters, Adapters)
        self.assertEqual(adapters.total_count, 1)
        self.assertIsInstance(adapters[0], AdapterSummary)
        self.request_session.get.assert_called_with(url=f"{self.base_url}/summary")

        adapter = self.client.adapterApi.get("adapter-1")

        self.assertIsInstance(adapter, AdapterSummary)
        self.assertEqual(adapter.element_id, "adapter-1")
        self.request_session.get.assert_called_with(url=f"{self.base_url}/summary")

    def test_get_missing_summary(self):
        self.request_session.get.return_value.text = json.dumps({"resources": [], "totalCount": 0})

        with self.assertRaises(KeyError):
            self.client.adapterApi.get("missing")

    def test_compact_post(self):
        compact_adapter = CompactAdapter(
            name="Adapter",
            app_id="org.example.adapter",
            configuration=[{"wait-time-ms": "1000"}],
        )
        result = self.client.adapterApi.post(compact_adapter)

        self.assertIsNone(result)
        self.request_session.post.assert_called_with(
            url="https://localhost:80/streampipes-backend/api/v2/connect/compact-adapters",
            data=json.dumps(compact_adapter.to_dict()),
            headers={"Content-type": "application/json"},
        )

    def test_post_rejects_non_compact_resources(self):
        invalid_resources = [
            AdapterSummary.model_validate(self.summary),
            DataStream(),
        ]

        for resource in invalid_resources:
            with self.subTest(resource_type=type(resource).__name__):
                with self.assertRaises(TypeError):
                    self.client.adapterApi.post(resource)

        self.request_session.post.assert_not_called()

    def test_put_is_not_supported(self):
        with self.assertRaises(NotImplementedError):
            self.client.adapterApi.put(
                CompactAdapter(name="Adapter", app_id="org.example.adapter"),
            )

        self.request_session.put.assert_not_called()

    def test_lifecycle_and_delete_routes(self):
        lifecycle_url = f"{self.base_url}/adapter-1"

        self.assertIsNone(self.client.adapterApi.start("adapter-1"))
        self.assertIsNone(self.client.adapterApi.stop("adapter-1"))
        self.client.adapterApi.delete("adapter-1")

        self.assertListEqual(
            self.request_session.post.call_args_list,
            [
                call(url=f"{lifecycle_url}/start"),
                call(url=f"{lifecycle_url}/stop"),
            ],
        )
        self.request_session.delete.assert_called_with(url=lifecycle_url)
