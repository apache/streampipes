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

package org.apache.streampipes.processors.transformation.jvm.processor.array.count;

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
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class CountArrayController extends StandaloneEventProcessingDeclarer<CountArrayParameters> {

  public static final String COUNT_NAME = "countValue";
  public static final String ARRAY_FIELD = "array-field";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.count-array")
        .category(DataProcessorType.COUNT_OPERATOR)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(
            StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(EpRequirements.listRequirement(),
                    Labels.withId(ARRAY_FIELD), PropertyScope.NONE)
                .build())
        .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.empty(), COUNT_NAME,
            SO.NUMBER)))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<CountArrayParameters> onInvocation(DataProcessorInvocation graph,
                                                                     ProcessingElementParameterExtractor extractor) {
    String arrayField = extractor.mappingPropertyValue(ARRAY_FIELD);

    CountArrayParameters params = new CountArrayParameters(graph, arrayField);
    return new ConfiguredEventProcessor<>(params, CountArray::new);
  }

}
