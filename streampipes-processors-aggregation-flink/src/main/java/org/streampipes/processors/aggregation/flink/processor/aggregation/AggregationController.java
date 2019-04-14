/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.processors.aggregation.flink.processor.aggregation;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.aggregation.flink.config.AggregationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;

public class AggregationController extends FlinkDataProcessorDeclarer<AggregationParameters> {

  private static final String RESOURCE_ID = "strings";
  private static final String PE_ID = "org.streampipes.processors.aggregation.flink.aggregation";

  private static final String AGGREGATE_KEY = "aggregate";
  private static final String AGGREGATED_VALUE_KEY = "aggregatedValue";
  private static final String GROUP_BY_KEY = "groupBy";
  private static final String OUTPUT_EVERY_KEY = "outputEvery";
  private static final String TIME_WINDOW_KEY = "timeWindow";
  private static final String OPERATION_KEY = "operation";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(getLabel(PE_ID))
            .category(DataProcessorType.AGGREGATE)
            .providesAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.numberReq(),
                            getLabel(AGGREGATE_KEY),
                            PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .naryMappingPropertyWithoutRequirement(
                    getLabel(GROUP_BY_KEY),
                    PropertyScope.DIMENSION_PROPERTY)
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(
                    getLabel(AGGREGATED_VALUE_KEY),
                    "aggregatedValue",
                    "http://schema.org/Number")))
            .requiredIntegerParameter(getLabel(OUTPUT_EVERY_KEY))
            .requiredIntegerParameter(getLabel(TIME_WINDOW_KEY))
            .requiredSingleValueSelection(getLabel(OPERATION_KEY),
                    Options.from(new Tuple2<>("Average", "AVG"),
                            new Tuple2<>("Sum", "SUM"),
                            new Tuple2<>("Min", "MIN"),
                            new Tuple2<>("Max", "MAX")))
            .supportedFormats(StandardTransportFormat.standardFormat())
            .supportedProtocols(StandardTransportFormat.standardProtocols())
            .build();
  }

  private Label getLabel(String id) {
    return Labels.fromResources(RESOURCE_ID, id);
  }

  @Override
  public FlinkDataProcessorRuntime<AggregationParameters> getRuntime(DataProcessorInvocation graph,
                                                                     ProcessingElementParameterExtractor extractor) {

    List<String> groupBy = new ArrayList<>();

    String aggregate = extractor.mappingPropertyValue(AGGREGATE_KEY);
    Integer outputEvery = extractor.singleValueParameter(OUTPUT_EVERY_KEY, Integer.class);
    Integer timeWindowSize = extractor.singleValueParameter(TIME_WINDOW_KEY, Integer.class);
    String aggregateOperation = extractor.selectedSingleValueInternalName(OPERATION_KEY, String.class);

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : graph.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    AggregationParameters staticParam = new AggregationParameters(graph, AggregationType.valueOf
            (aggregateOperation),
            outputEvery, groupBy,
            aggregate, timeWindowSize, selectProperties);

    return new AggregationProgram(staticParam, AggregationFlinkConfig.INSTANCE.getDebug());

  }
}
