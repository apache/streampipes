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

from enum import Enum
from typing import Dict, Optional

from streampipes.functions.broker import SupportedBroker
from streampipes.model.common import (
    EventGrounding,
    EventProperty,
    EventSchema,
    TransportProtocol,
)
from streampipes.model.resource.data_stream import DataStream


class RuntimeType(Enum):
    """Runtime type names for the attributes of a data stream.

    Attributes
    ----------
    STRING
    BOOLEAN
    DOUBLE
    FLOAT
    INTEGER
    LONG
    """

    STRING = "string"
    BOOLEAN = "boolean"
    DOUBLE = "double"
    FLOAT = "float"
    INTEGER = "integer"
    LONG = "long"


# TODO Use an more general approach to create a data stream
def create_data_stream(
    name: str,
    attributes: Dict[str, str],
    stream_id: Optional[str] = None,
    broker: SupportedBroker = SupportedBroker.NATS,
):
    """Creates a data stream

    Parameters
    ----------
    name: str
        Name of the data stream to be shown at the UI.
    attributes: Dict[str, str]
        Name and types of the attributes.
    stream_id: str
        The id of this data stream.

    Returns
    -------
    data_stream: DataStream
        The created data stream
    """

    event_schema = EventSchema(
        event_properties=[
            EventProperty(  # type: ignore
                label="timestamp",
                runtime_name="timestamp",
                domain_properties=["http://schema.org/DateTime"],
                property_scope="HEADER_PROPERTY",
                runtime_type="http://www.w3.org/2001/XMLSchema#long",
            )
        ]
        + [
            EventProperty(  # type: ignore
                label=attribute_name,
                runtime_name=attribute_name,
                index=i,
                runtime_type=f"http://www.w3.org/2001/XMLSchema#{attribute_type}",
            )
            for i, (attribute_name, attribute_type) in enumerate(attributes.items(), start=1)
        ]
    )

    transport_protocols = [TransportProtocol()]
    if broker == SupportedBroker.KAFKA:
        transport_protocols = [
            TransportProtocol(
                class_name="org.apache.streampipes.model.grounding.KafkaTransportProtocol",  # type: ignore
                broker_hostname="kafka",
                port=9092,
            )
        ]

    data_stream = DataStream(
        name=name, event_schema=event_schema, event_grounding=EventGrounding(transport_protocols=transport_protocols)
    )
    if stream_id:
        data_stream.element_id = stream_id
    return data_stream
