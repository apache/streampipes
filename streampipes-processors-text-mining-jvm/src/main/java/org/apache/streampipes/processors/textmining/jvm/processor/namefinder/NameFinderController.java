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

import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.processors.textmining.jvm.config.TextMiningJvmConfig;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NameFinderController extends StandaloneEventProcessingDeclarer<NameFinderParameters> implements ResolvesContainerProvidedOptions {

  private static final String MODEL = "model";
  private static final String TOKENS_FIELD_KEY = "tokensField";
  static final String FOUND_NAME_FIELD_KEY = "foundNames";

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
            .requiredSingleValueSelectionFromContainer(Labels.withId(MODEL))
            .outputStrategy(OutputStrategies.append(
                    EpProperties.listStringEp(
                            Labels.withId(FOUND_NAME_FIELD_KEY),
                            FOUND_NAME_FIELD_KEY,
                            "http://schema.org/ItemList")))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<NameFinderParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String tokens = extractor.mappingPropertyValue(TOKENS_FIELD_KEY);
    String model = extractor.selectedSingleValue(MODEL, String.class);

    NameFinderParameters params = new NameFinderParameters(graph, tokens, model);
    return new ConfiguredEventProcessor<>(params, NameFinder::new);
  }

  @Override
  public List<Option> resolveOptions(String requestId, StaticPropertyExtractor parameterExtractor) {
    String directoryPath = TextMiningJvmConfig.INSTANCE.getModelDirectory();

    List<Option> result = new ArrayList<>();

    File folder = new File(directoryPath);
    File[] listOfFiles = folder.listFiles();

    for (File file : listOfFiles) {
      result.add(new Option(file.getName()));
    }

    return result;
  }
}
