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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.imageenrichment;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.ImagePropertyConstants;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.ImageTransformer;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImageEnrichmentProcessor extends StreamPipesDataProcessor {
  private String imageProperty;
  private String boxArray;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.imageclassification.jvm.image-enricher")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.IMAGE_PROCESSING)
        .requiredStream(RequiredBoxStream.getBoxStream())
        .outputStrategy(OutputStrategies.fixed(
            EpProperties.stringEp(Labels.empty(), ImagePropertyConstants.IMAGE.getProperty(),
                "https://image.com")
        ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
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
      List<Map<String, Object>> allBoxesMap = imageTransformer.getAllBoxCoordinates(boxArray);

      for (Map<String, Object> box : allBoxesMap) {
        BoxCoordinates boxCoordinates = imageTransformer.getBoxCoordinatesWithAnnotations(image, box);

        Graphics2D graph = image.createGraphics();

        // set color
        Color color = ColorUtil.getColor(boxCoordinates.getClassesindex().hashCode());
        graph.setColor(color);

        // Box
        graph.setStroke(new BasicStroke(5));
        graph.draw(new Rectangle(boxCoordinates.getX(), boxCoordinates.getY(), boxCoordinates.getWidth(),
            boxCoordinates.getHeight()));

        // Label
        String str = boxCoordinates.getClassesindex() + ": " + boxCoordinates.getScore();

        FontMetrics fm = graph.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(str, graph);

        graph.fillRect(boxCoordinates.getX(),
            boxCoordinates.getY() - fm.getAscent(),
            (int) rect.getWidth(),
            (int) rect.getHeight());

        graph.setColor(Color.white);
        graph.drawString(str, boxCoordinates.getX(), boxCoordinates.getY());

        graph.dispose();
      }

      Optional<byte[]> finalImage = imageTransformer.makeImage(image);

      if (finalImage.isPresent()) {
        Event outEvent = new Event();
        outEvent.addField(ImagePropertyConstants.IMAGE.getProperty(),
            Base64.getEncoder().encodeToString(finalImage.get()));
        out.collect(outEvent);
      }
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
