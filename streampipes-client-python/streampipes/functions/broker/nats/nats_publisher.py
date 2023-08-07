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
import json
import logging
from typing import Any, Dict

from nats import connect

from streampipes.functions.broker import Publisher

logger = logging.getLogger(__name__)


class NatsPublisher(Publisher):
    """Implementation of a publisher for NATS"""

    async def _make_connection(self, hostname: str, port: int) -> None:
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
        self.nats_client = await connect([f"nats://{hostname}:{port}"])
        logger.info(f"Connecting to NATS at {hostname}:{port}")

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
        await self.nats_client.publish(subject=self.topic_name, payload=json.dumps(event).encode("utf-8"))

    async def disconnect(self) -> None:
        """Closes the connection to the server.

        Returns
        -------
        None
        """
        await self.nats_client.close()
        logger.info(f"Stopped connection to stream: {self.stream_id}")
