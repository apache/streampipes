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

package org.streampipes.processors.enricher.flink.processor.timestamp;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.enricher.flink.config.EnricherFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

public class TimestampController extends FlinkDataProcessorDeclarer<TimestampParameters> {

  private final String APPEND_PROPERTY = "appendedTime";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("enrich_configurable_timestamp", "Configurable Flink Timestamp Enrichment",
            "Appends the current time in ms to the event payload using Flink")
            .iconUrl(EnricherFlinkConfig.getIconUrl("enrich-timestamp-icon"))
            .requiredPropertyStream1(EpRequirements.anyProperty())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.longEp(Labels.empty(), APPEND_PROPERTY, SO.DateTime)))
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<TimestampParameters> getRuntime(
          DataProcessorInvocation graph,
          ProcessingElementParameterExtractor extractor) {

       TimestampParameters staticParam = new TimestampParameters(
            graph,
            APPEND_PROPERTY);

    if (EnricherFlinkConfig.INSTANCE.getDebug()) {
      return new TimestampProgram(staticParam);
    } else {
       return new TimestampProgram(staticParam, new FlinkDeploymentConfig(EnricherFlinkConfig.JAR_FILE,
              EnricherFlinkConfig.INSTANCE.getFlinkHost(), EnricherFlinkConfig.INSTANCE.getFlinkPort()));
    }
  }

}
