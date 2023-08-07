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
Implementation of the StreamPipes client.
The client is designed as the central point of interaction with the StreamPipes API and
provides all functionalities to communicate with the API.
"""

from __future__ import annotations

import logging
import sys
from typing import Dict, Optional

from requests import Session

from streampipes.client.config import StreamPipesClientConfig
from streampipes.endpoint import APIEndpoint
from streampipes.endpoint.api import (
    DataLakeMeasureEndpoint,
    DataStreamEndpoint,
    VersionEndpoint,
)

logger = logging.getLogger(__name__)


class StreamPipesClient:
    """The client to connect to StreamPipes.

    This is the central point of contact with StreamPipes and
    provides all the functionalities to interact with it.

    The client provides so-called "endpoints" each of which refers to
    an endpoint of the StreamPipes API, e.g. `.dataLakeMeasureApi`.
    An [endpoint][streampipes.endpoint.endpoint] provides the actual methods to interact with StreamPipes
    API.

    Parameters
    ----------
    client_config: StreamPipesClientConfig
        Configures the client to connect properly to the StreamPipes instance.
    logging_level: Optional[int]
        Influences the log messages emitted by the `StreamPipesClient`

    Attributes
    ----------
    dataLakeMeasureApi: DataLakeMeasureEndpoint
        Instance of the data lake measure endpoint
    dataStreamApi: DataStreamEndpoint
        Instance of the data stream endpoint

    Examples
    --------

    ```python
    from streampipes.client import StreamPipesClient
    from streampipes.client.config import StreamPipesClientConfig
    from streampipes.client.credential_provider import StreamPipesApiKeyCredentials
    ```

    ```python
    client_config = StreamPipesClientConfig(
        credential_provider=StreamPipesApiKeyCredentials(
             username="test-user",
             api_key="api-key"
         ),
         host_address="localhost",
         https_disabled=True
    )
    ```

    The following way of instantiating a client instance is
    intended to be consistent with the StreamPipes Java client.
    ```python
    client = StreamPipesClient.create(client_config=client_config)
    ```

    If you prefer a more pythonic way, you can simply write:
    ```python
    client = StreamPipesClient(client_config=client_config)
    ```

    To interact with an endpoint:
    ```python
    data_lake_measures = client.dataLakeMeasureApi.all()
    ```

    To inspect returned data as a pandas dataframe:
    ```python
    data_lake_measures.to_pandas()
    #
    #     measure_name timestamp_field  ... pipeline_is_running num_event_properties
    # 0           test   s0::timestamp  ...               False                    2
    # [1 rows x 6 columns]
    ```

    """

    def __init__(
        self,
        client_config: StreamPipesClientConfig,
        logging_level: Optional[int] = logging.INFO,
    ):
        self.client_config = client_config

        # set up a requests session
        # this allows to centrally determine the behavior of all requests made
        self.request_session = Session()
        self.request_session.headers.update(self.http_headers)

        self.logging_level = logging_level
        self._set_up_logging(logging_level=self.logging_level)  # type: ignore

        # provide all available endpoints here
        # name of the endpoint needs to be consistent with the Java client
        self.dataLakeMeasureApi = DataLakeMeasureEndpoint(parent_client=self)
        self.dataStreamApi = DataStreamEndpoint(parent_client=self)
        self.versionApi = VersionEndpoint(parent_client=self)

        self.server_version = self._get_server_version()

    def _get_server_version(self) -> str:
        """Connects to the StreamPipes server and retrieves its version.

        In addition to querying the server version, this method implicitly checks the specified credentials.

        Returns
        -------
        sp_version: str
            version of the connected StreamPipes instance

        """

        # retrieve metadata from the API via the Streampipes server
        # as a side effect, the specified credentials are also tested to ensure that authentication is successful.
        version_dict = self.versionApi.get(identifier="").to_dict(use_source_names=False)

        # remove SNAPSHOT-suffix if present
        sp_version = version_dict["backend_version"].replace("-SNAPSHOT", "")

        logger.info(
            "The StreamPipes version was successfully retrieved from the backend: %s. "
            "By means of that, authentication via the provided credentials is also tested successfully.",
            sp_version,
        )

        return sp_version

    @staticmethod
    def _set_up_logging(logging_level: int) -> None:
        """Configures the logging behavior of the `StreamPipesClient`.

        Parameters
        ----------
        logging_level: Optional[int]
            Influences the log messages emitted by the `StreamPipesClient`.

        Returns
        -------
        None
        """
        logging.basicConfig(
            level=logging_level,
            stream=sys.stdout,
            format="%(asctime)s - %(name)s - [%(levelname)s] - [%(filename)s:%(lineno)d] [%(funcName)s] - %(message)s",
        )

        logger.info(f"Logging successfully initialized with logging level {logging.getLevelName(logging_level)}.")

    @classmethod
    def create(
        cls,
        client_config: StreamPipesClientConfig,
        logging_level: int = logging.INFO,
    ) -> StreamPipesClient:
        """Returns an instance of the `StreamPipesPythonClient`.

        Provides consistency to the StreamPipes Java client.

        Parameters
        ----------
        client_config: StreamPipesClientConfig
            Configures the client to connect properly to the StreamPipes instance.
        logging_level: Optional[int]
            Influences the log messages emitted by the `StreamPipesClient`.

        Returns
        -------
        StreamPipesClient
        """
        return cls(client_config=client_config, logging_level=logging_level)

    @property
    def http_headers(self) -> Dict[str, str]:
        """Returns the HTTP headers used for all requests.

        The HTTP headers are composed of the authentication headers supplied by the credential
        provider and additional required headers (currently this is only the application header).

        Returns
        -------
        http_headers: Dict[str, str]
            header information for HTTP requests as string key-value pairs.
        """

        # create HTTP headers from credential provider and add additional headers needed
        return self.client_config.credential_provider.make_headers(
            {"Application": "application/json"},
        )

    @property
    def base_api_path(self) -> str:
        """Constructs the basic API URL from the given `client_config`.

        Returns
        -------
        base_api_path: str
            basic API path of the connected StreamPipes instance
        """
        return (
            f"{'http://' if self.client_config.https_disabled else 'https://'}"
            f"{self.client_config.host_address}:"
            f"{self.client_config.port}/streampipes-backend/"
        )

    def describe(self) -> None:
        """Prints a short description of the connected StreamPipes instance and the available resources to the console.

        Returns
        -------
            None

        Examples
        --------

        ```python
        client.describe()
        ```
        Output:
        ```
        Hi there!
        You are connected to a StreamPipes instance running at http://localhost:80.
        The following StreamPipes resources are available with this client:
        6x DataStreams
        1x DataLakeMeasures
        ```
        """

        # get all endpoints of this client
        available_endpoints = {
            attr_name for attr_name in dir(self) if isinstance(self.__getattribute__(attr_name), APIEndpoint)
        }

        # remove endpoints that are not suitable for the describe method
        # this is mainly due to not providing the `all()` method
        available_endpoints = available_endpoints.symmetric_difference(
            {
                "versionApi",
            }
        )

        # ensure deterministic order
        ordered_available_endpoints = sorted(available_endpoints)

        # collect the number of available resources per endpoint
        endpoint_stats = {
            (all_items := self.__getattribute__(endpoint_name).all()).__class__.__name__: len(all_items)
            for endpoint_name in ordered_available_endpoints
        }

        # sort the endpoints descending based on the number of resources
        sorted_endpoint_stats = {
            key: val for key, val in sorted(endpoint_stats.items(), key=lambda item: item[1], reverse=True)
        }

        base_message = (
            f"\nHi there!\n"
            f"You are connected to a StreamPipes instance running at "
            f"{'http://' if self.client_config.https_disabled else 'https://'}"
            f"{self.client_config.host_address}:{self.client_config.port} with version {self.server_version}.\n"
            f"The following StreamPipes resources are available with this client:\n"
        )

        endpoint_stats_message = "\n".join(f"{count}x {name}" for name, count in sorted_endpoint_stats.items())

        logger.info(base_message + endpoint_stats_message)
