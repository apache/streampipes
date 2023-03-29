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
from typing import Dict, List

from streampipes.client.client import StreamPipesClient
from streampipes.model.resource.data_stream import DataStream


class FunctionContext:
    """Container for the context of a StreamPipesFunction.

    Parameters
    ----------
    function_id: str
        The id of this function.
    schema: Dict[str, DataStream]
        A dictionary which contains the schema of a data stream for each stream id.
    client: StreamPipesClient
        The client to interact with the API.
    streams: List[str]
        The ids of the streams needed by this function.
    """

    def __init__(self, function_id: str, schema: Dict[str, DataStream], client: StreamPipesClient, streams: List[str]):
        self.function_id = function_id
        self.schema = schema
        self.client = client
        self.streams = streams

    def add_data_stream_schema(self, stream_id: str, data_stream: DataStream) -> None:
        """Adds a new data stream for a new stream id.

        Parameters
        ----------
        stream_id: str
            The id of the data stream.
        data_stream: DataStream
            The schema of the data stream.

        Returns
        -------
        None

        """
        self.schema[stream_id] = data_stream
