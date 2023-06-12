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

package org.apache.streampipes.processors.transformation.jvm.processor.timestampextractor;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
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
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimestampExtractorProcessor extends StreamPipesDataProcessor
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String TIMESTAMP_FIELD = "timestampField";
  public static final String SELECTED_OUTPUT_FIELDS = "selectedOutputFields";

  private String timestampField;
  private List<String> outputFields;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.processor.timestampextractor")
        .category(DataProcessorType.TIME)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(
            StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.withId(TIMESTAMP_FIELD), PropertyScope.NONE)
                .build())
        .requiredMultiValueSelection(Labels.withId(SELECTED_OUTPUT_FIELDS),
            Options.from(OutputFields.YEAR.toString(), OutputFields.MONTH.toString(), OutputFields.DAY.toString(),
                OutputFields.HOUR.toString(),
                OutputFields.MINUTE.toString(), OutputFields.SECOND.toString(), OutputFields.WEEKDAY.toString()))
        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor extractor) throws SpRuntimeException {
    EventSchema eventSchema = processingElement.getInputStreams().get(0).getEventSchema();

    List<String> selectedOutputField = extractor.selectedMultiValues(SELECTED_OUTPUT_FIELDS, String.class);

    for (String field : selectedOutputField) {
      if (field.equals(OutputFields.YEAR.toString())) {
        eventSchema.addEventProperty(
            EpProperties.numberEp(Labels.from("timestampYear", "Timestamp Year", ""), "timestampYear", SO.NUMBER));
      }
      if (field.equals(OutputFields.MONTH.toString())) {
        eventSchema.addEventProperty(
            EpProperties.numberEp(Labels.from("timestampMonth", "Timestamp Month", ""), "timestampMonth", SO.NUMBER));
      }
      if (field.equals(OutputFields.DAY.toString())) {
        eventSchema.addEventProperty(
            EpProperties.numberEp(Labels.from("timestampDay", "Timestamp Day", ""), "timestampDay", SO.NUMBER));
      }
      if (field.equals(OutputFields.HOUR.toString())) {
        eventSchema.addEventProperty(
            EpProperties.numberEp(Labels.from("timestampHour", "Timestamp Hour", ""), "timestampHour", SO.NUMBER));
      }
      if (field.equals(OutputFields.MINUTE.toString())) {
        eventSchema.addEventProperty(
            EpProperties.numberEp(Labels.from("timestampMinute", "Timestamp Minute", ""), "timestampMinute",
                SO.NUMBER));
      }
      if (field.equals(OutputFields.SECOND.toString())) {
        eventSchema.addEventProperty(
            EpProperties.numberEp(Labels.from("timestampSecond", "Timestamp Second", ""), "timestampSecond",
                SO.NUMBER));
      }
      if (field.equals(OutputFields.WEEKDAY.toString())) {
        eventSchema.addEventProperty(
            EpProperties.stringEp(Labels.from("timestampWeekday", "Timestamp Weekday", ""), "timestampWeekday",
                SO.TEXT));
      }
    }


    return eventSchema;
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    timestampField = extractor.mappingPropertyValue(TIMESTAMP_FIELD);
    outputFields = extractor.selectedMultiValues(SELECTED_OUTPUT_FIELDS, String.class);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    Long timestamp = event.getFieldBySelector(timestampField).getAsPrimitive().getAsLong();

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date(timestamp));

    for (String field : outputFields) {
      if (field.equals(OutputFields.YEAR.toString())) {
        event.addField("timestampYear", calendar.get(Calendar.YEAR));
      }
      if (field.equals(OutputFields.MONTH.toString())) {
        event.addField("timestampMonth", calendar.get(Calendar.MONTH) + 1);
      }
      if (field.equals(OutputFields.DAY.toString())) {
        event.addField("timestampDay", calendar.get(Calendar.DAY_OF_MONTH));
      }
      if (field.equals(OutputFields.HOUR.toString())) {
        event.addField("timestampHour", calendar.get(Calendar.HOUR_OF_DAY));
      }
      if (field.equals(OutputFields.MINUTE.toString())) {
        event.addField("timestampMinute", calendar.get(Calendar.MINUTE));
      }
      if (field.equals(OutputFields.SECOND.toString())) {
        event.addField("timestampSecond", calendar.get(Calendar.SECOND));
      }
      if (field.equals(OutputFields.WEEKDAY.toString())) {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String dayString = "";
        switch (day) {
          case Calendar.MONDAY:
            dayString = "Monday";
            break;
          case Calendar.TUESDAY:
            dayString = "Tuesday";
            break;
          case Calendar.WEDNESDAY:
            dayString = "Wednesday";
            break;
          case Calendar.THURSDAY:
            dayString = "Thursday";
            break;
          case Calendar.FRIDAY:
            dayString = "Friday";
            break;
          case Calendar.SATURDAY:
            dayString = "Saturday";
            break;
          case Calendar.SUNDAY:
            dayString = "Sunday";
            break;
        }
        event.addField("timestampWeekday", dayString);
      }
    }

    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
