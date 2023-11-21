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
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.test.generator.grounding.EventGroundingGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.BYTE_SIZE;
import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.KILOBYTE_SIZE;
import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.MEGABYTE_SIZE;
import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.SIZE_UNIT;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestSizeMeasureProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(TestSizeMeasureProcessor.class);

  @org.junit.runners.Parameterized.Parameters
  public static Iterable<Object[]> data() {

    /*
    249 bytes is the base size of an object when first tested.
    Allowing up to 250 bytes comparison error in case of inconsistent base sizes across platforms
     */
    return Arrays.asList(new Object[][]{
            { BYTE_SIZE, 10240 - 249, 10240.0, 250.0},
            { KILOBYTE_SIZE, 10240 - 249, 10.0, 0.025},
            { MEGABYTE_SIZE, (1024 * 1024) - 249, 1.0, 0.0025}
    });
  }

  @Parameterized.Parameter
  public String sizeUnit;

  @Parameterized.Parameter(1)
  public int numOfBytes;

  @Parameterized.Parameter(2)
  public double expectedSize;

  @Parameterized.Parameter(3)
  public double allowableError;

  @Test
  public void testSizeMeasureProcessor() {
    SizeMeasureProcessor processor = new SizeMeasureProcessor();
    DataProcessorDescription originalGraph = processor.declareModel();
    originalGraph.setSupportedGrounding(EventGroundingGenerator.makeDummyGrounding());

    DataProcessorInvocation graph =
            InvocationGraphGenerator.makeEmptyInvocation(originalGraph);


    ProcessorParams params = new ProcessorParams(graph);
    params.extractor().getStaticPropertyByName(SIZE_UNIT, OneOfStaticProperty.class).getOptions()
            .stream().filter(ot -> ot.getInternalName().equals(sizeUnit)).findFirst()
            .get().setSelected(true);


    SpOutputCollector eventCollector = new StoreEventCollector();

    processor.onInvocation(params, eventCollector, null);

    LOG.info("Sending event with numOfBytes "
        + numOfBytes + ", sizeUnit: " + sizeUnit);
    double size = sendEvents(processor, eventCollector);

    LOG.info("Expected size is {}", expectedSize);
    LOG.info("Actual size is {}", size);
    LOG.info("Allowable error is {}", allowableError);
    assertEquals(expectedSize, size, allowableError);
  }

  private Double sendEvents(SizeMeasureProcessor processor, SpOutputCollector spOut) {
    double size = 0.0;
    Event event = makeEvent(numOfBytes);

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