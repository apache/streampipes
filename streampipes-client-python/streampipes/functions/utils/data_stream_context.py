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
from typing import List

from streampipes.functions.broker import Consumer
from streampipes.functions.streampipes_function import StreamPipesFunction
from streampipes.model.resource.data_stream import DataStream


class DataStreamContext:
    """Container for the context of a data stream.

    Parameters
    ----------
    functions: List[StreamPipesFunction]
        StreamPipes Functions which require the data of this data stream.
    schema: DataStream
        The schema of this data stream.
    broker: Consumer
        The consumer to connect to this data stream.
    """

    def __init__(self, functions: List[StreamPipesFunction], schema: DataStream, broker: Consumer) -> None:
        self.functions = functions
        self.schema = schema
        self.broker = broker

    def add_function(self, function: StreamPipesFunction):
        """Adds a new StreamPipes Function.

        Parameters
        ----------
        function: StreamPipesFunction
            StreamPipesFunction which requires this data stream.

        Returns
        -------
        None
        """
        self.functions.append(function)
