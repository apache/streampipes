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

import io.siddhi.core.event.Event;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.query.api.definition.Attribute;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

import java.util.List;

public class SiddhiOutputStreamCallback extends StreamCallback {

  private SpOutputCollector collector;
  private EventProcessorRuntimeContext runtimeContext;

  private List<Attribute> streamAttributes;

  public SiddhiOutputStreamCallback(SpOutputCollector collector,
                                    EventProcessorRuntimeContext runtimeContext,
                                    List<Attribute> streamAttributes) {
    this.collector = collector;
    this.runtimeContext = runtimeContext;
    this.streamAttributes = streamAttributes;
  }

  @Override
  public void receive(Event[] events) {
    if (events.length > 0) {
      Event lastEvent = events[events.length - 1];
      collector.collect(SiddhiUtils.toSpEvent(lastEvent,
              runtimeContext.getOutputSchemaInfo(),
              runtimeContext.getOutputSourceInfo(),
              streamAttributes));
    }
  }
}
