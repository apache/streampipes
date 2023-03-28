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
import os
from abc import ABC, abstractmethod
from typing import Any, AsyncIterator, Dict

from streampipes.model.resource.data_stream import DataStream


class Broker(ABC):
    """Abstract implementation of a broker.

    A broker allows both to subscribe to a data stream and to publish events to a data stream.
    """

    async def connect(self, data_stream: DataStream) -> None:
        """Connects to the broker running in StreamPipes.

        Parameters
        ----------
        data_stream: DataStream
            Contains the meta information (resources) for a data stream.

        Returns
        -------
        None
        """
        self.stream_id = data_stream.element_id
        transport_protocol = data_stream.event_grounding.transport_protocols[0]
        self.topic_name = transport_protocol.topic_definition.actual_topic_name
        hostname = transport_protocol.broker_hostname
        if "BROKER-HOST" in os.environ.keys():
            hostname = os.environ["BROKER-HOST"]
        await self._makeConnection(hostname, transport_protocol.port)

    @abstractmethod
    async def _makeConnection(self, hostname: str, port: int) -> None:
        """Helper function to connect to a server.

        Parameters
        ----------
        hostname: str
            The hostname of the server, which the broker connects to.
        port: int
            The port number of the connection.

        Returns
        -------
        None
        """
        raise NotImplementedError  # pragma: no cover

    @abstractmethod
    async def createSubscription(self) -> None:
        """Creates a subscription to a data stream.

        Returns
        -------
        None
        """
        raise NotImplementedError  # pragma: no cover

    @abstractmethod
    async def publish_event(self, event: Dict[str, Any]) -> None:
        """Publish an event to a connected data stream.

        Parameters
        ----------
        event: Dict[str, Any]
            The event to be published.

        Returns
        -------
        None
        """
        raise NotImplementedError  # pragma: no cover

    @abstractmethod
    async def disconnect(self) -> None:
        """Closes the connection to the server.

        Returns
        -------
        None
        """
        raise NotImplementedError  # pragma: no cover

    @abstractmethod
    def get_message(self) -> AsyncIterator:
        """Get the published messages of the subscription.

        Returns
        -------
        iterator: AsyncIterator
            An async iterator for the messages.
        """
        raise NotImplementedError  # pragma: no cover
