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
package org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import static org.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.IMAGE_PROPERTY;

public class ImageEnrichmentController extends StandaloneEventProcessingDeclarer<ImageEnrichmentParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processor.imageclassification.jvm.image-enricher")
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.FILTER)
            .requiredStream(RequiredBoxStream.getBoxStream())
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.stringEp(Labels.empty(), "image", "https://image.com")

            ))
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<ImageEnrichmentParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation, ProcessingElementParameterExtractor extractor) {
    String imageProperty = extractor.mappingPropertyValue(IMAGE_PROPERTY);
    String boxArray = extractor.mappingPropertyValue(RequiredBoxStream.BOX_ARRAY_PROPERTY);

    ImageEnrichmentParameters params = new ImageEnrichmentParameters(dataProcessorInvocation, imageProperty,
            boxArray, "box_width", "box_height", "box_x", "box_y");

    return new ConfiguredEventProcessor<>(params, ImageEnricher::new);


  }


}
