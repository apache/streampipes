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
package org.apache.streampipes.processors.imageprocessing.jvm.processor.qrreader;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.PlainImageTransformer;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import boofcv.abst.fiducial.QrCodeDetector;
import boofcv.alg.fiducial.qrcode.QrCode;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class QrCodeReaderProcessor extends StreamPipesDataProcessor {

  private static final String PLACEHOLDER_VALUE = "placeholder-value";
  private static final String SEND_IF_NO_RESULT = "send-if-no-result";
  private static final String QR_VALUE = "qr-value";
  private static final Logger LOG = LoggerFactory.getLogger(QrCodeReaderProcessor.class);
  private String imagePropertyName;
  private String placeholderValue;
  private Boolean sendIfNoResult;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.imageclassification.qrcode")
        .category(DataProcessorType.IMAGE_PROCESSING)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.domainPropertyReq("https://image.com"),
                Labels.withId(RequiredBoxStream.IMAGE_PROPERTY), PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(SEND_IF_NO_RESULT), Options.from("Yes", "No"))
        .requiredTextParameter(Labels.withId(PLACEHOLDER_VALUE))
        .outputStrategy(OutputStrategies.fixed(
            EpProperties.timestampProperty("timestamp"),
            EpProperties.stringEp(Labels.withId(QR_VALUE), "qrvalue", "http://schema.org/text")))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    ProcessingElementParameterExtractor extractor = parameters.extractor();

    imagePropertyName = extractor.mappingPropertyValue(RequiredBoxStream.IMAGE_PROPERTY);
    placeholderValue = extractor.singleValueParameter(PLACEHOLDER_VALUE, String.class);
    sendIfNoResult = extractor.selectedSingleValue(SEND_IF_NO_RESULT, String.class).equals("Yes");
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    PlainImageTransformer imageTransformer = new PlainImageTransformer(in);
    Optional<BufferedImage> imageOpt = imageTransformer.getImage(imagePropertyName);

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
  public void onDetach() throws SpRuntimeException {

  }
}
