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

import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.ImageTransformer;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImageEnricher implements EventProcessor<ImageEnrichmentParameters> {

  private ImageEnrichmentParameters params;

  @Override
  public void onInvocation(ImageEnrichmentParameters params, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    this.params = params;
  }

  @Override
  public void onEvent(org.apache.streampipes.model.runtime.Event in, SpOutputCollector out) {
    ImageTransformer imageTransformer = new ImageTransformer(in, params);

    Optional<BufferedImage> imageOpt =
            imageTransformer.getImage();


    if (imageOpt.isPresent()) {
      BufferedImage image = imageOpt.get();
      List<Map<String, Object>> allBoxesMap = imageTransformer.getAllBoxCoordinates();

      for (Map<String, Object> box : allBoxesMap) {

        BoxCoordinates boxCoordinates = imageTransformer.getBoxCoordinatesWithAnnotations(image, box);

        Graphics2D graph = image.createGraphics();

        //set color
        Color color = ColorUtil.getColor(boxCoordinates.getClassesindex().hashCode());
        graph.setColor(color);

        //Box
        graph.setStroke(new BasicStroke(5));
        graph.draw(new Rectangle(boxCoordinates.getX(), boxCoordinates.getY(), boxCoordinates.getWidth(),
                boxCoordinates.getHeight()));

        //Label
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
        org.apache.streampipes.model.runtime.Event event = new org.apache.streampipes.model.runtime.Event();
        event.addField("image", Base64.getEncoder().encodeToString(finalImage.get()));
        out.collect(event);
      }
    }

  }

  @Override
  public void onDetach() {

  }
}
