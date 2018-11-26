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
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.imageprocessing.jvm.processor.commons.PlainImageTransformer;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QrCodeReader extends StandaloneEventProcessorEngine<QrCodeReaderParameters> {

  private QrCodeReaderParameters params;
  private static final Logger LOG = LoggerFactory.getLogger(QrCodeReader.class);

  public QrCodeReader(QrCodeReaderParameters params) {
    super(params);
  }

  @Override
  public void onInvocation(QrCodeReaderParameters qrCodeReaderParameters, DataProcessorInvocation dataProcessorInvocation) {
    this.params = qrCodeReaderParameters;
  }

  @Override
  public void onEvent(Map<String, Object> in, String s, SpOutputCollector out) {
    PlainImageTransformer<QrCodeReaderParameters> imageTransformer = new PlainImageTransformer<>(in, params);
    Optional<BufferedImage> imageOpt = imageTransformer.getImage(params.getImagePropertyName());

    if (imageOpt.isPresent()) {
      BufferedImage input = imageOpt.get();

      GrayU8 gray = ConvertBufferedImage.convertFrom(input,(GrayU8)null);

      QrCodeDetector<GrayU8> detector = FactoryFiducial.qrcode(null,GrayU8.class);

      detector.process(gray);
      List<QrCode> detections = detector.getDetections();

      if (detections.size() > 0) {
        LOG.info(detections.get(0).message);
        Map<String, Object> outMap = new HashMap<>();
        outMap.put("qrvalue", detections.get(0).message);
        outMap.put("timestamp", System.currentTimeMillis());
        out.onEvent(outMap);
      } else {
        LOG.info("Could not find any QR code");
      }


    }
  }

  @Override
  public void onDetach() {

  }
}
