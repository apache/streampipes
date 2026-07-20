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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PipelineHealthCheck implements HealthCheck {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineHealthCheck.class);
  private static final PipelinesStats pipelinesStats = new PipelinesStats();

  private final HealthCheckData healthCheckData;
  private final ExtensionServiceRequestManager requestManager;
  private final ResourceProvider resourceProvider;
  private final SpResourceManager resourceManager;
  private final PipelineRecoveryBackoff recoveryBackoff;

  public PipelineHealthCheck(HealthCheckData healthCheckData,
                             ExtensionServiceRequestManager requestManager,
                             ResourceProvider resourceProvider,
                             SpResourceManager resourceManager) {
    this(healthCheckData, requestManager, resourceProvider, resourceManager, new PipelineRecoveryBackoff());
  }

  PipelineHealthCheck(HealthCheckData healthCheckData,
                      ExtensionServiceRequestManager requestManager,
                      ResourceProvider resourceProvider,
                      SpResourceManager resourceManager,
                      PipelineRecoveryBackoff recoveryBackoff) {
    this.healthCheckData = healthCheckData;
    this.requestManager = requestManager;
    this.resourceProvider = resourceProvider;
    this.resourceManager = resourceManager;
    this.recoveryBackoff = recoveryBackoff;
  }

  @Override
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
    recoveryBackoff.retainOnly(getActiveRecoveryKeys());
    healthCheckData.activeResources().runningPipelines().forEach(pipeline -> {
      List<String> missingInstances = new ArrayList<>();
      List<String> recoveredInstances = new ArrayList<>();
      List<String> pipelineNotifications = new ArrayList<>();
      List<InvocableStreamPipesEntity> runningPipelineElements = Stream.concat(
          pipeline.getSepas().stream(),
          pipeline.getActions().stream()
      ).toList();

      runningPipelineElements.forEach(pipelineElement -> {
        String instanceId = HealthCheckUtils.extractInstanceId(pipelineElement);
        if (isNowhereRunning(instanceId)) {
          missingInstances.add(instanceId);
          if (recoveryBackoff.isAttemptDue(pipeline.getPipelineId(), instanceId)) {
            boolean success = restorePipelineElement(pipelineElement, pipeline.getPipelineId());
            if (!success) {
              var state = recoveryBackoff.recordFailure(pipeline.getPipelineId(), instanceId);
              HealthCheckUtils.addFailedAttemptNotification(pipelineNotifications, pipelineElement);
              logFailedRecovery(pipeline, pipelineElement, state);
            } else {
              missingInstances.remove(instanceId);
              recoveredInstances.add(instanceId);
              int previousFailures = recoveryBackoff.reset(pipeline.getPipelineId(), instanceId);
              logSuccessfulRecovery(pipeline, pipelineElement, previousFailures);
            }
          }
        } else {
          int previousFailures = recoveryBackoff.reset(pipeline.getPipelineId(), instanceId);
          if (previousFailures > 0) {
            recoveredInstances.add(instanceId);
            LOG.info("Pipeline element {} of pipeline {} is running again after {} failed recovery attempts",
                pipelineElement.getName(), pipeline.getName(), previousFailures);
          }
        }
      });
      boolean recoveredBeforeThisCheck = missingInstances.isEmpty()
          && pipeline.getHealthStatus() == PipelineHealthStatus.FAILURE;
      if (!missingInstances.isEmpty() || !recoveredInstances.isEmpty() || recoveredBeforeThisCheck) {
        var currentPipeline = resourceProvider.pipelineStorage().getElementById(pipeline.getPipelineId());
        if (!missingInstances.isEmpty()) {
          currentPipeline.setHealthStatus(PipelineHealthStatus.FAILURE);
          pipelinesStats.failedIncrease();
        } else {
          currentPipeline.setHealthStatus(PipelineHealthStatus.OK);
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

  private Set<PipelineRecoveryBackoff.RecoveryKey> getActiveRecoveryKeys() {
    return healthCheckData.activeResources().runningPipelines().stream()
        .flatMap(pipeline -> Stream.concat(pipeline.getSepas().stream(), pipeline.getActions().stream())
            .map(pipelineElement -> new PipelineRecoveryBackoff.RecoveryKey(
                pipeline.getPipelineId(),
                HealthCheckUtils.extractInstanceId(pipelineElement)
            )))
        .collect(Collectors.toSet());
  }

  private void logFailedRecovery(Pipeline pipeline,
                                 InvocableStreamPipesEntity pipelineElement,
                                 PipelineRecoveryBackoff.RecoveryState state) {
    var logMessage = "Could not restore pipeline element {} of pipeline {} on attempt {}; "
        + "next attempt in {} seconds";
    var delaySeconds = state.delay().toSeconds();
    if (state.failedAttempts() == 1) {
      LOG.warn(logMessage, pipelineElement.getName(), pipeline.getName(), state.failedAttempts(), delaySeconds);
    } else if (isPowerOfTwo(state.failedAttempts())) {
      LOG.info(logMessage, pipelineElement.getName(), pipeline.getName(), state.failedAttempts(), delaySeconds);
    } else {
      LOG.debug(logMessage, pipelineElement.getName(), pipeline.getName(), state.failedAttempts(), delaySeconds);
    }
  }

  private void logSuccessfulRecovery(Pipeline pipeline,
                                     InvocableStreamPipesEntity pipelineElement,
                                     int previousFailures) {
    if (previousFailures == 0) {
      LOG.info("Successfully restored pipeline element {} of pipeline {}",
          pipelineElement.getName(), pipeline.getName());
    } else {
      LOG.info("Successfully restored pipeline element {} of pipeline {} after {} failed attempts",
          pipelineElement.getName(), pipeline.getName(), previousFailures);
    }
  }

  private boolean isPowerOfTwo(int value) {
    return (value & (value - 1)) == 0;
  }

  protected boolean restorePipelineElement(InvocableStreamPipesEntity pipelineElement,
                                           String pipelineId) {
    try {
      var service = new ExtensionsServiceEndpointGenerator().selectService(
          pipelineElement.getAppId(),
          ExtensionsServiceEndpointUtils.getPipelineElementType(pipelineElement.getAppId()),
          Collections.emptySet()
      );
      new SecretService(new SecretDecrypter()).apply(pipelineElement);
      pipelineElement.setSelectedEndpointUrl(service.getServiceUrl());
      pipelineElement.setSelectedServiceId(service.getSvcId());
      boolean success = new InvokeExtensionRequest(requestManager, resourceManager)
          .execute(pipelineElement, pipelineId).isSuccess();
      new SecretService(new SecretEncrypter()).apply(pipelineElement);
      return success;
    } catch (NoServiceEndpointsAvailableException e) {
      return false;
    }
  }

  private boolean isNowhereRunning(String instanceId) {
    return (healthCheckData.activeExtensionInstances().entrySet().stream()
        .noneMatch(entry -> entry.getValue().runningPipelineElementInstanceIds().contains(instanceId)));
  }

  private int getElementsCount(List<Pipeline> allPipelines) {
    return allPipelines.stream().mapToInt(pipeline -> pipeline.getActions().size()).sum();
  }
}
