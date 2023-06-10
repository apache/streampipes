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

package org.apache.streampipes.processors.filters.jvm.processor.throughputmon;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.apache.commons.lang3.time.StopWatch;

public class ThroughputMonitorProcessor extends StreamPipesDataProcessor {

  private static final String BATCH_WINDOW_KEY = "batch-window-key";

  private static final String TIMESTAMP_FIELD = "timestamp";
  private static final String START_TIME_FIELD = "starttime";
  private static final String END_TIME_FIELD = "endtime";
  private static final String DURATION_FIELD = "duration";
  private static final String EVENT_COUNT_FIELD = "eventcount";
  private static final String THROUGHPUT_FIELD = "throughput";

  private int batchSize;

  private StopWatch stopWatch;
  private long eventCount = 0;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.throughputmon")
        .category(DataProcessorType.STRUCTURE_ANALYTICS)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty()).build())
        .outputStrategy(OutputStrategies.fixed(
            EpProperties.timestampProperty(TIMESTAMP_FIELD),
            EpProperties.longEp(Labels.withId(START_TIME_FIELD), START_TIME_FIELD, SO.DATE_TIME),
            EpProperties.longEp(Labels.withId(END_TIME_FIELD), END_TIME_FIELD, SO.DATE_TIME),
            EpProperties.longEp(Labels.withId(DURATION_FIELD), DURATION_FIELD, SO.NUMBER),
            EpProperties.longEp(Labels.withId(EVENT_COUNT_FIELD), EVENT_COUNT_FIELD, SO.NUMBER),
            EpProperties.doubleEp(Labels.withId(THROUGHPUT_FIELD), THROUGHPUT_FIELD, SO.NUMBER)))
        .requiredIntegerParameter(Labels.withId(BATCH_WINDOW_KEY))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.batchSize = parameters.extractor().singleValueParameter(BATCH_WINDOW_KEY, Integer.class);
    this.stopWatch = new StopWatch();
    this.restartTimer();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    this.eventCount++;
    if (this.eventCount == this.batchSize) {
      this.stopWatch.stop();
      Event outEvent = buildEvent();
      collector.collect(outEvent);
      this.eventCount = 0;
      this.restartTimer();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.stopWatch.stop();
  }

  private Event buildEvent() {
    Event event = new Event();
    event.addField(TIMESTAMP_FIELD, System.currentTimeMillis());
    event.addField(START_TIME_FIELD, this.stopWatch.getStartTime());
    event.addField(END_TIME_FIELD, this.stopWatch.getStopTime());
    event.addField(DURATION_FIELD, this.stopWatch.getTime());
    event.addField(EVENT_COUNT_FIELD, this.eventCount);
    event.addField(THROUGHPUT_FIELD, (this.eventCount / (((double) this.stopWatch.getTime()) / 1000)));

    return event;
  }

  private void restartTimer() {
    this.stopWatch.reset();
    this.eventCount = 0;
    this.stopWatch.start();
  }
}
