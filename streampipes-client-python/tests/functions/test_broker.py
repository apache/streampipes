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
import asyncio
from unittest import TestCase

from nats import connect
from streampipes_client.functions.broker.nats_broker import NatsBroker
from streampipes_client.functions.utils.async_iter_handler import AsyncIterHandler
from streampipes_client.model.common import (
    EventGrounding,
    TopicDefinition,
    TransportProtocol,
)
from streampipes_client.model.resource.data_stream import DataStream


def create_data_stream(element_id: str, hostname: str, port: int, topic_name: str) -> DataStream:
    return DataStream(
        elementId=element_id,
        eventGrounding=EventGrounding(
            transportProtocols=[
                TransportProtocol(
                    port=port, brokerHostname=hostname, topicDefinition=TopicDefinition(actualTopicName=topic_name)
                )
            ]
        ),
    )


class TestStreamPipesFunctions(TestCase):
    def test_nats_broker_one_data_stream(self):
        asyncio.run(self._test_nats_broker())

    async def _test_nats_broker(self):
        test_messages = [b"Hello!", b"test1", b"test2", b"Bye!"]
        received_messages = []
        broker = NatsBroker()
        data_stream = create_data_stream("test_stream", "nats", 4222, "test")

        host_address = "demo.nats.io"
        await broker.connect(data_stream, host_address)
        nats_client = await connect(host_address)
        for msg in test_messages:
            await nats_client.publish(
                data_stream.event_grounding.transport_protocols[0].topic_definition.actual_topic_name, msg
            )

        i = 0
        messages = {data_stream.element_id: broker.get_message()}
        async for stream_id, msg in AsyncIterHandler.combine_async_messages(messages):
            if stream_id == "stop":
                break
            received_messages.append(msg.data)
            self.assertEqual(stream_id, "test_stream")
            if i >= len(test_messages) - 1:
                await broker.disconnect()
            i += 1
        await nats_client.close()

        self.assertListEqual(received_messages, test_messages)

    def test_nats_broker_multiple_data_streams(self):
        asyncio.run(self._test_nats_broker_multiple_data_streams())

    async def _test_nats_broker_multiple_data_streams(self):
        test_messages = {
            "test_stream1": [b"Hello!", b"test1", b"test2", b"Bye!"],
            "test_stream2": [b"Start", b"1", b"2", b"3", b"Stop!"],
            "test_stream3": [b"0", b"1", b"2"],
            "test_stream4": [b"test"],
        }
        received_messages = {stream_id: [] for stream_id in test_messages.keys()}
        data_streams = [
            create_data_stream(f"test_stream{i+1}", "nats", 4222, f"test{i+1}") for i in range(len(test_messages))
        ]
        brokers = {stream_id: NatsBroker() for stream_id in test_messages.keys()}

        host_address = "demo.nats.io"
        nats_client = await connect(host_address)
        for data_stream in data_streams:
            await brokers[data_stream.element_id].connect(data_stream, host_address)
        for data_stream in data_streams:
            for msg in test_messages[data_stream.element_id]:
                await nats_client.publish(
                    data_stream.event_grounding.transport_protocols[0].topic_definition.actual_topic_name, msg
                )

        i = 0
        messages = {
            data_stream.element_id: brokers[data_stream.element_id].get_message() for data_stream in data_streams
        }
        n_messages = sum([len(test_message) for test_message in test_messages.values()])
        async for stream_id, msg in AsyncIterHandler.combine_async_messages(messages):
            if stream_id == "stop":
                break
            received_messages[stream_id].append(msg.data)
            if i >= n_messages - 1:
                for broker in brokers.values():
                    await broker.disconnect()
            i += 1
        await nats_client.close()

        self.assertDictEqual(received_messages, test_messages)
