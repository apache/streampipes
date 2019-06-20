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

package org.streampipes.processors.textmining.jvm.processor.language;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class LanguageDetectionController extends StandaloneEventProcessingDeclarer<LanguageDetectionParameters> {

  private static final String DETECTION_FIELD_KEY = "detectionField";
  static final String LANGUAGE_KEY = "language";
  static final String CONFIDENCE_KEY = "confidence";

  //TODO: Change Icon
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.textmining.jvm.language")
            .category(DataProcessorType.ENRICH_TEXT)
            .withAssets(Assets.DOCUMENTATION)
            .withLocales(Locales.EN)
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.stringReq(),
                            Labels.withId(DETECTION_FIELD_KEY),
                            PropertyScope.NONE)
                    .build())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.stringEp(
                            Labels.withId(LANGUAGE_KEY),
                            "language",
                            "http://schema.org/language"),
                    EpProperties.doubleEp(Labels.withId(CONFIDENCE_KEY),
                            "probability",
                            "https://schema.org/Float")))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<LanguageDetectionParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String detection = extractor.mappingPropertyValue(DETECTION_FIELD_KEY);

    LanguageDetectionParameters params = new LanguageDetectionParameters(graph, detection);
    return new ConfiguredEventProcessor<>(params, LanguageDetection::new);
  }
}
