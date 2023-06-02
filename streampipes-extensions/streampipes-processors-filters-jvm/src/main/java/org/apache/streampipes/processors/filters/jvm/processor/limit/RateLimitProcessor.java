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

package org.apache.streampipes.processors.filters.jvm.processor.limit;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.EventSelection;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.WindowFactory;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.WindowType;
import org.apache.streampipes.processors.filters.jvm.processor.limit.window.Window;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RateLimitProcessor extends StreamPipesDataProcessor {

  private static final String EVENT_SELECTION = "event-selection";
  private static final String WINDOW_TYPE = "window-type";
  private static final String LENGTH_WINDOW = "length-window";
  private static final String LENGTH_WINDOW_SIZE = "length-window-size";
  private static final String TIME_WINDOW = "time-window";
  private static final String TIME_WINDOW_SIZE = "time-window-size";
  private static final String CRON_WINDOW = "cron-window";
  private static final String CRON_WINDOW_EXPR = "cron-window-expr";
  private static final String GROUPING_FIELD = "grouping-field";
  private static final String GROUPING_ENABLED = "grouping-enabled";
  private static final String OPTION_FALSE = "False";
  private static final String OPTION_TRUE = "True";
  private static final String OPTION_FIRST = "First";
  private static final String OPTION_LAST = "Last";
  private static final String OPTION_ALL = "All";

  private static final String DEFAULT_GROUP = "default";
  private Boolean groupingEnabled;
  private String groupingField;
  private ConcurrentMap<Object, Window> windows;
  private WindowFactory factory;


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.limit")
        .category(DataProcessorType.FILTER, DataProcessorType.STRUCTURE_ANALYTICS)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredSingleValueSelection(Labels.withId(GROUPING_ENABLED),
            Options.from(OPTION_TRUE, OPTION_FALSE))
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                Labels.withId(GROUPING_FIELD), PropertyScope.NONE)
            .build())
        .requiredAlternatives(Labels.withId(WINDOW_TYPE),
            Alternatives.from(Labels.withId(TIME_WINDOW),
                StaticProperties.integerFreeTextProperty(Labels.withId(TIME_WINDOW_SIZE))),
            Alternatives.from(Labels.withId(CRON_WINDOW),
                StaticProperties.stringFreeTextProperty(Labels.withId(CRON_WINDOW_EXPR))),
            Alternatives.from(Labels.withId(LENGTH_WINDOW),
                StaticProperties.integerFreeTextProperty(Labels.withId(LENGTH_WINDOW_SIZE))))
        .requiredSingleValueSelection(Labels.withId(EVENT_SELECTION),
            Options.from(OPTION_FIRST, OPTION_LAST, OPTION_ALL))
        .outputStrategy(OutputStrategies.keep())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams
                               processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {

    this.groupingEnabled =
        Boolean.valueOf(processorParams.extractor().selectedSingleValue(GROUPING_ENABLED, String.class));
    this.groupingField = processorParams.extractor().mappingPropertyValue(GROUPING_FIELD);
    this.windows = new ConcurrentHashMap<>();

    EventSelection eventSelection = EventSelection.valueOf(processorParams.extractor()
        .selectedSingleValue(EVENT_SELECTION, String.class).toUpperCase());
    String windowType = processorParams.extractor().selectedAlternativeInternalId(WINDOW_TYPE);

    if (TIME_WINDOW.equals(windowType)) {
      Integer windowSize = processorParams.extractor().singleValueParameter(TIME_WINDOW_SIZE, Integer.class);
      this.factory = new WindowFactory(
          WindowType.TIME,
          windowSize,
          eventSelection,
          spOutputCollector);

    } else if (CRON_WINDOW.equals(windowType)) {
      String cronExpression = processorParams.extractor().singleValueParameter(CRON_WINDOW_EXPR, String.class);
      this.factory = new WindowFactory(
          WindowType.CRON,
          cronExpression,
          eventSelection,
          spOutputCollector);

    } else {
      Integer windowSize = processorParams.extractor().singleValueParameter(LENGTH_WINDOW_SIZE, Integer.class);
      this.factory = new WindowFactory(
          WindowType.LENGTH,
          windowSize,
          eventSelection,
          spOutputCollector);
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    Object group = groupingEnabled ? getGroupKey(event) : DEFAULT_GROUP;
    Window window = windows.get(group);
    if (window == null) {
      window = factory.create();
      window.init();
      windows.put(group, window);
    }
    window.onEvent(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    for (Window window : this.windows.values()) {
      window.destroy();
    }
  }

  private Object getGroupKey(Event event) {
    return event.getFieldBySelector(groupingField).getAsPrimitive().getAsString();
  }
}
