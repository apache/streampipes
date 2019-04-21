/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.transformation.jvm.processor.timestampextractor;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.container.api.ResolvesContainerProvidedOutputStrategy;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class TimestampExtractorController extends StandaloneEventProcessingDeclarer<TimestampExtractorParameters>
        implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

    public final static String TIMESTAMP_FIELD = "timestampField";
    public final static String SELECTED_OUTPUT_FIELDS = "selectedOutputFields";


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.streampipes.processors.transformation.jvm.processor.timestampextractor")
                .withLocales(Locales.EN)
            //    .iconUrl(TransformationJvmConfig.getIconUrl())
                .requiredStream(
                        StreamRequirementsBuilder.create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.withId(TIMESTAMP_FIELD), PropertyScope.NONE)
                                .build())
                .requiredMultiValueSelection(Labels.withId(SELECTED_OUTPUT_FIELDS),
                        Options.from(OutputFields.YEAR.toString(), OutputFields.MONTH.toString(), OutputFields.DAY.toString(), OutputFields.HOUR.toString(),
                                OutputFields.MINUTE.toString(), OutputFields.SECOND.toString(), OutputFields.WEEKDAY.toString()))
                .outputStrategy(OutputStrategies.customTransformation())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }

    @Override
    public ConfiguredEventProcessor<TimestampExtractorParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
        String timestampField = extractor.mappingPropertyValue(TIMESTAMP_FIELD);
        List<String> selectedMultiValue = extractor.selectedMultiValues(SELECTED_OUTPUT_FIELDS, String.class);

        TimestampExtractorParameters params = new TimestampExtractorParameters(graph, timestampField, selectedMultiValue);
        return new ConfiguredEventProcessor<>(params, TimestampExtractor::new);
    }

    @Override
    public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement, ProcessingElementParameterExtractor extractor) throws SpRuntimeException {
        EventSchema eventSchema = processingElement.getInputStreams().get(0).getEventSchema();

        List<String> selectedOutputField = extractor.selectedMultiValues(SELECTED_OUTPUT_FIELDS, String.class);

        for (String field : selectedOutputField) {
            if(field.equals(OutputFields.YEAR.toString())) {
                eventSchema.addEventProperty(EpProperties.numberEp(Labels.from("timestampYear", "Timestamp Year", ""), "timestampYear", SO.Number));
            }
            if(field.equals(OutputFields.MONTH.toString())) {
                eventSchema.addEventProperty(EpProperties.numberEp(Labels.from("timestampMonth", "Timestamp Month", ""), "timestampMonth", SO.Number));
            }
            if(field.equals(OutputFields.DAY.toString())) {
                eventSchema.addEventProperty(EpProperties.numberEp(Labels.from("timestampDay", "Timestamp Day", ""), "timestampDay", SO.Number));
            }
            if(field.equals(OutputFields.HOUR.toString())) {
                eventSchema.addEventProperty(EpProperties.numberEp(Labels.from("timestampHour", "Timestamp Hour", ""), "timestampHour", SO.Number));
            }
            if(field.equals(OutputFields.MINUTE.toString())) {
                eventSchema.addEventProperty(EpProperties.numberEp(Labels.from("timestampMinute", "Timestamp Minute", ""), "timestampMinute", SO.Number));
            }
            if(field.equals(OutputFields.SECOND.toString())) {
                eventSchema.addEventProperty(EpProperties.numberEp(Labels.from("timestampSecond", "Timestamp Second", ""), "timestampSecond", SO.Number));
            }
            if(field.equals(OutputFields.WEEKDAY.toString())) {
                eventSchema.addEventProperty(EpProperties.stringEp(Labels.from("timestampWeekday", "Timestamp Weekday", ""), "timestampWeekday", SO.Text));
            }
        }


        return eventSchema;
    }

}
