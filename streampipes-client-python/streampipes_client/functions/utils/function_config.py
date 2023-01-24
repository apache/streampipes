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
from typing import Dict

from streampipes_client.model.resource.data_stream import DataStream


class FunctionId:
    def __init__(self, id: str, version: int) -> None:
        self.id = id
        self.version = version


class FunctionConfig:
    def __init__(self, function_id: FunctionId) -> None:
        self.function_id = function_id
        self.__output_data_streams: Dict[str, DataStream] = dict()

    def add_output_data_stream(self, stream_id: str, data_stream: DataStream):
        self.__output_data_streams[stream_id] = data_stream
        return self

    def get_output_data_streams(self) -> Dict[str, DataStream]:
        return self.__output_data_streams
