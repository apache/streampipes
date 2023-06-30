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
from abc import abstractmethod
from typing import AsyncIterator

from streampipes.functions.broker import Broker
from streampipes.model.resource.data_stream import DataStream


class Consumer(Broker):
    """Abstract implementation a consumer for a broker.

    A consumer allows to subscribe to a data stream.
    """

    async def connect(self, data_stream: DataStream) -> None:
        """Connects to the broker running in StreamPipes and creates a subscription.

        Parameters
        ----------
        data_stream: DataStream
            Contains the meta information (resources) for a data stream.

        Returns
        -------
        None
        """
        await super().connect(data_stream)
        await self._create_subscription()

    @abstractmethod
    async def _create_subscription(self) -> None:
        """Creates a subscription to a data stream.

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
