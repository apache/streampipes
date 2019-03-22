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

package org.streampipes.processors.transformation.jvm.processor.value.change;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.transformation.jvm.config.TransformationJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ChangedValueController extends StandaloneEventProcessingDeclarer<ChangedValueParameters> {

  public static final String COMPARE_FIELD_ID = "compare";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors" +
            ".transformation.jvm.changed-value", "Value Changed", "This processor sends " +
            "out an event everytime a specific object changes. It also adds a timestamp in ms from the system time.")
            .iconUrl(TransformationJvmConfig.getIconUrl("splitarray"))
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(),
                            Labels.from(COMPARE_FIELD_ID, "Keep Properties", "The " +
                                    "property which might change over time"),
                            PropertyScope.NONE)
                    .build())
            .outputStrategy(OutputStrategies.append(EpProperties.timestampProperty("Timestamp")))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<ChangedValueParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String compare = extractor.mappingPropertyValue(COMPARE_FIELD_ID);

    ChangedValueParameters params = new ChangedValueParameters(graph, compare);
    return new ConfiguredEventProcessor<>(params, ChangedValue::new);
  }
}
