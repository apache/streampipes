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

package org.streampipes.processors.textmining.jvm.processor.namefinder;

import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NameFinderController extends StandaloneEventProcessingDeclarer<NameFinderParameters> implements ResolvesContainerProvidedOptions {

  private static final String MODEL = "model";
  private static final String TOKENS_FIELD_KEY = "tokensField";
  static final String FOUND_NAME_FIELD_KEY = "foundNames";

  //TODO: Change Icon
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.textmining.jvm.namefinder")
            .category(DataProcessorType.ENRICH_TEXT)
            .withAssets(Assets.DOCUMENTATION)
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
    String model = extractor.selectedSingleValueFromRemote(MODEL, String.class);

    NameFinderParameters params = new NameFinderParameters(graph, tokens, model);
    return new ConfiguredEventProcessor<>(params, NameFinder::new);
  }

  @Override
  public List<Option> resolveOptions(String requestId, StaticPropertyExtractor parameterExtractor) {
    String directoryPath = "/home/lennard/models"; //TextMiningJvmConfig.INSTANCE.getModelDirectory();

    List<Option> result = new ArrayList<>();

    File folder = new File(directoryPath);
    File[] listOfFiles = folder.listFiles();

    for (File file : listOfFiles) {
      result.add(new Option(file.getName()));
    }

    return result;
  }
}
