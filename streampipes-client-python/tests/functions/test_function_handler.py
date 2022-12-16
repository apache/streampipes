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
from json.encoder import JSONEncoder
from typing import Any, Dict, List, Tuple
from unittest import TestCase
from unittest.mock import AsyncMock, MagicMock, patch

from streampipes_client.client.client import StreamPipesClient, StreamPipesClientConfig
from streampipes_client.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes_client.functions.function_handler import FunctionHandler
from streampipes_client.functions.registration import Registration
from streampipes_client.functions.streampipes_function import StreamPipesFunction
from streampipes_client.functions.utils.function_context import FunctionContext
from streampipes_client.model.resource.data_stream import DataStream


class TestFunction(StreamPipesFunction):
    def getFunctionId(self) -> Tuple[str, int]:
        return ("org.test.TestFunction", 1)

    def requiredStreamIds(self) -> List[str]:
        return ["urn:streampipes.apache.org:eventstream:uPDKLI"]

    def onServiceStarted(self, context: FunctionContext):
        self.context = context
        self.data: List[Dict[str, Any]] = []

    def onEvent(self, event: Dict[str, Any], streamId: str):
        self.data.append(event)

    def onServiceStopped(self):
        self.stopped = True


class TestFunctionTwoStreams(StreamPipesFunction):
    def getFunctionId(self) -> Tuple[str, int]:
        return ("org.test.TestFunction2", 1)

    def requiredStreamIds(self) -> List[str]:
        return ["urn:streampipes.apache.org:eventstream:uPDKLI", "urn:streampipes.apache.org:eventstream:HHoidJ"]

    def onServiceStarted(self, context: FunctionContext):
        self.context = context
        self.data1: List[Dict[str, Any]] = []
        self.data2: List[Dict[str, Any]] = []

    def onEvent(self, event: Dict[str, Any], streamId: str):
        if streamId == self.requiredStreamIds()[0]:
            self.data1.append(event)
        else:
            self.data2.append(event)

    def onServiceStopped(self):
        self.stopped = True


class TestMessage:
    def __init__(self, data) -> None:
        self.data = JSONEncoder().encode(data).encode()


class TestMessageIterator:
    def __init__(self, test_data) -> None:
        self.test_data = test_data
        self.i = -1

    def __aiter__(self):
        return self

    async def __anext__(self):
        if self.i < len(self.test_data) - 1:
            self.i += 1
            return TestMessage(self.test_data[self.i])
        else:
            raise StopAsyncIteration


