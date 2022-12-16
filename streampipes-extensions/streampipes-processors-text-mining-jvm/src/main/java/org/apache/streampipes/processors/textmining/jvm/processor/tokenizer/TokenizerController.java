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

package org.apache.streampipes.processors.textmining.jvm.processor.tokenizer;

import org.apache.streampipes.client.StreamPipesClient;
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
import org.apache.streampipes.service.extensions.base.client.StreamPipesClientResolver;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class TokenizerController extends StandaloneEventProcessingDeclarer<TokenizerParameters> {

  private static final String DETECTION_FIELD_KEY = "detectionField";
  static final String TOKEN_LIST_FIELD_KEY = "tokenList";
  private static final String BINARY_FILE_KEY = "binary-file";

  //TODO: Maybe change outputStrategy to an array instead of tons of different strings
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.textmining.jvm.tokenizer")
        .category(DataProcessorType.ENRICH_TEXT)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredFile(Labels.withId(BINARY_FILE_KEY))
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.stringReq(),
                Labels.withId(DETECTION_FIELD_KEY),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(EpProperties.listStringEp(Labels.withId(TOKEN_LIST_FIELD_KEY),
            TOKEN_LIST_FIELD_KEY,
            "http://schema.org/ItemList")))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<TokenizerParameters> onInvocation(DataProcessorInvocation graph,
                                                                    ProcessingElementParameterExtractor extractor) {

    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    String filename = extractor.selectedFilename(BINARY_FILE_KEY);
    byte[] fileContent = client.fileApi().getFileContent(filename);
    String detection = extractor.mappingPropertyValue(DETECTION_FIELD_KEY);

    TokenizerParameters params = new TokenizerParameters(graph, detection, fileContent);
    return new ConfiguredEventProcessor<>(params, Tokenizer::new);
  }
}
