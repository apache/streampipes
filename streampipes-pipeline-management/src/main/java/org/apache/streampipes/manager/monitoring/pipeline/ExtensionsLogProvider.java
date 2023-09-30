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

package org.apache.streampipes.manager.monitoring.pipeline;

import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.monitoring.SpEndpointMonitoringInfo;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.monitoring.SpMetricsEntry;
import org.apache.streampipes.model.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ExtensionsLogProvider {

  INSTANCE;

  private static final int MAX_ITEMS = 50;

  private final Map<String, List<SpLogEntry>> allLogInfos = new HashMap<>();
  private final Map<String, SpMetricsEntry> allMetricsInfos = new HashMap<>();

  public void addMonitoringInfos(SpEndpointMonitoringInfo monitoringInfo) {
    allMetricsInfos.putAll(monitoringInfo.getMetricsInfos());
    monitoringInfo.getLogInfos().forEach((key, value) -> {
      if (!allLogInfos.containsKey(key)) {
        allLogInfos.put(key, new ArrayList<>());
      }

      var infos = allLogInfos.get(key);
      infos.addAll(0, value);

      if (infos.size() > MAX_ITEMS) {
        int numElementsToRemove = infos.size() - MAX_ITEMS;

        for (int i = 0; i < numElementsToRemove; i++) {
          infos.remove(infos.size() - 1);
        }
      }
    });
  }

  private <T> Map<String, T> getInfosForPipeline(Map<String, T> allElements,
                                                 Pipeline pipeline) {
    List<String> pipelineElementIds = collectPipelineElementIds(pipeline);

    return allElements.entrySet()
        .stream()
        .filter(x -> pipelineElementIds.contains(x.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Map<String, List<SpLogEntry>> getLogInfosForPipeline(Pipeline pipeline) {
    return getInfosForPipeline(allLogInfos, pipeline);
  }

  public Map<String, List<SpLogEntry>> getLogInfosForPipeline(String pipelineId) {
    var pipeline = PipelineManager.getPipeline(pipelineId);

    return getLogInfosForPipeline(pipeline);
  }

  public List<SpLogEntry> getLogInfosForResource(String resourceId) {
    return allLogInfos.getOrDefault(resourceId, Collections.emptyList());
  }

  public SpMetricsEntry getMetricInfosForResource(String resourceId) {
    if (allMetricsInfos.containsKey(resourceId)) {
      return allMetricsInfos.get(resourceId);
    } else {
      return new SpMetricsEntry();
    }
  }

  public Map<String, SpMetricsEntry> getMetricInfosForPipeline(String pipelineId) {
    var pipeline = PipelineManager.getPipeline(pipelineId);

    return getInfosForPipeline(allMetricsInfos, pipeline);
  }

  public void reset(String resourceId) {
    if (allMetricsInfos.containsKey(resourceId)) {
      allMetricsInfos.get(resourceId).reset();
    }
    if (allLogInfos.containsKey(resourceId)) {
      allLogInfos.get(resourceId).clear();
    }
  }

  public void remove(String resourceId) {
    this.allMetricsInfos.remove(resourceId);
    this.allLogInfos.remove(resourceId);
  }

  public Map<String, SpMetricsEntry> getAllMetricsInfos(){
    return this.allMetricsInfos;
  }

  private List<String> collectPipelineElementIds(Pipeline pipeline) {
    return Stream.concat(
        pipeline.getSepas().stream().map(NamedStreamPipesEntity::getElementId),
        pipeline.getActions().stream().map(NamedStreamPipesEntity::getElementId)
    ).collect(Collectors.toList());
  }

}
