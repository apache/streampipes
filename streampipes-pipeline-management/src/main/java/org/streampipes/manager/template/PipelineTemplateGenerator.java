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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.ElementNotFoundException;
import org.streampipes.manager.matching.DataSetGroundingSelector;
import org.streampipes.manager.matching.v2.ElementVerification;
import org.streampipes.manager.template.instances.DashboardPipelineTemplate;
import org.streampipes.manager.template.instances.DelmeExamplePipelineTemplate;
import org.streampipes.manager.template.instances.ElasticsearchPipelineTemplate;
import org.streampipes.manager.template.instances.PipelineTemplate;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.DataSetModificationMessage;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.streampipes.storage.management.StorageDispatcher;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PipelineTemplateGenerator {


  Logger logger = LoggerFactory.getLogger(PipelineTemplateGenerator.class);

  private List<PipelineTemplateDescription> availableDescriptions = new ArrayList<>();

  public List<PipelineTemplateDescription> getAllPipelineTemplates() {

    List<PipelineTemplate> allPipelineTemplates = new ArrayList<>();

    allPipelineTemplates.add(new DashboardPipelineTemplate());
    allPipelineTemplates.add(new ElasticsearchPipelineTemplate());
    allPipelineTemplates.add(new DelmeExamplePipelineTemplate());


    for (PipelineTemplate pt : allPipelineTemplates) {
      try {
        availableDescriptions.add(pt.declareModel());
      } catch (URISyntaxException e) {
        e.printStackTrace();
      } catch (ElementNotFoundException e) {
          logger.warn("Adapter template can not be used because some elements are not installed", e);
      }
    }

    return availableDescriptions;
  }

  public List<PipelineTemplateDescription> getCompatibleTemplates(String streamId) {
    List<PipelineTemplateDescription> compatibleTemplates = new ArrayList<>();
    ElementVerification verifier = new ElementVerification();
    SpDataStream streamOffer = null;

    try {
      streamOffer = getStream(streamId);

      if (streamOffer instanceof SpDataSet) {
        streamOffer = new SpDataSet((SpDataSet) prepareStream((SpDataSet) streamOffer));
      } else {
        streamOffer = new SpDataStream(streamOffer);
      }
      if (streamOffer != null) {
        for(PipelineTemplateDescription pipelineTemplateDescription : getAllPipelineTemplates()) {
          // TODO make this work for 2+ input streams
          InvocableStreamPipesEntity entity = cloneInvocation(pipelineTemplateDescription.getBoundTo().get(0).getPipelineElementTemplate());
          if (verifier.verify(streamOffer, entity)) {
            compatibleTemplates.add(pipelineTemplateDescription);
          }
        }
      }

    } catch (ElementNotFoundException e) {
      e.printStackTrace();
    }

    return compatibleTemplates;
  }

  private InvocableStreamPipesEntity cloneInvocation(InvocableStreamPipesEntity pipelineElementTemplate) {
    if (pipelineElementTemplate instanceof DataProcessorInvocation) {
      return new DataProcessorInvocation((DataProcessorInvocation) pipelineElementTemplate);
    } else {
      return new DataSinkInvocation((DataSinkInvocation) pipelineElementTemplate);
    }
  }

  private SpDataStream prepareStream(SpDataSet stream) {
    DataSetModificationMessage message = new DataSetGroundingSelector(stream).selectGrounding();
    stream.setEventGrounding(message.getEventGrounding());
    stream.setDatasetInvocationId(message.getInvocationId());
    return stream;
  }

  protected SpDataStream getStream(String streamId) throws ElementNotFoundException {
    SpDataStream result = getStorage()
            .getEventStreamById(streamId);

    if (result == null) {
      throw new ElementNotFoundException("Data stream " + streamId + " is not installed!");
    }

    return  result;
  }

  protected DataProcessorDescription getProcessor(String id) throws URISyntaxException, ElementNotFoundException {
    DataProcessorDescription result = getStorage()
            .getSEPAById(id);

    if (result == null) {
      throw new ElementNotFoundException("Data processor " + id + " is not installed!");
    }

    return  result;
  }

  protected DataSinkDescription getSink(String id) throws URISyntaxException, ElementNotFoundException {
    DataSinkDescription result = getStorage()
            .getSECByAppId(id);

    if (result == null) {
      throw new ElementNotFoundException("Data stream " + id + " is not installed!");
    }

    return  result;
  }

  protected IPipelineElementDescriptionStorage getStorage() {
    return StorageDispatcher
            .INSTANCE
            .getTripleStore()
            .getStorageAPI();
  }
}
