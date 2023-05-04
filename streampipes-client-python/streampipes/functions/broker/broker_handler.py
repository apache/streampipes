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
from enum import Enum

from streampipes.functions.broker import (
    Broker,
    KafkaConsumer,
    KafkaPublisher,
    NatsConsumer,
    NatsPublisher,
)
from streampipes.model.resource.data_stream import DataStream


class SupportedBroker(Enum):
    """Enum for the supported brokers."""

    NATS = "NatsTransportProtocol"
    KAFKA = "KafkaTransportProtocol"


# TODO Exception should be removed once all brokers are implemented.
class UnsupportedBrokerError(Exception):
    """Exception if a broker isn't implemented yet."""

    def __init__(self, broker_name: str):
        super().__init__(f'The python client doesn\'t support the broker: "{broker_name}" yet')


def get_broker(
    data_stream: DataStream, is_publisher: bool = False
) -> Broker:  # TODO implementation for more transport_protocols
    """Derive the broker for the given data stream.

    Parameters
    ----------
    data_stream: DataStream
        Data stream instance from which the broker is inferred

    Returns
    -------
    broker: Broker
        The corresponding broker instance derived from data stream.

    Raises
    ------
    UnsupportedBrokerError
        Is raised when the given data stream belongs to a broker that is currently not supported by StreamPipes Python.
    """
    broker_name = data_stream.event_grounding.transport_protocols[0].class_name
    if SupportedBroker.NATS.value in broker_name:
        if is_publisher:
            return NatsPublisher()
        return NatsConsumer()
    elif SupportedBroker.KAFKA.value in broker_name:
        if is_publisher:
            return KafkaPublisher()
        return KafkaConsumer()
    else:
        raise UnsupportedBrokerError(broker_name)


def get_broker_description(data_stream: DataStream) -> SupportedBroker:
    """Derive the decription of the broker for the given data stream.

    Parameters
    ----------
    data_stream: DataStream
        Data stream instance from which the broker is inferred

    Returns
    -------
    broker: SupportedBroker
        The corresponding broker description derived from data stream.

    Raises
    ------
    UnsupportedBrokerError
        Is raised when the given data stream belongs to a broker that is currently not supported by StreamPipes Python.
    """
    broker_name = data_stream.event_grounding.transport_protocols[0].class_name
    for b in SupportedBroker:
        if b.value in broker_name:
            return b
    raise UnsupportedBrokerError(broker_name)
