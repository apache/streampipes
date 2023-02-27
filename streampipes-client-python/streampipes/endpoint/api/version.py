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
Specific implementation of the StreamPipes API's version endpoint.
"""

__all__ = [
    "VersionEndpoint",
]

from typing import Tuple, Type

from streampipes.endpoint import APIEndpoint
from streampipes.model.container import Versions
from streampipes.model.container.resource_container import ResourceContainer
from streampipes.model.resource.resource import Resource


class VersionEndpoint(APIEndpoint):
    """Implementation of the Versions endpoint.

    This endpoint provides metadata about the StreamPipes version of the connected instance.
    It only allows to apply the `get()` method with an empty string as identifier.

    Parameters
    ----------
    parent_client: StreamPipesClient
        The instance of [StreamPipesClient][streampipes.client.StreamPipesClient] the endpoint is attached to.

    Examples
    --------

    >>> from streampipes.client import StreamPipesClient
    >>> from streampipes.client.config import StreamPipesClientConfig
    >>> from streampipes.client.credential_provider import StreamPipesApiKeyCredentials

    >>> client_config = StreamPipesClientConfig(
    ...     credential_provider=StreamPipesApiKeyCredentials(username="test-user", api_key="api-key"),
    ...     host_address="localhost",
    ...     port=8082,
    ...     https_disabled=True
    ... )

    >>> client = StreamPipesClient.create(client_config=client_config)

    >>> client.versionApi.get(identifier="").to_dict(use_source_names=False)
    {'backend_version': '0.92.0-SNAPSHOT'}
    """

    @property
    def _container_cls(self) -> Type[ResourceContainer]:
        """Defines the model container class the endpoint refers to.

        Returns
        -------
        [Versions][streampipes.model.container.Versions]
        """

        return Versions

    @property
    def _relative_api_path(self) -> Tuple[str, ...]:
        """Defines the relative api path to the DataStream endpoint.

        Each path within the URL is defined as an own string.

        Returns
        -------
        api_path: Tuple[str, ...]
            a tuple of strings of which every represents a path value of the endpoint's API URL.
        """

        return "api", "v2", "info", "versions"

    def all(self) -> ResourceContainer:
        """Usually, this method returns information about all resources provided by this endpoint.
        However, this endpoint does not support this kind of operation.

        Raises
        ------
        NotImplementedError
            this endpoint does not return multiple entries, therefore this method is not available

        """
        raise NotImplementedError("The `all()` method is not supported by this endpoint.")

    def get(self, identifier: str, **kwargs) -> Resource:
        """Queries the resource from the API endpoint.

        For this endpoint only one resource is available.

        Parameters
        ----------
        identifier: str
            Not supported by this endpoint, is set to an empty string.

        Returns
        -------
        versions: Version
            The specified resource as an instance of the corresponding model class([Version][streampipes.model.resource.Version]).  # noqa: 501
        """

        return super().get(identifier="")

    def post(self, resource: Resource) -> None:
        """Usually, this method allows to create via this endpoint.
        Since the data represented by this endpoint is immutable, it does not support this kind of operation.

        Raises
        ------
        NotImplementedError
            this endpoint does not allow for POST requests, therefore this method is not available

        """
        raise NotImplementedError("The `post()` method is not supported by this endpoint.")
