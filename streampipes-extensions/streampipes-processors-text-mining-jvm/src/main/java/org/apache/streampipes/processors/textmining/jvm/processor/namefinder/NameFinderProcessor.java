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

package org.apache.streampipes.processors.textmining.jvm.processor.namefinder;

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

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class NameFinderProcessor extends StreamPipesDataProcessor {

  private static final String MODEL = "model";
  private static final String TOKENS_FIELD_KEY = "tokensField";
  static final String FOUND_NAME_FIELD_KEY = "foundNames";

  private String tokens;
  private NameFinderME nameFinder;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.textmining.jvm.namefinder")
        .category(DataProcessorType.ENRICH_TEXT)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.listRequirement(Datatypes.String),
                Labels.withId(TOKENS_FIELD_KEY),
                PropertyScope.NONE)
            .build())
        .requiredFile(Labels.withId(MODEL))
        .outputStrategy(OutputStrategies.append(
            EpProperties.listStringEp(
                Labels.withId(FOUND_NAME_FIELD_KEY),
                FOUND_NAME_FIELD_KEY,
                "http://schema.org/ItemList")))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    String filename = parameters.extractor().selectedFilename(MODEL);
    byte[] fileContent = runtimeContext.getStreamPipesClient().fileApi().getFileContent(filename);
    this.tokens = parameters.extractor().mappingPropertyValue(TOKENS_FIELD_KEY);
    loadModel(fileContent);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    ListField tokens = event.getFieldBySelector(this.tokens).getAsList();

    String[] tokensArray = tokens.castItems(String.class).toArray(String[]::new);
    Span[] spans = nameFinder.find(tokensArray);

    // Generating the list of names from the found spans by the nameFinder
    List<String> names = TextMiningUtil.extractSpans(spans, tokensArray);

    nameFinder.clearAdaptiveData();

    event.addField(NameFinderProcessor.FOUND_NAME_FIELD_KEY, names);
    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  private void loadModel(byte[] modelContent) {
    try (InputStream modelIn = new ByteArrayInputStream(modelContent)) {
      TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
      nameFinder = new NameFinderME(model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
