package org.streampipes.processors.pattern.detection.flink.processor.and;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.pattern.detection.flink.config.PatternDetectionFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;

public class AndController extends FlinkDataProcessorDeclarer<AndParameters> {

  private static final String TIME_WINDOW = "time-window";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.pattern-detection.flink.and", "And", "Detects whether an event co-occurs with another event within a given time..")
            .category(DataProcessorType.PATTERN_DETECT)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredIntegerParameter(Labels.from(TIME_WINDOW, "Time Window Size", "Time window size (seconds)"))
            .outputStrategy(OutputStrategies.custom(true))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }


  @Override
  public FlinkDataProcessorRuntime<AndParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String timeUnit = extractor.selectedSingleValue("time-unit", String.class);
    //String matchingOperator = SepaUtils.getOneOfProperty(invocationGraph, "matching-operator");
    String matchingOperator = "";
    int duration = extractor.singleValueParameter("duration", Integer.class);
    //String partitionProperty = SepaUtils.getMappingPropertyName(invocationGraph, "partition", true);
    //List<String> matchingProperties = SepaUtils.getMatchingPropertyNames(invocationGraph, "matching");
    List<String> matchingProperties = new ArrayList<>();
    AndParameters params = new AndParameters(graph, timeUnit, matchingOperator, duration, matchingProperties);

    return new AndProgram(params, PatternDetectionFlinkConfig.INSTANCE.getDebug());

  }
}
