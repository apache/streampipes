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

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class SignalEdgeFilterController extends StandaloneEventProcessingDeclarer<SignalEdgeFilterParameters> {

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
  public ConfiguredEventProcessor<SignalEdgeFilterParameters> onInvocation(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {

    String booleanSignalField = extractor.mappingPropertyValue(BOOLEAN_SIGNAL_FIELD);
    String flank = extractor.selectedSingleValue(FLANK_ID, String.class);
    Integer delay = extractor.singleValueParameter(DELAY_ID, Integer.class);
    String eventSelection = extractor.selectedSingleValue(EVENT_SELECTION_ID, String.class);

    SignalEdgeFilterParameters params =
        new SignalEdgeFilterParameters(graph, booleanSignalField, flank, delay, eventSelection);

    return new ConfiguredEventProcessor<>(params, SignalEdgeFilter::new);
  }
}
