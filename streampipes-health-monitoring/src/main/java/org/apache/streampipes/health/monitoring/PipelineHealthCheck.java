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

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.prometheus.pipelines.PipelinesStats;
import org.apache.streampipes.health.monitoring.model.HealthCheckData;
import org.apache.streampipes.health.monitoring.utils.HealthCheckUtils;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.execution.http.InvokeExtensionRequest;
import org.apache.streampipes.manager.util.PipelineElementUtils;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.secret.SecretDecrypter;
import org.apache.streampipes.resource.management.secret.SecretEncrypter;
import org.apache.streampipes.resource.management.secret.SecretService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class PipelineHealthCheck {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineHealthCheck.class);
  private static final int MAX_FAILED_ATTEMPTS = 10;

  private static final Map<String, Integer> failedRestartAttempts = new HashMap<>();
  private static final PipelinesStats pipelinesStats = new PipelinesStats();

  private final HealthCheckData healthCheckData;
  private final ExtensionServiceRequestManager requestManager;
  private final ResourceProvider resourceProvider;
  private final SpResourceManager resourceManager;

  public PipelineHealthCheck(HealthCheckData healthCheckData,
                             ExtensionServiceRequestManager requestManager,
                             ResourceProvider resourceProvider,
                             SpResourceManager resourceManager) {
    this.healthCheckData = healthCheckData;
    this.requestManager = requestManager;
    this.resourceProvider = resourceProvider;
    this.resourceManager = resourceManager;
  }

  public void runCheck() {
    try {
      initPipelineMetrics();

      if (!healthCheckData.activeResources().runningPipelines().isEmpty()) {
        checkAndRestorePipelineElements();
      }
      pipelinesStats.metrics();
    } catch (Exception e) {
      LOG.error("Error while checking and restoring pipeline elements", e);
    }
  }

  private void initPipelineMetrics() {
    pipelinesStats.clear();
    pipelinesStats.setAllPipelines(healthCheckData.activeResources().allPipelines().size());
    pipelinesStats.setRunningPipelines(healthCheckData.activeResources().runningPipelines().size());
    pipelinesStats.setStoppedPipelines(pipelinesStats.getAllPipelines()
        - pipelinesStats.getRunningPipelines());

    for (Pipeline p : healthCheckData.activeResources().allPipelines()) {
      pipelinesStats.updatePipelineHealthState(
          p.getElementId(),
          p.getName(),
          Objects.nonNull(p.getHealthStatus())
              ? p.getHealthStatus().toString()
              : PipelineHealthStatus.REQUIRES_ATTENTION.toString());

      pipelinesStats.updatePipelineRunningState(
          p.getElementId(),
          p.getName(),
          p.isRunning());
    }
  }

  private void checkAndRestorePipelineElements() {
    healthCheckData.activeResources().runningPipelines().forEach(pipeline -> {
      AtomicBoolean shouldUpdatePipeline = new AtomicBoolean(false);
      List<String> failedInstances = new ArrayList<>();
      List<String> recoveredInstances = new ArrayList<>();
      List<String> pipelineNotifications = new ArrayList<>();
      List<InvocableStreamPipesEntity> runningPipelineElements = Stream.concat(
          pipeline.getSepas().stream(),
          pipeline.getActions().stream()
      ).toList();

      runningPipelineElements.forEach(pipelineElement -> {
        String instanceId = HealthCheckUtils.extractInstanceId(pipelineElement);
        if (isNowhereRunning(instanceId)) {
          if (shouldRetry(instanceId)) {
            shouldUpdatePipeline.set(true);
            boolean success;
            try {
              var service = new ExtensionsServiceEndpointGenerator().selectService(
                  pipelineElement.getAppId(),
                  ExtensionsServiceEndpointUtils.getPipelineElementType(pipelineElement.getAppId()),
                  Collections.emptySet()
              );
              new SecretService(new SecretDecrypter()).apply(pipelineElement);
              pipelineElement.setSelectedEndpointUrl(service.getServiceUrl());
              pipelineElement.setSelectedServiceId(service.getSvcId());
              success = new InvokeExtensionRequest(requestManager, resourceManager)
                  .execute(pipelineElement, pipeline.getPipelineId()).isSuccess();
              new SecretService(new SecretEncrypter()).apply(pipelineElement);
            } catch (NoServiceEndpointsAvailableException e) {
              success = false;
            }
            if (!success) {
              failedInstances.add(instanceId);
              HealthCheckUtils.addFailedAttemptNotification(pipelineNotifications, pipelineElement);
              increaseFailedAttempt(instanceId);
              LOG.info("Could not restore pipeline element {} of pipeline {} ({}/{})",
                  pipelineElement.getName(), pipeline.getName(), failedRestartAttempts.get(instanceId),
                  MAX_FAILED_ATTEMPTS);
            } else {
              recoveredInstances.add(instanceId);
              HealthCheckUtils.addSuccessfulRestoreNotification(pipelineNotifications, pipelineElement);
              resetFailedAttempts(instanceId);
              LOG.info("Successfully restored pipeline element {} of pipeline {}",
                  pipelineElement.getName(), pipeline.getName());
            }
          }
        }
      });
      if (shouldUpdatePipeline.get()) {
        var currentPipeline = resourceProvider.pipelineStorage().getElementById(pipeline.getPipelineId());
        if (!failedInstances.isEmpty()) {
          currentPipeline.setHealthStatus(PipelineHealthStatus.FAILURE);
          pipelinesStats.failedIncrease();
        } else if (!recoveredInstances.isEmpty()) {
          currentPipeline.setHealthStatus(PipelineHealthStatus.REQUIRES_ATTENTION);
          pipelinesStats.attentionRequiredIncrease();
        }
        currentPipeline.setSepas(
            PipelineElementUtils.filterInvocation(runningPipelineElements, DataProcessorInvocation.class)
        );
        currentPipeline.setActions(
            PipelineElementUtils.filterInvocation(runningPipelineElements, DataSinkInvocation.class)
        );
        currentPipeline.setPipelineNotifications(pipelineNotifications);
        healthCheckData.resourceProvider().pipelineStorage().updateElement(currentPipeline);
        pipelinesStats.updatePipelineHealthState(currentPipeline.getElementId(), currentPipeline.getName(),
            currentPipeline.getHealthStatus().toString());
      }
    });
    int healthNum = pipelinesStats.getRunningPipelines() - pipelinesStats.getFailedPipelines()
        - pipelinesStats.getAttentionRequiredPipelines();
    pipelinesStats.setHealthyPipelines(healthNum);
    pipelinesStats.setElementCount(getElementsCount(healthCheckData.activeResources().allPipelines()));
  }

  private boolean isNowhereRunning(String instanceId) {
    return (healthCheckData.activeExtensionInstances().entrySet().stream()
        .noneMatch(entry -> entry.getValue().runningPipelineElementInstanceIds().contains(instanceId)));
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

  private int getElementsCount(List<Pipeline> allPipelines) {
    return allPipelines.stream().mapToInt(pipeline -> pipeline.getActions().size()).sum();
  }

  private String getInvocationUrl(InvocableStreamPipesEntity pipelineElement,
                                  String baseUrl) {
    return ExtensionsServiceEndpointUtils
        .getPipelineElementType(pipelineElement)
        .getInvocationUrl(baseUrl, pipelineElement.getAppId());
  }
}
