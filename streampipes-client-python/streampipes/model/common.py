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
from uuid import uuid4

from pydantic import BaseModel, ConfigDict, Field, StrictInt, StrictStr

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

    model_config = ConfigDict(alias_generator=_snake_to_camel_case, populate_by_name=True)


class BaseElement(BasicModel):
    """Structure of a basic element in the StreamPipes Backend."""

    element_id: StrictStr | None = None


class ValueSpecification(BasicModel):
    """
    Data model of an `ValueSpecification` in compliance with the StreamPipes Backend.
    """

    class_name: StrictStr | None = Field(default=None, alias="@class")
    element_id: StrictStr | None = None
    min_value: int | None = None
    max_value: int | None = None
    step: float | None = None


class EventProperty(BasicModel):
    """
    Data model of an `EventProperty` in compliance with the StreamPipes Backend.
    """

    class_name: StrictStr = Field(alias="@class", default="org.apache.streampipes.model.schema.EventPropertyPrimitive")
    element_id: StrictStr = Field(default_factory=lambda: f"sp:eventproperty:{random_letters(6)}")
    label: StrictStr | None = None
    description: StrictStr | None = None
    runtime_name: StrictStr
    semantic_type: StrictStr | None = None
    property_scope: StrictStr | None = Field(default="MEASUREMENT_PROPERTY")
    runtime_id: StrictStr | None = None
    runtime_type: StrictStr = Field(default="http://www.w3.org/2001/XMLSchema#string")
    measurement_unit: StrictStr | None = None
    value_specification: ValueSpecification | None = None


class EventSchema(BasicModel):
    """
    Data model of an `EventSchema` in compliance with the StreamPipes Backend.
    """

    event_properties: list[EventProperty]


class ApplicationLink(BasicModel):
    """
    Data model of an `ApplicationLink` in compliance with the StreamPipes Backend.
    """

    class_name: StrictStr | None = Field(default=None, alias="@class")
    element_id: StrictStr | None = None
    application_name: StrictStr | None = None
    application_description: StrictStr | None = None
    application_url: StrictStr | None = None
    application_icon_url: StrictStr | None = None
    application_link_type: StrictStr | None = None


class TopicDefinition(BasicModel):
    """
    Data model of a `TopicDefinition` in compliance with the StreamPipes Backend.
    """

    class_name: StrictStr | None = Field(
        alias="@class", default="org.apache.streampipes.model.grounding.SimpleTopicDefinition"
    )
    actual_topic_name: StrictStr = Field(default_factory=lambda: f"org.apache.streampipes.connect.{uuid4()}")


class TransportProtocol(BasicModel):
    """
    Data model of a `TransportProtocol` in compliance with the StreamPipes Backend.
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
    Data model of a `TransportFormat` in compliance with the StreamPipes Backend.
    """

    rdf_type: list[StrictStr] = Field(default=["http://sepa.event-processing.org/sepa#json"])


class EventGrounding(BasicModel):
    """
    Data model of an `EventGrounding` in compliance to with StreamPipes Backend.
    """

    transport_protocols: list[TransportProtocol] = Field(default_factory=lambda: [TransportProtocol()])
    transport_formats: list[TransportFormat] = Field(default_factory=lambda: [TransportFormat()])


class MeasurementCapability(BasicModel):
    """
    Data model of a `MeasurementCapability` in compliance with the StreamPipes Backend.
    """

    capability: StrictStr | None = None
    element_id: StrictStr | None = None


class MeasurementObject(BasicModel):
    """
    Data model of a `MeasurementObject` in compliance with the StreamPipes Backend.
    """

    element_id: StrictStr | None = None
    measures_object: StrictStr | None = None
