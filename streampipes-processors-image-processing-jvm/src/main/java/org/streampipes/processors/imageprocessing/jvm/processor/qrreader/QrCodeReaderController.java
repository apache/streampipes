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

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.imageprocessing.jvm.config.ImageProcessingJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import static org.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.IMAGE_PROPERTY;

public class QrCodeReaderController extends StandaloneEventProcessingDeclarer<QrCodeReaderParameters> {

  @Override
  public ConfiguredEventProcessor<QrCodeReaderParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(dataProcessorInvocation);

    String imagePropertyName = extractor.mappingPropertyValue(IMAGE_PROPERTY);

    QrCodeReaderParameters params = new QrCodeReaderParameters(dataProcessorInvocation, imagePropertyName);

    return new ConfiguredEventProcessor<>(params, () -> new QrCodeReader(params));
  }

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("qr-code-reader", "QR Code Reader", ("QR Code Reader: Detects a QR Code " +
            "in an image"))
            .category(DataProcessorType.FILTER)
            .iconUrl(ImageProcessingJvmConfig.iconBaseUrl + "/qrcode.png")

            .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(EpRequirements
                            .domainPropertyReq("https://image.com"), Labels
                            .from(IMAGE_PROPERTY, "Image", ""),
                    PropertyScope.NONE).build())
            // TODO fix bug in transform output strategy
            .outputStrategy(OutputStrategies.transform(TransformOperations.staticRuntimeNameTransformation
                    (IMAGE_PROPERTY, "image"), TransformOperations.staticDomainPropertyTransformation
                    (IMAGE_PROPERTY, "http://schema.org/text")))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }
}
