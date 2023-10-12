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
package org.apache.streampipes.manager.health;


import org.apache.streampipes.commons.constants.InstanceIdExtractor;
import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.prometheus.pipelines.PipelinesStats;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.execution.http.InvokeHttpRequest;
import org.apache.streampipes.manager.storage.RunningPipelineElementStorage;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PipelineHealthCheck implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineHealthCheck.class);
  private static final int MAX_FAILED_ATTEMPTS = 10;

  private static final Map<String, Integer> failedRestartAttempts = new HashMap<>();

  private static final PipelinesStats pipelinesStats = new PipelinesStats();

  public PipelineHealthCheck() {

  }

  public void checkAndRestorePipelineElements() {

    List<Pipeline> allPipelines = getAllPipelines();
    List<Pipeline> runningPipelines = getRunningPipelines(allPipelines);

    pipelinesStats.clear();
    pipelinesStats.setAllPipelines(allPipelines.size());
    pipelinesStats.setRunningPipelines(runningPipelines.size());
    pipelinesStats.setStoppedPipelines(pipelinesStats.getAllPipelines() - pipelinesStats.getRunningPipelines());

    if (runningPipelines.size() > 0) {
      Map<String, List<InvocableStreamPipesEntity>> endpointMap = generateEndpointMap();
      List<String> allRunningInstances = findRunningInstances(endpointMap.keySet());

      runningPipelines.forEach(pipeline -> {
        AtomicBoolean shouldUpdatePipeline = new AtomicBoolean(false);
        List<String> failedInstances = new ArrayList<>();
        List<String> recoveredInstances = new ArrayList<>();
        List<String> pipelineNotifications = new ArrayList<>();
        List<InvocableStreamPipesEntity> graphs = RunningPipelineElementStorage
            .runningProcessorsAndSinks
            .get(pipeline.getPipelineId());

        graphs.forEach(graph -> {
          String instanceId = extractInstanceId(graph);
          if (allRunningInstances.stream().noneMatch(runningInstanceId -> runningInstanceId.equals(instanceId))) {
            if (shouldRetry(instanceId)) {
              String endpointUrl = graph.getSelectedEndpointUrl();
              shouldUpdatePipeline.set(true);
              boolean success;
              try {
                endpointUrl = findEndpointUrl(graph);
                success = new InvokeHttpRequest().execute(graph, endpointUrl, pipeline.getPipelineId()).isSuccess();
              } catch (NoServiceEndpointsAvailableException e) {
                success = false;
              }
              if (!success) {
                failedInstances.add(instanceId);
                addFailedAttemptNotification(pipelineNotifications, graph);
                increaseFailedAttempt(instanceId);
                LOG.info("Could not restore pipeline element {} of pipeline {} ({}/{})",
                    graph.getName(),
                    pipeline.getName(),
                    failedRestartAttempts.get(instanceId),
                    MAX_FAILED_ATTEMPTS);
              } else {
                recoveredInstances.add(instanceId);
                addSuccessfulRestoreNotification(pipelineNotifications, graph);
                resetFailedAttempts(instanceId);
                graph.setSelectedEndpointUrl(endpointUrl);
                LOG.info("Successfully restored pipeline element {} of pipeline {}", graph.getName(),
                    pipeline.getName());
              }
            }
          }
        });
        if (shouldUpdatePipeline.get()) {
          if (failedInstances.size() > 0) {
            pipeline.setHealthStatus(PipelineHealthStatus.FAILURE);
            pipelinesStats.failedIncrease();
          } else if (recoveredInstances.size() > 0) {
            pipeline.setHealthStatus(PipelineHealthStatus.REQUIRES_ATTENTION);
            pipelinesStats.attentionRequiredIncrease();
          }
          pipeline.setPipelineNotifications(pipelineNotifications);
          StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updatePipeline(pipeline);
        }
      });
      int healthNum = pipelinesStats.getRunningPipelines() - pipelinesStats.getFailedPipelines()
          - pipelinesStats.getAttentionRequiredPipelines();
      pipelinesStats.setHealthyPipelines(healthNum);
      pipelinesStats.setElementCount(getElementsCount(allPipelines));
      pipelinesStats.metrics();
    }
  }

  private String findEndpointUrl(InvocableStreamPipesEntity graph) throws NoServiceEndpointsAvailableException {
    SpServiceUrlProvider serviceUrlProvider = ExtensionsServiceEndpointUtils.getPipelineElementType(graph);
    return new ExtensionsServiceEndpointGenerator(graph.getAppId(), serviceUrlProvider).getEndpointResourceUrl();
  }

  private boolean shouldRetry(String instanceId) {
    if (!failedRestartAttempts.containsKey(instanceId)) {
      return true;
    } else {
      return failedRestartAttempts.get(instanceId) < MAX_FAILED_ATTEMPTS;
    }
  }

  private void resetFailedAttempts(String instanceId) {
    failedRestartAttempts.put(instanceId, 0);
  }

  private void increaseFailedAttempt(String instanceId) {
    if (!failedRestartAttempts.containsKey(instanceId)) {
      failedRestartAttempts.put(instanceId, 1);
    } else {
      Integer currentAttempt = failedRestartAttempts.get(instanceId) + 1;
      failedRestartAttempts.put(instanceId, currentAttempt);
    }
  }

  private void addSuccessfulRestoreNotification(List<String> pipelineNotifications,
                                                InvocableStreamPipesEntity graph) {
    pipelineNotifications.add(getCurrentDatetime()
        + "Pipeline element '"
        + graph.getName()
        + "' was not available and was successfully restored.");
  }

  private void addFailedAttemptNotification(List<String> pipelineNotifications,
                                            InvocableStreamPipesEntity graph) {
    pipelineNotifications.add(getCurrentDatetime()
        + "Pipeline element '"
        + graph.getName()
        + "' was not available and could not be restored.");
  }

  private String getCurrentDatetime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    return "[" + dtf.format(now) + "] ";
  }

  private String extractInstanceId(InvocableStreamPipesEntity graph) {
    return InstanceIdExtractor.extractId(graph.getElementId());
  }


  private List<String> findRunningInstances(Set<String> endpoints) {
    List<String> allRunningInstances = new ArrayList<>();
    endpoints.forEach(endpoint -> {
      try {
        allRunningInstances.addAll(new PipelineElementEndpointHealthCheck(endpoint).checkRunningInstances());
      } catch (IOException e) {
        LOG.error("Pipeline element endpoint {} is unavailable", endpoint);
      }
    });

    return allRunningInstances;
  }

  private Map<String, List<InvocableStreamPipesEntity>> generateEndpointMap() {
    Map<String, List<InvocableStreamPipesEntity>> endpointMap = new HashMap<>();
    RunningPipelineElementStorage.runningProcessorsAndSinks.forEach((pipelineId, graphs) ->
        graphs.forEach(graph -> addEndpoint(endpointMap, graph)));

    return endpointMap;
  }

  private void addEndpoint(Map<String, List<InvocableStreamPipesEntity>> endpointMap,
                           InvocableStreamPipesEntity graph) {
    String selectedEndpoint = graph.getSelectedEndpointUrl();
    if (!endpointMap.containsKey(selectedEndpoint)) {
      endpointMap.put(selectedEndpoint, new ArrayList<>());
    }
    List<InvocableStreamPipesEntity> existingGraphs = endpointMap.get(selectedEndpoint);
    existingGraphs.add(graph);
    endpointMap.put(selectedEndpoint, existingGraphs);
  }

  @Override
  public void run() {
    this.checkAndRestorePipelineElements();
  }

  private List<Pipeline> getRunningPipelines(List<Pipeline> allPipelines) {
    return allPipelines
        .stream()
        .filter(Pipeline::isRunning)
        .collect(Collectors.toList());

  }

  private List<Pipeline> getAllPipelines() {
    List<Pipeline> allPipelines = StorageDispatcher
            .INSTANCE
            .getNoSqlStore()
            .getPipelineStorageAPI()
            .getAllPipelines();

    return allPipelines;
  }

  private int getElementsCount(List<Pipeline> allPipelines){
    return allPipelines
        .stream()
        .mapToInt(pipeline -> pipeline.getActions().size())
        .sum();

  }
}
