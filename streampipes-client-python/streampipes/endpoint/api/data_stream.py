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

"""
Specific implementation of the StreamPipes API's data stream endpoints.
"""

__all__ = [
    "DataStreamEndpoint",
]
from typing import Tuple, Type

from streampipes.endpoint.endpoint import APIEndpoint
from streampipes.model.container import DataStreams
from streampipes.model.container.resource_container import ResourceContainer


class DataStreamEndpoint(APIEndpoint):
    """Implementation of the DataStream endpoint.

    Consequently, it allows querying metadata about available data streams (see `all()` method).
    The metadata is returned as an instance of [`DataStreams`][streampipes.model.container.DataStreams].

    Examples
    --------

    ```python
    from streampipes.client import StreamPipesClient
    from streampipes.client.config import StreamPipesClientConfig
    from streampipes.client.credential_provider import StreamPipesApiKeyCredentials

    client_config = StreamPipesClientConfig(
        credential_provider=StreamPipesApiKeyCredentials(username="test-user", api_key="api-key"),
        host_address="localhost",
        port=8082,
        https_disabled=True
    )
    client = StreamPipesClient.create(client_config=client_config)
    ```

    ```python
    # let's get all existing data streams in StreamPipes
    data_streams = client.dataStreamApi.all()
    len(data_streams)
    ```
    ```
    2
    ```

    """

    @property
    def _container_cls(self) -> Type[ResourceContainer]:
        """Defines the model container class the endpoint refers to.


        Returns
        -------
        `model.container.DataStreams`
        """
        return DataStreams

    @property
    def _relative_api_path(self) -> Tuple[str, ...]:
        """Defines the relative api path to the DataStream endpoint.
        Each path within the URL is defined as an own string.

        Returns
        -------
        A tuple of strings of which every represents a path value of the endpoint's API URL.
        """

        return "api", "v2", "streams"
