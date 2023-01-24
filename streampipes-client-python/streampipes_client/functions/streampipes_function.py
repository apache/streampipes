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
from typing import Any, Dict, List

from streampipes_client.functions.utils.function_config import FunctionConfig
from streampipes_client.functions.utils.function_context import FunctionContext


class StreamPipesFunction(ABC):
    """Abstract implementation of a StreamPipesFunction.
    A StreamPipesFunction allows users to get the data of a StreamPipes data streams easily.
    It makes it possible to work with the live data in python and enabels to use the powerful
    data analytics libaries there.
    """

    @abstractmethod
    def getFunctionConfig(self) -> FunctionConfig:
        """Get the id of the function.

        Returns
        -------
        FunctionConfig which contains the function id, version number and output streams
        """
        raise NotImplementedError

    @abstractmethod
    def requiredStreamIds(self) -> List[str]:
        """Get the ids of the streams needed by the function.

        Returns
        -------
        List of the stream ids
        """
        raise NotImplementedError

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
        raise NotImplementedError

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
        raise NotImplementedError

    @abstractmethod
    def onServiceStopped(self) -> None:
        """Is called when the function gets stopped.

        Returns
        -------
        None
        """
        raise NotImplementedError
