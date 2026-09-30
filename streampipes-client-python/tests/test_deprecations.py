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
from unittest.mock import MagicMock, patch

from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.endpoint.api.data_lake_measure import DataLakeMeasureEndpoint
from streampipes.endpoint.api.dataset import DatasetEndpoint
from streampipes.model.container import DataLakeMeasures, Datasets
from streampipes.model.resource import DataLakeMeasure, DatasetMetadata

DATASET_METADATA_RESPONSE: list[dict] = [
    {
        "@class": "org.apache.streampipes.model.dataset.DatasetMetadata",
        "elementId": "abc",
        "measureName": "flowrate",
        "timestampField": "s0::timestamp",
        "eventSchema": {"eventProperties": []},
        "pipelineId": "p1",
        "pipelineName": "pipe",
        "schemaVersion": "1.1",
        "schemaUpdateStrategy": "UPDATE_SCHEMA",
        "retentionTime": None,
    }
]


class TestDeprecatedDataLakeMeasureModel(TestCase):
    def test_dataset_metadata_parses_backend_response(self):
        datasets = Datasets.from_json(json.dumps(DATASET_METADATA_RESPONSE))

        self.assertIsInstance(datasets[0], DatasetMetadata)
        self.assertEqual("flowrate", datasets[0].measure_name)
        self.assertEqual("UPDATE_SCHEMA", datasets[0].schema_update_strategy)
        self.assertIn("schema_update_strategy", datasets.to_pandas().columns)

    def test_data_lake_measure_is_deprecated_but_still_parses(self):
        with self.assertWarns(DeprecationWarning):
            measure = DataLakeMeasure.model_validate(DATASET_METADATA_RESPONSE[0])

        self.assertIsInstance(measure, DatasetMetadata)
        self.assertEqual("flowrate", measure.measure_name)
        self.assertIsNone(measure.pipeline_is_running)

    def test_data_lake_measure_accepts_legacy_field(self):
        legacy_response = {**DATASET_METADATA_RESPONSE[0], "pipelineIsRunning": True}
        with self.assertWarns(DeprecationWarning):
            measure = DataLakeMeasure.model_validate(legacy_response)

        self.assertTrue(measure.pipeline_is_running)

    def test_data_lake_measures_container_is_deprecated(self):
        with self.assertWarns(DeprecationWarning):
            measures = DataLakeMeasures.from_json(json.dumps(DATASET_METADATA_RESPONSE))

        self.assertIsInstance(measures, Datasets)
        self.assertIsInstance(measures[0], DataLakeMeasure)
        self.assertEqual(1, len(measures))


class TestDeprecatedDataLakeMeasureApi(TestCase):
    @staticmethod
    def _create_client() -> StreamPipesClient:
        return StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_client_exposes_dataset_api(self, server_version: MagicMock):
        server_version.return_value = "0.x.y"

        client = self._create_client()

        self.assertIsInstance(client.datasetApi, DatasetEndpoint)
        self.assertNotIsInstance(client.datasetApi, DataLakeMeasureEndpoint)
        self.assertIs(client.datasetApi._container_cls, Datasets)

    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_data_lake_measure_api_is_deprecated(self, server_version: MagicMock):
        server_version.return_value = "0.x.y"

        client = self._create_client()

        with self.assertWarns(DeprecationWarning):
            endpoint = client.dataLakeMeasureApi

        self.assertIsInstance(endpoint, DataLakeMeasureEndpoint)
        self.assertIs(endpoint._container_cls, DataLakeMeasures)
        self.assertEqual(client.datasetApi.build_url(), endpoint.build_url())

        # the deprecated endpoint is created once and reused afterwards
        with self.assertWarns(DeprecationWarning):
            self.assertIs(endpoint, client.dataLakeMeasureApi)

    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_data_lake_measure_endpoint_warns_on_creation(self, server_version: MagicMock):
        server_version.return_value = "0.x.y"

        client = self._create_client()

        with self.assertWarns(DeprecationWarning):
            DataLakeMeasureEndpoint(parent_client=client)

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_data_lake_measure_api_all_returns_deprecated_container(
        self, server_version: MagicMock, http_session: MagicMock
    ):
        server_version.return_value = "0.x.y"
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.text = json.dumps(DATASET_METADATA_RESPONSE)
        http_session.return_value = http_session_mock

        client = self._create_client()

        with self.assertWarns(DeprecationWarning):
            measures = client.dataLakeMeasureApi.all()

        self.assertIsInstance(measures, DataLakeMeasures)
        self.assertIsInstance(measures[0], DataLakeMeasure)
        self.assertEqual("flowrate", measures[0].measure_name)  # type: ignore
