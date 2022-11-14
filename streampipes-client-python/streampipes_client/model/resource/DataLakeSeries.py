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
import json
from typing import Any, Dict, List, Optional

import pandas as pd
from pydantic import StrictInt, StrictStr
from streampipes_client.model.resource.resource import Resource


class DataLakeSeries(Resource):
    @classmethod
    def from_json(cls, json_string: str) -> Resource:

        # deserialize JSON string
        parsed_json = json.loads(json_string)

        if len(parsed_json["allDataSeries"]) != 1:
            raise RuntimeError("Not supported")

        data_series = parsed_json["allDataSeries"][0]

        return cls.parse_obj(data_series)

    def convert_to_pandas_representation(self) -> Dict:

        result: Dict = dict()

        for row in self.rows:
            for idx, value in enumerate(row):
                if (key := self.headers[idx]) in result.keys():
                    result[self.headers[idx]].append(value)
                else:
                    result.update({key: [value]})

        return result

    total: StrictInt
    headers: List[StrictStr]
    rows: List[List[Any]]
    tags: Optional[str]

    def to_pandas(self) -> pd.DataFrame:
        return pd.DataFrame(data=self.convert_to_pandas_representation())
