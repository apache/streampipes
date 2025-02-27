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

package org.apache.streampipes.processors.filters.jvm.processor.limit;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRateLimitProcessor {

  private RateLimitProcessor processor;

  @Mock
  private DataProcessorInvocation dataProcessorInvocation;

  @Mock
  private SpDataStream outputStream;

  @Mock
  private EventGrounding eventGrounding;

  @Mock
  private TransportProtocol transportProtocol;

  @Mock
  private TopicDefinition topicDefinition;

  @Mock
  private OutputStrategy outputStrategy;

  private ProcessingElementParameterExtractor extractor;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    processor = new RateLimitProcessor();

    // Mocking topic-related methods
    when(dataProcessorInvocation.getOutputStream()).thenReturn(outputStream);
    when(outputStream.getEventGrounding()).thenReturn(eventGrounding);
    when(eventGrounding.getTransportProtocol()).thenReturn(transportProtocol);
    when(transportProtocol.getTopicDefinition()).thenReturn(topicDefinition);
    when(topicDefinition.getActualTopicName()).thenReturn("test-topic");

    // Mocking output strategy
    CustomOutputStrategy customOutputStrategy = mock(CustomOutputStrategy.class);
    when(customOutputStrategy.getSelectedPropertyKeys()).thenReturn(Collections.singletonList("temperature"));
    when(dataProcessorInvocation.getOutputStrategies()).thenReturn(List.of(customOutputStrategy));

    // Initialize extractor properly
    extractor = new ProcessingElementParameterExtractor(dataProcessorInvocation);
  }

  @Test
  public void testOutputTopic() {
    assertEquals("test-topic", extractor.outputTopic());
  }

  @Test
  public void testOutputKeySelectors() {
    List<String> keys = extractor.outputKeySelectors();
    assertEquals(1, keys.size());
    assertEquals("temperature", keys.get(0));
  }

  @Test
  public void testOutputKeySelectors_WithCustomStrategy() {
    CustomOutputStrategy customOutputStrategy = mock(CustomOutputStrategy.class);
    when(customOutputStrategy.getSelectedPropertyKeys()).thenReturn(List.of("temperature", "humidity"));

    when(dataProcessorInvocation.getOutputStrategies()).thenReturn(List.of(customOutputStrategy));

    List<String> keys = extractor.outputKeySelectors();
    assertEquals(2, keys.size());
    assertEquals("temperature", keys.get(0));
    assertEquals("humidity", keys.get(1));
  }

  @Test
  public void testOutputKeySelectors_NoCustomStrategy() {
    OutputStrategy otherStrategy = mock(OutputStrategy.class);

    when(dataProcessorInvocation.getOutputStrategies()).thenReturn(List.of(otherStrategy));

    List<String> keys = extractor.outputKeySelectors();
    assertTrue(keys.isEmpty());
  }

  @Test
  public void testOutputKeySelectors_MultipleStrategies() {
    CustomOutputStrategy customOutputStrategy = mock(CustomOutputStrategy.class);
    when(customOutputStrategy.getSelectedPropertyKeys()).thenReturn(List.of("windSpeed"));

    OutputStrategy otherStrategy = mock(OutputStrategy.class);

    when(dataProcessorInvocation.getOutputStrategies()).thenReturn(List.of(customOutputStrategy, otherStrategy));

    List<String> keys = extractor.outputKeySelectors();
    assertEquals(1, keys.size());
    assertEquals("windSpeed", keys.get(0));
  }

  @Test
  public void testOutputTopic_NullTopicDefinition() {
    when(transportProtocol.getTopicDefinition()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> extractor.outputTopic());
  }

  @Test
  public void testOutputTopic_EmptyTopicName() {
    when(topicDefinition.getActualTopicName()).thenReturn("");

    assertEquals("", extractor.outputTopic());
  }

  @Test
  public void testOutputTopic_NullTopicName() {
    when(topicDefinition.getActualTopicName()).thenReturn(null);

    assertNull(extractor.outputTopic());
  }
}