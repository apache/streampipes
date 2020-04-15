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
package org.apache.streampipes.processors.filters.jvm.processor.enrich;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.processors.filters.jvm.config.FiltersJvmConfig;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class MergeByEnrichController extends StandaloneEventProcessingDeclarer<MergeByEnrichParameters> {

  private static final String SELECT_STREAM = "select-stream";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.enrich")
            .category(DataProcessorType.TRANSFORM)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredSingleValueSelection(Labels.withId(SELECT_STREAM),
                    Options.from("Stream 1", "Stream 2"))
            .outputStrategy(OutputStrategies.custom(true))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<MergeByEnrichParameters>
  onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    List<String> outputKeySelectors = extractor.outputKeySelectors();

    String selectedStream = extractor.selectedSingleValue(SELECT_STREAM, String.class);

    MergeByEnrichParameters staticParam = new MergeByEnrichParameters(
            graph, outputKeySelectors, selectedStream);

    return new ConfiguredEventProcessor<>(staticParam, MergeByEnrich::new);
  }
}
