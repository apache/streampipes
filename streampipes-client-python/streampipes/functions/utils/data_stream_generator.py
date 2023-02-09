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

import random
import string
from enum import Enum
from typing import Dict, Optional
from uuid import uuid4

from streampipes.model.resource.data_stream import DataStream


def random_letters(n: int):
    """Generates n random letters.

    Parameters
    ----------
    n: int
        number of letters
    """
    return "".join(random.choice(string.ascii_letters) for _ in range(n))


class RuntimeType(Enum):
    """Runtime types for the attributes of a data stream."""

    STRING = "string"
    BOOLEAN = "boolean"
    DOUBLE = "double"
    FLOAT = "float"
    INTEGER = "integer"
    LONG = "long"


# TODO Use an more general approach to create a data stream
def create_data_stream(name: str, attributes: Dict[str, str], stream_id: Optional[str] = None):
    """Creates a data stream

    Parameters
    ----------
    name: str
        Name of the data stream to be shown at the UI.
    attributes: Dict[str, str]
        Name and types of the attributes.
    stream_id: str
        The id of this data stream.
    """
    if not stream_id:
        stream_id = f"sp:spdatastream:{random_letters(6)}"
    return DataStream(
        **{
            "@class": "org.apache.streampipes.model.SpDataStream",
            "elementId": stream_id,
            "dom": None,
            "connectedTo": None,
            "name": name,
            "description": "",
            "iconUrl": None,
            "appId": None,
            "includesAssets": False,
            "includesLocales": False,
            "includedAssets": [],
            "includedLocales": [],
            "internallyManaged": False,
            "eventGrounding": {
                "transportProtocols": [
                    {
                        "@class": "org.apache.streampipes.model.grounding.NatsTransportProtocol",
                        "elementId": f"sp:transportprotocol:{random_letters(6)}",
                        "brokerHostname": "nats",
                        "topicDefinition": {
                            "@class": "org.apache.streampipes.model.grounding.SimpleTopicDefinition",
                            "actualTopicName": f"org.apache.streampipes.connect.{uuid4()}",
                        },
                        "port": 4222,
                    }
                ],
                "transportFormats": [{"rdfType": ["http://sepa.event-processing.org/sepa#json"]}],
            },
            "eventSchema": {
                "eventProperties": [
                    {
                        "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
                        "elementId": f"sp:eventproperty:{random_letters(6)}",
                        "label": "timestamp",
                        "description": None,
                        "runtimeName": "timestamp",
                        "required": False,
                        "domainProperties": ["http://schema.org/DateTime"],
                        "propertyScope": "HEADER_PROPERTY",
                        "index": 0,
                        "runtimeId": None,
                        "runtimeType": "http://www.w3.org/2001/XMLSchema#long",
                        "measurementUnit": None,
                        "valueSpecification": None,
                    }
                ]
                + [
                    {
                        "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
                        "elementId": f"sp:eventproperty:{random_letters(6)}",
                        "label": attribute_name,
                        "description": None,
                        "runtimeName": attribute_name,
                        "required": False,
                        "domainProperties": [],
                        "propertyScope": "MEASUREMENT_PROPERTY",
                        "index": i,
                        "runtimeId": None,
                        "runtimeType": f"http://www.w3.org/2001/XMLSchema#{attribute_type}",
                        "measurementUnit": None,
                        "valueSpecification": None,
                    }
                    for i, (attribute_name, attribute_type) in enumerate(attributes.items(), start=1)
                ]
            },
            "category": None,
            "index": 0,
            "correspondingAdapterId": None,
            "uri": stream_id,
            "_rev": None,
        }
    )
