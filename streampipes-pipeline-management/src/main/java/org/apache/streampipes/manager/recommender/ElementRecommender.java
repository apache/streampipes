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

package org.apache.streampipes.manager.recommender;

import org.apache.streampipes.commons.exceptions.NoSuitableSepasAvailableException;
import org.apache.streampipes.manager.matching.PipelineVerificationHandlerV2;
import org.apache.streampipes.manager.matching.v2.StreamMatch;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendation;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendationMessage;
import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ElementRecommender {

  private static final Logger LOG = LoggerFactory.getLogger(ElementRecommender.class);

  private final Pipeline pipeline;
  private final String baseRecDomId;
  private final PipelineElementRecommendationMessage recommendationMessage;

  public ElementRecommender(Pipeline partialPipeline,
                            String baseRecDomId) {
    this.pipeline = partialPipeline;
    this.baseRecDomId = baseRecDomId;
    this.recommendationMessage = new PipelineElementRecommendationMessage();
  }

  public PipelineElementRecommendationMessage findRecommendedElements() throws NoSuitableSepasAvailableException {
    AllElementsProvider elementsProvider = new AllElementsProvider(this.pipeline);
    try {
      Optional<SpDataStream> outputStream = getOutputStream(elementsProvider);
      outputStream.ifPresent(spDataStream -> validate(spDataStream, getAll()));
    } catch (Exception e) {
      LOG.warn("Could not find root node or output stream of provided pipeline");
    }
    return recommendationMessage;
  }

  private void validate(SpDataStream offer, List<ConsumableStreamPipesEntity> entities) {
    for (ConsumableStreamPipesEntity sepa : entities) {
      SpDataStream requirement = sepa.getSpDataStreams().get(0);
      requirement.setEventGrounding(sepa.getSupportedGrounding());
      boolean matches = new StreamMatch().match(offer, requirement, new ArrayList<>());
      if (matches) {
        addPossibleElements(sepa);
      }
    }
  }

  private void addPossibleElements(NamedStreamPipesEntity sepa) {
    recommendationMessage.addPossibleElement(
        new PipelineElementRecommendation(sepa.getElementId(), sepa.getName(), sepa.getDescription()));
  }

  private List<ConsumableStreamPipesEntity> getAllDataProcessors() {
    List<String> userObjects = new SpResourceManager().manageDataProcessors().findAllIdsOnly();
    return getNoSqlStore()
        .getAllDataProcessors()
        .stream()
        .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getAppId())))
        .map(DataProcessorDescription::new)
        .collect(Collectors.toList());
  }


  private List<ConsumableStreamPipesEntity> getAllDataSinks() {
    List<String> userObjects = new SpResourceManager().manageDataSinks().findAllIdsOnly();
    return getNoSqlStore()
        .getAllDataSinks()
        .stream()
        .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getAppId())))
        .map(DataSinkDescription::new)
        .collect(Collectors.toList());
  }

  private List<ConsumableStreamPipesEntity> getAll() {
    List<ConsumableStreamPipesEntity> allElements = new ArrayList<>();
    allElements.addAll(getAllDataProcessors());
    allElements.addAll(getAllDataSinks());
    return allElements;
  }

  private IPipelineElementDescriptionStorage getNoSqlStore() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineElementDescriptionStorage();
  }

  private Optional<SpDataStream> getOutputStream(AllElementsProvider elementsProvider) {

    NamedStreamPipesEntity entity = elementsProvider.findElement(this.baseRecDomId);

    if (entity instanceof SpDataStream) {
      return Optional.of((SpDataStream) entity);
    } else {
      Pipeline partialPipeline =
          new PartialPipelineGenerator(this.baseRecDomId, elementsProvider).makePartialPipeline();
      PipelineModificationMessage modifications = new PipelineVerificationHandlerV2(partialPipeline).verifyPipeline();
      return modifications.getPipelineModifications()
          .stream()
          .filter(m -> m.getDomId().equals(this.baseRecDomId))
          .map(PipelineModification::getOutputStream)
          .findFirst();
    }
  }
}
