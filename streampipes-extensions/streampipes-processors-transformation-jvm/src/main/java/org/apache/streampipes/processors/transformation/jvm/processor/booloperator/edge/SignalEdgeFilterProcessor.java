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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.ArrayList;
import java.util.List;

public class SignalEdgeFilterProcessor extends StreamPipesDataProcessor {

  public static final String BOOLEAN_SIGNAL_FIELD = "boolean_signal_field";
  public static final String FLANK_ID = "flank";
  public static final String DELAY_ID = "delay";
  private static final String EVENT_SELECTION_ID = "event-selection-id";

  public static final String FLANK_UP = "FALSE -> TRUE";
  public static final String FLANK_DOWN = "TRUE -> FALSE";
  public static final String BOTH = "BOTH";
  public static final String OPTION_FIRST = "First";
  public static final String OPTION_LAST = "Last";
  public static final String OPTION_ALL = "All";

  private String booleanSignalField;
  private String flank;
  private Integer delay;
  private String eventSelection;

  private boolean lastValue;
  private int delayCount;
  private List<Event> resultEvents;
  private boolean edgeDetected;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge")
        .category(DataProcessorType.BOOLEAN_OPERATOR, DataProcessorType.FILTER)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.booleanReq(), Labels.withId(BOOLEAN_SIGNAL_FIELD),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(FLANK_ID), Options.from(BOTH, FLANK_UP, FLANK_DOWN))
        .requiredIntegerParameter(Labels.withId(DELAY_ID), 0)
        .requiredSingleValueSelection(Labels.withId(EVENT_SELECTION_ID),
            Options.from(OPTION_FIRST, OPTION_LAST, OPTION_ALL))
        .outputStrategy(OutputStrategies.keep())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    booleanSignalField = parameters.extractor().mappingPropertyValue(BOOLEAN_SIGNAL_FIELD);
    flank = parameters.extractor().selectedSingleValue(FLANK_ID, String.class);
    delay = parameters.extractor().singleValueParameter(DELAY_ID, Integer.class);
    eventSelection = parameters.extractor().selectedSingleValue(EVENT_SELECTION_ID, String.class);

    this.lastValue = false;
    this.delayCount = 0;
    this.resultEvents = new ArrayList<>();
    this.edgeDetected = false;
  }

  @Override
  public void onEvent(Event inputEvent,
                      SpOutputCollector collector) throws SpRuntimeException {
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
          collector.collect(event);
        }

        this.edgeDetected = false;

      } else {
        this.delayCount++;
      }
    }

    this.lastValue = value;
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  private boolean detectEdge(boolean value, boolean lastValue) {
    if (this.flank.equals(SignalEdgeFilterProcessor.FLANK_UP)) {
      return !lastValue && value;
    } else if (this.flank.equals(SignalEdgeFilterProcessor.FLANK_DOWN)) {
      return lastValue && !value;
    } else if (this.flank.equals(SignalEdgeFilterProcessor.BOTH)) {
      return value != lastValue;
    }

    return false;
  }

  private void addResultEvent(Event event) {
    if (this.eventSelection.equals(SignalEdgeFilterProcessor.OPTION_FIRST)) {
      if (this.resultEvents.size() == 0) {
        this.resultEvents.add(event);
      }
    } else if (this.eventSelection.equals(SignalEdgeFilterProcessor.OPTION_LAST)) {
      this.resultEvents = new ArrayList<>();
      this.resultEvents.add(event);
    } else if (this.eventSelection.equals(SignalEdgeFilterProcessor.OPTION_ALL)) {
      this.resultEvents.add(event);
    }
  }
}
