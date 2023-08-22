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
from unittest.mock import MagicMock, call, patch

from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.model.resource.exceptions import StreamPipesUnsupportedDataSeries


class TestDataLakeSeries(TestCase):
    def setUp(self) -> None:
        self.base_headers = [
            "changeDetectedHigh",
            "changeDetectedLow",
            "cumSumHigh",
            "cumSumLow",
            "level",
            "overflow",
            "sensorId",
            "underflow",
        ]

        self.headers = ["time"] + self.base_headers

        self.headers_expected = ["timestamp"] + self.base_headers

        self.data_series = {
            "total": 2,
            "rows": [
                [
                    "2022-11-05T14:47:50.838Z",
                    False,
                    False,
                    "0.0",
                    "0.0",
                    73.37740325927734,
                    False,
                    "level01",
                    False,
                ],
                [
                    "2022-11-05T14:47:54.906Z",
                    False,
                    False,
                    "0.0",
                    "-0.38673634857474815",
                    70.03279876708984,
                    False,
                    "level01",
                    False,
                ],
            ],
            "tags": None,
            "headers": self.headers,
        }

    @staticmethod
    def get_result_as_panda(http_session: MagicMock, data: dict):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.json.return_value = data
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        result = client.dataLakeMeasureApi.get(identifier="test")

        http_session.assert_has_calls(
            [call().get(url="https://localhost:80/streampipes-backend/api/v4/datalake/measurements/test?limit=1000")],
            any_order=True,
        )

        return result.to_pandas()

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_to_pandas(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

        query_result = {
            "total": 1,
            "headers": self.headers,
            "spQueryStatus": "OK",
            "allDataSeries": [
                self.data_series
            ],
        }

        result_pd = self.get_result_as_panda(http_session, query_result)

        self.assertEqual(2, len(result_pd))
        self.assertListEqual(
            self.headers_expected,
            list(result_pd.columns),
        )
        self.assertEqual(73.37740325927734, result_pd["level"][0])

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_group_by_to_pandas(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

        query_result = {
            "total": 2,
            "headers": self.headers,
            "spQueryStatus": "OK",
            "allDataSeries": [
                self.data_series,
                self.data_series
            ],
        }

        result_pd = self.get_result_as_panda(http_session, query_result)

        self.assertEqual(4, len(result_pd))
        self.assertListEqual(
            self.headers_expected,
            list(result_pd.columns),
        )
        self.assertEqual(70.03279876708984, result_pd["level"][3])

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_different_headers_exception(self, server_version: MagicMock, http_session: MagicMock):

        server_version.return_value = {"backendVersion": "0.x.y"}

        query_result = {
            "total": 1,
            "headers": ['one'],
            "spQueryStatus": "OK",
            "allDataSeries": [
                self.data_series
            ],
        }

        with self.assertRaises(StreamPipesUnsupportedDataSeries):
            self.get_result_as_panda(http_session, query_result)
