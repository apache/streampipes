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
Implementation of credential providers.
A credential provider supplies the specified sort of credentials in the appropriate HTTP header format.
The headers are then used by the client to connect to StreamPipes.
"""

from __future__ import annotations

import os
from abc import ABC, abstractmethod
from typing import Dict, Optional

__all__ = [
    "CredentialProvider",
    "StreamPipesApiKeyCredentials",
]


class CredentialProvider(ABC):
    """Abstract implementation of a credential provider.
    Must be inherited by all credential providers.
    """

    def make_headers(self, http_headers: Optional[Dict[str, str]] = None) -> Dict[str, str]:
        """Creates the HTTP headers for the specific credential provider.

        Concrete authentication headers must be defined in the implementation of a credential provider.

        Parameters
        ----------
        http_headers: Optional[Dict[str, str]]
            Additional HTTP headers the generated headers are extended by.

        Returns
        -------
        https_headers: Dict[str, str]
            Dictionary with header information as string key-value pairs. <br>
            Contains all pairs given as parameter plus the header pairs for authentication
            determined by the credential provider.

        """
        if http_headers is None:
            http_headers = {}

        http_headers.update(self._authentication_headers)

        return http_headers

    @property
    @abstractmethod
    def _authentication_headers(self) -> Dict[str, str]:
        """Provides the HTTP headers used for the authentication with the concrete `CredentialProvider`.

        Returns
        -------
        Dictionary with authentication headers as string key-value pairs.

        """
        raise NotImplementedError  # pragma: no cover


class StreamPipesApiKeyCredentials(CredentialProvider):
    """A credential provider that allows authentication via a StreamPipes API Token.

    The required token can be generated via the StreamPipes UI (see the description on our [start-page](../../../).

    Parameters
    ----------
    username: str
        The username to which the API token is granted, e.g., `demo-user@streampipes.apche.org`.
    api_key: str
        The StreamPipes API key as it is displayed in the UI.

    Examples
    --------
    see [StreamPipesClient][streampipes.client.StreamPipesClient]

    """

    @classmethod
    def from_env(cls, username_env: str, api_key_env: str) -> StreamPipesApiKeyCredentials:
        """Returns an api key provider parameterized via environment variables.

        Parameters
        ----------
        username_env: str
            Name of the environment variable that contains the username
        api_key_env: str
            Name of the environment variable that contains the API key

        Returns
        -------
        StreamPipesApiKeyCredentials

        Raises
        ------
        KeyError
            If one of the environment variables is not defined

        """

        username = os.getenv(username_env, None)
        api_key = os.getenv(api_key_env, None)

        if username is None or api_key is None:
            raise KeyError(
                f"Ups, the following environment variables have not been found: "
                f"{'`' + username_env + '`,' if username is None else ''}"
                f"{'`' + api_key_env +'`' if api_key is None else ''}. "  # noqa: W291
                "Please check them to be properly set."
            )

        return cls(username=username, api_key=api_key)

    def __init__(
        self,
        username: str,
        api_key: str,
    ):
        self.username = username
        self.api_key = api_key

    @property
    def _authentication_headers(self) -> Dict[str, str]:
        """Provides the HTTP headers used for the authentication with the API token.

        Returns
        -------
        Dictionary with authentication headers as string key-value pairs.

        """
        return {
            "X-API-User": self.username,
            "X-API-Key": self.api_key,
        }
