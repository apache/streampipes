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
from typing import List, Optional

from pydantic import StrictBool, StrictInt, StrictStr
from streampipes.model.common import (
    ApplicationLink,
    EventGrounding,
    EventSchema,
    MeasurementCapability,
    MeasurementObject,
)
from streampipes.model.resource.resource import Resource

__all__ = [
    "DataStream",
]


class DataStream(Resource):
    """Implementation of a resource for data streams.
    This resource defines the data model used by resource container (`model.container.DataStreams`).
    It inherits from Pydantic's BaseModel to get all its superpowers,
    which are used to parse, validate the API response and to easily switch between
    the Python representation (both serialized and deserialized) and Java representation (serialized only).
    """

    def convert_to_pandas_representation(self):
        """Returns the dictionary representation of a data stream to be used when creating a pandas Dataframe.

        Returns
        -------
        Dictionary
        """
        return {
            **self.dict(
                exclude={
                    "event_grounding",
                    "measurement_capability",
                    "application_links",
                    "included_assets",
                    "connected_to",
                    "category",
                    "event_schema",
                    "included_locales",
                }
            ),
            "num_transport_protocols": len(self.event_grounding.transport_protocols),
            "num_measurement_capability": len(self.measurement_capability)
            if self.measurement_capability is not None
            else 0,
            "num_application_links": len(self.application_links) if self.application_links is not None else 0,
            "num_included_assets": len(self.included_assets) if self.included_assets is not None else 0,
            "num_connected_to": len(self.connected_to) if self.connected_to is not None else 0,
            "num_category": len(self.category) if self.category is not None else 0,
            "num_event_properties": len(self.event_schema.event_properties) if self.event_schema is not None else 0,
            "num_included_locales": len(self.included_locales) if self.included_locales is not None else 0,
        }

    element_id: Optional[StrictStr]
    name: Optional[StrictStr]
    description: Optional[StrictStr]
    icon_url: Optional[StrictStr]
    app_id: Optional[StrictStr]
    includes_assets: Optional[StrictBool]
    includes_locales: Optional[StrictBool]
    included_assets: Optional[List[Optional[StrictStr]]]
    included_locales: Optional[List[Optional[StrictStr]]]
    application_links: Optional[List[Optional[ApplicationLink]]]
    internally_managed: Optional[StrictBool]
    connected_to: Optional[List[Optional[StrictStr]]]
    event_grounding: EventGrounding
    event_schema: Optional[EventSchema]
    measurement_capability: Optional[List[Optional[MeasurementCapability]]]
    measurement_object: Optional[List[Optional[MeasurementObject]]]
    index: Optional[StrictInt]
    corresponding_adapter_id: Optional[StrictStr]
    category: Optional[List[Optional[StrictStr]]]
    uri: Optional[StrictStr]
    dom: Optional[StrictStr]
