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
from abc import ABC, abstractmethod
from enum import Enum
from typing import AsyncIterator

from streampipes.model.resource.data_stream import DataStream


class SupportedBroker(Enum):
    """Enum for the supported brokers."""

    NATS = "nats"


class Broker(ABC):
    """Abstract implementation of a broker.
    A broker is used to subscribe to a data stream and to consume the published events.
    """

    async def connect(self, data_stream: DataStream, host_address: str) -> None:
        """Connects the broker to a server and subscribes to a data stream.

        Parameters
        ----------
         data_stream: DataStream
            Contains the meta information (resources) for a data stream.

        host_address: str
            The host address of the server, which the broker connects to.

        Returns
        -------
        None
        """
        self.stream_id = data_stream.element_id
        transport_protocol = data_stream.event_grounding.transport_protocols[0]
        self.topic_name = transport_protocol.topic_definition.actual_topic_name
        await self._makeConnection(host_address, transport_protocol.port)
        await self._createSubscription()

    @abstractmethod
    async def _makeConnection(self, host_address: str, port: int) -> None:
        """Helper function to connect to a server.

        Parameters
        ----------

        host_address: str
            The host adress of the server, which the broker connects to.

        port: int
            The port number of the connection.

        Returns
        -------
        None
        """
        raise NotImplementedError

    @abstractmethod
    async def _createSubscription(self) -> None:
        """Helper function to create a subscription for a data stream.

        Returns
        -------
        None
        """
        raise NotImplementedError

    @abstractmethod
    async def disconnect(self) -> None:
        """Closes the connection to the server.

        Returns
        -------
        None
        """
        raise NotImplementedError

    @abstractmethod
    def get_message(self) -> AsyncIterator:
        """Get the published messages of the subscription.

        Returns
        -------
        An async iterator for the messages.
        """
        raise NotImplementedError
