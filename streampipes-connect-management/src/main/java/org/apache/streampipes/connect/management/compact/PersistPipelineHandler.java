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

package org.apache.streampipes.connect.management.compact;

import org.apache.streampipes.manager.template.PipelineTemplateManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.vocabulary.SO;

import java.net.URI;
import java.util.List;

public class PersistPipelineHandler {

  private static final String templateId = "org.apache.streampipes.manager.template.instances.DataLakePipelineTemplate";
  private static final String configPrefix = "jsplumb_domId2";

  private final PipelineTemplateManagement pipelineTemplateManagement;
  private final String authenticatedUserSid;

  public PersistPipelineHandler(PipelineTemplateManagement pipelineTemplateManagement,
                                String authenticatedUserSid) {
    this.pipelineTemplateManagement = pipelineTemplateManagement;
    this.authenticatedUserSid = authenticatedUserSid;
  }

  public PipelineOperationStatus createAndStartPersistPipeline(AdapterDescription adapterDescription) {
    var pipelineTemplateInvocation = pipelineTemplateManagement.prepareInvocation(
        adapterDescription.getCorrespondingDataStreamElementId(),
        templateId
    );

    applyPipelineName(pipelineTemplateInvocation, adapterDescription.getName());
    applyDataLakeConfig(pipelineTemplateInvocation, adapterDescription);

    return pipelineTemplateManagement.createAndStartPipeline(pipelineTemplateInvocation, authenticatedUserSid);
  }

  private void applyPipelineName(PipelineTemplateInvocation pipelineTemplateInvocation,
                                 String adapterName) {
    pipelineTemplateInvocation.setPipelineTemplateId(templateId);
    pipelineTemplateInvocation.setKviName(adapterName);
  }

  private void applyDataLakeConfig(PipelineTemplateInvocation pipelineTemplateInvocation,
                                   AdapterDescription adapterDescription) {
    pipelineTemplateInvocation.getStaticProperties().forEach(sp -> {
      if (sp.getInternalName().equalsIgnoreCase(withPrefix("db_measurement"))) {
        ((FreeTextStaticProperty) sp).setValue(adapterDescription.getName());
      }
      if (sp.getInternalName().equalsIgnoreCase(withPrefix("timestamp_mapping"))) {
        ((MappingPropertyUnary) sp).setSelectedProperty(
            String.format("s0::%s", getTimestampField(adapterDescription)
        ));
      }
      if (sp.getInternalName().equalsIgnoreCase(withPrefix("schema_update"))) {
        ((OneOfStaticProperty) sp).getOptions().forEach(o -> {
          if (o.getName().equals("Update schema")) {
            o.setSelected(true);
          }
        });
      }
    });
  }

  private String withPrefix(String config) {
    return configPrefix + config;
  }

  private String getTimestampField(AdapterDescription adapterDescription) {
    return adapterDescription
        .getDataStream()
        .getEventSchema()
        .getEventProperties()
        .stream()
        .filter(ep -> ep instanceof EventPropertyPrimitive)
        .map(ep -> (EventPropertyPrimitive) ep)
        .filter(ep -> hasTimestampType(ep.getDomainProperties()))
        .map(EventProperty::getRuntimeName)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Could not find timestamp field in schema"));
  }

  private boolean hasTimestampType(List<URI> semanticTypes) {
    return semanticTypes
        .stream()
        .map(URI::toString)
        .anyMatch(s -> s.equalsIgnoreCase(SO.DATE_TIME));
  }
}
