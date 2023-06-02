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


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.ImagePropertyConstants;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.ImageTransformer;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.BoxCoordinates;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImageCropperProcessor extends StreamPipesDataProcessor {

  private String imageProperty;
  private String boxArray;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.imageclassification.jvm.image-cropper")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.IMAGE_PROCESSING)
        .requiredStream(RequiredBoxStream.getBoxStream())
        .outputStrategy(OutputStrategies.append(EpProperties.integerEp(Labels.empty(),
                ImagePropertyConstants.CLASS_NAME.getProperty(), "https://streampipes.org/classname"),
            EpProperties.doubleEp(Labels.empty(), ImagePropertyConstants.SCORE.getProperty(),
                "https://streampipes.org/Label")))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    ProcessingElementParameterExtractor extractor = parameters.extractor();
    this.imageProperty = extractor.mappingPropertyValue(RequiredBoxStream.IMAGE_PROPERTY);
    this.boxArray = extractor.mappingPropertyValue(RequiredBoxStream.BOX_ARRAY_PROPERTY);
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    ImageTransformer imageTransformer = new ImageTransformer(in);
    Optional<BufferedImage> imageOpt = imageTransformer.getImage(imageProperty);

    if (imageOpt.isPresent()) {
      BufferedImage image = imageOpt.get();
      List<Map<String, Object>> allBoxCoordinates = imageTransformer.getAllBoxCoordinates(boxArray);

      for (Map<String, Object> box : allBoxCoordinates) {
        BoxCoordinates boxCoordinates = imageTransformer.getBoxCoordinates(image, box);

        BufferedImage dest = image.getSubimage(boxCoordinates.getX(), boxCoordinates.getY(), boxCoordinates.getWidth(),
            boxCoordinates.getHeight());

        Optional<byte[]> finalImage = imageTransformer.makeImage(dest);

        if (finalImage.isPresent()) {
          Event outEvent = new Event();

          outEvent.addField(ImagePropertyConstants.TIMESTAMP.getProperty(),
              in.getFieldByRuntimeName(ImagePropertyConstants.TIMESTAMP.getProperty()).getAsPrimitive().getAsLong());

          outEvent.addField(ImagePropertyConstants.IMAGE.getProperty(),
              Base64.getEncoder().encodeToString(finalImage.get()));

          outEvent.addField(ImagePropertyConstants.CLASS_NAME.getProperty(),
              box.get(ImagePropertyConstants.CLASS_NAME.getProperty()));

          outEvent.addField(ImagePropertyConstants.SCORE.getProperty(),
              box.get(ImagePropertyConstants.SCORE.getProperty()));

          out.collect(outEvent);
        }
      }
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
