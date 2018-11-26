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

import static org.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.IMAGE_PROPERTY;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.imageprocessing.jvm.config.ImageProcessingJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.CollectedStreamRequirements;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ImageEnrichmentController extends StandaloneEventProcessingDeclarer<ImageEnrichmentParameters> {

  public static final String BOX_ARRAY_PROPERTY = "box-array-property";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processor.imageclassification.jvm.image-enricher", "Image Enricher", "Image Enrichment: Enriches an " +
            "image with " +
            "given bounding box coordinates")
            .iconUrl(ImageProcessingJvmConfig.getIconUrl( "image_enrich"))
            .category(DataProcessorType.FILTER)
            .requiredStream(getStreamRequirements())

            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.stringEp(Labels.empty(), "image", "https://image.com")

            ))
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  private CollectedStreamRequirements getStreamRequirements() {
        return StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq("https://image.com"), Labels
                            .from(IMAGE_PROPERTY, "Image Classification", ""),
                    PropertyScope.NONE)
                // TODO add again
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReqList("https://streampipes.org/boundingboxes"),
                    Labels.from(BOX_ARRAY_PROPERTY, "Array Width Bounding Boxes", "Contains an array with bounding boxes"),
                    PropertyScope.NONE)
            .build();
  }

  @Override
  public ConfiguredEventProcessor<ImageEnrichmentParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(dataProcessorInvocation);

    String imageProperty = extractor.mappingPropertyValue(IMAGE_PROPERTY);
    String boxArray = extractor.mappingPropertyValue(BOX_ARRAY_PROPERTY);
//    String boxArray = "boxes";

    ImageEnrichmentParameters params = new ImageEnrichmentParameters(dataProcessorInvocation, imageProperty,
            boxArray, "box_width", "box_height", "box_x", "box_y");

    return new ConfiguredEventProcessor<>(params, () -> new ImageEnricher(params));


  }


}
