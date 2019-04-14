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
package org.streampipes.processors.imageprocessing.jvm.processor.imageenrichment;

import org.streampipes.model.runtime.field.AbstractField;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

public class ImageEnricher implements EventProcessor<ImageEnrichmentParameters> {

    private ImageEnrichmentParameters params;


    @Override
    public void onInvocation(ImageEnrichmentParameters params, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
        this.params = params;
    }

    @Override
    public void onEvent(org.streampipes.model.runtime.Event in, SpOutputCollector out) {
// TODO
        List<Map<String, AbstractField>> allBoxes = in.getFieldBySelector(params.getBoxArray())
                .getAsList()
                .parseAsCustomType(value -> value.getAsComposite().getRawValue());

        List<Map<String, Object>> allBoxesMap = new ArrayList<>();
        allBoxes.forEach(box -> {
            Map<String, Object> boxMap = new HashMap<>();
            box.forEach((key, value) -> boxMap.put(value.getFieldNameIn(), value.getRawValue()));
            allBoxesMap.add(boxMap);
        });

        Optional<BufferedImage> imageOpt = getImage(in.getFieldBySelector(params.getImageProperty
                ()).getAsPrimitive().getRawValue());

        if (imageOpt.isPresent()) {
            BufferedImage image = imageOpt.get();

            for (Map<String, Object> box : allBoxesMap) {
//
                BoxCoordinates boxCoordinates = getBoxCoordinates(image, box);

                Graphics2D graph = image.createGraphics();
                int alpha = 180;
                Color color = new Color(133, 148, 229, alpha);
                graph.setColor(color);
                graph.fill(new Rectangle(boxCoordinates.getX(), boxCoordinates.getY(), boxCoordinates.getWidth(),
                        boxCoordinates.getHeight()));
                graph.dispose();

            }

            // TODO howto change final image
            Optional<byte[]> finalImage = makeImage(image);

            if (finalImage.isPresent()) {
                org.streampipes.model.runtime.Event event = new org.streampipes.model.runtime.Event();
                event.addField("image", Base64.getEncoder().encodeToString(finalImage.get()));
                out.collect(event);
            }
        }

    }

    private BoxCoordinates getBoxCoordinates(BufferedImage image, Map<String, Object> box) {
        Float x = toFloat(box.get(params.getBoxX()));
        Float y = toFloat(box.get(params.getBoxY()));
        Float width = toFloat(box.get(params.getBoxWidth()));
        Float height = toFloat(box.get(params.getBoxHeight()));

        return BoxCoordinates.make(image.getWidth(), image.getHeight(), width, height, x, y);
      }

  private Float toFloat(Object obj) {
    return Float.parseFloat(String.valueOf(obj));
  }


    private Optional<byte[]> makeImage(BufferedImage image) {

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


    private Optional<BufferedImage> getImage(Object image) {
        String imageBase64 = String.valueOf(image);

        InputStream img = new ByteArrayInputStream(Base64.getDecoder().decode(imageBase64));
        try {
            return Optional.of(ImageIO.read(img));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    @Override
    public void onDetach() {

    }
}
