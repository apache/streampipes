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
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.BoxCoordinates;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImageTransformer extends PlainImageTransformer {

  public ImageTransformer(Event in) {
    super(in);
  }

  public Optional<BufferedImage> getImage(String imageProperty) {

    return getImage(imageProperty);
  }

  public List<Map<String, Object>> getAllBoxCoordinates(String boxArrayProperty) {
    List<Map<String, AbstractField>> allBoxes = in.getFieldBySelector(boxArrayProperty)
        .getAsList()
        .parseAsCustomType(value -> value.getAsComposite().getRawValue());

    List<Map<String, Object>> allBoxesMap = new ArrayList<>();
    allBoxes.forEach(box -> {
      Map<String, Object> boxMap = new HashMap<>();
      box.forEach((key, value) -> boxMap.put(value.getFieldNameIn(), value.getRawValue()));
      allBoxesMap.add(boxMap);
    });

    return allBoxesMap;
  }

  public BoxCoordinates getBoxCoordinates(BufferedImage image, Map<String, Object> box) {
    Float x = toFloat(box.get(ImagePropertyConstants.BOX_X.getProperty()));
    Float y = toFloat(box.get(ImagePropertyConstants.BOX_Y.getProperty()));
    Float width = toFloat(box.get(ImagePropertyConstants.BOX_WIDTH.getProperty()));
    Float height = toFloat(box.get(ImagePropertyConstants.BOX_HEIGHT.getProperty()));


    return BoxCoordinates.make(width, height, x, y);
  }

  public BoxCoordinates getBoxCoordinatesWithAnnotations(BufferedImage image, Map<String, Object> box) {
    Float x = toFloat(box.get(ImagePropertyConstants.BOX_X.getProperty()));
    Float y = toFloat(box.get(ImagePropertyConstants.BOX_Y.getProperty()));
    Float width = toFloat(box.get(ImagePropertyConstants.BOX_WIDTH.getProperty()));
    Float height = toFloat(box.get(ImagePropertyConstants.BOX_WIDTH.getProperty()));
    Float score = toFloat(box.get(ImagePropertyConstants.SCORE.getProperty()));
    String classesindex = toString(box.get(ImagePropertyConstants.CLASS_INDEX.getProperty()));

    return BoxCoordinates.make(width, height, x, y, score, classesindex);
  }

  private Float toFloat(Object obj) {
    return Float.parseFloat(toString(obj));
  }

  private String toString(Object obj) {
    return String.valueOf(obj);
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
}
