package de.fzi.cep.sepa.flink.samples.peak;

import static de.fzi.cep.sepa.flink.samples.statistics.StatisticsSummaryController.*;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.flink.samples.statistics.StatisticsSummaryController;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.Statistics;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.extractor.ProcessingElementParameterExtractor;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 20.04.2017.
 */
public class PeakDetectionController extends AbstractFlinkAgentDeclarer<PeakDetectionParameters> {

  private static final String VALUE_TO_OBSERVE = "value-to-observe";
  private static final String PARTITION_BY = "partition-by";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";
  private static final String LAG_KEY = "sp-lag";
  private static final String THRESHOLD_KEY = "sp-threshold";
  private static final String INFLUENCE_KEY = "sp-influence";

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("peak-detection", "Peak Detection ",
            "Detect peaks in time-series data.")
            .category(EpaType.ALGORITHM)
            .iconUrl(Config.getIconUrl("peak-detection-icon"))
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.numberReq(),
                    VALUE_TO_OBSERVE, "Value to " +
                            "observe", "Provide a value where statistics are calculated upon")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.timestampReq(),
                    TIMESTAMP_MAPPING, "Time", "Provide a time parameter")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.stringReq(),
                    PARTITION_BY, "Group by", "Partition the stream by a given id")
            .requiredIntegerParameter(LAG_KEY, "Lag", "Defines the lag of the smoothing function")
            .requiredFloatParameter(THRESHOLD_KEY, "Threshold", "Defines the standard deviation " +
                    "threshold")
            .requiredFloatParameter(INFLUENCE_KEY, "Influence", "Defines the influence")
            .outputStrategy(OutputStrategies.append(
                    EpProperties.timestampProperty("timestamp"),
                    EpProperties.stringEp("machineId", "http://schema.org/id"),
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
  protected FlinkSepaRuntime<PeakDetectionParameters> getRuntime(SepaInvocation sepa) {
    ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

    String valueToObserve = extractor.mappingPropertyValue(VALUE_TO_OBSERVE);
    String timestampMapping = extractor.mappingPropertyValue(TIMESTAMP_MAPPING);
    String groupBy = extractor.mappingPropertyValue(PARTITION_BY);

    Integer lag = extractor.singleValueParameter(LAG_KEY, Integer.class);
    Double threshold = extractor.singleValueParameter(THRESHOLD_KEY, Double.class);
    Double influence = extractor.singleValueParameter(INFLUENCE_KEY, Double.class);


    PeakDetectionParameters params = new PeakDetectionParameters(graph,
            valueToObserve, timestampMapping, groupBy, lag, threshold, influence);

    return new PeakDetectionProgram(params, new FlinkDeploymentConfig(Config.JAR_FILE,
            Config
                    .FLINK_HOST, Config.FLINK_PORT));

  }
}
