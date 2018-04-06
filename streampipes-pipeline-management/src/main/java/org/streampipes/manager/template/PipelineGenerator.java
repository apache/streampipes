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
import org.streampipes.manager.matching.PipelineVerificationHandler;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.exception.InvalidConnectionException;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineModificationMessage;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.template.BoundPipelineElement;
import org.streampipes.model.template.PipelineTemplateInvocation;
import org.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PipelineGenerator {

  private PipelineTemplateInvocation pipelineTemplateInvocation;
  private Pipeline pipeline;

  private int count = 0;

  public PipelineGenerator(PipelineTemplateInvocation pipelineTemplateInvocation) {
    this.pipelineTemplateInvocation = pipelineTemplateInvocation;
    this.pipeline = new Pipeline();
  }

  public Pipeline makePipeline() {

    pipeline.setName(pipelineTemplateInvocation.getKviName());

    pipeline.setStreams(Collections.singletonList(prepareStream(pipelineTemplateInvocation.getDataSetId())));
    pipeline.setSepas(new ArrayList<>());
    pipeline.setActions(new ArrayList<>());
    collectInvocations("domId" + count, pipeline.getStreams().get(0), pipelineTemplateInvocation.getPipelineTemplateDescription().getConnectedTo());

    return pipeline;
  }

  private SpDataStream prepareStream(String datasetId) {
    SpDataStream stream = new SpDataStream(getStream(datasetId));
    stream.setDOM(getDom());
    return stream;
  }

  private void collectInvocations(String currentDomId, SpDataStream inputStream, List<BoundPipelineElement> boundPipelineElements) {
    for (BoundPipelineElement pipelineElement : boundPipelineElements) {
      InvocableStreamPipesEntity entity = pipelineElement.getPipelineElementTemplate();
      entity.setConnectedTo(Arrays.asList(currentDomId));
      entity.setDOM(getDom());
      //entity.setConfigured(true);
      // TODO hack
      entity.setInputStreams(Arrays.asList(inputStream));
      if (entity instanceof DataProcessorInvocation) {
        pipeline.getSepas().add((DataProcessorInvocation) entity);
        try {
          PipelineModificationMessage message = new PipelineVerificationHandler(pipeline).validateConnection().computeMappingProperties().getPipelineModificationMessage();
        } catch (RemoteServerNotAccessibleException e) {
          e.printStackTrace();
        } catch (NoMatchingJsonSchemaException e) {
          e.printStackTrace();
        } catch (InvalidConnectionException e) {
          e.printStackTrace();
        } catch (NoSepaInPipelineException e) {
          e.printStackTrace();
        }
        if (pipelineElement.getConnectedTo().size() > 0) {
          collectInvocations(entity.getDOM(), ((DataProcessorInvocation) entity).getOutputStream(), pipelineElement.getConnectedTo());
        }
      } else {
        pipeline.getActions().add((DataSinkInvocation) entity);
      }
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
