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
from typing import Any, Dict

from streampipes.functions.broker import Broker


class Publisher(Broker):
    """Abstract implementation of a publisher for a broker.

    A publisher allows to publish events to a data stream.
    """

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
