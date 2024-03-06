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

from unittest import TestCase

from streampipes.model.resource import DataStream


class TestDataStreamWorkaround(TestCase):
    """
    Testcase that assures behavior of workaround introduced
    as a temporary fix for https://github.com/apache/streampipes/issues/1245
    Needs to be removed as soon as possible
    """

    def test_nats_case(self):
        data_stream_def = {
            "elementId": "some-random-id",
            "eventGrounding": {
                "transportProtocols": [
                    {
                        "@class": "org.apache.streampipes.model.grounding.NatsTransportProtocol",
                        "brokerHostname": "broker-host-name",
                        "topicDefinition": {"@class": "some-class-name", "actualTopicName": "actual-topic-name"},
                        "port": 50,
                    }
                ]
            },
        }

        data_stream = DataStream.parse_obj(data_stream_def)

        self.assertEqual(50, data_stream.to_dict()["eventGrounding"]["transportProtocols"][0]["port"])

    def test_kafka_case(self):

        data_stream_def = {
            "elementId": "some-random-id",
            "eventGrounding": {
                "transportProtocols": [
                    {
                        "@class": "org.apache.streampipes.model.grounding.KafkaTransportProtocol",
                        "brokerHostname": "broker-host-name",
                        "topicDefinition": {"@class": "some-class-name", "actualTopicName": "actual-topic-name"},
                        "kafkaPort": 50,
                    }
                ]
            },
        }

        data_stream = DataStream.parse_obj(data_stream_def)

        self.assertEqual(50, data_stream.to_dict()["eventGrounding"]["transportProtocols"][0]["kafkaPort"])
