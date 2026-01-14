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

package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.health.monitoring.model.ActiveCoreInstances;
import org.apache.streampipes.health.monitoring.model.ActiveResources;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ResourceProvider(IPipelineStorage pipelineStorage,
                               IAdapterStorage adapterInstanceStorage,
                               AdapterMasterManagement adapterMasterManagement) {

  public ActiveResources loadActiveResources() {
    var allPipelines = pipelineStorage.findAll();
    var runningPipelines = allPipelines.stream().filter(Pipeline::isRunning).toList();
    var allAdapters = adapterInstanceStorage.findAll();
    var runningAdapters = allAdapters.stream().filter(AdapterDescription::isRunning).toList();

    return new ActiveResources(
        allPipelines,
        runningPipelines,
        allAdapters,
        runningAdapters
    );
  }

  public Map<String, ActiveCoreInstances> loadActiveInstances(ActiveResources activeResources) {
    Map<String, List<AdapterDescription>> adaptersByUrl =
        activeResources.runningAdapters()
            .stream()
            .filter(a -> a.getSelectedEndpointUrl() != null)
            .collect(Collectors.groupingBy(AdapterDescription::getSelectedEndpointUrl));

    Map<String, Map<String, InvocableStreamPipesEntity>> elementsByUrl =
        activeResources.runningPipelines()
            .stream()
            .flatMap(p -> {
              String pipelineId = p.getPipelineId();
              return Optional.of(
                      Stream.concat(
                          p.getSepas().stream(),
                          p.getActions().stream()
                      ).toList()
                  )
                  .orElseGet(List::of)
                  .stream()
                  .filter(Objects::nonNull)
                  .map(e -> new AbstractMap.SimpleEntry<>(pipelineId, e));
            })
            .filter(entry -> entry.getValue().getSelectedEndpointUrl() != null)
            .collect(Collectors.groupingBy(
                entry -> entry.getValue().getSelectedEndpointUrl(),
                Collectors.toMap(
                    Map.Entry::getKey,
                    AbstractMap.SimpleEntry::getValue,
                    (left, right) -> left)
            ));

    Set<String> allUrls = new HashSet<>();
    allUrls.addAll(adaptersByUrl.keySet());
    allUrls.addAll(elementsByUrl.keySet());

    Map<String, ActiveCoreInstances> result = new HashMap<>();
    for (String url : allUrls) {
      List<AdapterDescription> adapters = adaptersByUrl.getOrDefault(url, List.of());
      Map<String, InvocableStreamPipesEntity> elementsPerPipeline = elementsByUrl.getOrDefault(url, Map.of());
      result.put(url, new ActiveCoreInstances(adapters, elementsPerPipeline));
    }

    return result;
  }
}
