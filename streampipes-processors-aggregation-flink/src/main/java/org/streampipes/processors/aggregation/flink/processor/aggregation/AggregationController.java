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
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;

public class AggregationController extends FlinkDataProcessorDeclarer<AggregationParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.aggregation" +
                    ".flink.aggregation",
            "Aggregation", "Performs different " +
                    "aggregation functions")
            .category(DataProcessorType.AGGREGATE)
            .iconUrl(AggregationFlinkConfig.iconBaseUrl + "/Aggregation_Icon_HQ.png")
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(), Labels.from("aggregate",
                            "Property Selection", "Specifies the event property from your stream that should be aggregated" +
                                    "."), PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .naryMappingPropertyWithoutRequirement(Labels.from("groupBy", "Group by", "Partitions the incoming stream" +
                    " by the selected event " +
                    "properties"), PropertyScope.DIMENSION_PROPERTY)
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.from("aggregated-value",
                    "Aggregated Value", "The calculated aggregation value"),
                    "aggregatedValue",
                    "http://schema.org/Number")))
            .requiredIntegerParameter(Labels.from("outputEvery", "Output Frequency", "Output " +
                    "values every (seconds)"))
            .requiredIntegerParameter(Labels.from("timeWindow", "Time Window Size", "Size of the " +
                    "time window in seconds"))
            .requiredSingleValueSelection(Labels.from("operation", "Operation", "Aggregation " +
                            "operation type"),
                    Options.from(new Tuple2<>("Average", "AVG"),
                            new Tuple2<>("Sum", "SUM"),
                            new Tuple2<>("Min", "MIN"),
                            new Tuple2<>("Max", "MAX")))
            .supportedFormats(StandardTransportFormat.standardFormat())
            .supportedProtocols(StandardTransportFormat.standardProtocols())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<AggregationParameters> getRuntime(DataProcessorInvocation graph,
                                                                     ProcessingElementParameterExtractor extractor) {

    List<String> groupBy = new ArrayList<>();

    String aggregate = extractor.mappingPropertyValue("aggregate");
    Integer outputEvery = extractor.singleValueParameter("outputEvery", Integer.class);
    Integer timeWindowSize = extractor.singleValueParameter("timeWindow", Integer.class);
    String aggregateOperation = extractor.selectedSingleValueInternalName("operation", String.class);

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
