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

from streampipes.functions.broker import Broker, NatsBroker
from streampipes.model.resource.data_stream import DataStream


class SupportedBroker(Enum):
    """Enum for the supported brokers."""

    NATS = "NatsTransportProtocol"


# TODO Exception should be removed once all brokers are implemented.
class UnsupportedBroker(Exception):
    """Exception if a broker isn't implemented yet."""

    def __init__(self, message):
        super().__init__(message)


def get_broker(data_stream: DataStream) -> Broker:  # TODO implementation for more transport_protocols
    """Get a broker by a name.

    Parameters
    ----------
    broker_name: str
        A string that represents a broker.

    Returns
    -------
    The broker which belongs to the name.
    """
    broker_name = data_stream.event_grounding.transport_protocols[0].class_name
    if SupportedBroker.NATS.value in broker_name:
        return NatsBroker()
    else:
        raise UnsupportedBroker(f'The python client doesn\'t include the broker: "{broker_name}" yet')
