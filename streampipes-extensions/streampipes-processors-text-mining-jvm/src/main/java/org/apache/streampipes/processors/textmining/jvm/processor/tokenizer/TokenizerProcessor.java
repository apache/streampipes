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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TokenizerProcessor extends StreamPipesDataProcessor {

  private static final String DETECTION_FIELD_KEY = "detectionField";
  static final String TOKEN_LIST_FIELD_KEY = "tokenList";
  private static final String BINARY_FILE_KEY = "binary-file";

  private String detection;
  private TokenizerME tokenizer;


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
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    String filename = parameters.extractor().selectedFilename(BINARY_FILE_KEY);
    byte[] fileContent = runtimeContext.getStreamPipesClient().fileApi().getFileContent(filename);
    this.detection = parameters.extractor().mappingPropertyValue(DETECTION_FIELD_KEY);

    InputStream modelIn = new ByteArrayInputStream(fileContent);
    TokenizerModel model;
    try {
      model = new TokenizerModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    tokenizer = new TokenizerME(model);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String text = event.getFieldBySelector(detection).getAsPrimitive().getAsString();

    event.addField(TokenizerProcessor.TOKEN_LIST_FIELD_KEY, tokenizer.tokenize(text));

    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
