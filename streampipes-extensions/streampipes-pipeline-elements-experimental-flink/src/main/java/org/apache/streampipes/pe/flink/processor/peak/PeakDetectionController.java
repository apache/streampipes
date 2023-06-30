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

package org.apache.streampipes.pe.flink.processor.peak;

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
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

public class PeakDetectionController extends FlinkDataProcessorDeclarer<PeakDetectionParameters> {

  private static final String VALUE_TO_OBSERVE = "value-to-observe";
  private static final String PARTITION_BY = "partition-by";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";
  private static final String LAG_KEY = "sp-lag";
  private static final String THRESHOLD_KEY = "sp-threshold";
  private static final String COUNT_WINDOW_SIZE = "sp-count-window";
  private static final String INFLUENCE_KEY = "sp-influence";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.pattern-detection.flink.peak-detection")
        .category(DataProcessorType.ALGORITHM)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(EpRequirements
                    .numberReq(),
                Labels.withId(VALUE_TO_OBSERVE), PropertyScope.MEASUREMENT_PROPERTY)
            .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                Labels.withId(TIMESTAMP_MAPPING), PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                Labels.withId(PARTITION_BY), PropertyScope
                    .DIMENSION_PROPERTY).build())
        .requiredIntegerParameter(Labels.withId(COUNT_WINDOW_SIZE), 60)
        .requiredIntegerParameter(Labels.withId(LAG_KEY), 5)
        .requiredFloatParameter(Labels.withId(THRESHOLD_KEY), 2.0f)
        .requiredFloatParameter(Labels.withId(INFLUENCE_KEY), 0.5f)
        .outputStrategy(OutputStrategies.fixed(
            EpProperties.timestampProperty("timestamp"),
            EpProperties.stringEp(Labels.empty(), "id", "http://schema.org/id"),
            EpProperties.integerEp(Labels.empty(), "signal", "http://schema.org/Number")))
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<PeakDetectionParameters> getProgram(DataProcessorInvocation sepa,
                                                                       ProcessingElementParameterExtractor extractor) {
    String valueToObserve = extractor.mappingPropertyValue(VALUE_TO_OBSERVE);
    String timestampMapping = extractor.mappingPropertyValue(TIMESTAMP_MAPPING);
    String groupBy = extractor.mappingPropertyValue(PARTITION_BY);

    Integer countWindowSize = extractor.singleValueParameter(COUNT_WINDOW_SIZE, Integer.class);

    Integer lag = extractor.singleValueParameter(LAG_KEY, Integer.class);
    Double threshold = extractor.singleValueParameter(THRESHOLD_KEY, Double.class);
    Double influence = extractor.singleValueParameter(INFLUENCE_KEY, Double.class);


    PeakDetectionParameters params = new PeakDetectionParameters(sepa,
        valueToObserve, timestampMapping, groupBy, countWindowSize, lag, threshold, influence);

    return new PeakDetectionProgram(params);
  }
}
