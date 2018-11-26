package org.streampipes.processors.aggregation.flink.processor.rate;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.aggregation.flink.config.AggregationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class EventRateController extends FlinkDataProcessorDeclarer<EventRateParameter> {

  private static final String RATE_KEY = "rate";

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("org.streampipes.processor.aggregation.flink.rate", "Event rate", "Computes current event rate")
            .category(DataProcessorType.AGGREGATE)
            .iconUrl(AggregationFlinkConfig.getIconUrl("event_rate"))
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.fixed(EpProperties.doubleEp(Labels.empty(), "rate",
                    "http://schema.org/Number")))
            .requiredIntegerParameter(Labels.from(RATE_KEY, "Time Baseline", "Time window size used for calculating the rate" +
                    "in seconds, also defines the output rate"))
            .supportedFormats(StandardTransportFormat.standardFormat())
            .supportedProtocols(StandardTransportFormat.standardProtocols())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<EventRateParameter> getRuntime(DataProcessorInvocation graph,
                                                                  ProcessingElementParameterExtractor extractor) {
    Integer avgRate = extractor.singleValueParameter(RATE_KEY, Integer.class);

    EventRateParameter staticParam = new EventRateParameter(graph, avgRate);

    return new EventRateProgram(staticParam, AggregationFlinkConfig.INSTANCE.getDebug());

  }
}