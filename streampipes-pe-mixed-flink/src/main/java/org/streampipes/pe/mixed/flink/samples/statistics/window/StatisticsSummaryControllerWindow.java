package org.streampipes.pe.mixed.flink.samples.statistics.window;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.pe.mixed.flink.samples.count.aggregate.CountAggregateConstants;
import org.streampipes.pe.mixed.flink.samples.statistics.StatisticsSummaryController;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.vocabulary.Statistics;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

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
            .iconUrl(FlinkConfig.getIconUrl("statistics-icon"))
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.numberReq(),
                    VALUE_TO_OBSERVE, "Value to " +
                            "observe", "Provide a value where statistics are calculated upon")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.timestampReq(),
                    TIMESTAMP_MAPPING, "Time", "Provide a time parameter")
            .requiredPropertyStream1WithUnaryMapping(EpRequirements.stringReq(),
                    PARTITION_BY, "Group by", "Partition the stream by a given id")
            .requiredIntegerParameter(CountAggregateConstants.TIME_WINDOW, "Time Window Size", "Size of the time window")
            .requiredSingleValueSelection(CountAggregateConstants.SCALE, "Time Window Scale", "",
                    Options.from("Hours", "Minutes", "Seconds"))
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.timestampProperty("timestamp"),
                    EpProperties.stringEp("id", "http://schema.org/id"),
                    EpProperties.doubleEp(StatisticsSummaryController.MEAN, Statistics.MEAN),
                    EpProperties.doubleEp(StatisticsSummaryController.MIN, Statistics.MIN),
                    EpProperties.doubleEp(StatisticsSummaryController.MAX, Statistics.MAX),
                    EpProperties.doubleEp(StatisticsSummaryController.SUM, Statistics.SUM),
                    EpProperties.doubleEp(StatisticsSummaryController.STDDEV, Statistics.STDDEV),
                    EpProperties.doubleEp(StatisticsSummaryController.VARIANCE, Statistics.VARIANCE),
                    EpProperties.doubleEp(StatisticsSummaryController.N, Statistics.N)))
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

    return new StatisticsSummaryProgramWindow(params, serializableParams, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));

  }
}
