package org.streampipes.processors.aggregation.flink.processor.rate;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.aggregation.flink.config.AggregationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class EventRateController extends FlinkDataProcessorDeclarer<EventRateParameter> {

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("eventrate", "Event rate", "Computes current event rate")
            .category(DataProcessorType.AGGREGATE)
            .iconUrl(AggregationFlinkConfig.getIconUrl("event_rate"))
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.fixed(EpProperties.doubleEp(Labels.empty(), "rate",
                    "http://schema.org/Number")))
            .requiredIntegerParameter("rate", "Average/Sec", "" +
                    "in seconds")
            .requiredIntegerParameter("output", "Output Every (seconds)", "")
            .supportedFormats(StandardTransportFormat.standardFormat())
            .supportedProtocols(StandardTransportFormat.standardProtocols())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<EventRateParameter> getRuntime(DataProcessorInvocation graph,
                                                                  ProcessingElementParameterExtractor extractor) {
    Integer avgRate = extractor.singleValueParameter("rate", Integer.class);
    Integer outputRate = extractor.singleValueParameter("output", Integer.class);

    String topicPrefix = "topic://";
    EventRateParameter staticParam = new EventRateParameter(graph, avgRate, outputRate
            , topicPrefix + graph.getOutputStream().getEventGrounding().getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName());

    return new EventRateProgram(staticParam, AggregationFlinkConfig.INSTANCE.getDebug());

  }
}