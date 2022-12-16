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
package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.SelectionStaticProperty;
import org.apache.streampipes.model.template.BoundPipelineElement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BoundPipelineElementBuilder {

  private BoundPipelineElement boundPipelineElement;
  private InvocableStreamPipesEntity streamPipesEntity;
  private List<BoundPipelineElement> connectedTo;

  private BoundPipelineElementBuilder(InvocableStreamPipesEntity streamPipesEntity) {
    this.streamPipesEntity = streamPipesEntity;
    // TODO fix this hack
    this.streamPipesEntity.setElementId(this.streamPipesEntity.getBelongsTo() + ":" + UUID.randomUUID().toString());
    this.boundPipelineElement = new BoundPipelineElement();
    this.connectedTo = new ArrayList<>();
  }

  public static BoundPipelineElementBuilder create(DataProcessorDescription dataProcessorDescription) {
    return new BoundPipelineElementBuilder(new DataProcessorInvocation(dataProcessorDescription));
  }

  public static BoundPipelineElementBuilder create(DataSinkDescription dataSinkDescription) {
    return new BoundPipelineElementBuilder(new DataSinkInvocation(dataSinkDescription));
  }

  public BoundPipelineElementBuilder connectTo(BoundPipelineElement boundPipelineElement) {
    this.connectedTo.add(boundPipelineElement);
    return this;
  }

  public BoundPipelineElementBuilder withPredefinedFreeTextValue(String internalStaticPropertyId, String value) {
    this.streamPipesEntity.getStaticProperties().stream().filter(sp -> sp instanceof FreeTextStaticProperty)
        .forEach(sp -> {
          if (sp.getInternalName().equals(internalStaticPropertyId)) {
            sp.setPredefined(true);
            ((FreeTextStaticProperty) sp).setValue(value);
          }
        });

    return this;
  }

  public BoundPipelineElementBuilder withPredefinedSelection(String internalStaticPropertyId,
                                                             List<String> selectedOptions) {
    this.streamPipesEntity.getStaticProperties().stream().filter(sp -> sp instanceof SelectionStaticProperty)
        .forEach(sp -> {
          if (sp.getInternalName().equals(internalStaticPropertyId)) {
            sp.setPredefined(true);
            ((SelectionStaticProperty) sp).getOptions().forEach(o -> {
              if (selectedOptions.stream().anyMatch(so -> so.equals(o.getName()))) {
                o.setSelected(true);
              }
            });
          }
        });
    return this;
  }

  public BoundPipelineElementBuilder withOverwrittenLabel(String internalStaticPropertyId, String newLabel) {
    this.streamPipesEntity.getStaticProperties().forEach(sp -> {
      sp.setPredefined(true);
      if (sp.getInternalName().equals(internalStaticPropertyId)) {
        sp.setLabel(newLabel);
      }
    });
    return this;
  }

  public BoundPipelineElement build() {
    this.boundPipelineElement.setPipelineElementTemplate(streamPipesEntity);
    this.boundPipelineElement.setConnectedTo(connectedTo);
    return boundPipelineElement;
  }

  public BoundPipelineElement buildWithStandardSinks() {
    // TODO implement
    return this.build();
  }

}
