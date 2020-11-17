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
"""contains relevant base classes"""
import abc
import json
import logging
import threading
from confluent_kafka.admin import AdminClient
from confluent_kafka import Producer, Consumer


class EventProcessor(object):
    __metaclass__ = abc.ABC

    _DEFAULT_KAFKA_CONSUMER_CONFIG = {
        'bootstrap.servers': 'kafka:9092',
        'enable.auto.commit': True,
        'auto.commit.interval.ms': 10000,
        'session.timeout.ms': 30000,
        'fetch.max.bytes': 5000012,
        'auto.offset.reset': 'latest',
    }

    _DEFAULT_KAFKA_PRODUCER_CONFIG = {
        'bootstrap.servers': 'kafka:9092',
        'acks': 'all',
        'retries': 0,
        'linger.ms': 20,
    }

    def __init__(self, **kwargs):
        """initialize EventProcessor with Kafka Prodcuer and Consumer"""
        self.logger = logging.getLogger(__name__)

        self._input_topics = kwargs.get('input_topics')
        self._output_topics = kwargs.get('output_topics')
        self._invocation_id = kwargs.get('invocation_id')
        self._bootstrap_servers = kwargs.get('bootstrap_servers')
        self.static_properties = kwargs.get('static_properties')

        self._running = False
        self._threads = {}

        if self._bootstrap_servers is not None:
            self._DEFAULT_KAFKA_CONSUMER_CONFIG['bootstrap.servers'] = self._bootstrap_servers
            self._DEFAULT_KAFKA_PRODUCER_CONFIG['bootstrap.servers'] = self._bootstrap_servers

        self._DEFAULT_KAFKA_CONSUMER_CONFIG['group.id'] = 'streampipes_python_' + self._invocation_id

        self._producer = Producer(self._DEFAULT_KAFKA_PRODUCER_CONFIG)
        self._consumer = Consumer(self._DEFAULT_KAFKA_CONSUMER_CONFIG)
        #self._create_topic(topic=self._output_topics, conf=self._DEFAULT_KAFKA_PRODUCER_CONFIG)

        self.on_invocation()

    def init(self):
        self.logger.info('start processor {}'.format(self.invocation_id))
        thread = threading.Thread(target=self._consume, name=self.invocation_id)
        thread.start()
        self._threads['kafka'] = thread

    def active_threads(self):
        return self._threads

    @property
    def invocation_id(self):
        return self._invocation_id

    def __del__(self):
        pass

    @abc.abstractmethod
    def on_invocation(self):
        """ on_invocation is called when processor is started """

    @abc.abstractmethod
    def on_event(self, event):
        """ on_event receives kafka consumer messages """
        pass

    @abc.abstractmethod
    def on_detach(self):
        """ on_detach is called when processor is stopped """
        pass

    def _on_event(self, event):
        result = self.on_event(event)

        if result is not None:
            self._produce(result)

    def _consume(self):
        """ retrieve events from kafka """
        self._consumer.subscribe(topics=[self._input_topics])
        self._running = True

        while self._running:
            # fetch records from kafka and send to
            msg = self._consumer.poll(timeout=1.0)

            if msg is None:
                continue
            elif msg.error():
                if msg.error().str() != "Broker: No more messages":
                    self.logger.error("Consumer error: {}".format(msg.error()))
                    continue
            else:
                try:
                    # json -> dict
                    event = json.loads(msg.value().decode('utf-8'))
                    if isinstance(event, int):
                        self.logger.info("Integer not allowed {}".format(event))
                        continue
                except ValueError as e:
                    self.logger.info("Not a valid json {}".format(e))
                    continue

                self._on_event(event)

    def _produce(self, result):
        """ send events to kafka """
        event = json.dumps(result).encode('utf-8')
        try:
            # dict -> json
            self._producer.produce(self._output_topics, value=event)
        except BufferError:
            self._producer.poll(1)

    # def _create_topic(self, topic=None, conf=None):
    #     """ Create the topic if it doesn't exist """
    #     admin = AdminClient(conf)
    #     fs = admin.create_topics([NewTopic(topic, num_partitions=1, replication_factor=1)])
    #     f = fs[topic]
    #     try:
    #         f.result()
    #     except KafkaException as ex:
    #         if ex.args[0].code() == KafkaError.TOPIC_ALREADY_EXISTS:
    #             self.logger.warning("Topic {} already exists: continue".format(topic))
    #         else:
    #             raise

    def stop(self):
        self.logger.info('stop processor {}'.format(self.invocation_id))
        self._running = False
        self._consumer.close()
        self._producer.flush()
        self.on_detach()
