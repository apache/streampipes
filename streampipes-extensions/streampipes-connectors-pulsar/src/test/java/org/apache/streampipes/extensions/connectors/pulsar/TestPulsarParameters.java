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
package org.apache.streampipes.extensions.connectors.pulsar;

import static org.apache.streampipes.extensions.connectors.pulsar.sink.PulsarPublisherSink.PULSAR_HOST_KEY;
import static org.apache.streampipes.extensions.connectors.pulsar.sink.PulsarPublisherSink.PULSAR_PORT_KEY;
import static org.apache.streampipes.extensions.connectors.pulsar.sink.PulsarPublisherSink.TOPIC_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.connectors.pulsar.sink.PulsarParameters;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;

import org.junit.jupiter.api.Test;

public class TestPulsarParameters {
  @Test
  public void testInitPulsarParameters() {
    String pulsarHost = "localhost";
    Integer pulsarPort = 6650;
    String topic = "test";

    var params = mock(IDataSinkParameters.class);
    DataSinkParameterExtractor extractor = mock(DataSinkParameterExtractor.class);
    when(params.extractor()).thenReturn(extractor);
    when(extractor.singleValueParameter(PULSAR_HOST_KEY, String.class)).thenReturn(pulsarHost);
    when(extractor.singleValueParameter(PULSAR_PORT_KEY, Integer.class)).thenReturn(pulsarPort);
    when(extractor.singleValueParameter(TOPIC_KEY, String.class)).thenReturn(topic);

    PulsarParameters pulsarParameters = new PulsarParameters(params);

    assertEquals(pulsarHost, pulsarParameters.getPulsarHost());
    assertEquals(pulsarPort, pulsarParameters.getPulsarPort());
    assertEquals(topic, pulsarParameters.getTopic());
  }
}
