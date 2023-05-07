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
from typing import Any, Dict, List, Tuple
from unittest import TestCase
from unittest.mock import AsyncMock, MagicMock, patch

from streampipes.client.client import StreamPipesClient, StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.function_zoo.river_function import OnlineML
from streampipes.functions.utils.data_stream_generator import (
    RuntimeType,
    create_data_stream,
)
from tests.functions.test_function_handler import TestMessageIterator


class TestUnsupervisedModel:
    def __init__(self) -> None:
        self.data_x: List[Dict[str, Any]] = []

    def learn_one(self, x):
        pass

    def predict_one(self, x):
        self.data_x.append(x)
        return int(x["bool"])


class TestSupervisedModel:
    def __init__(self) -> None:
        self.data_x: List[Dict[str, Any]] = []
        self.data_y: List[Dict[str, Any]] = []

    def learn_one(self, x, y):
        self.data_y.append(y)

    def predict_one(self, x):
        self.data_x.append(x)
        return x["number"] < 13


class TestRiverFunction(TestCase):
    def setUp(self) -> None:
        self.data_stream = create_data_stream(
            "test", attributes={"number": RuntimeType.FLOAT.value, "bool": RuntimeType.BOOLEAN.value}
        ).to_dict()

        self.test_stream_data = [
            {"number": 10.3, "bool": True, "timestamp": 1670000001000},
            {"number": 13.4, "bool": False, "timestamp": 1670000002000},
            {"number": 12.6, "bool": True, "timestamp": 1670000003000},
            {"number": 9.0, "bool": True, "timestamp": 1670000004000},
            {"number": 10.6, "bool": False, "timestamp": 1670000005000},
        ]

    @patch("streampipes.functions.broker.NatsPublisher.disconnect", autospec=True)
    @patch("streampipes.functions.broker.NatsPublisher._make_connection", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer.disconnect", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer._make_connection", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer._create_subscription", autospec=True)
    @patch("streampipes.functions.streampipes_function.time", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer.get_message", autospec=True)
    @patch("streampipes.functions.broker.NatsPublisher.publish_event", autospec=True)
    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_river_function_unsupervised(
        self,
        server_version: MagicMock,
        http_session: MagicMock,
        pulish_event: MagicMock,
        get_message: MagicMock,
        time: MagicMock,
        *args: Tuple[AsyncMock]
    ):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.json.return_value = self.data_stream
        http_session.return_value = http_session_mock

        server_version.return_value = {"backendVersion": '0.x.y'}

        output_events = []

        def save_event(self, event: Dict[str, Any]):
            output_events.append(event)

        pulish_event.side_effect = save_event
        get_message.return_value = TestMessageIterator(self.test_stream_data)
        time.side_effect = lambda: 0

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        def on_event(self, event, stream_id):
            if len(self.model.data_x) >= 3:
                self.learning = False

        model = TestUnsupervisedModel()

        online_learning = OnlineML(
            client=client,
            stream_ids=["sp:spdatastream:xboBFK"],
            model=model,
            prediction_type=RuntimeType.INTEGER.value,
            on_event=on_event,
        )
        online_learning.start()

        self.assertListEqual(
            output_events,
            [
                {"learning": True, "prediction": 1, "timestamp": 0},
                {"learning": True, "prediction": 0, "timestamp": 0},
                {"learning": True, "prediction": 1, "timestamp": 0},
                {"learning": False, "prediction": 1, "timestamp": 0},
                {"learning": False, "prediction": 0, "timestamp": 0},
            ],
        )

        self.assertListEqual(model.data_x, self.test_stream_data)

    @patch("streampipes.functions.broker.NatsPublisher.disconnect", autospec=True)
    @patch("streampipes.functions.broker.NatsPublisher._make_connection", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer.disconnect", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer._make_connection", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer._create_subscription", autospec=True)
    @patch("streampipes.functions.streampipes_function.time", autospec=True)
    @patch("streampipes.functions.broker.NatsConsumer.get_message", autospec=True)
    @patch("streampipes.functions.broker.NatsPublisher.publish_event", autospec=True)
    @patch("streampipes.client.client.Session", autospec=True)
    @patch("streampipes.client.client.StreamPipesClient._get_server_version", autospec=True)
    def test_river_function_supervised(
        self,
        server_version: MagicMock,
        http_session: MagicMock,
        pulish_event: MagicMock,
        get_message: MagicMock,
        time: MagicMock,
        *args: Tuple[AsyncMock]
    ):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.json.return_value = self.data_stream
        http_session.return_value = http_session_mock

        server_version.return_value = {"backendVersion": '0.x.y'}

        output_events = []

        def save_event(self, event: Dict[str, Any]):
            output_events.append(event)

        pulish_event.side_effect = save_event
        get_message.return_value = TestMessageIterator(self.test_stream_data)
        time.side_effect = lambda: 0

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        def on_event(self, event, stream_id):
            if len(self.model.data_x) >= 3:
                self.learning = False

        model = TestSupervisedModel()

        online_learning = OnlineML(
            client=client,
            stream_ids=["sp:spdatastream:xboBFK"],
            model=model,
            prediction_type=RuntimeType.BOOLEAN.value,
            supervised=True,
            target_label="bool",
            on_event=on_event,
        )
        online_learning.start()

        self.assertListEqual(
            output_events,
            [
                {"truth": True, "learning": True, "prediction": True, "timestamp": 0},
                {"truth": False, "learning": True, "prediction": False, "timestamp": 0},
                {"truth": True, "learning": True, "prediction": True, "timestamp": 0},
                {"truth": True, "learning": False, "prediction": True, "timestamp": 0},
                {"truth": False, "learning": False, "prediction": True, "timestamp": 0},
            ],
        )

        self.assertListEqual(
            model.data_x,
            [
                {"number": 10.3, "timestamp": 1670000001000},
                {"number": 13.4, "timestamp": 1670000002000},
                {"number": 12.6, "timestamp": 1670000003000},
                {"number": 9.0, "timestamp": 1670000004000},
                {"number": 10.6, "timestamp": 1670000005000},
            ],
        )
        self.assertListEqual(model.data_y, [True, False, True])
