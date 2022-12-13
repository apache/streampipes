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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.imagecropper;


import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import static org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.IMAGE_PROPERTY;


public class ImageCropperController extends StandaloneEventProcessingDeclarer<ImageCropperParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.imageclassification.jvm.image-cropper")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.IMAGE_PROCESSING)
        .requiredStream(RequiredBoxStream.getBoxStream())
        .outputStrategy(OutputStrategies.append(
            EpProperties.integerEp(Labels.empty(), "classname", "https://streampipes.org/classname"),
            EpProperties.doubleEp(Labels.empty(), "score", "https://streampipes.org/Label")
        ))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<ImageCropperParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation,
                                                                       ProcessingElementParameterExtractor extractor) {

    String imageProperty = extractor.mappingPropertyValue(IMAGE_PROPERTY);
    String boxArray = extractor.mappingPropertyValue(RequiredBoxStream.BOX_ARRAY_PROPERTY);

    ImageCropperParameters params = new ImageCropperParameters(dataProcessorInvocation, imageProperty,
        boxArray, "box_width", "box_height", "box_x", "box_y");

    return new ConfiguredEventProcessor<>(params, ImageCropper::new);
  }

}
