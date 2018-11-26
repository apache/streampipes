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
package org.streampipes.processors.imageprocessing.jvm.processor.imagerectification;

import static org.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.IMAGE_PROPERTY;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ImageRectificationController extends StandaloneEventProcessingDeclarer<ImageRectificationParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processor.imageclassification.image-rectifier", "Image Rectifier", "Image Rectification: Rectifies " +
            "an image")
            .category(DataProcessorType.FILTER)
            .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(EpRequirements
                            .domainPropertyReq("https://image.com"), Labels
                            .from(IMAGE_PROPERTY, "Image Classification", ""),
                    PropertyScope.NONE).build())
            .outputStrategy(OutputStrategies.keep())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<ImageRectificationParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(dataProcessorInvocation);

    String imagePropertyName = extractor.mappingPropertyValue(IMAGE_PROPERTY);

    ImageRectificationParameters params = new ImageRectificationParameters(dataProcessorInvocation, imagePropertyName);

    return new ConfiguredEventProcessor<>(params, () -> new ImageRectifier(params));
  }

}
