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
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class TimestampController extends FlinkDataProcessorDeclarer<TimestampParameters> {

  private final String APPEND_PROPERTY = "appendedTime";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.enricher.flink.timestamp")
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.longEp(Labels.empty(), APPEND_PROPERTY, SO.DateTime)))
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<TimestampParameters> getRuntime(
          DataProcessorInvocation graph,
          ProcessingElementParameterExtractor extractor) {

    TimestampParameters staticParam = new TimestampParameters(
            graph,
            APPEND_PROPERTY);


    return new TimestampProgram(staticParam, EnricherFlinkConfig.INSTANCE.getDebug());
  }

}
