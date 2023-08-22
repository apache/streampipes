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
from streampipes.model.resource.data_stream import DataStream
from streampipes.model.resource.resource import Resource


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


class FunctionDefinition(Resource):
    """Configuration for a StreamPipes Function.

    This class maps to the `FunctionDefinition` class in the StreamPipes model.
    It contains all metadata that are required to register a function at the StreamPipes backend.

    Parameters
    ----------
    consumed_streams: List[str]
        List of data streams the function is consuming from
    function_id: FunctionId
        identifier object of a StreamPipes function

    Attributes
    ----------
    output_data_streams: Dict[str, DataStream]
        Map off all output data streams added to the function definition

    """

    function_id: FunctionId = Field(default_factory=FunctionId)
    consumed_streams: List[str] = Field(default_factory=list)
    output_data_streams: Dict[str, DataStream] = Field(default_factory=dict)

    def convert_to_pandas_representation(self) -> Dict:
        """Returns the dictionary representation of a function definition
        to be used when creating a pandas Dataframe.

        Returns
        -------
        pandas_repr: Dict[str, Any]
            Pandas representation of the resource as a dictionary, which is then used by the respource container
            to create a data frame from a collection of resources.
        """

        return self.to_dict(use_source_names=False)

    def add_output_data_stream(self, data_stream: DataStream):
        """Adds an output data stream to the function which makes it possible to write data back to StreamPipes.

        Parameters
        ----------
        data_stream: DataStream
            The schema of the output data stream.

        Returns
        -------
        self: FunctionDefinition
            Instance of the function definition that is extended by the provided `DataStream`

        """

        self.output_data_streams[data_stream.element_id] = data_stream
        return self

    def get_output_data_streams(self) -> Dict[str, DataStream]:
        """Get the output data streams of the function.

        Returns
        -------
        output_streams: Dict[str, DataStream]
            Dictionary with every known stream id and the related output stream.

        """

        return self.output_data_streams

    def get_output_stream_ids(self) -> List[str]:
        """Get the stream ids of the output data streams.

        Returns
        -------
        output_stream_ids: List[str]
            List of all stream ids

        """

        return list(self.output_data_streams.keys())
