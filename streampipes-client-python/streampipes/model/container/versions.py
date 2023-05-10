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
Implementation of a resource container for the versions endpoint.
"""

__all__ = [
    "Versions",
]

from typing import Type

from streampipes.model.container.resource_container import ResourceContainer
from streampipes.model.resource import Version
from streampipes.model.resource.resource import Resource


class Versions(ResourceContainer):
    """Implementation of the resource container for the versions endpoint.

    This resource container is a collection of versions returned by the StreamPipes API.
    It is capable of parsing the response content directly into a list of queried `Version`.
    Furthermore, the resource container makes them accessible in a pythonic manner.

    Parameters
    ----------
    resources: List[Version]
        A list of resources ([Version][streampipes.model.resource.Version]) to be contained in the `ResourceContainer`.

    """

    @classmethod
    def _resource_cls(cls) -> Type[Resource]:
        """Returns the class of the resource that are bundled.

        Returns
        -------
        [Version][streampipes.model.resource.Version]
        """
        return Version