class TestFunctionHandler(TestCase):
    def setUp(self) -> None:
        # set example responses from endpoints
        self.data_stream: Dict[str, Any] = {
            "elementId": "urn:streampipes.apache.org:eventstream:uPDKLI",
            "name": "Test",
            "description": "",
            "iconUrl": None,
            "appId": None,
            "includesAssets": False,
            "includesLocales": False,
            "includedAssets": [],
            "includedLocales": [],
            "applicationLinks": [],
            "internallyManaged": True,
            "connectedTo": None,
            "eventGrounding": {
                "elementId": "urn:streampipes.apache.org:spi:eventgrounding:TwGIQA",
                "transportProtocols": [
                    {
                        "elementId": "urn:streampipes.apache.org:spi:natstransportprotocol:VJkHmZ",
                        "brokerHostname": "nats",
                        "topicDefinition": {
                            "elementId": "urn:streampipes.apache.org:spi:simpletopicdefinition:QzCiFI",
                            "actualTopicName": "test1",
                        },
                        "port": 4222,
                    }
                ],
                "transportFormats": [
                    {
                        "elementId": "urn:streampipes.apache.org:spi:transportformat:CMGsLP",
                        "rdfType": ["http://sepa.event-processing.org/sepa#json"],
                    }
                ],
            },
            "eventSchema": {
                "elementId": "urn:streampipes.apache.org:spi:eventschema:rARlLX",
                "eventProperties": [
                    {
                        "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:yogPNV",
                        "label": "Density",
                        "description": "Denotes the current density of the fluid",
                        "runtimeName": "density",
                        "required": False,
                        "domainProperties": ["http://schema.org/Number"],
                        "eventPropertyQualities": [],
                        "requiresEventPropertyQualities": [],
                        "propertyScope": "MEASUREMENT_PROPERTY",
                        "index": 5,
                        "runtimeId": None,
                        "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                        "measurementUnit": None,
                        "valueSpecification": None,
                    },
                    {
                        "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:GjZgFg",
                        "label": "Temperature",
                        "description": "Denotes the current temperature in degrees celsius",
                        "runtimeName": "temperature",
                        "required": False,
                        "domainProperties": ["http://schema.org/Number"],
                        "eventPropertyQualities": [],
                        "requiresEventPropertyQualities": [],
                        "propertyScope": "MEASUREMENT_PROPERTY",
                        "index": 4,
                        "runtimeId": None,
                        "runtimeType": "http://www.w3.org/2001/XMLSchema#float",
                        "measurementUnit": "http://codes.wmo.int/common/unit/degC",
                        "valueSpecification": {
                            "elementId": "urn:streampipes.apache.org:spi:quantitativevalue:ZQSJfk",
                            "minValue": 0,
                            "maxValue": 100,
                            "step": 0.1,
                        },
                    },
                    {
                        "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
                        "elementId": "urn:streampipes.apache.org:spi:eventpropertyprimitive:NRKdap",
                        "label": "Timestamp",
                        "description": "The current timestamp value",
                        "runtimeName": "timestamp",
                        "required": False,
                        "domainProperties": ["http://schema.org/DateTime"],
                        "eventPropertyQualities": [],
                        "requiresEventPropertyQualities": [],
                        "propertyScope": "HEADER_PROPERTY",
                        "index": 0,
                        "runtimeId": None,
                        "runtimeType": "http://www.w3.org/2001/XMLSchema#long",
                        "measurementUnit": None,
                        "valueSpecification": None,
                    },
                ],
            },
            "measurementCapability": None,
            "measurementObject": None,
            "index": 0,
            "correspondingAdapterId": "urn:streampipes.apache.org:spi:org.apache.streampipes.connect.iiot...",
            "category": None,
            "uri": "urn:streampipes.apache.org:eventstream:uPDKLI",
            "dom": None,
        }

        self.test_stream_data1 = [
            {"density": 10.3, "temperature": 20.5, "timestamp": 1670000001000},
            {"density": 13.4, "temperature": 20.4, "timestamp": 1670000002000},
            {"density": 12.6, "temperature": 20.7, "timestamp": 1670000003000},
            {"density": 9.0, "temperature": 21.0, "timestamp": 1670000004000},
            {"density": 10.6, "temperature": 20.4, "timestamp": 1670000005000},
            {"density": 11.3, "temperature": 19.5, "timestamp": 1670000006000},
            {"density": 15.7, "temperature": 19.3, "timestamp": 1670000007000},
        ]
        self.test_stream_data2 = [
            {"density": 5.3, "temperature": 30.5, "timestamp": 1670000001000},
            {"density": 2.6, "temperature": 31.7, "timestamp": 1670000002000},
            {"density": 3.6, "temperature": 30.4, "timestamp": 1670000003000},
            {"density": 5.7, "temperature": 29.3, "timestamp": 1670000004000},
            {"density": 5.3, "temperature": 30.5, "timestamp": 1670000005000},
            {"density": 2.6, "temperature": 31.7, "timestamp": 1670000006000},
            {"density": 3.6, "temperature": 30.4, "timestamp": 1670000007000},
        ]

    @patch("streampipes_client.functions.function_handler.NatsBroker.disconnect", autospec=True)
    @patch("streampipes_client.functions.function_handler.NatsBroker._createSubscription", autospec=True)
    @patch("streampipes_client.functions.function_handler.NatsBroker._makeConnection", autospec=True)
    @patch("streampipes_client.functions.function_handler.NatsBroker.get_message", autospec=True)
    @patch("streampipes_client.client.client.Session", autospec=True)
    def test_function_handler(self, http_session: MagicMock, nats_broker: MagicMock, *args: Tuple[AsyncMock]):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.json.return_value = self.data_stream
        http_session.return_value = http_session_mock

        nats_broker.return_value = TestMessageIterator(self.test_stream_data1)

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        registration = Registration()
        test_function = TestFunction()
        registration.register(test_function)
        function_handler = FunctionHandler(registration, client)
        function_handler.initializeFunctions()

        self.assertEqual(test_function.context.client, client)
        self.assertDictEqual(
            test_function.context.schema, {self.data_stream["elementId"]: DataStream(**self.data_stream)}
        )
        self.assertListEqual(test_function.context.streams, test_function.requiredStreamIds())
        self.assertEqual(test_function.context.function_id, test_function.getFunctionId()[0])

        self.assertListEqual(test_function.data, self.test_stream_data1)
        self.assertTrue(test_function.stopped)

    @patch("streampipes_client.functions.function_handler.NatsBroker.disconnect", autospec=True)
    @patch("streampipes_client.functions.function_handler.NatsBroker._createSubscription", autospec=True)
    @patch("streampipes_client.functions.function_handler.NatsBroker._makeConnection", autospec=True)
    @patch("streampipes_client.functions.function_handler.NatsBroker.get_message", autospec=True)
    @patch("streampipes_client.endpoint.endpoint.APIEndpoint.get", autospec=True)
    def test_function_handler_two_streams(self, endpoint: MagicMock, nats_broker: MagicMock, *args: Tuple[AsyncMock]):
        def get_stream(endpoint, stream_id):
            if stream_id == "urn:streampipes.apache.org:eventstream:uPDKLI":
                return DataStream(**self.data_stream)
            elif stream_id == "urn:streampipes.apache.org:eventstream:HHoidJ":
                data_stream = DataStream(**self.data_stream)
                data_stream.element_id = stream_id
                data_stream.event_grounding.transport_protocols[0].topic_definition.actual_topic_name = "test2"
                return data_stream

        endpoint.side_effect = get_stream

        def get_message(broker):
            if broker.topic_name == "test1":
                return TestMessageIterator(self.test_stream_data1)
            elif broker.topic_name == "test2":
                return TestMessageIterator(self.test_stream_data2)

        nats_broker.side_effect = get_message

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        registration = Registration()
        test_function1 = TestFunction()
        test_function2 = TestFunctionTwoStreams()
        registration.register(test_function1).register(test_function2)
        function_handler = FunctionHandler(registration, client)
        function_handler.initializeFunctions()

        self.assertListEqual(test_function1.data, self.test_stream_data1)
        self.assertTrue(test_function1.stopped)

        stream_ids = test_function2.requiredStreamIds()
        self.assertDictEqual(
            test_function2.context.schema,
            {
                stream_ids[0]: get_stream(None, stream_ids[0]),
                stream_ids[1]: get_stream(None, stream_ids[1]),
            },
        )
        self.assertListEqual(test_function2.context.streams, test_function2.requiredStreamIds())
        self.assertListEqual(test_function2.data1, self.test_stream_data1)
        self.assertListEqual(test_function2.data2, self.test_stream_data2)
        self.assertTrue(test_function2.stopped)
