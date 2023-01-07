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
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.message.PipelineModificationMessage;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendation;
import org.apache.streampipes.model.pipeline.PipelineElementRecommendationMessage;
import org.apache.streampipes.model.pipeline.PipelineModification;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.storage.management.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ElementRecommender {

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

    String rootNodeId;
    try {
      rootNodeId = getRootNodeId(elementsProvider);
      Optional<SpDataStream> outputStream = getOutputStream(elementsProvider);
      outputStream.ifPresent(spDataStream -> validate(spDataStream, getAll()));
    } catch (Exception e) {
      e.printStackTrace();
      return recommendationMessage;
    }

    if (recommendationMessage.getPossibleElements().size() == 0) {
      throw new NoSuitableSepasAvailableException();
    } else {
      recommendationMessage
          .setRecommendedElements(calculateWeights(
              filterOldElements(getNoSqlStorage()
                  .getConnectionStorageApi()
                  .getRecommendedElements(rootNodeId))));
      return recommendationMessage;
    }
  }

  private String getRootNodeId(AllElementsProvider elementsProvider) {
    NamedStreamPipesEntity pe = elementsProvider.findElement(this.baseRecDomId);
    return pe instanceof InvocableStreamPipesEntity ? ((InvocableStreamPipesEntity) pe).getBelongsTo() :
        pe.getElementId();
  }

  private List<PipelineElementRecommendation> filterOldElements(
      List<PipelineElementRecommendation> recommendedElements) {
    return recommendedElements
        .stream()
        .filter(r -> getAll()
            .stream()
            .anyMatch(a -> a.getElementId().equals(r.getElementId())))
        .collect(Collectors.toList());
  }

  private List<PipelineElementRecommendation> calculateWeights(
      List<PipelineElementRecommendation> recommendedElements) {
    int allConnectionsCount = recommendedElements
        .stream()
        .mapToInt(PipelineElementRecommendation::getCount)
        .sum();

    recommendedElements
        .forEach(r -> {
          r.setWeight(getWeight(r.getCount(), allConnectionsCount));
          r.setName(getName(r.getElementId()));
          r.setDescription(getDescription(r.getElementId()));
        });

    return recommendedElements;
  }

  private String getName(String elementId) {
    return filter(elementId).getName();
  }

  private String getDescription(String elementId) {
    return filter(elementId).getDescription();
  }

  private NamedStreamPipesEntity filter(String elementId) {
    List<ConsumableStreamPipesEntity> allElements = getAll();
    return allElements
        .stream()
        .filter(a -> a.getElementId().equals(elementId))
        .findFirst()
        .get();
  }

  private Float getWeight(Integer count, Integer allConnectionsCount) {
    return ((float) (count)) / allConnectionsCount;
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
    return getTripleStore()
        .getAllDataProcessors()
        .stream()
        .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getElementId())))
        .map(DataProcessorDescription::new)
        .collect(Collectors.toList());
  }


  private List<ConsumableStreamPipesEntity> getAllDataSinks() {
    List<String> userObjects = new SpResourceManager().manageDataSinks().findAllIdsOnly();
    return getTripleStore()
        .getAllDataSinks()
        .stream()
        .filter(e -> userObjects.stream().anyMatch(u -> u.equals(e.getElementId())))
        .map(DataSinkDescription::new)
        .collect(Collectors.toList());
  }

  private List<ConsumableStreamPipesEntity> getAll() {
    List<ConsumableStreamPipesEntity> allElements = new ArrayList<>();
    allElements.addAll(getAllDataProcessors());
    allElements.addAll(getAllDataSinks());
    return allElements;
  }

  private IPipelineElementDescriptionStorage getTripleStore() {
    return StorageManager.INSTANCE.getPipelineElementStorage();
  }

  private INoSqlStorage getNoSqlStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore();
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
