/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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
 *
 */

package org.streampipes.processors.filters.jvm.processor.projection;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.processors.filters.jvm.config.FiltersJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectionController extends StandaloneEventProcessingDeclarer<ProjectionParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.filters.jvm.project", "Projection", "Outputs a selectable subset of an input event type")
            .category(DataProcessorType.TRANSFORM)
            .iconUrl(FiltersJvmConfig.getIconUrl("projection"))
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.custom())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.jms(), SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<ProjectionParameters>
  onInvocation(DataProcessorInvocation graph) {

    List<String> outputKeys = graph
            .getOutputStream()
            .getEventSchema()
            .getEventProperties()
            .stream()
            .map(EventProperty::getRuntimeName)
            .collect(Collectors.toList());

    ProjectionParameters staticParam = new ProjectionParameters(
            graph, outputKeys);

    return new ConfiguredEventProcessor<>(staticParam, () -> new Projection(staticParam));
  }
}
