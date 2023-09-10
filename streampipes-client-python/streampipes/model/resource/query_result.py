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

from itertools import chain
from typing import Any, Dict, List, Literal, Union

import pandas as pd
from pydantic import Field, StrictInt, StrictStr

from streampipes.model.resource import DataSeries
from streampipes.model.resource.exceptions import StreamPipesUnsupportedDataSeries
from streampipes.model.resource.resource import Resource

__all__ = [
    "QueryResult",
]


class QueryResult(Resource):
    """Implementation of a resource for query result.
    This resource defines the data model used by its resource container(`model.container.DataLakeMeasures`).
    It inherits from Pydantic's BaseModel to get all its superpowers,
    which are used to parse, validate the API response and to easily switch between
    the Python representation (both serialized and deserialized) and Java representation (serialized only).
    """

    def convert_to_pandas_representation(self) -> Dict[str, Union[List[str], List[List[Any]]]]:
        """Returns the dictionary representation of a data lake series
        to be used when creating a pandas Dataframe.

        It contains only the "header rows" (the column names) and "rows" that contain the actual data.

        Returns
        -------
        dict
            Dictionary with the keys `headers` and `rows`

        Raises
        ------
        StreamPipesUnsupportedDataLakeSeries
            If the query result returned by the StreamPipes API cannot be converted to the pandas representation

        """
        for series in self.all_data_series:
            if self.headers != series.headers:
                raise StreamPipesUnsupportedDataSeries("Headers of series does not match query result headers")

        if self.headers[0] == "time":
            self.headers[0] = "timestamp"
        else:
            raise StreamPipesUnsupportedDataSeries(f"Unsupported headers {self.headers}")

        return {
            "headers": self.headers,
            "rows": list(chain.from_iterable([series.rows for series in self.all_data_series])),
        }

    total: StrictInt
    headers: List[StrictStr]
    all_data_series: List[DataSeries]
    query_status: Literal["OK", "TOO_MUCH_DATA"] = Field(alias="spQueryStatus")

    def to_pandas(self) -> pd.DataFrame:
        """Returns the data lake series in representation of a Pandas Dataframe.

        Returns
        -------
        df: pd.DataFrame
            Pandas df containing the query result
        """

        pandas_representation = self.convert_to_pandas_representation()

        df = pd.DataFrame(data=pandas_representation["rows"], columns=pandas_representation["headers"])

        return df
