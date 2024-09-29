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
package org.apache.streampipes.extensions.connectors.rocketmq;

import static org.apache.streampipes.extensions.connectors.rocketmq.sink.RocketMQPublisherSink.ENDPOINT_KEY;
import static org.apache.streampipes.extensions.connectors.rocketmq.sink.RocketMQPublisherSink.TOPIC_KEY;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.connectors.rocketmq.sink.RocketMQPublisherSink;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;

import java.io.IOException;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TestRocketMQPublisherSink {

  @Test
  public void testSimpleEventSink() throws IOException, ClientException {
    String endpoint = "localhost:8081";
    String topic = "TopicTest";

    var params = mock(IDataSinkParameters.class);
    DataSinkParameterExtractor extractor = mock(DataSinkParameterExtractor.class);

    when(params.extractor()).thenReturn(extractor);
    when(extractor.singleValueParameter(ENDPOINT_KEY, String.class)).thenReturn(endpoint);
    when(extractor.singleValueParameter(TOPIC_KEY, String.class)).thenReturn(topic);

    ClientServiceProvider provider = Mockito.mock(ClientServiceProvider.class);

    ProducerBuilder producerBuilder = Mockito.mock(ProducerBuilder.class);
    Producer producer = Mockito.mock(Producer.class);

    when(provider.newProducerBuilder()).thenReturn(producerBuilder);
    when(producerBuilder.build()).thenReturn(producer);
    when(producerBuilder.setTopics(anyString())).thenReturn(producerBuilder);
    when(producerBuilder.setClientConfiguration(any())).thenReturn(producerBuilder);

    // test onInvocation
    RocketMQPublisherSink publisherSink = new RocketMQPublisherSink(provider);
    publisherSink.onPipelineStarted(params, null);

    // test onEvent
    Event event = mock(Event.class);

    MessageBuilder messageBuilder = Mockito.mock(MessageBuilder.class);
    Message message = Mockito.mock(Message.class);

    when(provider.newMessageBuilder()).thenReturn(messageBuilder);
    when(messageBuilder.build()).thenReturn(message);
    when(messageBuilder.setTopic(anyString())).thenReturn(messageBuilder);
    when(messageBuilder.setBody(any())).thenReturn(messageBuilder);

    publisherSink.onEvent(event);
    verify(producer, times(1)).send(any(Message.class));

    // test onDetach
    publisherSink.onPipelineStopped();
    verify(producer, times(1)).close();
  }
}
