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

import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.model.template.BoundPipelineElement;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.storage.management.StorageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PipelineGenerator {

  private final PipelineTemplateDescription pipelineTemplateDescription;
  private final String datasetId;
  private final Pipeline pipeline;
  private final String pipelineName;

  private int count = 0;

  public PipelineGenerator(String datasetId,
                           PipelineTemplateDescription pipelineTemplateDescription,
                           String pipelineName) {
    this.pipelineTemplateDescription = pipelineTemplateDescription;
    this.datasetId = datasetId;
    this.pipelineName = pipelineName;
    this.pipeline = new Pipeline();
  }

  public Pipeline makePipeline() {

    pipeline.setName(pipelineName);
    pipeline.setPipelineId(UUID.randomUUID().toString());

    pipeline.setStreams(Collections.singletonList(prepareStream(datasetId)));
    pipeline.setSepas(new ArrayList<>());
    pipeline.setActions(new ArrayList<>());
    collectInvocations("jsplumb_domId" + count, pipelineTemplateDescription.getBoundTo());

    return pipeline;
  }

  private SpDataStream prepareStream(String streamId) {
    SpDataStream stream = getStream(streamId);
    stream = new SpDataStream(stream);
    stream.setDom(getDom());
    return stream;
  }

  private void collectInvocations(String currentDomId,
                                  List<BoundPipelineElement> boundPipelineElements) {
    for (BoundPipelineElement pipelineElement : boundPipelineElements) {
      InvocableStreamPipesEntity entity = clonePe(pipelineElement.getPipelineElementTemplate());
      entity.setConnectedTo(Collections.singletonList(currentDomId));
      entity.setDom(getDom());
      if (entity instanceof DataProcessorInvocation) {
        pipeline.getSepas().add((DataProcessorInvocation) entity);
        if (pipelineElement.getConnectedTo().size() > 0) {
          collectInvocations(entity.getDom(), pipelineElement.getConnectedTo());
        }
      } else {
        pipeline.getActions().add((DataSinkInvocation) entity);
      }
    }
    PipelineModificationMessage message = new PipelineVerificationHandlerV2(pipeline).verifyPipeline();
    handleModifications(message);
  }

  private void handleModifications(PipelineModificationMessage message) {
    pipeline.getSepas().forEach(processor -> {
      PipelineModification modification = getModification(message, processor.getDom());
      processor.setOutputStream(modification.getOutputStream());
      processor.setOutputStrategies(modification.getOutputStrategies());
      processor.setStaticProperties(modification.getStaticProperties());
      processor.setInputStreams(modification.getInputStreams());
      processor.setElementId(modification.getElementId());
    });
    pipeline.getActions().forEach(sink -> {
      PipelineModification modification = getModification(message, sink.getDom());
      sink.setStaticProperties(modification.getStaticProperties());
      sink.setInputStreams(modification.getInputStreams());
      sink.setElementId(modification.getElementId());
    });
  }

  private PipelineModification getModification(PipelineModificationMessage message,
                                               String domId) {
    return message.getPipelineModifications()
        .stream()
        .filter(pm -> pm.getDomId().equals(domId))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

  private InvocableStreamPipesEntity clonePe(InvocableStreamPipesEntity pipelineElementTemplate) {
    if (pipelineElementTemplate instanceof DataProcessorInvocation) {
      return new DataProcessorInvocation((DataProcessorInvocation) pipelineElementTemplate);
    } else {
      return new DataSinkInvocation((DataSinkInvocation) pipelineElementTemplate);
    }
  }

  private SpDataStream getStream(String datasetId) {
    return StorageManager
        .INSTANCE
        .getPipelineElementStorage()
        .getEventStreamById(datasetId);
  }


  private String getDom() {
    count++;
    return "jsplumb_domId" + count;
  }
}
