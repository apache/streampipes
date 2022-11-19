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
Specific implementation of the StreamPipes API's data lake measure endpoints.
This endpoint allows to consume data stored in StreamPipes' data lake
"""
from typing import Tuple, Type

from streampipes_client.endpoint.endpoint import APIEndpoint
from streampipes_client.model.container import DataLakeMeasures
from streampipes_client.model.container.resource_container import ResourceContainer
from streampipes_client.model.resource.data_lake_series import DataLakeSeries

__all__ = [
    "DataLakeMeasureEndpoint",
]


class DataLakeMeasureEndpoint(APIEndpoint):
    """Implementation of the DataLakeMeasure endpoint.
    This endpoint provides an interfact to all data stored in the StreamPipes data lake.

    Consequently, it allows uerying metadata about available data sets (see `all()` method).
    The metadata is returned as an instance of `model.container.DataLakeMeasures`.

    In addition, the endpoint provides direct access to the data stored in the data laka by querying a
    specific data lake measure using the `get()` method.

    Parameters
    ----------
    parent_client: StreamPipesClient
        The instance of `client.StreamPipesClient` the endpoint is attached to.

    Examples
    --------

    >>> from streampipes_client.client import StreamPipesClient
    >>> from streampipes_client.client.client_config import StreamPipesClientConfig
    >>> from streampipes_client.client.credential_provider import StreamPipesApiKeyCredentials

    >>> client_config = StreamPipesClientConfig(
    ...     credential_provider=StreamPipesApiKeyCredentials(username="test-user", api_key="api-key"),
    ...     host_address="localhost",
    ...     port=8082,
    ...     https_disabled=True
    ... )

    >>> client = StreamPipesClient.create(client_config=client_config)

    >>> data_lake_measures = client.dataLakeMeasureApi.all()

    >>> len(data_lake_measures)
    5
    """

    @property
    def _resource_cls(self) -> Type[DataLakeSeries]:
        """
        Additional reference to resource class.
        This endpoint deviates from the desired relationship
        that the resource class of the resource container is
        the return type of the get endpoint.
        Therefore, this is only a temporary implementation and will be removed soon.
        """
        return DataLakeSeries

    @property
    def _container_cls(self) -> Type[ResourceContainer]:
        """Defines the model container class the endpoint refers to.


        Returns
        -------
        `model.container.DataLakeMeasures`
        """
        return DataLakeMeasures

    @property
    def _relative_api_path(self) -> Tuple[str, ...]:
        """Defines the relative api path to the DataLakeMeasurement endpoint.
        Each path within the URL is defined as an own string.

        Returns
        -------
        A tuple of strings of which every represents a path value of the endpoint's API URL.
        """

        return "api", "v4", "datalake", "measurements"

    def get(self, identifier: str) -> DataLakeSeries:
        """Queries the specified data lake measure from the API.

        Parameters
        ----------
        identifier: str
            The identifier of the data lake measure to be queried.

        Returns
        -------
        The specified data lake measure as an instance of the corresponding model class (`model.DataLakeSeries`).
        """

        response = self._make_request(
            request_method=self._parent_client.request_session.get, url=f"{self.build_url()}/{identifier}"
        )
        return self._resource_cls.from_json(json_string=response.text)
