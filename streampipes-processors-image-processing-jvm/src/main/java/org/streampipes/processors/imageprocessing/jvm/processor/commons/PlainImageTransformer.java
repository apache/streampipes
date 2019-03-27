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

import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

import javax.imageio.ImageIO;

public class PlainImageTransformer<T extends EventProcessorBindingParams> {

  protected Event in;
  protected T params;

  public PlainImageTransformer(Event in, T params) {
    this.in = in;
    this.params = params;
  }

  public Optional<BufferedImage> getImage(String imagePropertyName) {
    System.out.println(imagePropertyName);
    String imageBase64 = in.getFieldBySelector(imagePropertyName).getAsPrimitive().getAsString();

    InputStream img = new ByteArrayInputStream(Base64.getDecoder().decode(imageBase64));
    try {
      return Optional.of(ImageIO.read(img));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }
}
