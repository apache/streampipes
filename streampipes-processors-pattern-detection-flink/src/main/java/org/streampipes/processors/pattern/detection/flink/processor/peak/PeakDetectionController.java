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

package org.streampipes.processors.pattern.detection.flink.processor.peak;

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
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

/**
 * Created by riemer on 20.04.2017.
 */
public class PeakDetectionController extends FlinkDataProcessorDeclarer<PeakDetectionParameters> {

  private static final String VALUE_TO_OBSERVE = "value-to-observe";
  private static final String PARTITION_BY = "partition-by";
  private static final String TIMESTAMP_MAPPING = "timestamp-mapping";
  private static final String LAG_KEY = "sp-lag";
  private static final String THRESHOLD_KEY = "sp-threshold";
  private static final String COUNT_WINDOW_SIZE = "sp-count-window";
  private static final String INFLUENCE_KEY = "sp-influence";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("peak-detection", "Peak Detection",
            "Detect peaks in time series data")
            .category(DataProcessorType.ALGORITHM)
            .iconUrl(PatternDetectionFlinkConfig.getIconUrl("peak-detection-icon"))
            .requiredStream(StreamRequirementsBuilder.create().requiredPropertyWithUnaryMapping(EpRequirements
                            .numberReq(),
                    Labels.from(VALUE_TO_OBSERVE, "Value to " +
                            "observe", "Provide a value where statistics are calculated upon"), PropertyScope.MEASUREMENT_PROPERTY)
                    .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                            Labels.from(TIMESTAMP_MAPPING, "Time", "Provide a time parameter"), PropertyScope.NONE)
                    .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                            Labels.from(PARTITION_BY, "Group by", "Partition the stream by a given id"), PropertyScope
                                    .DIMENSION_PROPERTY).build())
            .requiredIntegerParameter(COUNT_WINDOW_SIZE, "Count Window Size", "Defines " +
                    "the size of the count window", 60)
            .requiredIntegerParameter(LAG_KEY, "Lag", "Defines the lag of the smoothing " +
                    "function", 5)
            .requiredFloatParameter(THRESHOLD_KEY, "Threshold", "Defines the standard deviation " +
                    "threshold", 2.0f)
            .requiredFloatParameter(INFLUENCE_KEY, "Influence", "Defines the influence", 0.5f)
            .outputStrategy(OutputStrategies.fixed(
                    EpProperties.timestampProperty("timestamp"),
                    EpProperties.stringEp(Labels.empty(), "id", "http://schema.org/id"),
                    EpProperties.integerEp(Labels.empty(), "signal", "http://schema.org/Number")))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<PeakDetectionParameters> getRuntime(DataProcessorInvocation sepa,
                                                                       ProcessingElementParameterExtractor extractor) {
    String valueToObserve = extractor.mappingPropertyValue(VALUE_TO_OBSERVE);
    String timestampMapping = extractor.mappingPropertyValue(TIMESTAMP_MAPPING);
    String groupBy = extractor.mappingPropertyValue(PARTITION_BY);

    Integer countWindowSize = extractor.singleValueParameter(COUNT_WINDOW_SIZE, Integer.class);

    Integer lag = extractor.singleValueParameter(LAG_KEY, Integer.class);
    Double threshold = extractor.singleValueParameter(THRESHOLD_KEY, Double.class);
    Double influence = extractor.singleValueParameter(INFLUENCE_KEY, Double.class);


    PeakDetectionParameters params = new PeakDetectionParameters(sepa,
            valueToObserve, timestampMapping, groupBy, countWindowSize, lag, threshold, influence);

    if (PatternDetectionFlinkConfig.INSTANCE.getDebug()) {
      return new PeakDetectionProgram(params);
    } else {
      return new PeakDetectionProgram(params, new FlinkDeploymentConfig(PatternDetectionFlinkConfig.JAR_FILE,
              PatternDetectionFlinkConfig.INSTANCE.getFlinkHost(), PatternDetectionFlinkConfig.INSTANCE.getFlinkPort()));
    }


  }
}
