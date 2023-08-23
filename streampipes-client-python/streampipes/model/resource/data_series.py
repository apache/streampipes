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
from typing import Any, Dict, List, Optional, Union

import pandas as pd
from pydantic import StrictInt, StrictStr

from streampipes.model.resource.exceptions import StreamPipesUnsupportedDataSeries
from streampipes.model.resource.resource import Resource

__all__ = [
    "DataSeries",
]


class DataSeries(Resource):
    """Implementation of a resource for data series.
    This resource defines the data model used by its resource container(`model.container.DataLakeMeasures`).
    It inherits from Pydantic's BaseModel to get all its superpowers,
    which are used to parse, validate the API response and to easily switch between
    the Python representation (both serialized and deserialized) and Java representation (serialized only).

    Notes
    ------
        This class will only exist temporarily in it its current appearance since
        there are some inconsistencies in the StreamPipes API.

    """

    @classmethod
    def from_json(cls, json_string: str) -> DataSeries:
        """Creates an instance of `DataSeries` from a given JSON string.

        This method is used by the resource container to parse the JSON response of
        the StreamPipes API.
        Currently, it only supports data lake series that consist of exactly one series of data.

        Parameters
        ----------
        json_string: str
            The JSON string the data lake series should be created on.

        Returns
        -------
        DataSeries
            Instance of `DataSeries` that is created based on the given JSON string.

        Raises
        ------
        StreamPipesUnsupportedDataLakeSeries
            If the data lake series returned by the StreamPipes API cannot be parsed
            with the current version of the Python client.

        """

        # deserialize JSON string
        parsed_json = json.loads(json_string)

        # check if the provided JSON has only one data series entry
        # otherwise raise the proper exception
        if len(parsed_json["allDataSeries"]) != 1:
            raise StreamPipesUnsupportedDataSeries()

        # get the data data series
        data_series = parsed_json["allDataSeries"][0]

        return cls.parse_obj(data_series)

    def convert_to_pandas_representation(self) -> Dict[str, Union[List[str], List[List[Any]]]]:
        """Returns the dictionary representation of a data lake series
        to be used when creating a pandas Dataframe.

        It contains only the "header rows" (the column names) and "rows" that contain the actual data.

        Returns
        -------
        pandas_repr: dict[str, Any]
            Dictionary with the keys `headers` and `rows`

        """
        return self.dict(include={"headers", "rows"})

    total: StrictInt
    headers: List[StrictStr]
    rows: List[List[Any]]
    tags: Optional[str]

    def to_pandas(self) -> pd.DataFrame:
        """Returns the data lake series in representation of a Pandas Dataframe.

        Returns
        -------
        pd: pd.DataFrame
            The data lake series in form of a pandas dataframe
        """

        pandas_representation = self.convert_to_pandas_representation()
        return pd.DataFrame(data=pandas_representation["rows"], columns=pandas_representation["headers"])
