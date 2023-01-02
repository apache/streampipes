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
package org.apache.streampipes.processors.textmining.flink.processor.language;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
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
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class LanguageDetectionController extends FlinkDataProcessorDeclarer<LanguageDetectionParameters> {

  private static final String DETECTION_FIELD_KEY = "detectionField";
  private static final String LANGUAGE_KEY = "language";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.textmining.flink.languagedetection")
        .category(DataProcessorType.ENRICH_TEXT)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.stringReq(),
                Labels.withId(DETECTION_FIELD_KEY),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(EpProperties.stringEp(
            Labels.withId(LANGUAGE_KEY),
            "language",
            "http://schema.org/language")))
        .build();
  }

  @Override
  public FlinkDataProcessorRuntime<LanguageDetectionParameters> getRuntime(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor,
      ConfigExtractor configExtractor,
      StreamPipesClient streamPipesClient) {
    String fieldName = extractor.mappingPropertyValue(DETECTION_FIELD_KEY);

    return new LanguageDetectionProgram(new LanguageDetectionParameters(graph, fieldName), configExtractor,
        streamPipesClient);
  }
}
