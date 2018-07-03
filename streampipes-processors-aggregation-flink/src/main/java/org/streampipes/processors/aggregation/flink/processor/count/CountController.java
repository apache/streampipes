package org.streampipes.processors.aggregation.flink.processor.count;

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

public class CountController extends FlinkDataProcessorDeclarer<CountParameters> {

  private static final String GROUP_BY_KEY = "groupBy";
  private static final String TIME_WINDOW_KEY = "timeWindow";
  private static final String SCALE_KEY = "scale";

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("org.streampipes.processors.aggregation.flink.count", "Count Aggregation",
            "Performs an aggregation based on a given event property and outputs the number of occurrences.")
            .category(DataProcessorType.AGGREGATE)
            .iconUrl(AggregationFlinkConfig.iconBaseUrl + "/Counter_Icon_HQ.png")
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .naryMappingPropertyWithoutRequirement(Labels.from(GROUP_BY_KEY, "Group Stream By", ""), PropertyScope.DIMENSION_PROPERTY)
            .outputStrategy(OutputStrategies.append(EpProperties.integerEp(Labels.empty(), "countValue",
                    "http://schema.org/Number")))
            .requiredIntegerParameter(Labels.from(TIME_WINDOW_KEY, "Time Window Size", "Size of the time window " +
                    "in seconds"))
            .requiredSingleValueSelection(Labels.from(SCALE_KEY, "Time Window Scale", ""),
                    Options.from(new Tuple2<>("Hours", "HOURS"),
                            new Tuple2<>("Minutes", "MINUTES"),
                            new Tuple2<>("Seconds", "SECONDS")))
            .supportedFormats(StandardTransportFormat.standardFormat())
            .supportedProtocols(StandardTransportFormat.standardProtocols())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<CountParameters> getRuntime(DataProcessorInvocation graph,
                                                               ProcessingElementParameterExtractor extractor) {
    List<String> groupBy = extractor.mappingPropertyValues(GROUP_BY_KEY);

    int timeWindowSize = extractor.singleValueParameter(TIME_WINDOW_KEY, Integer.class);
    String scale = extractor.selectedSingleValueInternalName(SCALE_KEY, String.class);

    TimeScale timeScale = TimeScale.valueOf(scale);

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : graph.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    CountParameters staticParam = new CountParameters(graph, timeWindowSize, groupBy, timeScale,
            selectProperties);

    return new CountProgram(staticParam, AggregationFlinkConfig.INSTANCE.getDebug());

  }
}
