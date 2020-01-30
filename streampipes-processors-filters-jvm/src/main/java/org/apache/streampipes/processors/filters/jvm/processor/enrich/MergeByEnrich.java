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
package org.apache.streampipes.processors.filters.jvm.processor.enrich;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.List;

public class MergeByEnrich implements EventProcessor<MergeByEnrichParameters> {

  private EventSchema outputSchema;
  private List<String> outputKeySelectors;
  private String selectedStream;

  private Event eventBuffer;

  @Override
  public void onInvocation(MergeByEnrichParameters composeParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    this.outputSchema = composeParameters.getGraph().getOutputStream().getEventSchema();
    this.outputKeySelectors = composeParameters.getOutputKeySelectors();

    if (composeParameters.getSelectedStream().equals("Stream 1")) {
      this.selectedStream = "s0";
    } else {
      this.selectedStream = "s1";
    }

    this.eventBuffer = null;
  }


  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) {
    String streamId = event.getSourceInfo().getSelectorPrefix();

    // Enrich the selected stream and store last event of other stream
    if (this.selectedStream.equals(streamId)) {
      if (this.eventBuffer != null) {
        Event result = mergeEvents(event, this.eventBuffer);
        spOutputCollector.collect(result);
      }
    } else {
      this.eventBuffer = event;
    }

  }

  @Override
  public void onDetach() {
    this.eventBuffer = null;
  }

  private Event mergeEvents(Event e1, Event e2) {
    return EventFactory.fromEvents(e1, e2, outputSchema).getSubset(outputKeySelectors);
  }

}
