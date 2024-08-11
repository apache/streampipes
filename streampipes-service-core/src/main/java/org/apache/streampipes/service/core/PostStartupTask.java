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
import org.apache.streampipes.connect.management.management.WorkerAdministrationManagement;
import org.apache.streampipes.manager.execution.PipelineExecutor;
import org.apache.streampipes.manager.health.ServiceHealthCheck;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;
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

  public PostStartupTask(IPipelineStorage pipelineStorage) {
    this.pipelineStorage = pipelineStorage;
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    this.workerAdministrationManagement = new WorkerAdministrationManagement(
        StorageDispatcher.INSTANCE.getNoSqlStore()
                                  .getAdapterInstanceStorage(),
        AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
        new SpResourceManager().manageAdapters(),
        new SpResourceManager().manageDataStreams()
    );
  }

  @Override
  public void run() {
    new ServiceHealthCheck().run();
    performAdapterAssetUpdate();
    startAllPreviouslyStoppedPipelines();
    startAdapters();
  }

  private void performAdapterAssetUpdate() {
    var installedAppIds = CouchDbStorageManager.INSTANCE.getExtensionsServiceStorage()
                                                        .findAll()
                                                        .stream()
                                                        .flatMap(config -> config.getTags()
                                                                                 .stream())
                                                        .filter(tag -> tag.getPrefix() == SpServiceTagPrefix.ADAPTER)
                                                        .toList();
    workerAdministrationManagement.performAdapterMigrations(installedAppIds);
  }

  private void startAdapters() {
    workerAdministrationManagement.checkAndRestore(0);
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

    LOG.info("Checking for gracefully shut down pipelines to be restarted...");

    List<Pipeline> pipelinesToRestart = allPipelines
        .stream()
        .filter(p -> !(p.isRunning()))
        .filter(Pipeline::isRestartOnSystemReboot)
        .toList();

    LOG.info("Found {} pipelines that we are attempting to restart...", pipelinesToRestart.size());

    pipelinesToRestart.forEach(pipeline -> {
      startPipeline(pipeline, false);
    });

    LOG.info("No more pipelines to restore...");
  }

  private void startPipeline(Pipeline pipeline, boolean restartOnReboot) {
    PipelineOperationStatus status = new PipelineExecutor(pipeline).startPipeline();
    if (status.isSuccess()) {
      LOG.info("Pipeline {} successfully restarted", status.getPipelineName());
      Pipeline storedPipeline = getPipelineStorage().getElementById(pipeline.getPipelineId());
      storedPipeline.setRestartOnSystemReboot(restartOnReboot);
      getPipelineStorage().updateElement(storedPipeline);
    } else {
      storeFailedRestartAttempt(pipeline);
      int failedAttemptCount = failedPipelines.get(pipeline.getPipelineId());
      if (failedAttemptCount <= MAX_PIPELINE_START_RETRIES) {
        LOG.error(
            "Pipeline {} could not be restarted - I'll try again in {} seconds ({}/{} failed attempts)",
            pipeline.getName(),
            WAIT_TIME_AFTER_FAILURE_IN_SECONDS,
            failedAttemptCount,
            MAX_PIPELINE_START_RETRIES
        );

        schedulePipelineStart(pipeline, restartOnReboot);
      } else {
        LOG.error(
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
    return StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getPipelineStorageAPI();
  }
}
