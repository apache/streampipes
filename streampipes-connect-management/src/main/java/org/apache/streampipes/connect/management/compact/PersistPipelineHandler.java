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

import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.manager.pipeline.compact.CompactPipelineManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CreateOptions;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.pipeline.compact.CompactPipeline;
import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.template.CompactPipelineTemplate;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.vocabulary.SO;

import java.util.List;
import java.util.Map;

import static org.apache.streampipes.manager.template.instances.PersistDataLakePipelineTemplate.DATA_LAKE_CONNECTOR_ID;
import static org.apache.streampipes.manager.template.instances.PersistDataLakePipelineTemplate.DATA_LAKE_DIMENSIONS_FIELD;
import static org.apache.streampipes.manager.template.instances.PersistDataLakePipelineTemplate.DATA_LAKE_MEASUREMENT_FIELD;
import static org.apache.streampipes.manager.template.instances.PersistDataLakePipelineTemplate.DATA_LAKE_TEMPLATE_ID;
import static org.apache.streampipes.manager.template.instances.PersistDataLakePipelineTemplate.DATA_LAKE_TIMESTAMP_FIELD;

public class PersistPipelineHandler {

  private final CRUDStorage<CompactPipelineTemplate> templateStorage;
  private final CompactPipelineManagement pipelineManagement;
  private final String authenticatedUserSid;

  public PersistPipelineHandler(CRUDStorage<CompactPipelineTemplate> templateStorage,
                                CompactPipelineManagement pipelineManagement,
                                String authenticatedUserSid) {
    this.templateStorage = templateStorage;
    this.pipelineManagement = pipelineManagement;
    this.authenticatedUserSid = authenticatedUserSid;
  }

  public PipelineOperationStatus createAndStartPersistPipeline(AdapterDescription adapterDescription) throws Exception {
    var template = getTemplate();
    if (template != null) {
      var compactPipeline = new CompactPipeline(
          String.format("persist-%s", adapterDescription.getName().replaceAll(" ", "-")),
          String.format("Persist %s", adapterDescription.getName()),
          null,
          makeTemplateConfig(adapterDescription, template.getPipeline()),
          new CreateOptions(false, true)
      );
      var pipelineGenerationResult = pipelineManagement.makePipeline(compactPipeline);
      if (pipelineGenerationResult.allPipelineElementsValid()) {
        String pipelineId = PipelineManager.addPipeline(authenticatedUserSid, pipelineGenerationResult.pipeline());
        if (compactPipeline.createOptions().start()) {
          return PipelineManager.startPipeline(pipelineId);
        }
      }
    }
    throw new IllegalArgumentException("Could not start persist pipeline");
  }

  private CompactPipelineTemplate getTemplate() {
    return this.templateStorage.getElementById(DATA_LAKE_TEMPLATE_ID);
  }

  private List<CompactPipelineElement> makeTemplateConfig(AdapterDescription adapterDescription,
                                                          List<CompactPipelineElement> pipelineElements) {
    pipelineElements.get(0).configuration().addAll(
        List.of(
            Map.of(DATA_LAKE_MEASUREMENT_FIELD, adapterDescription.getName()),
            Map.of(DATA_LAKE_TIMESTAMP_FIELD, String.format("s0::%s", getTimestampField(adapterDescription))),
            Map.of(DATA_LAKE_DIMENSIONS_FIELD, getDimensions(adapterDescription))
        )
    );
    pipelineElements.add(new CompactPipelineElement(
        "stream",
        DATA_LAKE_CONNECTOR_ID,
        adapterDescription.getCorrespondingDataStreamElementId(),
        null,
        null
    ));
    return pipelineElements;
  }

  private List<String> getDimensions(AdapterDescription adapterDescription) {
    return adapterDescription.getEventSchema().getEventProperties()
        .stream()
        .filter(ep -> ep.getPropertyScope().equalsIgnoreCase(PropertyScope.DIMENSION_PROPERTY.name()))
        .map(EventProperty::getRuntimeName)
        .toList();
  }

  private String getTimestampField(AdapterDescription adapterDescription) {
    return adapterDescription
        .getDataStream()
        .getEventSchema()
        .getEventProperties()
        .stream()
        .filter(ep -> ep instanceof EventPropertyPrimitive)
        .map(ep -> (EventPropertyPrimitive) ep)
        .filter(ep -> hasTimestampType(ep.getSemanticType()))
        .map(EventProperty::getRuntimeName)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Could not find timestamp field in schema"));
  }

  private boolean hasTimestampType(String semanticType) {
    return SO.DATE_TIME.equalsIgnoreCase(semanticType);
  }
}
