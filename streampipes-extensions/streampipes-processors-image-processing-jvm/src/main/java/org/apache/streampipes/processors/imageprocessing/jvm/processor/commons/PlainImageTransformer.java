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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.commons;

import org.apache.streampipes.model.runtime.Event;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

public class PlainImageTransformer {

  protected Event in;

  public PlainImageTransformer(Event in) {
    this.in = in;
  }

  public Optional<BufferedImage> getImage(String imagePropertyName) {
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
