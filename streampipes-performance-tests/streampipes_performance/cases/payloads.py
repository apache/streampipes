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

"""Serialize supplied schemas and settings into API payloads."""

from urllib.parse import urlsplit

from streampipes_performance.cases.scripts import Script


def nats_stream(identifier, name, subject, broker_url, fields):
    broker = urlsplit(broker_url)
    properties = []
    for key, datatype in fields:
        event_property = {
            "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive",
            "runtimeName": key,
            "runtimeType": f"http://www.w3.org/2001/XMLSchema#{datatype}",
            "propertyScope": "HEADER_PROPERTY" if key == "timestamp" else "MEASUREMENT_PROPERTY",
        }
        if key == "timestamp":
            event_property["semanticType"] = "http://schema.org/DateTime"
        properties.append(event_property)
    return {
        "@class": "org.apache.streampipes.model.SpDataStream",
        "elementId": identifier,
        "appId": identifier,
        "name": name,
        "eventGrounding": {
            "transportProtocols": [
                {
                    "@class": "org.apache.streampipes.model.grounding.NatsTransportProtocol",
                    "brokerHostname": broker.hostname,
                    "port": broker.port or 4222,
                    "topicDefinition": {
                        "@class": "org.apache.streampipes.model.grounding.SimpleTopicDefinition",
                        "actualTopicName": subject,
                    },
                },
            ],
        },
        "eventSchema": {"eventProperties": properties},
    }


def transformation_config(transformation: Script | None):
    return {
        "scriptActive": transformation is not None,
        "script": transformation.script if transformation is not None else "",
    }
