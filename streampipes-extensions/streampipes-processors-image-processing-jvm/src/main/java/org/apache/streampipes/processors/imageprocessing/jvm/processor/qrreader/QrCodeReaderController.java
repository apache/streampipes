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

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
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
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import static org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.RequiredBoxStream.IMAGE_PROPERTY;

public class QrCodeReaderController extends StandaloneEventProcessingDeclarer<QrCodeReaderParameters> {

  private static final String PLACEHOLDER_VALUE = "placeholder-value";
  private static final String SEND_IF_NO_RESULT = "send-if-no-result";
  private static final String QR_VALUE = "qr-value";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processor.imageclassification.qrcode")
        .category(DataProcessorType.IMAGE_PROCESSING)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(EpRequirements
                .domainPropertyReq("https://image.com"), Labels
                .withId(IMAGE_PROPERTY),
            PropertyScope.NONE).build())
        .requiredSingleValueSelection(Labels.withId(SEND_IF_NO_RESULT), Options.from("Yes", "No"))
        .requiredTextParameter(Labels.withId(PLACEHOLDER_VALUE))
        .outputStrategy(OutputStrategies.fixed(EpProperties.timestampProperty("timestamp"),
            EpProperties.stringEp(Labels.withId(QR_VALUE),
                "qrvalue", "http://schema.org/text")))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<QrCodeReaderParameters> onInvocation(DataProcessorInvocation dataProcessorInvocation,
                                                                       ProcessingElementParameterExtractor extractor) {
    String imagePropertyName = extractor.mappingPropertyValue(IMAGE_PROPERTY);
    String placeholderValue = extractor.singleValueParameter(PLACEHOLDER_VALUE, String.class);
    Boolean sendIfNoResult = extractor.selectedSingleValue(SEND_IF_NO_RESULT, String.class)
        .equals("Yes");

    QrCodeReaderParameters params = new QrCodeReaderParameters(dataProcessorInvocation,
        imagePropertyName, placeholderValue, sendIfNoResult);

    return new ConfiguredEventProcessor<>(params, QrCodeReader::new);
  }

}
