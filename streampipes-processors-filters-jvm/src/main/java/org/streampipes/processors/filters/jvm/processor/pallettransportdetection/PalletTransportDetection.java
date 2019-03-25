/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.processors.filters.jvm.processor.pallettransportdetection;

import java.util.List;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.EventFactory;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class PalletTransportDetection implements EventProcessor<PalletTransportDetectionParameters> {

  private EventSchema outputSchema;
  private List<String> outputKeySelectors;

  private String startTs;
  private String endTs;

  private boolean startValid = false;
  private Event lastStartEvent;


  @Override
  public void onInvocation(PalletTransportDetectionParameters palletTransportDetectionParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    this.outputSchema = palletTransportDetectionParameters.getGraph().getOutputStream().getEventSchema();
    this.outputKeySelectors = palletTransportDetectionParameters.getOutputKeySelectors();

    this.startTs = palletTransportDetectionParameters.getStartTs();
    this.endTs = palletTransportDetectionParameters.getEndTs();

  }

  @Override
  public void onDetach() {
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) {
    if (event.getSourceInfo().getSelectorPrefix().equals("s0")) {
      // Startevent
      startValid = true;
      lastStartEvent = event;
    } else {
      // Endevent
      if (startValid) {
        // Merge events
        spOutputCollector.collect(mergeEvents(event));
        startValid = false;
      }
    }
  }

  private Event mergeEvents(Event event) {
    return EventFactory
        .fromEvents(lastStartEvent, event, outputSchema)
        .getSubset(outputKeySelectors);
  }
}
