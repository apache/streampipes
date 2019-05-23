package org.streampipes.processors.pattern.detection.flink.processor.and;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.pattern.detection.flink.config.PatternDetectionFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

import java.util.List;

public class AndController extends FlinkDataProcessorDeclarer<AndParameters> {

  private static final String TIME_WINDOW = "time-window";
  private static final String TIME_UNIT = "time-unit";
  private static final String LEFT_MAPPING = "left-mapping";
  private static final String RIGHT_MAPPING = "right-mapping";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.pattern-detection.flink.and")
            .category(DataProcessorType.PATTERN_DETECT)
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(),
                            Labels.withId(LEFT_MAPPING)
                            , PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(),
                            Labels.withId(RIGHT_MAPPING)
                            , PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .requiredSingleValueSelection(Labels.withId(TIME_UNIT), Options.from("Seconds",
                    "Minutes", "Hours"))
            .requiredIntegerParameter(Labels.withId(TIME_WINDOW))
            .outputStrategy(OutputStrategies.custom(true))
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }


  @Override
  public FlinkDataProcessorRuntime<AndParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    List<String> leftMappings = extractor.mappingPropertyValues(LEFT_MAPPING);
    List<String> rightMappings = extractor.mappingPropertyValues(RIGHT_MAPPING);
    TimeUnit timeUnit = TimeUnit.valueOf(extractor.selectedSingleValue(TIME_UNIT, String.class));
    Integer timeWindow = extractor.singleValueParameter(TIME_WINDOW, Integer.class);

    AndParameters params = new AndParameters(graph, timeUnit, timeWindow, leftMappings, rightMappings);
    return new AndProgram(params, PatternDetectionFlinkConfig.INSTANCE.getDebug());

  }
}
