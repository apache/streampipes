package org.streampipes.processors.aggregation.flink.processor.count;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.processors.aggregation.flink.config.AggregationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;

public class CountController extends FlinkDataProcessorDeclarer<CountParameters> {

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("org.streampipes.count", "Count Aggregation",
            "Performs an aggregation based on a given event property and outputs the number of occurrences.")
            .category(DataProcessorType.AGGREGATE)
            .iconUrl(AggregationFlinkConfig.iconBaseUrl + "/Counter_Icon_HQ.png")
//						.requiredPropertyStream1(EpRequirements.anyProperty())
            .setStream1()
            .naryMappingPropertyWithoutRequirement("groupBy", "Group Stream By", "")
            .outputStrategy(OutputStrategies.append(EpProperties.integerEp(Labels.empty(), "countValue",
                    "http://schema.org/Number")))
            .requiredIntegerParameter("timeWindow", "Time Window Size", "Size of the time window " +
                    "in seconds")
            .requiredSingleValueSelection("scale", "Time Window Scale", "",
                    Options.from("Hours", "Minutes", "Seconds"))
            .supportedFormats(StandardTransportFormat.standardFormat())
            .supportedProtocols(StandardTransportFormat.standardProtocols())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<CountParameters> getRuntime(DataProcessorInvocation graph,
                                                               ProcessingElementParameterExtractor extractor) {
    List<String> groupBy = extractor.mappingPropertyValues("groupBy");

    int timeWindowSize = extractor.singleValueParameter("timeWindow", Integer.class);
    String scale = extractor.selectedSingleValue("scale", String.class);

    TimeScale timeScale;

    if (scale.equals("Hours")) {
      timeScale = TimeScale.HOURS;
    } else if (scale.equals("Minutes")) {
      timeScale = TimeScale.MINUTES;
    } else {
      timeScale = TimeScale.SECONDS;
    }

    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : graph.getInputStreams().get(0).getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    CountParameters staticParam = new CountParameters(graph, timeWindowSize, groupBy, timeScale,
            selectProperties);

    return new CountProgram(staticParam, AggregationFlinkConfig.INSTANCE.getDebug());

  }
}
