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

import org.apache.streampipes.commons.exceptions.ElementNotFoundException;
import org.apache.streampipes.manager.matching.v2.ElementVerification;
import org.apache.streampipes.manager.template.instances.DataLakePipelineTemplate;
import org.apache.streampipes.manager.template.instances.PipelineTemplate;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.management.StorageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PipelineTemplateGenerator {


  Logger logger = LoggerFactory.getLogger(PipelineTemplateGenerator.class);

  private List<PipelineTemplateDescription> availableDescriptions = new ArrayList<>();

  public List<PipelineTemplateDescription> getAllPipelineTemplates() {

    List<PipelineTemplate> allPipelineTemplates = new ArrayList<>();

    allPipelineTemplates.add(new DataLakePipelineTemplate());


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
      streamOffer = new SpDataStream(streamOffer);
      if (streamOffer != null) {
        for (PipelineTemplateDescription pipelineTemplateDescription : getAllPipelineTemplates()) {
          // TODO make this work for 2+ input streams
          InvocableStreamPipesEntity entity =
              cloneInvocation(pipelineTemplateDescription.getBoundTo().get(0).getPipelineElementTemplate());
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

  protected SpDataStream getStream(String streamId) throws ElementNotFoundException {
    SpDataStream result = getStorage()
        .getEventStreamById(streamId);

    if (result == null) {
      throw new ElementNotFoundException("Data stream " + streamId + " is not installed!");
    }

    return result;
  }

  protected DataProcessorDescription getProcessor(String id) throws ElementNotFoundException {
    DataProcessorDescription result = getStorage()
        .getDataProcessorByAppId(id);

    if (result == null) {
      throw new ElementNotFoundException("Data processor " + id + " is not installed!");
    }

    return result;
  }

  protected DataSinkDescription getSink(String id) throws ElementNotFoundException {
    try {
      return getStorage()
          .getDataSinkByAppId(id);
    } catch (IllegalArgumentException e) {
      throw new ElementNotFoundException("Data stream " + id + " is not installed!");
    }
  }

  protected IPipelineElementDescriptionStorage getStorage() {
    return StorageManager
        .INSTANCE
        .getPipelineElementStorage();
  }
}
