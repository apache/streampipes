/*
 * Copyright 2017 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.processors.pattern.detection.flink.processor.increase;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.pattern.detection.flink.config.PatternDetectionFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class IncreaseController extends FlinkDataProcessorDeclarer<IncreaseParameters> {

  private static final String PARTITION_BY = "partition-by";
  private static final String TIMESTAMP = "timestamp";
  private static final String VALUE_MAPPING = "value-mapping";
  private static final String INCREASE = "increase";
  private static final String DURATION = "duration";
  private static final String OPERATION = "operation";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.pattern-detection.flink.increase")
            .withLocales(Locales.EN)
            .category(DataProcessorType.PATTERN_DETECT)
            .iconUrl(PatternDetectionFlinkConfig.getIconUrl("increase-icon"))
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements
                            .numberReq(), Labels.withId(VALUE_MAPPING),
                            PropertyScope.MEASUREMENT_PROPERTY)
                    .requiredPropertyWithUnaryMapping(EpRequirements
                            .timestampReq(), Labels.withId(TIMESTAMP),
                            PropertyScope.HEADER_PROPERTY)
                    .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                            Labels.withId(PARTITION_BY), PropertyScope
                                    .DIMENSION_PROPERTY)
                    .build())
            .requiredIntegerParameter(Labels.withId(INCREASE), 0, 500, 1)
            .requiredIntegerParameter(Labels.withId(DURATION))
            .requiredSingleValueSelection(Labels.withId(OPERATION))
            .outputStrategy(OutputStrategies.custom(true))
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<IncreaseParameters> getRuntime(DataProcessorInvocation graph,
                                                                  ProcessingElementParameterExtractor extractor) {

    String operation = extractor.selectedSingleValue(OPERATION, String.class);
    Integer increase = extractor.singleValueParameter(INCREASE, Integer.class);
    Integer duration = extractor.singleValueParameter(DURATION, Integer.class);
    String mapping = extractor.mappingPropertyValue(VALUE_MAPPING);
    String groupBy = extractor.mappingPropertyValue(PARTITION_BY);
    String timestampField = extractor.mappingPropertyValue(TIMESTAMP);

    IncreaseParameters params = new IncreaseParameters(graph, getOperation(operation), increase, duration, mapping,
            groupBy, timestampField);

    return new IncreaseProgram(params, PatternDetectionFlinkConfig.INSTANCE.getDebug());

  }

  private Operation getOperation(String operation) {
    if (operation.equals("Increase")) {
      return Operation.INCREASE;
    } else {
      return Operation.DECREASE;
    }
  }
}
