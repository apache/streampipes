package de.fzi.cep.sepa.flink.samples.statistics.window;

import static de.fzi.cep.sepa.flink.samples.statistics.StatisticsSummaryController.*;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.flink.samples.count.aggregate.CountAggregateConstants;
import de.fzi.cep.sepa.flink.samples.statistics.StatisticsSummaryController;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.Statistics;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.Options;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

import java.util.concurrent.TimeUnit;

/**
 * Created by riemer on 20.04.2017.
 */
public class StatisticsSummaryControllerWindow extends
        AbstractFlinkAgentDeclarer<StatisticsSummaryParametersWindow> {

  private static final String VALUE_TO_OBSERVE = "value-to-observe";
  private static final String PARTITION_BY = "partition-by";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("statistics-summary-window", "Sliding Descriptive " +
                    "Statistics",
            "Calculate" +
                    " simple descriptive summary statistics based on a configurable time window")
            .iconUrl(Config.getIconUrl("statistics-icon"))
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.numberReq(),
                    VALUE_TO_OBSERVE, "Value to " +
                            "observe", "Provide a value where statistics are calculated upon")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.timestampReq(),
                    TIMESTAMP_MAPPING, "Time", "Provide a time parameter")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.stringReq(),
                    PARTITION_BY, "Group by", "Partition the stream by a given id")
            .requiredIntegerParameter(CountAggregateConstants.TIME_WINDOW, "Time Window Size", "Size of the time window")
            .requiredSingleValueSelection(CountAggregateConstants.SCALE, "Time Window Scale", "",
                    Options.from("Hours", "Minutes", "Seconds"))
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.timestampProperty("timestamp"),
                    EpProperties.stringEp("id", "http://schema.org/id"),
                    EpProperties.doubleEp(StatisticsSummaryController.MEAN, Statistics.MEAN),
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
  protected FlinkSepaRuntime<StatisticsSummaryParametersWindow> getRuntime(SepaInvocation sepa) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

    String valueToObserve = extractor.mappingPropertyValue(VALUE_TO_OBSERVE);
    String timestampMapping = extractor.mappingPropertyValue(TIMESTAMP_MAPPING);

    String groupBy = extractor.mappingPropertyValue(PARTITION_BY);

    int timeWindowSize = extractor.singleValueParameter(CountAggregateConstants.TIME_WINDOW, Integer.class);
    String scale = SepaUtils.getOneOfProperty(sepa, CountAggregateConstants.SCALE);

    TimeUnit timeUnit;

    if (scale.equals("Hours")) {
      timeUnit = TimeUnit.HOURS;
    }
    else if (scale.equals("Minutes")) {
      timeUnit = TimeUnit.MINUTES;
    }
    else {
      timeUnit = TimeUnit.SECONDS;
    }

    StatisticsSummaryParametersWindow params = new StatisticsSummaryParametersWindow(sepa,
            valueToObserve, timestampMapping, groupBy, (long) timeWindowSize, timeUnit);

    StatisticsSummaryParamsSerializable serializableParams = new StatisticsSummaryParamsSerializable
            (valueToObserve, timestampMapping, groupBy, (long) timeWindowSize, timeUnit);

    return new StatisticsSummaryProgramWindow(params, serializableParams, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));

  }
}
