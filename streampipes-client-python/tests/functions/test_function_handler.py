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

from streampipes.client.client import StreamPipesClient, StreamPipesClientConfig
from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
from streampipes.functions.function_handler import FunctionHandler
from streampipes.functions.registration import Registration
from streampipes.functions.streampipes_function import StreamPipesFunction
from streampipes.functions.utils.data_stream_generator import (
    RuntimeType,
    create_data_stream,
)
from streampipes.functions.utils.function_context import FunctionContext
from streampipes.model.resource.data_stream import DataStream
from streampipes.model.resource.function_definition import FunctionDefinition


class TestFunction(StreamPipesFunction):
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


class TestFunctionOutput(StreamPipesFunction):
    def requiredStreamIds(self) -> List[str]:
        return ["urn:streampipes.apache.org:eventstream:uPDKLI"]

    def onServiceStarted(self, context: FunctionContext):
        self.context = context
        self.i = 0

    def onEvent(self, event: Dict[str, Any], streamId: str):
        self.add_output(self.function_definition.get_output_stream_ids()[0], event={"number": self.i})
        self.i += 1

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
                        "@class": "org.apache.streampipes.model.grounding.NatsTransportProtocol",
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

    @patch("streampipes.functions.broker.nats_broker.NatsBroker.disconnect", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker.createSubscription", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker._makeConnection", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker.get_message", autospec=True)
    @patch("streampipes.client.client.Session", autospec=True)
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
        self.assertEqual(test_function.context.function_id, test_function.getFunctionId().id)

        self.assertListEqual(test_function.data, self.test_stream_data1)
        self.assertTrue(test_function.stopped)

    @patch("streampipes.functions.broker.nats_broker.NatsBroker.disconnect", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker.createSubscription", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker._makeConnection", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker.get_message", autospec=True)
    @patch("streampipes.endpoint.endpoint.APIEndpoint.get", autospec=True)
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

    @patch("streampipes.functions.broker.nats_broker.NatsBroker.disconnect", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker.createSubscription", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker._makeConnection", autospec=True)
    @patch("streampipes.functions.streampipes_function.time", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker.get_message", autospec=True)
    @patch("streampipes.functions.broker.nats_broker.NatsBroker.publish_event", autospec=True)
    @patch("streampipes.client.client.Session", autospec=True)
    def test_function_output_stream(
        self,
        http_session: MagicMock,
        pulish_event: MagicMock,
        get_message: MagicMock,
        time: MagicMock,
        *args: Tuple[AsyncMock]
    ):
        http_session_mock = MagicMock()
        http_session_mock.get.return_value.json.return_value = self.data_stream
        http_session.return_value = http_session_mock

        output_events = []

        def save_event(self, event: Dict[str, Any]):
            output_events.append(event)

        pulish_event.side_effect = save_event
        get_message.return_value = TestMessageIterator(self.test_stream_data1)
        time.side_effect = lambda: 0

        client = StreamPipesClient(
            client_config=StreamPipesClientConfig(
                credential_provider=StreamPipesApiKeyCredentials(username="user", api_key="key"),
                host_address="localhost",
            )
        )

        output_stream = create_data_stream("test", attributes={"number": RuntimeType.INTEGER.value})
        test_function = TestFunctionOutput(
            function_definition=FunctionDefinition().add_output_data_stream(output_stream)
        )
        registration = Registration()
        registration.register(test_function)
        function_handler = FunctionHandler(registration, client)
        function_handler.initializeFunctions()

        self.assertEqual(test_function.context.client, client)
        self.assertDictEqual(
            test_function.context.schema, {self.data_stream["elementId"]: DataStream(**self.data_stream)}
        )
        self.assertListEqual(test_function.context.streams, test_function.requiredStreamIds())
        self.assertEqual(test_function.context.function_id, test_function.getFunctionId().id)
        self.assertTrue(test_function.stopped)

        self.assertListEqual(output_events, [{"number": i, "timestamp": 0} for i in range(len(self.test_stream_data1))])
