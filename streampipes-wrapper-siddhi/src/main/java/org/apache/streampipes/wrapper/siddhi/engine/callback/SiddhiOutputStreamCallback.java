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
package org.apache.streampipes.wrapper.siddhi.engine.callback;

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.siddhi.output.SiddhiListOutputConfig;
import org.apache.streampipes.wrapper.siddhi.output.SiddhiOutputConfig;
import org.apache.streampipes.wrapper.siddhi.output.SiddhiOutputType;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

import io.siddhi.core.event.Event;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.query.api.definition.Attribute;

import java.util.Arrays;
import java.util.List;

public class SiddhiOutputStreamCallback extends StreamCallback {

  private final SpOutputCollector collector;
  private final IDataProcessorParameters runtimeParameters;
  private final SiddhiOutputConfig outputConfig;

  private final List<Attribute> streamAttributes;

  public SiddhiOutputStreamCallback(SpOutputCollector collector,
                                    IDataProcessorParameters runtimeParameters,
                                    List<Attribute> streamAttributes,
                                    SiddhiOutputConfig outputConfig) {
    this.collector = collector;
    this.runtimeParameters = runtimeParameters;
    this.streamAttributes = streamAttributes;
    this.outputConfig = outputConfig;
  }

  private void sendEvents(List<Event> events) {
    collector.collect(SiddhiUtils.toSpEvent(events,
        ((SiddhiListOutputConfig) outputConfig).getListFieldName(),
        runtimeParameters.getOutputSchemaInfo(),
        runtimeParameters.getOutputSourceInfo(),
        streamAttributes));
  }

  private void sendEvent(Event event) {
    collector.collect(SiddhiUtils.toSpEvent(event,
        runtimeParameters.getOutputSchemaInfo(),
        runtimeParameters.getOutputSourceInfo(),
        streamAttributes));
  }

  @Override
  public void receive(Event[] inEvents) {
    if (inEvents.length > 0) {
      if (this.outputConfig.getSiddhiOutputType() == SiddhiOutputType.FIRST) {
        Event lastEvent = inEvents[inEvents.length - 1];
        sendEvent(lastEvent);
      } else if (this.outputConfig.getSiddhiOutputType() == SiddhiOutputType.LIST) {
        sendEvents(Arrays.asList(inEvents));
      }
    }
  }
}
