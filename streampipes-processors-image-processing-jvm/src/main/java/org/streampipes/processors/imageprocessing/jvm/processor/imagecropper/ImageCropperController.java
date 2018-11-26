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
package org.streampipes.processors.imageprocessing.jvm.processor.imagecropper;


import static org.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.*;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.imageprocessing.jvm.config.ImageProcessingJvmConfig;
import org.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;


public class ImageCropperController extends StandaloneEventProcessingDeclarer<ImageCropperParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processor.imageclassification.jvm.image-cropper", "Image Cropper", "Image Enrichment: Crops an " +
            "image based on " +
            "given bounding box coordinates")
            .iconUrl(ImageProcessingJvmConfig.getIconUrl( "crop"))
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
  public ConfiguredEventProcessor<ImageCropperParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(dataProcessorInvocation);

    String imageProperty = extractor.mappingPropertyValue(IMAGE_PROPERTY);
    String boxWidthProperty = extractor.mappingPropertyValue(BOX_WIDTH_PROPERTY);
    String boxHeightProperty = extractor.mappingPropertyValue(BOX_HEIGHT_PROPERTY);
    String boxXProperty = extractor.mappingPropertyValue(BOX_X_PROPERTY);
    String boxYProperty = extractor.mappingPropertyValue(BOX_Y_PROPERTY);

    ImageCropperParameters params = new ImageCropperParameters(dataProcessorInvocation, imageProperty,
            boxWidthProperty, boxHeightProperty, boxXProperty, boxYProperty);

    return new ConfiguredEventProcessor<>(params, () -> new ImageCropper(params));
  }

}
