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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge;

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.List;

public class SignalEdgeFilter implements EventProcessor<SignalEdgeFilterParameters> {

  private static Logger log;

  private String booleanSignalField;
  private String flank;
  private Integer delay;
  private String eventSelection;

  private boolean lastValue;
  private int delayCount;
  private List<Event> resultEvents;
  private boolean edgeDetected;

  @Override
  public void onInvocation(SignalEdgeFilterParameters booleanInverterParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    log = booleanInverterParameters.getGraph().getLogger(SignalEdgeFilter.class);
    this.booleanSignalField = booleanInverterParameters.getBooleanSignalField();
    this.flank = booleanInverterParameters.getFlank();
    this.delay = booleanInverterParameters.getDelay();
    this.eventSelection = booleanInverterParameters.getEventSelection();

    this.lastValue = false;
    this.delayCount = 0;
    this.resultEvents = new ArrayList<>();
    this.edgeDetected = false;
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

    boolean value = inputEvent.getFieldBySelector(this.booleanSignalField).getAsPrimitive().getAsBoolean();

    // Detect edges in signal
    if (detectEdge(value, lastValue)) {
      this.edgeDetected = true;
      this.resultEvents = new ArrayList<>();
      this.delayCount = 0;
    }

    if (edgeDetected) {
      // Buffer event(s) according to user configuration
      addResultEvent(inputEvent);

      // Detect if the delay has been waited for
      if (this.delay == delayCount) {
        for (Event event : this.resultEvents) {
          out.collect(event);
        }

        this.edgeDetected = false;

      } else {
        this.delayCount++;
      }
    }

    this.lastValue = value;
  }

  @Override
  public void onDetach() {
  }

  private boolean detectEdge(boolean value, boolean lastValue) {
    if (this.flank.equals(SignalEdgeFilterController.FLANK_UP)) {
      return !lastValue && value;
    } else if (this.flank.equals(SignalEdgeFilterController.FLANK_DOWN)) {
      return lastValue && !value;
    } else if (this.flank.equals(SignalEdgeFilterController.BOTH)) {
      return value != lastValue;
    }

    return false;
  }

  private void addResultEvent(Event event) {
    if (this.eventSelection.equals(SignalEdgeFilterController.OPTION_FIRST)) {
      if (this.resultEvents.size() == 0) {
        this.resultEvents.add(event);
      }
    } else if (this.eventSelection.equals(SignalEdgeFilterController.OPTION_LAST)) {
      this.resultEvents = new ArrayList<>();
      this.resultEvents.add(event);
    } else if (this.eventSelection.equals(SignalEdgeFilterController.OPTION_ALL)) {
      this.resultEvents.add(event);
    }
  }
}
