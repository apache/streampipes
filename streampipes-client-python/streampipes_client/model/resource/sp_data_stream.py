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
from streampipes_client.model.common import (
    ApplicationLink,
    EventGrounding,
    EventSchema,
    EventStreamQualityDefinition,
    EventStreamQualityRequirement,
    MeasurementCapability,
    MeasurementObject,
)
from streampipes_client.model.resource.resource import Resource

__all__ = ["SpDataStream"]


class SpDataStream(Resource):
    def convert_to_pandas_representation(self):
        return self.dict()

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
    has_event_stream_qualities: Optional[List[Optional[EventStreamQualityDefinition]]]
    requires_event_stream_qualities: Optional[List[Optional[EventStreamQualityRequirement]]]
    event_grounding: Optional[EventGrounding]
    event_schema: Optional[EventSchema]
    measurement_capability: Optional[List[Optional[MeasurementCapability]]]
    measurement_object: Optional[List[Optional[MeasurementObject]]]
    index: Optional[StrictInt]
    corresponding_adapter_id: Optional[StrictStr]
    category: Optional[List[Optional[StrictStr]]]
    uri: Optional[StrictStr]
    dom: Optional[StrictStr]
    _rev: Optional[StrictStr]
