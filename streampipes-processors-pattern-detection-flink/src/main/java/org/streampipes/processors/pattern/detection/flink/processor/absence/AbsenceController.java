package org.streampipes.processors.pattern.detection.flink.processor.absence;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.processors.pattern.detection.flink.config.PatternDetectionFlinkConfig;
import org.streampipes.processors.pattern.detection.flink.processor.and.TimeUnit;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.ArrayList;
import java.util.List;

public class AbsenceController extends FlinkDataProcessorDeclarer<AbsenceParameters> {

  private static final String TIME_WINDOW = "time-window";
  private static final String TIME_UNIT = "time-unit";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.pattern-detection.flink.absence", "Absence", "Detects whether an event does not arrive within a specified time after the occurrence of another event.")
            .category(DataProcessorType.PATTERN_DETECT)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredSingleValueSelection(Labels.from(TIME_UNIT, "Time Unit", "The time unit used for detecting the co-occurrence."), Options.from("Seconds", "Minutes", "Hours"))
            .requiredIntegerParameter(Labels.from(TIME_WINDOW, "Time Window Size", "Time window size (seconds)"))
            .outputStrategy(OutputStrategies.custom(false))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<AbsenceParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    List<String> selectProperties = new ArrayList<>();
    for (EventProperty p : graph.getOutputStream().getEventSchema().getEventProperties()) {
      selectProperties.add(p.getRuntimeName());
    }

    TimeUnit timeUnit = TimeUnit.valueOf(extractor.selectedSingleValue(TIME_UNIT, String.class));
    Integer timeWindow = extractor.singleValueParameter(TIME_WINDOW, Integer.class);

    AbsenceParameters params = new AbsenceParameters(graph, selectProperties, timeWindow, timeUnit);

    return new AbsenceProgram(params, PatternDetectionFlinkConfig.INSTANCE.getDebug());
  }
}
