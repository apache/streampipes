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
from streampipes.model.compact import CompactPipeline, CompactPipelineElement
from streampipes.model.container import Pipelines
from streampipes.model.resource import DataStream, PipelineSummary


class TestPipelineEndpoint(TestCase):
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
        self.base_url = "https://localhost:80/streampipes-backend/api/v2/pipelines"
        self.summary = {
            "elementId": "pipeline-1",
            "name": "Pipeline",
            "description": "Description",
            "createdAt": 123,
            "running": False,
            "healthStatus": "OK",
            "pipelineNotifications": [],
            "valid": True,
        }
        self.compact_pipeline = CompactPipeline(
            name="Pipeline",
            pipeline_elements=[
                CompactPipelineElement(
                    type="stream",
                    id="stream-1",
                )
            ],
        )

    def test_summary_routes(self):
        response_payload = {"resources": [self.summary], "totalCount": 1}
        self.request_session.get.return_value.text = json.dumps(response_payload)

        pipelines = self.client.pipelineApi.all()

        self.assertIsInstance(pipelines, Pipelines)
        self.assertEqual(pipelines.total_count, 1)
        self.assertIsInstance(pipelines[0], PipelineSummary)
        self.request_session.get.assert_called_with(url=f"{self.base_url}/summary")

        pipeline = self.client.pipelineApi.get("pipeline-1")

        self.assertIsInstance(pipeline, PipelineSummary)
        self.assertEqual(pipeline.element_id, "pipeline-1")
        self.request_session.get.assert_called_with(url=f"{self.base_url}/summary")

    def test_get_missing_summary(self):
        self.request_session.get.return_value.text = json.dumps({"resources": [], "totalCount": 0})

        with self.assertRaises(KeyError):
            self.client.pipelineApi.get("missing")

    def test_compact_post(self):
        result = self.client.pipelineApi.post(self.compact_pipeline)

        self.assertIsNone(result)
        self.request_session.post.assert_called_with(
            url="https://localhost:80/streampipes-backend/api/v2/compact-pipelines",
            data=json.dumps(self.compact_pipeline.to_dict()),
            headers={"Content-type": "application/json"},
        )

    def test_post_rejects_non_compact_resources(self):
        invalid_resources = [
            PipelineSummary.model_validate(self.summary),
            DataStream(),
        ]

        for resource in invalid_resources:
            with self.subTest(resource_type=type(resource).__name__):
                with self.assertRaises(TypeError):
                    self.client.pipelineApi.post(resource)

        self.request_session.post.assert_not_called()

    def test_put_is_not_supported(self):
        with self.assertRaises(NotImplementedError):
            self.client.pipelineApi.put(self.compact_pipeline)

        self.request_session.put.assert_not_called()

    def test_lifecycle_and_delete_routes(self):
        lifecycle_url = f"{self.base_url}/pipeline-1"

        self.assertIsNone(self.client.pipelineApi.start("pipeline-1"))
        self.assertIsNone(self.client.pipelineApi.stop("pipeline-1"))
        self.client.pipelineApi.delete("pipeline-1")

        self.assertListEqual(
            self.request_session.get.call_args_list,
            [
                call(url=f"{lifecycle_url}/start"),
                call(url=f"{lifecycle_url}/stop"),
            ],
        )
        self.request_session.delete.assert_called_with(url=lifecycle_url)
