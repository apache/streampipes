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

package org.apache.streampipes.processors.textmining.jvm.processor.chunker;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
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
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ChunkerController extends StandaloneEventProcessingDeclarer<ChunkerParameters> {

  private static final String TAGS_FIELD_KEY = "tagsField";
  private static final String TOKENS_FIELD_KEY = "tokensField";
  static final String CHUNK_TYPE_FIELD_KEY = "chunkType";
  static final String CHUNK_FIELD_KEY = "chunk";
  private static final String BINARY_FILE_KEY = "binary-file";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.textmining.jvm.chunker")
        .category(DataProcessorType.ENRICH_TEXT)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredFile(Labels.withId(BINARY_FILE_KEY))
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.listRequirement(Datatypes.String),
                Labels.withId(TAGS_FIELD_KEY),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.listRequirement(Datatypes.String),
                Labels.withId(TOKENS_FIELD_KEY),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(
            EpProperties.listStringEp(
                Labels.withId(CHUNK_TYPE_FIELD_KEY),
                CHUNK_TYPE_FIELD_KEY,
                "http://schema.org/ItemList"),
            EpProperties.listStringEp(
                Labels.withId(CHUNK_FIELD_KEY),
                CHUNK_FIELD_KEY,
                "http://schema.org/ItemList")))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<ChunkerParameters> onInvocation(DataProcessorInvocation graph,
                                                                  ProcessingElementParameterExtractor extractor) {

    StreamPipesClient client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    String filename = extractor.selectedFilename(BINARY_FILE_KEY);
    byte[] fileContent = client.fileApi().getFileContent(filename);

    String tags = extractor.mappingPropertyValue(TAGS_FIELD_KEY);
    String tokens = extractor.mappingPropertyValue(TOKENS_FIELD_KEY);

    ChunkerParameters params = new ChunkerParameters(graph, tags, tokens, fileContent);
    return new ConfiguredEventProcessor<>(params, Chunker::new);
  }
}
