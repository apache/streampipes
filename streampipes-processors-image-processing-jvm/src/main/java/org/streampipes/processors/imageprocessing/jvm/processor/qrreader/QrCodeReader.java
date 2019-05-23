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
package org.streampipes.processors.imageprocessing.jvm.processor.qrreader;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.imageprocessing.jvm.processor.commons.PlainImageTransformer;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class QrCodeReader implements EventProcessor<QrCodeReaderParameters> {

  private QrCodeReaderParameters params;
  private Boolean sendIfNoResult;
  private String placeholderValue;
  private static final Logger LOG = LoggerFactory.getLogger(QrCodeReader.class);

  @Override
  public void onInvocation(QrCodeReaderParameters qrCodeReaderParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    this.params = qrCodeReaderParameters;
    this.sendIfNoResult = qrCodeReaderParameters.getSendIfNoResult();
    this.placeholderValue = qrCodeReaderParameters.getPlaceholderValue();
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) {
    PlainImageTransformer<QrCodeReaderParameters> imageTransformer = new PlainImageTransformer<>
            (in, params);
    Optional<BufferedImage> imageOpt = imageTransformer.getImage(params.getImagePropertyName());

    if (imageOpt.isPresent()) {
      BufferedImage input = imageOpt.get();

      GrayU8 gray = ConvertBufferedImage.convertFrom(input, (GrayU8) null);

      QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(null, GrayU8.class);

      detector.process(gray);
      List<QrCode> detections = detector.getDetections();
      List<QrCode> failures = detector.getFailures();

      if (detections.size() > 0) {
        LOG.info(detections.get(0).message);
        Event event = makeEvent(detections.get(0).message);
        out.collect(event);
      } else {
        LOG.info("Could not find any QR code");
        if (sendIfNoResult) {
          Event event = makeEvent(placeholderValue);
          out.collect(event);
        }
      }


    }
  }

  private Event makeEvent(String qrCodeValue) {
    Event event = new Event();
    event.addField("qrvalue", qrCodeValue);
    event.addField("timestamp", System.currentTimeMillis());
    return event;
  }


  @Override
  public void onDetach() {

  }
}
