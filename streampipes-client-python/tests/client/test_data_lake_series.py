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

from streampipes_client.client import StreamPipesClient
from streampipes_client.client.client_config import StreamPipesClientConfig
from streampipes_client.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes_client.model.resource.data_lake_series import (
    StreamPipesUnsupportedDataLakeSeries,
)


class TestDataLakeSeries(TestCase):
    def setUp(self) -> None:
        self.series_regular = {
            "total": 1,
            "headers": [
                "time",
                "changeDetectedHigh",
                "changeDetectedLow",
                "cumSumHigh",
                "cumSumLow",
                "level",
                "overflow",
                "sensorId",
                "underflow",
            ],
            "allDataSeries": [
                {
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
                    "headers": [
                        "time",
                        "changeDetectedHigh",
                        "changeDetectedLow",
                        "cumSumHigh",
                        "cumSumLow",
                        "level",
                        "overflow",
                        "sensorId",
                        "underflow",
                    ],
                }
            ],
        }

        self.series_missing = {
            "total": 1,
            "headers": [
                "time",
                "changeDetectedHigh",
                "changeDetectedLow",
                "cumSumHigh",
                "cumSumLow",
                "level",
                "overflow",
                "sensorId",
                "underflow",
            ],
            "allDataSeries": [],
        }

    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_to_pandas(self, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.text = json.dumps(self.series_regular)
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )
        result = client.dataLakeMeasureApi.get(identifier="test")

        http_session.assert_has_calls(
            [call().get(url="https://localhost:80/streampipes-backend/api/v4/datalake/measurements/test")],
            any_order=True,
        )

        result_pd = result.to_pandas()

        self.assertEqual(2, len(result_pd))
        self.assertListEqual(
            [
                "time",
                "changeDetectedHigh",
                "changeDetectedLow",
                "cumSumHigh",
                "cumSumLow",
                "level",
                "overflow",
                "sensorId",
                "underflow",
            ],
            list(result_pd.columns),
        )
        self.assertEqual(73.37740325927734, result_pd["level"][0])

    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_to_pandas_unsupported_series(self, http_session: MagicMock):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.text = json.dumps(self.series_missing)
        http_session.return_value = http_session_mock

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )
        with self.assertRaises(StreamPipesUnsupportedDataLakeSeries):
            client.dataLakeMeasureApi.get(identifier="test")
