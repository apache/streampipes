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

package org.streampipes.processors.textmining.jvm.processor.chunker;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ChunkerController extends StandaloneEventProcessingDeclarer<ChunkerParameters> {

  private static final String TAGS_FIELD_KEY = "tagsField";
  private static final String TOKENS_FIELD_KEY = "tokensField";
  static final String CHUNK_TYPE_FIELD_KEY = "chunkType";
  static final String CHUNK_FIELD_KEY = "chunk";

  //TODO: Change Icon
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.textmining.jvm.chunker")
            .category(DataProcessorType.ENRICH_TEXT)
            .withAssets(Assets.DOCUMENTATION)
            .withLocales(Locales.EN)
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
  public ConfiguredEventProcessor<ChunkerParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String tags = extractor.mappingPropertyValue(TAGS_FIELD_KEY);
    String tokens = extractor.mappingPropertyValue(TOKENS_FIELD_KEY);

    ChunkerParameters params = new ChunkerParameters(graph, tags, tokens);
    return new ConfiguredEventProcessor<>(params, Chunker::new);
  }
}
