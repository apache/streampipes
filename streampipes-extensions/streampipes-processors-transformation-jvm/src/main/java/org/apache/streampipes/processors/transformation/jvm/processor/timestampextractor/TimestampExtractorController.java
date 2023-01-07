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
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
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
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class TimestampExtractorController extends StandaloneEventProcessingDeclarer<TimestampExtractorParameters>
    implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  public static final String TIMESTAMP_FIELD = "timestampField";
  public static final String SELECTED_OUTPUT_FIELDS = "selectedOutputFields";


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
  public ConfiguredEventProcessor<TimestampExtractorParameters> onInvocation(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {
    String timestampField = extractor.mappingPropertyValue(TIMESTAMP_FIELD);
    List<String> selectedMultiValue = extractor.selectedMultiValues(SELECTED_OUTPUT_FIELDS, String.class);

    TimestampExtractorParameters params = new TimestampExtractorParameters(graph, timestampField, selectedMultiValue);
    return new ConfiguredEventProcessor<>(params, TimestampExtractor::new);
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

}
