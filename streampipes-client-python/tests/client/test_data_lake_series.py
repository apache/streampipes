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

from pydantic import ValidationError

from streampipes.client import StreamPipesClient
from streampipes.client.config import StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.model.resource.data_series import DataSeries
from streampipes.model.resource.exceptions import StreamPipesUnsupportedDataSeries
from streampipes.model.resource.query_result import QueryResult


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
            "allDataSeries": [self.data_series],
            "sourceIndex": 0,
            "forId": None,
            "lastTimestamp": 1717936808802,
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
            "allDataSeries": [self.data_series, self.data_series],
            "sourceIndex": 0,
            "forId": None,
            "lastTimestamp": 1717936808802,
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
            "headers": ["one"],
            "spQueryStatus": "OK",
            "allDataSeries": [self.data_series],
            "sourceIndex": 0,
            "forId": None,
            "lastTimestamp": 1717936808802,
        }

        with self.assertRaises(StreamPipesUnsupportedDataSeries):
            self.get_result_as_panda(http_session, query_result)

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_empty_result_to_pandas(self, server_version: MagicMock, http_session: MagicMock):
        server_version.return_value = {"backendVersion": "0.x.y"}
        for headers in (None, [], ["time", "mean_temperature"]):
            with self.subTest(headers=headers):
                result = self.get_result_as_panda(
                    http_session,
                    {
                        "total": 0,
                        "headers": headers,
                        "spQueryStatus": "OK",
                        "allDataSeries": [],
                        "sourceIndex": 0,
                        "forId": None,
                        "lastTimestamp": 0,
                    },
                )
                self.assertTrue(result.empty)
                self.assertEqual(list(result.columns), ["timestamp", "mean_temperature"] if headers else [])

    def test_too_much_data_preserves_status(self):
        result = QueryResult.model_validate(
            {
                "total": 2000,
                "headers": None,
                "spQueryStatus": "TOO_MUCH_DATA",
                "allDataSeries": [],
                "sourceIndex": 0,
                "lastTimestamp": 0,
            }
        )
        self.assertEqual(result.query_status, "TOO_MUCH_DATA")
        self.assertEqual(result.total, 2000)
        self.assertTrue(result.to_pandas().empty)

    def test_to_pandas_preserves_headers(self):
        result = QueryResult.model_validate(
            {
                "total": 2,
                "headers": self.headers,
                "spQueryStatus": "OK",
                "allDataSeries": [self.data_series],
                "sourceIndex": 0,
                "lastTimestamp": 0,
            }
        )
        first = result.to_pandas()
        self.assertTrue(first.equals(result.to_pandas()))
        self.assertEqual(result.headers, self.headers)
        self.assertEqual(result.all_data_series[0].headers, self.headers)

    def test_nonempty_result_without_headers_is_rejected(self):
        result = QueryResult.model_validate(
            {
                "total": 2,
                "headers": None,
                "spQueryStatus": "OK",
                "allDataSeries": [self.data_series],
                "sourceIndex": 0,
                "lastTimestamp": 0,
            }
        )
        with self.assertRaises(StreamPipesUnsupportedDataSeries):
            result.to_pandas()

    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_grouped_response_preserves_tag_maps(self, server_version: MagicMock, http_session: MagicMock):
        server_version.return_value = {"backendVersion": "0.x.y"}
        headers = ["time", "mean_temperature"]
        rows = [["2023-02-24T17:20:00Z", 45.0]]
        tags = [{"sensorId": "flowrate01"}, {"sensorId": "flowrate02", "location": "factory"}]
        http_session.return_value = MagicMock()
        http_session.return_value.get.return_value.json.return_value = {
            "total": 2,
            "headers": headers,
            "spQueryStatus": "OK",
            "allDataSeries": [{"total": 1, "headers": headers, "rows": rows, "tags": group} for group in tags],
            "sourceIndex": 0,
            "lastTimestamp": 0,
        }
        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )
        result = client.dataLakeMeasureApi.get(
            identifier="Flowrate",
            columns=["temperature"],
            aggregation_function="MEAN",
            group_by=["sensorId"],
            time_interval="5m",
            fill="none",
            limit=2000,
        )
        self.assertEqual([series.tags for series in result.all_data_series], tags)
        for series, expected_tags in zip(result.all_data_series, tags):
            self.assertEqual(
                series.to_pandas().to_dict("list"),
                {
                    "time": ["2023-02-24T17:20:00Z"],
                    "mean_temperature": [45.0],
                },
            )
            self.assertEqual(series.model_dump()["tags"], expected_tags)

    def test_data_series_tag_compatibility(self):
        for tags in (None, {}, {"sensorId": "flowrate01"}):
            with self.subTest(tags=tags):
                series = DataSeries.model_validate({**self.data_series, "tags": tags})
                self.assertEqual(series.tags, tags)
        without_tags = {key: value for key, value in self.data_series.items() if key != "tags"}
        self.assertIsNone(DataSeries.model_validate(without_tags).tags)

    def test_data_series_rejects_string_tags(self):
        with self.assertRaises(ValidationError):
            DataSeries.model_validate({**self.data_series, "tags": "sensorId=flowrate01"})
