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
from typing import Any, Coroutine, Dict

from streampipes.functions.broker import Publisher, get_broker
from streampipes.model.resource.data_stream import DataStream


class OutputCollector:
    """Collector for output events. The events are published to an output data stream.
    Therefore, the output collector establishes a connection to the broker.

    Parameters
    ----------
    data_stream: DataStream
        The output data stream that will receive the events.

    Attributes
    ----------
    publisher: Publisher
        The publisher instance that sends the data to StreamPipes

    """

    def __init__(self, data_stream: DataStream) -> None:
        self.publisher: Publisher = get_broker(data_stream, is_publisher=True)  # type: ignore
        self._run_coroutine(self.publisher.connect(data_stream))

    def collect(self, event: Dict[str, Any]) -> None:
        """Publishes an event to the output stream.

        Parameters
        ----------
        event: Dict[str, Any]
            The event to be published.

        Returns
        -------
        None
        """
        self._run_coroutine(self.publisher.publish_event(event))

    def disconnect(self) -> None:
        """Disconnects the broker of the output collector.

        Returns
        -------
        None
        """
        self._run_coroutine(self.publisher.disconnect())

    @staticmethod
    def _run_coroutine(coroutine: Coroutine) -> None:
        """Run a coroutine in the event loop or create a new one if there is a running event loop.

        Parameters
        ----------
        coroutine: Coroutine
            The coroutine to run.
        """
        try:
            loop = asyncio.get_running_loop()
        except RuntimeError:
            asyncio.run(coroutine)
        else:
            loop.create_task(coroutine)
