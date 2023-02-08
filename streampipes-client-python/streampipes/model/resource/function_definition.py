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

__all__ = [
    "FunctionDefinition",
]

from typing import Dict, List
from uuid import uuid4

from pydantic import Field, StrictInt, StrictStr
from streampipes.model.common import BasicModel
from streampipes.model.resource.resource import Resource


class FunctionDefinition(Resource):
    """Configuration for a StreamPipes Function.

    This class maps to the `FunctionDefinition` class in the StreamPipes model.
    It contains all metadata that are required to register a function at the StreamPipes backend.

    Parameters
    ----------
    function_id: FunctionId
        identifier object of a StreamPipes function
    consumed_streams: List[str]
        list of data streams the function is consuming from
    """

    def convert_to_pandas_representation(self) -> Dict:
        """Returns the dictionary representation of a function definition
        to be used when creating a pandas Dataframe.
        """

        return self.to_dict(use_source_names=False)

    class FunctionId(BasicModel):
        """Identification object for a StreamPipes function.

        Maps to the `FunctionId` class defined in the StreamPipes model.

        Parameters
        ----------
        id: str
            unique identifier of the function instance
        version: int
            version of the corresponding function
        """

        id: StrictStr = Field(default_factory=lambda: str(uuid4()))
        version: StrictInt = Field(default=1)

        def __hash__(self):
            return hash((self.id, self.version))

    function_id: FunctionId = Field(default_factory=FunctionId)
    consumed_streams: List[str] = Field(default_factory=list)
