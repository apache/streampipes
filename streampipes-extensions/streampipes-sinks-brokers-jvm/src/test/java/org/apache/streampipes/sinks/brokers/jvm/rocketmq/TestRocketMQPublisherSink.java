/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.sinks.brokers.jvm.rocketmq;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.junit.Test;

import java.io.IOException;

import static org.apache.streampipes.sinks.brokers.jvm.rocketmq.RocketMQPublisherSink.ENDPOINT_KEY;
import static org.apache.streampipes.sinks.brokers.jvm.rocketmq.RocketMQPublisherSink.TOPIC_KEY;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestRocketMQPublisherSink {

  @Test
  public void testSimpleEventSink() throws IOException, ClientException {
    String endpoint = "localhost:8081";
    String topic = "TopicTest";

    SinkParams params = mock(SinkParams.class);
    DataSinkParameterExtractor extractor = mock(DataSinkParameterExtractor.class);

    when(params.extractor()).thenReturn(extractor);
    when(extractor.singleValueParameter(ENDPOINT_KEY, String.class)).thenReturn(endpoint);
    when(extractor.singleValueParameter(TOPIC_KEY, String.class)).thenReturn(topic);

    ClientServiceProvider provider = mock(ClientServiceProvider.class);

    ProducerBuilder producerBuilder = mock(ProducerBuilder.class);
    Producer producer = mock(Producer.class);

    when(provider.newProducerBuilder()).thenReturn(producerBuilder);
    when(producerBuilder.build()).thenReturn(producer);
    when(producerBuilder.setTopics(anyString())).thenReturn(producerBuilder);
    when(producerBuilder.setClientConfiguration(any())).thenReturn(producerBuilder);

    // test onInvocation
    RocketMQPublisherSink publisherSink = new RocketMQPublisherSink(provider);
    publisherSink.onInvocation(params, null);

    // test onEvent
    Event event = mock(Event.class);

    MessageBuilder messageBuilder = mock(MessageBuilder.class);
    Message message = mock(Message.class);

    when(provider.newMessageBuilder()).thenReturn(messageBuilder);
    when(messageBuilder.build()).thenReturn(message);
    when(messageBuilder.setTopic(anyString())).thenReturn(messageBuilder);
    when(messageBuilder.setBody(any())).thenReturn(messageBuilder);

    publisherSink.onEvent(event);
    verify(producer, times(1)).send(any(Message.class));

    // test onDetach
    publisherSink.onDetach();
    verify(producer, times(1)).close();
  }
}
