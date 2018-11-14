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

import org.streampipes.commons.exceptions.NoMatchingJsonSchemaException;
import org.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.streampipes.commons.exceptions.RemoteServerNotAccessibleException;
import org.streampipes.manager.matching.DataSetGroundingSelector;
import org.streampipes.manager.matching.PipelineVerificationHandler;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.exception.InvalidConnectionException;
import org.streampipes.model.client.pipeline.DataSetModificationMessage;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineModificationMessage;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.template.BoundPipelineElement;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.storage.management.StorageDispatcher;

import java.util.*;

public class PipelineGenerator {

  private PipelineTemplateDescription pipelineTemplateDescription;
  private String datasetId;
  private Pipeline pipeline;
  private String pipelineName;

  private int count = 0;

  public PipelineGenerator(String datasetId, PipelineTemplateDescription pipelineTemplateDescription, String pipelineName) {
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
    collectInvocations("domId" + count, pipelineTemplateDescription.getBoundTo());

    return pipeline;
  }

  private SpDataStream prepareStream(String streamId) {
    SpDataStream stream = getStream(streamId);
    if (stream instanceof SpDataSet) {
      stream = new SpDataSet((SpDataSet) stream);
      DataSetModificationMessage message = new DataSetGroundingSelector((SpDataSet) stream).selectGrounding();
      stream.setEventGrounding(message.getEventGrounding());
      ((SpDataSet) stream).setDatasetInvocationId(message.getInvocationId());
    } else {
      stream = new SpDataStream(stream);
    }
    stream.setDOM(getDom());
    return stream;
  }

  private void collectInvocations(String currentDomId, List<BoundPipelineElement> boundPipelineElements) {
    for (BoundPipelineElement pipelineElement : boundPipelineElements) {
      InvocableStreamPipesEntity entity = clonePe(pipelineElement.getPipelineElementTemplate());
      entity.setConnectedTo(Collections.singletonList(currentDomId));
      entity.setDOM(getDom());
      //entity.setConfigured(true);
      // TODO hack
      //entity.setInputStreams(Arrays.asList(inputStream));
      if (entity instanceof DataProcessorInvocation) {
        pipeline.getSepas().add((DataProcessorInvocation) entity);
        try {
          PipelineModificationMessage message = new PipelineVerificationHandler(pipeline).validateConnection().computeMappingProperties().getPipelineModificationMessage();
          //entity.setInputStreams(message.getPipelineModifications().get(0).getInputStreams());
          pipeline.getSepas().remove(entity);
          entity.setConfigured(true);
          entity.setStaticProperties(message.getPipelineModifications().get(0).getStaticProperties());
          pipeline.getSepas().add((DataProcessorInvocation) entity);
        } catch (RemoteServerNotAccessibleException | NoMatchingJsonSchemaException | NoSepaInPipelineException | InvalidConnectionException e) {
          e.printStackTrace();
        }
        if (pipelineElement.getConnectedTo().size() > 0) {
          collectInvocations(entity.getDOM(), pipelineElement.getConnectedTo());
        }
      } else {
        pipeline.getActions().add((DataSinkInvocation) entity);
        try {
          PipelineModificationMessage message = new PipelineVerificationHandler(pipeline).validateConnection().computeMappingProperties().getPipelineModificationMessage();
          pipeline.getActions().remove(entity);
          //entity.setInputStreams(message.getPipelineModifications().get(0).getInputStreams());
          entity.setConfigured(true);
          entity.setStaticProperties(message.getPipelineModifications().get(0).getStaticProperties());
          pipeline.getActions().add((DataSinkInvocation) entity);
        } catch (RemoteServerNotAccessibleException | NoMatchingJsonSchemaException | NoSepaInPipelineException | InvalidConnectionException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private InvocableStreamPipesEntity clonePe(InvocableStreamPipesEntity pipelineElementTemplate) {
    if (pipelineElementTemplate instanceof DataProcessorInvocation) {
      return new DataProcessorInvocation((DataProcessorInvocation) pipelineElementTemplate);
    } else {
      return new DataSinkInvocation((DataSinkInvocation) pipelineElementTemplate);
    }
  }

  private SpDataStream getStream(String datasetId) {
    return StorageDispatcher
            .INSTANCE
            .getTripleStore()
            .getStorageAPI()
            .getEventStreamById(datasetId);
  }


  private String getDom() {
    count++;
    return "domId" + count;
  }
}
