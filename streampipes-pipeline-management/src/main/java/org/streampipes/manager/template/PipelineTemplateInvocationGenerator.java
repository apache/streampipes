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
package org.streampipes.manager.template;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateInvocation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineTemplateInvocationGenerator {

  private SpDataStream spDataStream;
  private PipelineTemplateDescription pipelineTemplateDescription;

  public PipelineTemplateInvocationGenerator(SpDataStream dataStream, PipelineTemplateDescription pipelineTemplateDescription) {
    this.spDataStream = dataStream;
    this.pipelineTemplateDescription = pipelineTemplateDescription;
  }

  public PipelineTemplateInvocation generateInvocation() {

    Pipeline pipeline = new PipelineGenerator(spDataStream.getElementId(), pipelineTemplateDescription, "test").makePipeline();

    PipelineTemplateInvocation pipelineTemplateInvocation = new PipelineTemplateInvocation();
    pipelineTemplateInvocation.setStaticProperties(collectStaticProperties(pipeline));
    pipelineTemplateInvocation.setDataSetId(spDataStream.getElementId());
    //pipelineTemplateInvocation.setPipelineTemplateDescription(pipelineTemplateDescription);
    pipelineTemplateInvocation.setPipelineTemplateId(pipelineTemplateDescription.getPipelineTemplateId());
    return pipelineTemplateInvocation;
  }

  private List<StaticProperty> collectStaticProperties(Pipeline pipeline) {
    List<StaticProperty> staticProperties = new ArrayList<>();

    pipeline.getSepas().forEach(pe -> {
      pe.getStaticProperties().forEach(sp -> sp.setInternalName(pe.getDOM() + sp.getInternalName()));
      staticProperties.addAll(pe.getStaticProperties());
    });
    pipeline.getActions().forEach(pe -> {
      pe.getStaticProperties().forEach(sp -> sp.setInternalName(pe.getDOM() + sp.getInternalName()));
      staticProperties.addAll(pe.getStaticProperties());
    });

    staticProperties
            .stream()
            .filter(sp -> sp instanceof MappingPropertyUnary)
            .forEach(mp -> ((MappingPropertyUnary) mp)
                    .setMapsTo(URI.create(((MappingPropertyUnary) mp)
                            .getMapsFromOptions()
                            .get(0)
                            .getElementId())));

    return staticProperties;
  }

  private List<StaticProperty> filter(List<StaticProperty> staticProperties) {
    return staticProperties
            .stream()
            // TODO fix (predefined is always true
            //.filter(sp -> !(sp instanceof MappingProperty))
            .filter(sp -> !(sp.isPredefined()))
            .collect(Collectors.toList());
  }
}
