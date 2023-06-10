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
package org.apache.streampipes.sinks.brokers.jvm.pulsar;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.sinks.brokers.jvm.pulsar.PulsarPublisherSink.PULSAR_HOST_KEY;
import static org.apache.streampipes.sinks.brokers.jvm.pulsar.PulsarPublisherSink.PULSAR_PORT_KEY;
import static org.apache.streampipes.sinks.brokers.jvm.pulsar.PulsarPublisherSink.TOPIC_KEY;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestPulsarPublisherSink {
  @Test
  public void testSimpleEventSink() throws PulsarClientException {
    String pulsarHost = "localhost";
    Integer pulsarPort = 6650;
    String topic = "test";

    Map<String, Object> rawMap = new HashMap<>(2);

    rawMap.put("key1", "value1");
    rawMap.put("key2", "value2");

    SinkParams params = mock(SinkParams.class);
    DataSinkParameterExtractor extractor = mock(DataSinkParameterExtractor.class);
    when(params.extractor()).thenReturn(extractor);
    when(extractor.singleValueParameter(PULSAR_HOST_KEY, String.class)).thenReturn(pulsarHost);
    when(extractor.singleValueParameter(PULSAR_PORT_KEY, Integer.class)).thenReturn(pulsarPort);
    when(extractor.singleValueParameter(TOPIC_KEY, String.class)).thenReturn(topic);

    ClientBuilder clientBuilder = mock(ClientBuilder.class);
    PulsarClient pulsarClient = mock(PulsarClient.class);
    ProducerBuilder<byte[]> producerBuilder = mock(ProducerBuilder.class);
    Producer<byte[]> producer = mock(Producer.class);
    when(clientBuilder.serviceUrl(anyString())).thenReturn(clientBuilder);
    when(clientBuilder.build()).thenReturn(pulsarClient);
    when(pulsarClient.newProducer()).thenReturn(producerBuilder);
    when(producerBuilder.topic(topic)).thenReturn(producerBuilder);
    when(producerBuilder.create()).thenReturn(producer);
    when(producer.send(Mockito.any(byte[].class))).thenAnswer(data -> {
      HashMap<String, String> map;
      ObjectMapper mapper = new ObjectMapper();
      String json = new String((byte[]) data.getArgument(0));
      map = mapper.readValue(json, new TypeReference<>() {
      });
      Assert.assertEquals(map, rawMap);
      return null;
    });

    PulsarPublisherSink pulsarPublisherSink = new PulsarPublisherSink(clientBuilder);

    // Test invocation
    pulsarPublisherSink.onInvocation(params, null);

    verify(clientBuilder).serviceUrl(String.format("pulsar://%s:%d", pulsarHost, pulsarPort));

    // Test publish event
    Event event = mock(Event.class);
    when(event.getRaw()).thenReturn(rawMap);

    pulsarPublisherSink.onEvent(event);

    verify(producer, times(1)).send(Mockito.any(byte[].class));
  }
}
