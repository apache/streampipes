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

package org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure;

import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.enums.Sizes;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestSizeMeasureProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestSizeMeasureProcessor.class);

  @ParameterizedTest
  @EnumSource(Sizes.class)
  public void testSizeMeasureProcessor(Sizes sizes) {
    SizeMeasureProcessor processor = new SizeMeasureProcessor();
    DataProcessorDescription originalGraph = processor.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph =
            InvocationGraphGenerator.makeEmptyInvocation(originalGraph);


    ProcessorParams params = new ProcessorParams(graph);
    params.extractor().getStaticPropertyByName(SizeMeasureProcessor.SIZE_UNIT, OneOfStaticProperty.class).getOptions()
            .stream().filter(ot -> ot.getInternalName().equals(sizes.getSizeUnit())).findFirst()
            .get().setSelected(true);


    SpOutputCollector eventCollector = new StoreEventCollector();

    processor.onInvocation(params, eventCollector, null);

    LOG.info("Sending event with numOfBytes "
            + sizes.getNumOfBytes() + ", sizeUnit: " + sizes.getSizeUnit());
    double size = sendEvents(processor, eventCollector, sizes);

    LOG.info("Expected size is {}", sizes.getExpectedSize());
    LOG.info("Actual size is {}", size);
    LOG.info("Allowable error is {}", sizes.getAllowableError());
    assertEquals(sizes.getExpectedSize(), size, sizes.getAllowableError());
  }

  private Double sendEvents(SizeMeasureProcessor processor, SpOutputCollector spOut, Sizes sizes) {
    double size = 0.0;
    Event event = makeEvent(sizes.getNumOfBytes());

    processor.onEvent(event, spOut);
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    try {
      size = event.getFieldBySelector(SizeMeasureProcessor.EVENT_SIZE)
              .getAsPrimitive()
              .getAsDouble();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
    return size;
  }

  private Event makeEvent(int numOfBytes) {
    Map<String, Object> map = new HashMap<>();
    map.put("Test", new ObjectWithSize(numOfBytes));
    return EventFactory.fromMap(map,
            new SourceInfo("test" + "-topic", "s0"),
            new SchemaInfo(null, new ArrayList<>()));
  }

  public static class ObjectWithSize implements Serializable {
    private final byte[] data;

    public ObjectWithSize(int numOfBytes) {
      this.data = new byte[numOfBytes];
    }
  }
}