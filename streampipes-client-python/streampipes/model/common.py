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

"""
Classes of the StreamPipes data model that are commonly shared.
"""

import random
import string
from typing import List, Optional
from uuid import uuid4

from pydantic import BaseModel, Field, StrictBool, StrictInt, StrictStr

__all__ = [
    "BaseElement",
    "BasicModel",
    "EventSchema",
]


def random_letters(n: int) -> str:
    """Generates a string consisting of random letters.

    Parameters
    ----------
    n: int
        number of letters

    Returns
    -------
    rand_str: str
        String consisting of `n` random letters
    """
    return "".join(random.choice(string.ascii_letters) for _ in range(n))


def _snake_to_camel_case(snake_case_string: str) -> str:
    """Converts a string in snake_case format to camelCase style.

    Parameters
    ----------
    snake_case_string: str
        string in snake_case format

    Returns
    -------
    camel_case: str
        The exact same string formatted as camelCase

    """

    tokens = snake_case_string.split("_")

    return tokens[0] + "".join(t.title() for t in tokens[1:])


class BasicModel(BaseModel):
    """Basic model class used for the whole Python StreamPipes data model."""

    class Config:
        """Configuration class for Pydantic.
        Defines alias generator to convert field names from camelCase (API) to snake_case (Python codebase).
        """

        alias_generator = _snake_to_camel_case
        allow_population_by_field_name = True


class BaseElement(BasicModel):
    """Structure of a basic element in the StreamPipes backend"""

    element_id: Optional[StrictStr]


class ValueSpecification(BasicModel):
    """
    Data model of an `ValueSpecification` in compliance to the StreamPipes Backend.
    """

    class_name: Optional[StrictStr] = Field(alias="@class")
    element_id: Optional[StrictStr]
    min_value: Optional[int]
    max_value: Optional[int]
    step: Optional[float]


class EventProperty(BasicModel):
    """
    Data model of an `EventProperty` in compliance to the StreamPipes Backend.
    """

    class_name: StrictStr = Field(alias="@class", default="org.apache.streampipes.model.schema.EventPropertyPrimitive")
    element_id: StrictStr = Field(default_factory=lambda: f"sp:eventproperty:{random_letters(6)}")
    label: Optional[StrictStr]
    description: Optional[StrictStr]
    runtime_name: StrictStr
    required: StrictBool = Field(default=False)
    domain_properties: Optional[List[StrictStr]] = Field(default_factory=list)
    property_scope: Optional[StrictStr] = Field(default="MEASUREMENT_PROPERTY")
    index: StrictInt = Field(default=0)
    runtime_id: Optional[StrictStr]
    runtime_type: StrictStr = Field(default="http://www.w3.org/2001/XMLSchema#string")
    measurement_unit: Optional[StrictStr]
    value_specification: Optional[ValueSpecification]


class EventSchema(BasicModel):
    """
    Data model of an `EventSchema` in compliance to the StreamPipes Backend.
    """

    event_properties: List[EventProperty]


class ApplicationLink(BasicModel):
    """
    Data model of an `ApplicationLink` in compliance to the StreamPipes Backend.
    """

    class_name: Optional[StrictStr] = Field(alias="@class")
    element_id: Optional[StrictStr]
    application_name: Optional[StrictStr]
    application_description: Optional[StrictStr]
    application_url: Optional[StrictStr]
    application_icon_url: Optional[StrictStr]
    application_link_type: Optional[StrictStr]


class TopicDefinition(BasicModel):
    """
    Data model of a `TopicDefinition` in compliance to the StreamPipes Backend.
    """

    class_name: Optional[StrictStr] = Field(
        alias="@class", default="org.apache.streampipes.model.grounding.SimpleTopicDefinition"
    )
    actual_topic_name: StrictStr = Field(default_factory=lambda: f"org.apache.streampipes.connect.{uuid4()}")


class TransportProtocol(BasicModel):
    """
    Data model of a `TransportProtocol` in compliance to the StreamPipes Backend.
    """

    class_name: StrictStr = Field(
        alias="@class", default="org.apache.streampipes.model.grounding.NatsTransportProtocol"
    )
    element_id: StrictStr = Field(default_factory=lambda: f"sp:transportprotocol:{random_letters(6)}")
    broker_hostname: StrictStr = Field(default="nats")
    topic_definition: TopicDefinition = Field(default_factory=TopicDefinition)
    port: StrictInt = Field(alias="kafkaPort", default=4222)


class TransportFormat(BasicModel):
    """
    Data model of a `TransportFormat` in compliance to the StreamPipes Backend.
    """

    rdf_type: List[StrictStr] = Field(default=["http://sepa.event-processing.org/sepa#json"])


class EventGrounding(BasicModel):
    """
    Data model of an `EventGrounding` in compliance to the StreamPipes Backend.
    """

    transport_protocols: List[TransportProtocol] = Field(default_factory=lambda: [TransportProtocol()])
    transport_formats: List[TransportFormat] = Field(default_factory=lambda: [TransportFormat()])


class MeasurementCapability(BasicModel):
    """
    Data model of a `MeasurementCapability` in compliance to the StreamPipes Backend.
    """

    capability: Optional[StrictStr]
    element_id: Optional[StrictStr]


class MeasurementObject(BasicModel):
    """
    Data model of a `MeasurementObject` in compliance to the StreamPipes Backend.
    """

    element_id: Optional[StrictStr]
    measures_object: Optional[StrictStr]
