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
from typing import Optional

from pydantic import StrictBool, StrictStr

from streampipes.model.common import EventSchema
from streampipes.model.resource.resource import Resource

__all__ = [
    "DataLakeMeasure",
]


class DataLakeMeasure(Resource):
    """Implementation of a resource for data lake measures.

    This resource defines the data model used by resource container (`model.container.DataLakeMeasures`).
    It inherits from Pydantic's BaseModel to get all its superpowers,
    which are used to parse, validate the API response, and to easily switch between
    the Python representation (both serialized and deserialized) and Java representation (serialized only).
    """

    def convert_to_pandas_representation(self):
        """Returns the dictionary representation of a data lake measure
        to be used when creating a pandas Dataframe.

        It excludes the following fields: `element_id`, `event_schema`, `schema_version`.
        Instead of the whole event schema the number of event properties contained
        is returned with the column name `num_event_properties`.

        Returns
        -------
        pandas_repr: Dict[str, Any]
            Pandas representation of the resource as a dictionary, which is then used by the respource container
            to create a data frame from a collection of resources.

        """

        return {
            **self.dict(exclude={"element_id", "event_schema", "schema_version"}),
            "num_event_properties": len(self.event_schema.event_properties),
        }

    element_id: Optional[StrictStr]
    measure_name: StrictStr
    timestamp_field: StrictStr
    event_schema: Optional[EventSchema]
    pipeline_id: Optional[StrictStr]
    pipeline_name: Optional[StrictStr]
    pipeline_is_running: StrictBool
    schema_version: Optional[StrictStr]
