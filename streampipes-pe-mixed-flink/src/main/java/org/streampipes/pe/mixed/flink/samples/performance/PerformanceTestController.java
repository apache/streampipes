/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.mixed.flink.samples.performance;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.mixed.flink.FlinkUtils;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;


public class PerformanceTestController extends FlinkDataProcessorDeclarer<PerformanceTestParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("performance-test-timestamp", "Timestamp enricher for performance test",
            "Appends the current time in ms to the event payload using Flink")
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.longEp(Labels.empty(), "appendedTime", SO.DateTime)))
            .requiredTextParameter(Labels.from("timestamp-field-name", "Timestamp field name", ""))
            .requiredIntegerParameter(Labels.from("scale-factor", "Scale Factor", ""))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<PerformanceTestParameters> getRuntime(
          DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    String timestampFieldName = extractor.singleValueParameter("timestamp-field-name", String.class);
    Integer scaleFactor = extractor.singleValueParameter("scale-factor", Integer.class);
    PerformanceTestParameters staticParam = new PerformanceTestParameters(
            graph,
            timestampFieldName, scaleFactor);

    return new PerformanceTestProgram(staticParam, FlinkUtils.getFlinkDeploymentConfig());
  }
}
