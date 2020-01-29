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
package org.apache.streampipes.processors.filters.jvm.processor.merge;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.List;

public class MergeByTime implements EventProcessor<MergeByTimeParameters> {

  private EventSchema outputSchema;
  private List<String> outputKeySelectors;

  private String timestampFieldStream0;
  private String timestampFieldStream1;
  private Integer timeInterval;

  private StreamBuffer streamBufferS0;
  private StreamBuffer streamBufferS1;

  @Override
  public void onInvocation(MergeByTimeParameters composeParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    this.outputSchema = composeParameters.getGraph().getOutputStream().getEventSchema();
    this.outputKeySelectors = composeParameters.getOutputKeySelectors();
    this.timestampFieldStream0 = composeParameters.getTimestampFieldStream1();
    this.timestampFieldStream1 = composeParameters.getTimestampFieldStream2();
    this.timeInterval = composeParameters.getTimeInterval();

    this.streamBufferS0 = new StreamBuffer(this.timestampFieldStream0);
    this.streamBufferS1 = new StreamBuffer(this.timestampFieldStream1);
  }


  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) {
    String streamId = event.getSourceInfo().getSelectorPrefix();

    // Decide to which buffer the event should be added
    if ("s0".equals(streamId)) {
      this.streamBufferS0.add(event);
    } else {
      this.streamBufferS1.add(event);
    }

    // Calculate matching events between data streams
    for (Event e0 : this.streamBufferS0.getList()) {
      long time0 = e0.getFieldBySelector(timestampFieldStream0).getAsPrimitive().getAsLong();
      for (Event e1 : this.streamBufferS1.getList()) {
        long time1 = e1.getFieldBySelector(timestampFieldStream1).getAsPrimitive().getAsLong();

        if (time0 + timeInterval > time1 && time1 > time0 - timeInterval) {
          Event resultingEvent = mergeEvents(e0, e1);
          spOutputCollector.collect(resultingEvent);
          this.streamBufferS0.removeOldEvents(time0);
          this.streamBufferS1.removeOldEvents(time1);
        }
      }
    }

    // Clean up buffer if events do not match to avoid buffer overflow
    if (this.streamBufferS0.getLength() > 0 && this.streamBufferS1.getLength() > 0) {
      Event e0 = this.streamBufferS0.get(0);
      Event e1 = this.streamBufferS1.get(0);

      long time0 = e0.getFieldBySelector(this.timestampFieldStream0).getAsPrimitive().getAsLong();
      long time1 = e1.getFieldBySelector(this.timestampFieldStream1).getAsPrimitive().getAsLong();

      if (time0 > time1) {
        this.streamBufferS0.removeOldEvents(time0);
        this.streamBufferS1.removeOldEvents(time0);
      } else {
        this.streamBufferS0.removeOldEvents(time1);
        this.streamBufferS1.removeOldEvents(time1);
      }
    }

  }

  @Override
  public void onDetach() {
    this.streamBufferS0.reset();
    this.streamBufferS1.reset();
  }

  private Event mergeEvents(Event e1, Event e2) {
    return EventFactory.fromEvents(e1, e2, outputSchema).getSubset(outputKeySelectors);
  }

}
