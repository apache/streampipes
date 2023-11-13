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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.ListField;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.textmining.jvm.processor.TextMiningUtil;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ChunkerProcessor extends StreamPipesDataProcessor {

  private static final String TAGS_FIELD_KEY = "tagsField";
  private static final String TOKENS_FIELD_KEY = "tokensField";
  static final String CHUNK_TYPE_FIELD_KEY = "chunkType";
  static final String CHUNK_FIELD_KEY = "chunk";
  private static final String BINARY_FILE_KEY = "binary-file";

  private String tags;
  private String tokens;
  private ChunkerME chunker;

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
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext context) throws SpRuntimeException {
    this.tags = parameters.extractor().mappingPropertyValue(TAGS_FIELD_KEY);
    this.tokens = parameters.extractor().mappingPropertyValue(TOKENS_FIELD_KEY);
    String filename = parameters.extractor().selectedFilename(BINARY_FILE_KEY);
    byte[] fileContent = context.getStreamPipesClient().fileApi().getFileContent(filename);

    InputStream modelIn = new ByteArrayInputStream(fileContent);
    ChunkerModel model;
    try {
      model = new ChunkerModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    chunker = new ChunkerME(model);
  }

  @Override
  public void onEvent(Event event,
                      SpOutputCollector collector) throws SpRuntimeException {
    ListField tags = event.getFieldBySelector(this.tags).getAsList();
    ListField tokens = event.getFieldBySelector(this.tokens).getAsList();


    String[] tagsArray = tags.castItems(String.class).toArray(String[]::new);
    String[] tokensArray = tokens.castItems(String.class).toArray(String[]::new);

    Span[] spans = chunker.chunkAsSpans(tokensArray, tagsArray);

    List<String> chunks = TextMiningUtil.extractSpans(spans, tokensArray);
    String[] types = Arrays.stream(spans).map(Span::getType).toArray(String[]::new);

    event.addField(ChunkerProcessor.CHUNK_TYPE_FIELD_KEY, types);
    event.addField(ChunkerProcessor.CHUNK_FIELD_KEY, chunks);

    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
