package org.streampipes.pe.mixed.flink.samples.statistics;

import org.streampipes.sdk.helpers.Labels;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.vocabulary.Statistics;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;

/**
 * Created by riemer on 29.01.2017.
 */
public class StatisticsSummaryController extends AbstractFlinkAgentDeclarer<StatisticsSummaryParameters> {

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
  protected FlinkSepaRuntime<StatisticsSummaryParameters> getRuntime(DataProcessorInvocation graph) {
    String listPropertyMapping = SepaUtils.getMappingPropertyName(graph, listPropertyMappingName);

    StatisticsSummaryParameters params = new StatisticsSummaryParameters(graph, listPropertyMapping);

    return new StatisticsSummaryProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));

  }
}
