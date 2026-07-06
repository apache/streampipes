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

package org.apache.streampipes.service.core;

import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.WorkerAdministrationManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.health.monitoring.ExtensionHealthCheck;
import org.apache.streampipes.health.monitoring.HealthCheck;
import org.apache.streampipes.health.monitoring.PostStartupRecovery;
import org.apache.streampipes.health.monitoring.ResourceProvider;
import org.apache.streampipes.health.monitoring.ServiceHealthCheck;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.core.INoSqlStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PostStartupTask implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(PostStartupTask.class);

  private static final int MAX_PIPELINE_START_RETRIES = 3;
  private static final int WAIT_TIME_AFTER_FAILURE_IN_SECONDS = 10;

  private final IPipelineStorage pipelineStorage;
  private final Map<String, Integer> failedPipelines = new HashMap<>();
  private final ScheduledExecutorService executorService;
  private final WorkerAdministrationManagement workerAdministrationManagement;
  private final PostStartupRecovery postStartupRecovery;
  private final ExtensionServiceRequestManager extensionServiceRequestManager;
  private final SpResourceManager resourceManager;

  private final INoSqlStorage storage = StorageDispatcher.INSTANCE.getNoSqlStore();

  public PostStartupTask(IPipelineStorage pipelineStorage,
                         ExtensionServiceRequestManager extensionServiceRequestManager,
                         WorkerRestClient workerRestClient,
                         SpResourceManager resourceManager,
                         List<HealthCheck> registeredHealthChecks) {
    this.pipelineStorage = pipelineStorage;
    this.extensionServiceRequestManager = extensionServiceRequestManager;
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    this.resourceManager = resourceManager;
    this.workerAdministrationManagement = new WorkerAdministrationManagement(
        storage.getAdapterDescriptionStorage(),
        resourceManager,
        extensionServiceRequestManager);
    this.postStartupRecovery = new PostStartupRecovery(
        new ExtensionHealthCheck(
            new ResourceProvider(
                resourceManager.managePipelines().getDb(),
                resourceManager.manageAdapters().getDb(),
                new AdapterMasterManagement(
                    resourceManager,
                    AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
                    workerRestClient,
                    StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage(),
                    extensionServiceRequestManager
                )
            ),
            StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage(),
            extensionServiceRequestManager,
            resourceManager,
            registeredHealthChecks
        )
    );
  }

  @Override
  public void run() {
    new ServiceHealthCheck(storage.getExtensionsServiceStorage(), extensionServiceRequestManager, resourceManager).run();
    performAdapterAssetUpdate();
    startAllPreviouslyStoppedPipelines();
    runHealthCheckOnce();
  }

  private void performAdapterAssetUpdate() {
    var installedAppIds = storage.getExtensionsServiceStorage()
        .findAll()
        .stream()
        .flatMap(config -> config.getTags()
            .stream())
        .filter(tag -> tag.getPrefix() == SpServiceTagPrefix.ADAPTER)
        .toList();
    workerAdministrationManagement.performAdapterMigrations(installedAppIds);
  }

  private void runHealthCheckOnce() {
    postStartupRecovery.checkAndRestore(0);
  }

  private void startAllPreviouslyStoppedPipelines() {
    var allPipelines = pipelineStorage.findAll();
    LOG.info("Checking for orphaned pipelines...");
    List<Pipeline> orphanedPipelines = allPipelines
        .stream()
        .filter(Pipeline::isRunning)
        .toList();

    LOG.info("Found {} orphaned pipelines", orphanedPipelines.size());

    orphanedPipelines.forEach(pipeline -> {
      LOG.info("Restoring orphaned pipeline {}", pipeline.getName());
      startPipeline(pipeline, false);
    });

    List<Pipeline> pipelinesToRestart = allPipelines
        .stream()
        .filter(p -> !(p.isRunning()))
        .filter(Pipeline::isRestartOnSystemReboot)
        .toList();

    LOG.info("Found {} pipelines that will be restarted", pipelinesToRestart.size());

    pipelinesToRestart.forEach(pipeline -> {
      startPipeline(pipeline, false);
    });

    LOG.info("No more pipelines to restore...");
  }

  private void startPipeline(Pipeline pipeline, boolean restartOnReboot) {
    PipelineOperationStatus status = new PipelineExecutor(pipeline, extensionServiceRequestManager, resourceManager)
        .startPipeline();
    if (status.isSuccess()) {
      LOG.info("Pipeline {} successfully restarted", status.getPipelineName());
      Pipeline storedPipeline = getPipelineStorage().getElementById(pipeline.getPipelineId());
      storedPipeline.setRestartOnSystemReboot(restartOnReboot);
      getPipelineStorage().updateElement(storedPipeline);
    } else {
      storeFailedRestartAttempt(pipeline);
      int failedAttemptCount = failedPipelines.get(pipeline.getPipelineId());
      if (failedAttemptCount <= MAX_PIPELINE_START_RETRIES) {
        LOG.warn(
            "Pipeline {} could not be restarted - I'll try again in {} seconds ({}/{} failed attempts)",
            pipeline.getName(),
            WAIT_TIME_AFTER_FAILURE_IN_SECONDS,
            failedAttemptCount,
            MAX_PIPELINE_START_RETRIES
        );

        schedulePipelineStart(pipeline, restartOnReboot);
      } else {
        LOG.warn(
            "Pipeline {} could not be restarted - are all pipeline element containers running?",
            status.getPipelineName()
        );
      }
    }
  }

  private void schedulePipelineStart(Pipeline pipeline, boolean restartOnReboot) {
    executorService.schedule(() -> {
      startPipeline(pipeline, restartOnReboot);
    }, WAIT_TIME_AFTER_FAILURE_IN_SECONDS, TimeUnit.SECONDS);
  }

  private void storeFailedRestartAttempt(Pipeline pipeline) {
    String pipelineId = pipeline.getPipelineId();
    if (!failedPipelines.containsKey(pipelineId)) {
      failedPipelines.put(pipelineId, 1);
    } else {
      int failedAttempts = failedPipelines.get(pipelineId) + 1;
      failedPipelines.put(pipelineId, failedAttempts);
    }
  }

  private IPipelineStorage getPipelineStorage() {
    return resourceManager.managePipelines().getDb();
  }
}
