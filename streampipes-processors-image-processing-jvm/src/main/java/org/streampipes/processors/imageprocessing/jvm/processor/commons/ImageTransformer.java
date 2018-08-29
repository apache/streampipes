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
package org.streampipes.processors.imageprocessing.jvm.processor.commons;

import org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.BoxCoordinates;
import org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.ImageEnrichmentParameters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ImageTransformer extends PlainImageTransformer<ImageEnrichmentParameters> {

  public ImageTransformer(Map<String, Object> in, ImageEnrichmentParameters params) {
   super(in, params);
  }

  public Optional<BufferedImage> getImage() {

    return getImage(params.getImageProperty());
  }

  public BoxCoordinates getBoxCoordinates(BufferedImage image) {
    Float x = toFloat(in.get(params.getBoxX()));
    Float y = toFloat(in.get(params.getBoxY()));
    Float width = toFloat(in.get(params.getBoxWidth()));
    Float height = toFloat(in.get(params.getBoxHeight()));

    return BoxCoordinates.make(image.getWidth(), image.getHeight(), width, height, x, y);
  }

  public Optional<byte[]> makeImage(BufferedImage image) {

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", baos);
      baos.flush();
      byte[] finalImage = baos.toByteArray();
      baos.close();
      return Optional.of(finalImage);
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }

  }

  private Float toFloat(Object obj) {
    return Float.parseFloat(String.valueOf(obj));
  }

}
