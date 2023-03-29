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
Configuration class for the StreamPipes client.
"""


from dataclasses import dataclass
from typing import Optional

__all__ = [
    "StreamPipesClientConfig",
]


from streampipes.client.credential_provider import CredentialProvider


@dataclass
class StreamPipesClientConfig:
    """Configure the StreamPipes client in accordance to the actual StreamPipes instance to connect to.

    An instance is provided to the `StreamPipesClient` to configure it properly.

    Parameters
    ----------
    credential_provider: CredentialProvider
        Provides the credentials to authenticate with the StreamPipes API.
    host_address:
        Host address of the StreamPipes instance to connect to.
        Should be provided without the protocol/scheme, e.g. as `localhost` or `streampipes.xyz`.
    https_disabled: Optional[bool]
        Determines whether https is used to connect to StreamPipes.
    port: Optional[int]
        Specifies the port under which the StreamPipes API is available,
        e.g., `80` (with http) or `443` (with https)

    Examples
    --------

    see [StreamPipesClient][streampipes.client.StreamPipesClient]

    """

    credential_provider: CredentialProvider
    host_address: str
    https_disabled: Optional[bool] = False
    port: Optional[int] = 80
