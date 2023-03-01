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

from __future__ import annotations

import json
from itertools import chain
from typing import Any, Dict, List, Optional, Union, Literal

import pandas as pd
from pydantic import StrictInt, StrictStr, Field

from streampipes.model.resource import DataSeries
from streampipes.model.resource.resource import Resource

__all__ = [
    "QueryResult",
]


class QueryResult(Resource):

    def convert_to_pandas_representation(self) -> Dict[str, Union[List[str], List[List[Any]]]]:
        """Returns the dictionary representation of a data lake series
        to be used when creating a pandas Dataframe.

        It contains only the "header rows" (the column names) and "rows" that contain the actual data.

        Returns
        -------
        Dictionary
            Dictionary with the keys `headers` and `rows`

        """
        return self.dict(include={"headers", "rows"})

    total: StrictInt
    headers: List[StrictStr]
    all_data_series: List[DataSeries]
    query_status: Literal['OK', 'TOO_MUCH_DATA'] = Field(alias="spQueryStatus")

    def to_pandas(self) -> pd.DataFrame:
        """Returns the data lake series in representation of a Pandas Dataframe.

        Returns
        -------
        pd.DataFrame
        """

        # Pseudocode:
        # pandas_representation = self.convert_to_pandas_representation()
        #
        # pd = pd.DataFrame(data=chain(*[item.rows for item in self.all_data_series]),
        #              columns=pandas_representation["headers"])
        #
        # if tags not None:
        #     pd.groupBy(tags)

        return pd


    test ={})