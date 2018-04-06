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
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.template.BoundPipelineElement;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.model.template.PipelineTemplateInvocation;

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

    List<StaticProperty> propertiesToConfigure = new ArrayList<>();
    collectStaticProperties(propertiesToConfigure, pipelineTemplateDescription.getConnectedTo());

    PipelineTemplateInvocation pipelineTemplateInvocation = new PipelineTemplateInvocation();
    pipelineTemplateInvocation.setStaticProperties(propertiesToConfigure);
    pipelineTemplateInvocation.setDataSetId(spDataStream.getElementId());
    pipelineTemplateInvocation.setPipelineTemplateDescription(pipelineTemplateDescription);
    return pipelineTemplateInvocation;
  }

  private void collectStaticProperties(List<StaticProperty> staticProperties, List<BoundPipelineElement> boundPipelineElements) {
      for(BoundPipelineElement element : boundPipelineElements) {
        staticProperties.addAll(filter(element.getPipelineElementTemplate().getStaticProperties()));
        if (element.getConnectedTo().size() > 0) {
          collectStaticProperties(staticProperties, element.getConnectedTo());
        }
      }
  }

  private List<StaticProperty> filter(List<StaticProperty> staticProperties) {
    return staticProperties
            .stream()
            // TODO fix (predefined is always true
            //.filter(sp -> !(sp instanceof MappingProperty))
            //.filter(sp -> !(sp.isPredefined()))
            .collect(Collectors.toList());
  }
}
