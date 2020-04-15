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
package org.apache.streampipes.processors.filters.jvm.processor.merge;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.filters.jvm.config.FiltersJvmConfig;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class MergeByTimeController extends StandaloneEventProcessingDeclarer<MergeByTimeParameters> {

  private static final String TIMESTAMP_MAPPING_STREAM_1_KEY = "timestamp_mapping_stream_1";
  private static final String TIMESTAMP_MAPPING_STREAM_2_KEY = "timestamp_mapping_stream_2";
  private static final String NUMBER_MAPPING = "number_mapping";
  private static final String TIME_INTERVAL = "time-interval";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.merge")
            .category(DataProcessorType.TRANSFORM)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON, "merge_description.png")
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(
                    EpRequirements.timestampReq(),
                    Labels.withId(TIMESTAMP_MAPPING_STREAM_1_KEY),
                    PropertyScope.NONE).build())
            .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(
                    EpRequirements.timestampReq(),
                    Labels.withId(TIMESTAMP_MAPPING_STREAM_2_KEY),
                    PropertyScope.NONE).build())
            .requiredIntegerParameter(Labels.withId(TIME_INTERVAL), NUMBER_MAPPING)
            .outputStrategy(OutputStrategies.custom(true))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<MergeByTimeParameters>
  onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    List<String> outputKeySelectors = extractor.outputKeySelectors();

    String timestampFieldStream1 = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_STREAM_1_KEY);
    String timestampFieldStream2 = extractor.mappingPropertyValue(TIMESTAMP_MAPPING_STREAM_2_KEY);
    Integer timeInterval = extractor.singleValueParameter(TIME_INTERVAL, Integer.class);

    MergeByTimeParameters staticParam = new MergeByTimeParameters(
            graph, outputKeySelectors, timestampFieldStream1, timestampFieldStream2, timeInterval);

    return new ConfiguredEventProcessor<>(staticParam, MergeByTime::new);
  }
}
