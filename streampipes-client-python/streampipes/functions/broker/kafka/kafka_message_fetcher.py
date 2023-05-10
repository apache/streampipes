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
from confluent_kafka import Consumer  # type: ignore


class KafkaMessage:
    """An internal representation of a Kafka message

    Parameters
    ----------
    data: bytes
        The received Kafka message as byte array
    """

    def __init__(self, data):
        self.data = data


class KafkaMessageFetcher:
    """Fetches the next message from Kafka

    Parameters
    ----------
    consumer: Consumer
        The Kafka consumer
    """

    def __init__(self, consumer: Consumer):
        self.consumer = consumer

    def __aiter__(self):
        return self

    async def __anext__(self):
        msg = None
        while not msg:
            msg = self.consumer.poll(0.1)
        return KafkaMessage(msg.value())
