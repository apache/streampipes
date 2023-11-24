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

package org.apache.streampipes.processors.transformation.jvm.processor.datetime;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFromStringProcessor extends StreamPipesDataProcessor {

  public static final String FIELD_ID = "inputField";
  public static final String OUTPUT_DATETIME_RUNTIME_NAME = "dateTime";
  public static final String SELECTED_INPUT_TIMEZONE = "inputTimeZone";

  private String streamInputDateTimeFieldName;
  private String selectedTimeZone;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.transformation.jvm.timeoperator.datetime", 0)
        .category(DataProcessorType.STRING_OPERATOR, DataProcessorType.TIME)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.stringReq(),
                Labels.withId(FIELD_ID),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(SELECTED_INPUT_TIMEZONE),
            Options.from(getTimeZoneOptions()))
        .outputStrategy(OutputStrategies.append(
            EpProperties.timestampProperty(OUTPUT_DATETIME_RUNTIME_NAME)))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
      EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    ProcessingElementParameterExtractor extractor = parameters.extractor();
    this.streamInputDateTimeFieldName = extractor.mappingPropertyValue(FIELD_ID);
    this.selectedTimeZone = extractor.selectedSingleValue(SELECTED_INPUT_TIMEZONE, String.class);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) {
    String dateTimeString = event.getFieldBySelector(streamInputDateTimeFieldName).getAsPrimitive().getAsString();
    DateTimeFormatter dtFormatter = DateTimeFormatter.ISO_DATE_TIME;
    ZonedDateTime zdt = parseDateTime(dateTimeString, dtFormatter);

    event.addField(OUTPUT_DATETIME_RUNTIME_NAME, zdt);
    collector.collect(event);
  }

  @Override
  public void onDetach() {

  }

  private ZonedDateTime parseDateTime(String dateTimeString, DateTimeFormatter dtf) {
    ZonedDateTime zdt;
    try {
      zdt = ZonedDateTime.parse(dateTimeString);

    } catch (DateTimeParseException e1) {
      try {
        LocalDateTime ldt = LocalDateTime.parse(dateTimeString, dtf);
        ZoneId timeZoneId = ZoneId.of(selectedTimeZone);
        zdt = ldt.atZone(timeZoneId);
      } catch (DateTimeParseException e2) {
        throw new RuntimeException("Could not parse DateTime String: " + dateTimeString);
      }
    }
    return zdt;
  }

  private static String[] getTimeZoneOptions() {
    return ZoneId.getAvailableZoneIds().toArray(new String[0]);
  }
}
