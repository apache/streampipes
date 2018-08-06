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

package org.streampipes.processors.statistics.flink.processor.stat;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.processors.statistics.flink.config.StatisticsFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.vocabulary.Statistics;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class StatisticsSummaryController extends FlinkDataProcessorDeclarer<StatisticsSummaryParameters> {

  private static final String listPropertyMappingName = "list-property";

  public static final String MIN = "min";
  public static final String MAX = "max";
  public static final String SUM = "sum";
  public static final String STDDEV = "stddev";
  public static final String VARIANCE = "variance";
  public static final String MEAN = "mean";
  public static final String N = "n";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("statistics-summary", "Statistics Summary", "Calculate" +
            " simple descriptive summary statistics")
            .iconUrl(StatisticsFlinkConfig.getIconUrl("statistical_summary"))
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.listRequirement(Datatypes
                    .Number), listPropertyMappingName, "Property", "Select a list property")
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.empty(), MEAN, Statistics
                            .MEAN),
                    EpProperties.doubleEp(Labels.empty(), MIN, Statistics.MIN),
                    EpProperties.doubleEp(Labels.empty(), MAX, Statistics.MAX),
                    EpProperties.doubleEp(Labels.empty(), SUM, Statistics.SUM),
                    EpProperties.doubleEp(Labels.empty(), STDDEV, Statistics.STDDEV),
                    EpProperties.doubleEp(Labels.empty(), VARIANCE, Statistics.VARIANCE),
                    EpProperties.doubleEp(Labels.empty(), N, Statistics.N)))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<StatisticsSummaryParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String listPropertyMapping = SepaUtils.getMappingPropertyName(graph, listPropertyMappingName);

    StatisticsSummaryParameters params = new StatisticsSummaryParameters(graph, listPropertyMapping);

    return new StatisticsSummaryProgram(params);

  }
}
