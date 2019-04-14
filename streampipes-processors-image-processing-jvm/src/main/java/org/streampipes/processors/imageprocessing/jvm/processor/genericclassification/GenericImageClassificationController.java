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
package org.streampipes.processors.imageprocessing.jvm.processor.genericclassification;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class GenericImageClassificationController extends StandaloneEventProcessingDeclarer<GenericImageClassificationParameters> {

  private static final String IMAGE = "IMAGE";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processor.imageclassification.jvm.generic-image-classification", "Generic Image Classification", "Image " +
            "Classification Description (Generic Model)")
            .category(DataProcessorType.FILTER)
            .providesAssets(Assets.DOCUMENTATION)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements
                                    .domainPropertyReq("https://image.com"), Labels.from(IMAGE, "Image Classification", ""),
                            PropertyScope.NONE)
                    .build())
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.doubleEp(Labels.empty(), "score", "https://schema.org/score"),
                    EpProperties.stringEp(Labels.empty(), "category", "https://schema.org/category")

            ))
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<GenericImageClassificationParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String imageProperty = extractor.mappingPropertyValue(IMAGE);

    GenericImageClassificationParameters staticParam = new GenericImageClassificationParameters(graph, imageProperty);

    return new ConfiguredEventProcessor<>(staticParam, GenericImageClassification::new);
  }
}
