/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.manager.template;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineTemplateInvocationGenerator {

  private SpDataStream spDataStream;
  private PipelineTemplateDescription pipelineTemplateDescription;

  public PipelineTemplateInvocationGenerator(SpDataStream dataStream,
                                             PipelineTemplateDescription pipelineTemplateDescription) {
    this.spDataStream = dataStream;
    this.pipelineTemplateDescription = pipelineTemplateDescription;
  }

  public PipelineTemplateInvocation generateInvocation() {

    Pipeline pipeline =
        new PipelineGenerator(spDataStream.getElementId(), pipelineTemplateDescription, "test").makePipeline();

    PipelineTemplateInvocation pipelineTemplateInvocation = new PipelineTemplateInvocation();
    pipelineTemplateInvocation.setStaticProperties(collectStaticProperties(pipeline));
    pipelineTemplateInvocation.setDataStreamId(spDataStream.getElementId());
    //pipelineTemplateInvocation.setPipelineTemplateDescription(pipelineTemplateDescription);
    pipelineTemplateInvocation.setPipelineTemplateId(pipelineTemplateDescription.getPipelineTemplateId());
    return pipelineTemplateInvocation;
  }

  private List<StaticProperty> collectStaticProperties(Pipeline pipeline) {
    List<StaticProperty> staticProperties = new ArrayList<>();

    pipeline.getSepas().forEach(pe -> {
      pe.getStaticProperties().forEach(sp -> sp.setInternalName(pe.getDom() + sp.getInternalName()));
      staticProperties.addAll(pe.getStaticProperties());
    });
    pipeline.getActions().forEach(pe -> {
      pe.getStaticProperties().forEach(sp -> sp.setInternalName(pe.getDom() + sp.getInternalName()));
      staticProperties.addAll(pe.getStaticProperties());
    });

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
