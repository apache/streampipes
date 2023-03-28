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

import logging
from typing import Any, AsyncIterator, Dict

from confluent_kafka import Consumer  # type: ignore
from streampipes.functions.broker.broker import Broker
from streampipes.functions.broker.kafka_message_fetcher import KafkaMessageFetcher
from streampipes.model.common import random_letters

logger = logging.getLogger(__name__)


class KafkaBroker(Broker):
    """Implementation of the broker for Kafka"""

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
        self.kafka_consumer = Consumer(
            {"bootstrap.servers": f"{hostname}:{port}", "group.id": random_letters(6), "auto.offset.reset": "latest"}
        )

    async def createSubscription(self) -> None:
        """Creates a subscription to a data stream.

        Returns
        -------
        None
        """
        self.kafka_consumer.subscribe([self.topic_name])

        logger.info(f"Subscribed to stream: {self.stream_id}")

    async def publish_event(self, event: Dict[str, Any]):
        """Publish an event to a connected data stream.

        Parameters
        ----------
        event: Dict[str, Any]
            The event to be published.

        Returns
        -------
        None
        """

        # await self.publish(subject=self.topic_name, payload=json.dumps(event).encode("utf-8"))

    async def disconnect(self) -> None:
        """Closes the connection to the server.

        Returns
        -------
        None
        """
        self.kafka_consumer.close()
        logger.info(f"Stopped connection to stream: {self.stream_id}")

    def get_message(self) -> AsyncIterator:
        """Get the published messages of the subscription.

        Returns
        -------
        iterator: AsyncIterator
            An async iterator for the messages.
        """

        return KafkaMessageFetcher(self.kafka_consumer)
