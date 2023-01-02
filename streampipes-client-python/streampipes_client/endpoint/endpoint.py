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
General implementation for an endpoint.
Provided classes and assets are aimed to be used for developing endpoints.
An endpoint is provides all options to communicate with a central endpoint of the StreamPipes API in a handy way.
"""

import logging
from abc import ABC, abstractmethod
from http import HTTPStatus
from typing import Callable, Tuple, Type

from requests import Response
from requests.exceptions import HTTPError

__all__ = [
    "APIEndpoint",
]

from streampipes_client.model.container.resource_container import ResourceContainer
from streampipes_client.model.resource.resource import Resource

logger = logging.getLogger(__name__)

# define custom logging messages for some specific HTTP status
_error_code_to_message = {
    401: "\nThe StreamPipes Backend returned an unauthorized error.\n"
    "Please check your user name and/or password to be correct.",
    403: "\nThere seems to be an issue with the access rights of the given user and the resource you queried.\n"
    "Apparently, this user is not allowed to query the resource.\n"
    "Please check the user's permissions or contact your StreamPipes admin.",
    **dict.fromkeys(
        [404, 405],
        "\nOops, there seems to be an issue with the Python Client calling the API inappropriately.\n"
        "This should not happen, but unfortunately did.\n"
        "If you don't mind, it would be awesome to let us know by creating an issue"
        " at github.com/apache/streampipes.\n"
        "Please paste the following information to the issue description:\n\n",
    ),
}


class Endpoint(ABC):
    """Abstract implementation of an StreamPipes endpoint.
    Serves as template for all endpoints used for interaction with a StreamPipes instance.
    By design, endpoints are only instantiated within the `__init__` method of the StreamPipesClient.

    Parameters
    ----------
    parent_client: StreamPipesClient
        This parameter expects the instance of the `client.StreamPipesClient` the endpoint is attached to.

    """

    def __init__(self, parent_client: "StreamPipesClient"):  # type: ignore # noqa: F821
        self._parent_client = parent_client

    @property
    @abstractmethod
    def _container_cls(self) -> Type[ResourceContainer]:
        """Defines the model container class the endpoint refers to.
        This model container class corresponds to the Python data model,
        which handles multiple resources returned from the endpoint.

        Returns
        -------
        The corresponding container class from the data model,
        needs to a subclass of `model.container.ResourceContainer`.
        """
        raise NotImplementedError  # pragma: no cover


class APIEndpoint(Endpoint):
    """Abstract implementation of an API endpoint.
    Serves as template for all endpoints for the StreamPipes API.
    By design, endpoints are only instantiated within the `__init__` method of the StreamPipesClient.

    Parameters
    ----------
    parent_client: StreamPipesClient
        This parameter expects the instance of the `client.StreamPipesClient` the endpoint is attached to.
    """

    @property
    @abstractmethod
    def _relative_api_path(self) -> Tuple[str, ...]:
        """Defines the relative api path with regard to the StreamPipes API URL.
        Each path within the URL is defined as an own string.

        Returns
        -------
        A tuple of strings of which every represents a path value of the endpoint's API URL.

        """
        raise NotImplementedError  # pragma: no cover

    @staticmethod
    def _make_request(
        request_method: Callable[..., Response],
        url: str,
    ) -> Response:
        """Helper method to send requests to the StreamPipes API endpoint.
        Should be used from methods of this class that interacts with the API, e.g. `all()` and `get()`.

        Parameters
        ----------
        request_method: Callable[..., Response]
            The HTTP method with which to submit the request.
            Must be one of HTTP methods provided by the `requests` library, e.g. `requests.get`.
        url: str
            The full URL to which the request should be applied.

        Returns
        -------
        An HTTP response, which is of type `requests.Response` and
        contains both the actual API response and some metadata.
        Returned only if the request was successful,
        otherwise it raises an exception (see `Raises`).

        Raises
        ------
        requests.exceptions.HTTPError
            If the HTTP status code of the error is between `400` and `600`.
        """

        response = request_method(url=url)

        # check if the API request was successful
        try:
            response.raise_for_status()
        except HTTPError as err:

            status_code = err.response.status_code

            # get custom error message based on the returned status code
            error_message = _error_code_to_message[status_code]

            if status_code in [
                HTTPStatus.METHOD_NOT_ALLOWED.numerator,
                HTTPStatus.NOT_FOUND.numerator,
            ]:
                error_message += f"url: {err.response.url}\nstatus code: {status_code}"

            logger.debug(f"HTTP error response: {err.response.text}")
            raise HTTPError(error_message) from err

        else:
            logger.debug("Successfully retrieved resources from %s.", url)
            logger.info("Successfully retrieved all resources.")

        return response

    def build_url(self) -> str:
        """Creates the URL of the API path for the endpoint.

        Returns
        -------
        The URL of the Endpoint
        """
        return f"{self._parent_client.base_api_path}" f"{'/'.join(api_path for api_path in self._relative_api_path)}"

    def all(self) -> ResourceContainer:
        """Get all resources of this endpoint provided by the StreamPipes API.
        Results are provided as an instance of a `model.container.ResourceContainer` that
        allows to handle the returned resources in a comfortable and pythonic way.

        Returns
        -------
        A model container instance (`model.container.ResourceContainer`) bundling the resources returned.
        """

        response = self._make_request(
            request_method=self._parent_client.request_session.get,
            url=self.build_url(),
        )
        return self._container_cls.from_json(json_string=response.text)

    def get(self, identifier: str) -> Resource:
        """Queries the specified resource from the API endpoint.

        Parameters
        ----------
        identifier: str
            The identifier of the resource to be queried.

        Returns
        -------
        The specified resource as an instance of the corresponding model class (`model.Resource`).
        """

        response = self._make_request(
            request_method=self._parent_client.request_session.get, url=f"{self.build_url()}/{identifier}"
        )

        return self._container_cls._resource_cls()(**response.json())
