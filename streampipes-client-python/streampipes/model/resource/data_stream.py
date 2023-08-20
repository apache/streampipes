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

from pydantic import Field, StrictBool, StrictInt, StrictStr

from streampipes.model.common import (
    ApplicationLink,
    EventGrounding,
    EventSchema,
    MeasurementCapability,
    MeasurementObject,
    random_letters,
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
        pandas_repr: Dict[str, Any]
            Pandas representation of the resource as a dictionary, which is then used by the respource container
            to create a data frame from a collection of resources.

        """
        return {
            **self.dict(
                exclude={
                    "class_name",
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

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        if not self.uri:
            self.uri = self.element_id

    class_name: StrictStr = Field(alias="@class", default_factory=lambda: "org.apache.streampipes.model.SpDataStream")
    element_id: StrictStr = Field(default_factory=lambda: f"sp:spdatastream:{random_letters(6)}")
    name: StrictStr = Field(default="Unnamed")
    description: Optional[StrictStr]
    icon_url: Optional[StrictStr]
    app_id: Optional[StrictStr]
    includes_assets: StrictBool = Field(default=False)
    includes_locales: StrictBool = Field(default=False)
    included_assets: List[StrictStr] = Field(default_factory=list)
    included_locales: List[StrictStr] = Field(default_factory=list)
    application_links: List[ApplicationLink] = Field(default_factory=list)
    internally_managed: StrictBool = Field(default=False)
    connected_to: Optional[List[StrictStr]]
    event_grounding: EventGrounding = Field(default_factory=EventGrounding)
    event_schema: Optional[EventSchema]
    measurement_capability: Optional[List[MeasurementCapability]]
    measurement_object: Optional[List[MeasurementObject]]
    index: StrictInt = Field(default=0)
    corresponding_adapter_id: Optional[StrictStr]
    category: Optional[List[StrictStr]]
    uri: Optional[StrictStr]
    dom: Optional[StrictStr]
    rev: Optional[StrictStr] = Field(alias="_rev")

    def to_dict(self, use_source_names=True):
        """Returns the resource in dictionary representation.

        Parameters
        ----------
        use_source_names: bool
            Indicates if the dictionary keys are in python representation or
            equally named to the StreamPipes backend

        Returns
        ------
        resource: Dict[str, Any]
            The resource as dictionary representation

        """

        # This serves as a temporary fix for https://github.com/apache/streampipes/issues/1245
        # should be removed as soon as possible

        resource_dict = self.dict(by_alias=use_source_names)

        if (
            use_source_names
            and (transport_protocol_dict := resource_dict["eventGrounding"]["transportProtocols"][0])["@class"]
            != "org.apache.streampipes.model.grounding.KafkaTransportProtocol"
        ):
            port = transport_protocol_dict.pop("kafkaPort")
            transport_protocol_dict.update({"port": port})
            resource_dict["eventGrounding"]["transportProtocols"][0] = transport_protocol_dict

        return resource_dict
