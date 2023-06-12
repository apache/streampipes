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

from typing_extensions import deprecated


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

    Both parameters can either be passed as arguments or remain unset.
    If they are not passed, they are retrieved from environment variables:

    * `SP_USERNAME` is expected to contain the username
    * `SP_API_KEY` is expected to contain the API key

    Parameters
    ----------
    username: Optional[str]
        The username to which the API token is granted, e.g., `demo-user@streampipes.apche.org`.<br>
        If not passed, the username is retrieved from environment variable `SP_USERNAME`.
    api_key: Optional[str]
        The StreamPipes API key as it is displayed in the UI.<br>
        If not passed, the api key is retrieved from environment variable `SP_API_KEY`

    Examples
    --------
    see [StreamPipesClient][streampipes.client.StreamPipesClient]

    """

    _ENV_KEY_API = "SP_API_KEY"
    _ENV_KEY_USERNAME = "SP_USERNAME"

    @classmethod
    @deprecated("deprecated since 0.93.0; please use the class constructor instead.")
    def from_env(cls, username_env: str, api_key_env: str) -> StreamPipesApiKeyCredentials:
        """DEPRECATED - use the class constructor instead

        Returns an api key provider parameterized via environment variables.

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
                f"{'`' + api_key_env + '`' if api_key is None else ''}. "  # noqa: W291
                "Please check them to be properly set."
            )

        return cls(username=username, api_key=api_key)

    def __init__(
        self,
        username: Optional[str] = None,
        api_key: Optional[str] = None,
    ):
        # if both parameters are passed we can add them directly to the instance
        if all({username, api_key}):
            self.username = username
            self.api_key = api_key

        # otherwise we need to check if environment variables are properly set
        else:
            retrieved_api_key = os.environ.get(self._ENV_KEY_API, None)
            retrieved_user_name = os.environ.get(self._ENV_KEY_USERNAME, None)

            self.username = retrieved_user_name
            self.api_key = retrieved_api_key

            if username:
                self.username = username

                if retrieved_api_key is None:
                    raise AttributeError(
                        "API key not found in the environment variables - please provide the API key "
                        "via the parameter `api_key` or ensure that it is passed to "
                        f"the environment variable `{self._ENV_KEY_API}`."
                    )
                self.api_key = retrieved_api_key

            if api_key:
                self.api_key = api_key

                if retrieved_user_name is None:
                    raise AttributeError(
                        "Username not found - please provide the username "
                        "via the parameter `username` or ensure that it is passed to "
                        f"the environment variable `{self._ENV_KEY_USERNAME}`."
                    )
                self.username = retrieved_user_name

            if not all({self.username, self.api_key}):
                if None in {retrieved_user_name, retrieved_api_key}:
                    raise AttributeError(
                        "Both parameters not found - please provide both api key and username "
                        "either via the parameters (`api_key` and `username`) or via "
                        f"the corresponding environment variables (`{self._ENV_KEY_API}` and "
                        f"`{self._ENV_KEY_USERNAME}`)."
                    )

    @property
    def _authentication_headers(self) -> Dict[str, str]:
        """Provides the HTTP headers used for the authentication with the API token.

        Returns
        -------
        Dictionary with authentication headers as string key-value pairs.

        """

        user: str = self.username  # type: ignore  # mypy mistakenly detects this attribute as optional
        token: str = self.api_key  # type: ignore  # mypy mistakenly detects this attribute as optional

        return {
            "X-API-User": user,
            "X-API-Key": token,
        }
