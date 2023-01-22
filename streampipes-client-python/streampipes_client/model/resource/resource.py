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
General and abstract implementation for a resource.
A resource defines the data model that is used by a resource container (`model.container.resourceContainer`).
"""
from abc import ABC, abstractmethod
from typing import Dict

from streampipes_client.model.common import BasicModel

__all__ = [
    "Resource",
]


class Resource(ABC, BasicModel):
    """General and abstract implementation for a resource.
    A resource defines the data model used by a resource container (`model.container.resourceContainer`).
    It inherits from Pydantic's BaseModel to get all its superpowers,
    which are used to parse, validate the API response and to easily switch between
    the Python representation (both serialized and deserialized) and Java representation (serialized only).
    """

    @abstractmethod
    def convert_to_pandas_representation(self) -> Dict:
        """Returns a dictionary representation to be used when creating a pandas Dataframe."""
        raise NotImplementedError  # pragma: no cover
