package org.streampipes.pe.mixed.flink.samples.peak;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;

/**
 * Created by riemer on 20.04.2017.
 */
public class PeakDetectionController extends AbstractFlinkAgentDeclarer<PeakDetectionParameters> {

  private static final String VALUE_TO_OBSERVE = "value-to-observe";
  private static final String PARTITION_BY = "partition-by";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";
  private static final String LAG_KEY = "sp-lag";
  private static final String THRESHOLD_KEY = "sp-threshold";
  private static final String COUNT_WINDOW_SIZE = "sp-count-window";
  private static final String INFLUENCE_KEY = "sp-influence";

  @Override
  public SepaDescription declareModel() {
    return ProcessingElementBuilder.create("peak-detection", "Peak Detection",
            "Detect peaks in time series data")
            .category(EpaType.ALGORITHM)
            .iconUrl(FlinkConfig.getIconUrl("peak-detection-icon"))
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.numberReq(),
                    VALUE_TO_OBSERVE, "Value to " +
                            "observe", "Provide a value where statistics are calculated upon")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.timestampReq(),
                    TIMESTAMP_MAPPING, "Time", "Provide a time parameter")
            .stream1PropertyRequirementWithUnaryMapping(EpRequirements.stringReq(),
                    PARTITION_BY, "Group by", "Partition the stream by a given id")
            .requiredIntegerParameter(COUNT_WINDOW_SIZE, "Count Window Size", "Defines " +
                    "the size of the count window", 60)
            .requiredIntegerParameter(LAG_KEY, "Lag", "Defines the lag of the smoothing " +
                    "function", 5)
            .requiredFloatParameter(THRESHOLD_KEY, "Threshold", "Defines the standard deviation " +
                    "threshold", 2.0f)
            .requiredFloatParameter(INFLUENCE_KEY, "Influence", "Defines the influence", 0.5f)
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.timestampProperty("timestamp"),
                    EpProperties.stringEp("id", "http://schema.org/id"),
                    EpProperties.integerEp("signal", "http://schema.org/Number")))
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

    Integer countWindowSize = extractor.singleValueParameter(COUNT_WINDOW_SIZE, Integer.class);

    Integer lag = extractor.singleValueParameter(LAG_KEY, Integer.class);
    Double threshold = extractor.singleValueParameter(THRESHOLD_KEY, Double.class);
    Double influence = extractor.singleValueParameter(INFLUENCE_KEY, Double.class);


    PeakDetectionParameters params = new PeakDetectionParameters(sepa,
            valueToObserve, timestampMapping, groupBy, countWindowSize, lag, threshold, influence);

    //return new PeakDetectionProgram(params);

    return new PeakDetectionProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
            FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));

  }
}
