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
import json
import logging
from typing import AsyncIterator, Dict, List

from streampipes.client.client import StreamPipesClient
from streampipes.functions.broker import Broker, NatsBroker, SupportedBroker
from streampipes.functions.registration import Registration
from streampipes.functions.utils.async_iter_handler import AsyncIterHandler
from streampipes.functions.utils.data_stream_context import DataStreamContext
from streampipes.functions.utils.function_context import FunctionContext
from streampipes.model.resource.data_stream import DataStream

logger = logging.getLogger(__name__)


# TODO Exception should be removed once all brokers are implemented.
class UnsupportedBroker(Exception):
    """Exception if a broker isn't implemented yet."""

    def __init__(self, message):
        super().__init__(message)


class FunctionHandler:
    """The function handler manages the StreamPipesFunctions.
    It controls the connection to the brokers, starts the functions, manages the broadcast of the live data
    and is able to stop the connection to the brokers and functions.

    Parameters
    ----------
    registration: Registration
        The registration, that contains the StreamPipesFunctions.
    client: StreamPipesClient
        The client to interact with the API.
    """

    def __init__(self, registration: Registration, client: StreamPipesClient) -> None:
        self.registration = registration
        self.client = client
        self.stream_contexts: Dict[str, DataStreamContext] = {}
        self.brokers: List[Broker] = []

    def initializeFunctions(self) -> None:
        """Creates the context for every data stream and starts the event loop to manage the StreamPipesFunctions.

        Returns
        -------
        None
        """
        # Choose the broker and collect the schema for every data stream
        for streampipes_function in self.registration.getFunctions():
            for stream_id in streampipes_function.requiredStreamIds():
                # Get the data stream schema from the API
                data_stream: DataStream = self.client.dataStreamApi.get(stream_id)  # type: ignore
                # Get the broker
                broker = self._get_broker(data_stream.event_grounding.transport_protocols[0].broker_hostname)
                # Assign the functions, broker and schema to every stream
                if stream_id in self.stream_contexts.keys():
                    self.stream_contexts[stream_id].add_function(streampipes_function)
                else:
                    self.stream_contexts[stream_id] = DataStreamContext(
                        functions=[streampipes_function], schema=data_stream, broker=broker
                    )
                logger.info(f"Using {broker.__class__.__name__} for {streampipes_function}")

        # Start the function loop or add it as tasks if a loop is already running
        try:
            loop = asyncio.get_running_loop()
        except RuntimeError:
            asyncio.run(self._function_loop())
        else:
            loop.create_task(self._function_loop())

    def _get_broker(self, broker_name: str) -> Broker:  # TODO implementation for more transport_protocols
        """Get a broker by a name.

        Parameters
        ----------
        broker_name: str
            A string that represents a broker.

        Returns
        -------
        The broker which belongs to the name.
        """
        if broker_name == SupportedBroker.NATS.value:
            return NatsBroker()
        else:
            raise UnsupportedBroker(f'The python client doesn\'t include the broker: "{broker_name}" yet')

    async def _function_loop(self) -> None:
        """Loops through all messages and sends them to the functions until the function handler gets stopped.

        Returns
        -------
        None
        """
        messages: Dict[str, AsyncIterator] = dict()
        contexts: Dict[str, FunctionContext] = dict()

        for stream_id in self.stream_contexts.keys():
            data_stream = self.stream_contexts[stream_id].schema
            broker = self.stream_contexts[stream_id].broker
            # Connect the broker
            await broker.connect(data_stream, self.client.client_config.host_address)
            self.brokers.append(broker)
            # Get the messages
            messages[stream_id] = broker.get_message()
            # Generate the function context
            for streampipes_function in self.stream_contexts[stream_id].functions:
                function_id = streampipes_function.getFunctionId().id
                if function_id in contexts.keys():
                    contexts[function_id].add_data_stream_schema(stream_id, data_stream)
                else:
                    contexts[function_id] = FunctionContext(
                        function_id,
                        schema={stream_id: data_stream},
                        client=self.client,
                        streams=streampipes_function.requiredStreamIds(),
                    )
        # Start the functions
        for streampipes_function in self.registration.getFunctions():
            streampipes_function.onServiceStarted(contexts[streampipes_function.getFunctionId().id])

        # Get the messages continuously and send them to the functions
        async for stream_id, msg in AsyncIterHandler.combine_async_messages(messages):
            if stream_id == "stop":
                break
            for streampipes_function in self.stream_contexts[stream_id].functions:
                streampipes_function.onEvent(json.loads(msg.data.decode()), stream_id)

        # Stop the functions
        self._stop_functions()

    def _stop_functions(self) -> None:
        """Helper function to stop the StreamPipesFunctions.

        Returns
        -------
        None
        """
        for streampipes_function in self.registration.getFunctions():
            streampipes_function.onServiceStopped()

    def force_stop_functions(self) -> None:
        """Stops the StreamPipesFunctions when the event loop was stopped without stopping the functions.

        Returns
        -------
        None

        Raises
        ------
        UserWarning
            If there is a running event loop and the functions should be stopped by disconnecting from the broker.
        """
        try:
            asyncio.get_running_loop()
        except RuntimeError:
            self._stop_functions()
        else:
            raise UserWarning(
                "Don't stop the functions when the event loop is running. Use FunctionHandler().disconnect() instead"
            )

    def disconnect(self) -> None:
        """Disconnects from the brokers and stops the functions.

        Returns
        -------
        None
        """
        asyncio.get_event_loop().create_task(self._disconnect())

    async def _disconnect(self) -> None:
        """Helper function to disconnect from the brokers.

        Returns
        -------
        None
        """
        for broker in self.brokers:
            await broker.disconnect()
