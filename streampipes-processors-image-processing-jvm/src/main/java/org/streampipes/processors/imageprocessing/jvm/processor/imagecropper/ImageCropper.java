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

import org.streampipes.model.runtime.Event;
import org.streampipes.processors.imageprocessing.jvm.processor.commons.ImageTransformer;
import org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.BoxCoordinates;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.Optional;

public class ImageCropper implements EventProcessor<ImageCropperParameters> {

  private ImageCropperParameters params;

  @Override
  public void onInvocation(ImageCropperParameters imageCropperParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    this.params = imageCropperParameters;
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) {
    ImageTransformer imageTransformer = new ImageTransformer(in.getRaw(), params);
    Optional<BufferedImage> imageOpt = imageTransformer.getImage();

    if (imageOpt.isPresent()) {
      BufferedImage image = imageOpt.get();
      BoxCoordinates boxCoordinates = imageTransformer.getBoxCoordinates(image);

      BufferedImage dest = image.getSubimage(boxCoordinates.getX(), boxCoordinates.getY(), boxCoordinates.getWidth(),
              boxCoordinates.getHeight());

      Optional<byte[]> finalImage = imageTransformer.makeImage(dest);

      if (finalImage.isPresent()) {
        Event outEvent = new Event();
        outEvent.addField("image", Base64.getEncoder().encodeToString(finalImage.get()));
        out.collect(outEvent);
      }
    }
  }

  @Override
  public void onDetach() {

  }
}
