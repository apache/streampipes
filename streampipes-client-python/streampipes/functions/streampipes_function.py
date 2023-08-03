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
from time import time
from typing import Any, Dict, List, Optional

from streampipes.functions.broker.output_collector import OutputCollector
from streampipes.functions.utils.function_context import FunctionContext
from streampipes.model.resource import FunctionDefinition
from streampipes.model.resource.function_definition import FunctionId


class StreamPipesFunction(ABC):
    """Abstract implementation of a StreamPipesFunction.

    A StreamPipesFunction allows users to get the data of a StreamPipes data streams easily.
    It makes it possible to work with the live data in python and enables to use the powerful
    data analytics libraries there.

    Parameters
    ----------
    function_definition: FunctionDefinition
        the definition of the function that contains metadata about the connected function

    Attributes
    ----------
    output_collectors: Dict[str, OutputCollector]
        List of all output collectors which are created based on the provided function definitions.
    """

    def __init__(self, function_definition: Optional[FunctionDefinition] = None):
        self.function_definition = function_definition or FunctionDefinition()
        self.output_collectors = {
            stream_id: OutputCollector(data_stream)
            for stream_id, data_stream in self.function_definition.output_data_streams.items()
        }

    def add_output(self, stream_id: str, event: Dict[str, Any]):
        """Send an event via an output data stream to StreamPipes

        Parameters
        ----------
        stream_id: str
            The id of the output data stream
        event: Dict[str, Any]
            The event which should be sent

        Returns
        -------
        None
        """
        event["timestamp"] = int(1000 * time())
        self.output_collectors[stream_id].collect(event)

    def getFunctionId(self) -> FunctionId:
        """Returns the id of the function.

        Returns
        -------
        function_id: FunctionId
            Identification object of the StreamPipes function
        """
        return self.function_definition.function_id

    def stop(self) -> None:
        """Stops the function and disconnects from the output streams"""

        for collector in self.output_collectors.values():
            collector.disconnect()
        self.onServiceStopped()

    def requiredStreamIds(self) -> List[str]:
        """Get the ids of the streams needed by the function.

        Returns
        -------
        stream_ids: List[str]
            List of the stream ids
        """
        return self.function_definition.consumed_streams

    @abstractmethod
    def onServiceStarted(self, context: FunctionContext) -> None:
        """Is called when the function gets started.

        Parameters
        ----------
        context: FunctionContext
            The context in which the function gets started.

        Returns
        -------
        None
        """
        raise NotImplementedError  # pragma: no cover

    @abstractmethod
    def onEvent(self, event: Dict[str, Any], streamId: str) -> None:
        """Is called for every event of a data stream.

        Parameters
        ----------
        event: Dict[str, Any]
            The received event from the data stream.
        streamId: str
            The id of the data stream which the event belongs to.

        Returns
        -------
        None
        """
        raise NotImplementedError  # pragma: no cover

    @abstractmethod
    def onServiceStopped(self) -> None:
        """Is called when the function gets stopped.

        Returns
        -------
        None
        """
        raise NotImplementedError  # pragma: no cover
