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

package org.apache.streampipes.processors.filters.jvm.processor.textfilter;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.EventStreamGenerator;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestTextFilterProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestTextFilterProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {
    return Arrays.asList(new Object[][] {
        {"TestLowerCaseMatch", "keyword", StringOperator.MATCHES, Arrays.asList("keyword", "KeyWord", "KEYWORD"), 1},
        {"TestUpperCaseMatch", "KEYWORD", StringOperator.MATCHES, Arrays.asList("keyword", "KeyWord", "KEYWORD"), 1},
        {"TestMixMatch", "KeyWord", StringOperator.MATCHES, Arrays.asList("keyword", "KeyWord", "KEYWORD"), 1},
        {"TestEmptyMatch", "keYWord", StringOperator.MATCHES, Arrays.asList("keyword", "KeyWord", "KEYWORD"), 0},
        {"TestMultipleMatch", "KeyWord", StringOperator.MATCHES,
            Arrays.asList("keyword", "KeyWord", "KEYWORD", "KeyWord"), 2},

        {"TestContainsWord", "keyword", StringOperator.CONTAINS,
            Arrays.asList("text contains keyword", "text doesn't have word"), 1},
        {"TestNotContainsWord", "keyword", StringOperator.CONTAINS,
            Arrays.asList("text is empty", "text doesn't have word"), 0},
    });
  }

  @org.junit.runners.Parameterized.Parameter
  public String selectedFieldName;

  @org.junit.runners.Parameterized.Parameter(1)
  public String keyword;

  @org.junit.runners.Parameterized.Parameter(2)
  public StringOperator stringOperator;

  @org.junit.runners.Parameterized.Parameter(3)
  public List<String> eventStrings;

  @org.junit.runners.Parameterized.Parameter(4)
  public int expectedFilteredTextCount;

  @Test
  public void testTextFilter() {
    TextFilterProcessor textFilterProcessor = new TextFilterProcessor();
    DataProcessorDescription originalGraph = textFilterProcessor.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(originalGraph);
    graph.setInputStreams(Collections
        .singletonList(EventStreamGenerator
            .makeStreamWithProperties(Collections.singletonList(selectedFieldName))));
    graph.setOutputStream(EventStreamGenerator.makeStreamWithProperties(Collections.singletonList(selectedFieldName)));
    graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicDefinition()
        .setActualTopicName("output-topic");

    graph.getStaticProperties().stream()
        .filter(p -> p instanceof MappingPropertyUnary)
        .map(p -> (MappingPropertyUnary) p)
        .filter(p -> p.getInternalName().equals(TextFilterProcessor.MAPPING_PROPERTY_ID))
        .findFirst().get().setSelectedProperty("s0::" + selectedFieldName);
    ProcessorParams params = new ProcessorParams(graph);
    params.extractor().getStaticPropertyByName(TextFilterProcessor.OPERATION_ID, OneOfStaticProperty.class)
        .getOptions()
        .stream().filter(o -> o.getName().equals(stringOperator.name())).findFirst().get().setSelected(true);
    params.extractor().getStaticPropertyByName(TextFilterProcessor.KEYWORD_ID, FreeTextStaticProperty.class)
        .setValue(keyword);
    StoreEventCollector collector = new StoreEventCollector();

    textFilterProcessor.onInvocation(params, collector, null);
    int result = sendEvents(textFilterProcessor, collector);

    LOG.info("Expected filtered text count is {}", expectedFilteredTextCount);
    LOG.info("Actual filtered text count is {}", result);
    assertEquals(expectedFilteredTextCount, result);

  }

  private int sendEvents(TextFilterProcessor textFilterProcessor, StoreEventCollector collector) {
    List<Event> events = makeEvents();
    for (Event e : events) {
      textFilterProcessor.onEvent(e, collector);
    }
    return collector.getEvents().size();
  }

  private List<Event> makeEvents() {
    List<Event> events = new ArrayList<>();
    for (String eventString : eventStrings) {
      events.add(makeEvent(eventString));
    }
    return events;
  }

  private Event makeEvent(String eventString) {
    Map<String, Object> map = Maps.newHashMap();
    map.put(selectedFieldName, eventString);
    return EventFactory.fromMap(map,
        new SourceInfo("test", "s0"),
        new SchemaInfo(null, Lists.newArrayList()));
  }
}
