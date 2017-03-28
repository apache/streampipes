package de.fzi.cep.sepa.flink.samples.statistics;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.Statistics;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;
import de.fzi.cep.sepa.sdk.utils.Datatypes;

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
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("statistics-summary", "Statistics Summary", "Calculate" +
            " simple descriptive summary statistics")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.listRequirement(Datatypes
                    .Number), listPropertyMappingName, "Property", "Select a list property")
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(MEAN, Statistics
                            .MEAN),
                    EpProperties.doubleEp(MIN, Statistics.MIN),
                    EpProperties.doubleEp(MAX, Statistics.MAX),
                    EpProperties.doubleEp(SUM, Statistics.SUM),
                    EpProperties.doubleEp(STDDEV, Statistics.STDDEV),
                    EpProperties.doubleEp(VARIANCE, Statistics.VARIANCE),
                    EpProperties.doubleEp(N, Statistics.N)))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  protected FlinkSepaRuntime<StatisticsSummaryParameters> getRuntime(SepaInvocation graph) {
    String listPropertyMapping = SepaUtils.getMappingPropertyName(graph, listPropertyMappingName);

    StatisticsSummaryParameters params = new StatisticsSummaryParameters(graph, listPropertyMapping);

    return new StatisticsSummaryProgram(params);

  }
}
