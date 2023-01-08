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

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.manager.health.PipelineHealthCheck;
import org.apache.streampipes.manager.monitoring.pipeline.ExtensionsServiceLogExecutor;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.manager.setup.AutoInstallation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.rest.security.SpPermissionEvaluator;
import org.apache.streampipes.service.base.BaseNetworkingConfig;
import org.apache.streampipes.service.base.StreamPipesServiceBase;
import org.apache.streampipes.service.core.migrations.MigrationsHandler;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.couchdb.utils.CouchDbViewGenerator;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableAutoConfiguration
@Import({StreamPipesResourceConfig.class,
    WelcomePageController.class,
    StreamPipesPasswordEncoder.class,
    WebSecurityConfig.class,
    SpPermissionEvaluator.class
})
@ComponentScan({"org.apache.streampipes.rest.*"})
public class StreamPipesBackendApplication extends StreamPipesServiceBase {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesBackendApplication.class.getCanonicalName());

  private static final int MAX_PIPELINE_START_RETRIES = 3;
  private static final int WAIT_TIME_AFTER_FAILURE_IN_SECONDS = 10;

  private static final int LOG_FETCH_INTERVAL = 60;
  private static final TimeUnit LOG_FETCH_UNIT = TimeUnit.SECONDS;

  private static final int HEALTH_CHECK_INTERVAL = 60;
  private static final TimeUnit HEALTH_CHECK_UNIT = TimeUnit.SECONDS;

  private ScheduledExecutorService executorService;
  private ScheduledExecutorService healthCheckExecutorService;
  private ScheduledExecutorService logCheckExecutorService;

  private Map<String, Integer> failedPipelines = new HashMap<>();

  public static void main(String[] args) {
    StreamPipesBackendApplication application = new StreamPipesBackendApplication();
    try {
      BaseNetworkingConfig networkingConfig = BaseNetworkingConfig.defaultResolution(8030);
      application.startStreamPipesService(StreamPipesBackendApplication.class,
          DefaultSpServiceGroups.CORE,
          application.serviceId(),
          networkingConfig);
    } catch (UnknownHostException e) {
      LOG.error(
          "Could not auto-resolve host address - please manually provide the hostname"
              + " using the `SP_HOST` environment variable");
    }
  }

  private String serviceId() {
    return DefaultSpServiceGroups.CORE + "-" + AUTO_GENERATED_SERVICE_ID;
  }

  @PostConstruct
  public void init() {
    this.executorService = Executors.newSingleThreadScheduledExecutor();
    this.healthCheckExecutorService = Executors.newSingleThreadScheduledExecutor();
    this.logCheckExecutorService = Executors.newSingleThreadScheduledExecutor();

    new StreamPipesEnvChecker().updateEnvironmentVariables();
    new CouchDbViewGenerator().createGenericDatabaseIfNotExists();

    if (!isConfigured()) {
      doInitialSetup();
    }

    new MigrationsHandler().performMigrations();

    executorService.schedule(this::startAllPreviouslyStoppedPipelines, 5, TimeUnit.SECONDS);
    LOG.info("Pipeline health check will run every {} seconds", HEALTH_CHECK_INTERVAL);
    healthCheckExecutorService.scheduleAtFixedRate(new PipelineHealthCheck(),
        HEALTH_CHECK_INTERVAL,
        HEALTH_CHECK_INTERVAL,
        HEALTH_CHECK_UNIT);

    LOG.info("Extensions logs will be fetched every {} seconds", LOG_FETCH_INTERVAL);
    logCheckExecutorService.scheduleAtFixedRate(new ExtensionsServiceLogExecutor(),
        LOG_FETCH_INTERVAL,
        LOG_FETCH_INTERVAL,
        LOG_FETCH_UNIT);


  }

  private boolean isConfigured() {
    return BackendConfig.INSTANCE.isConfigured();
  }

  private void doInitialSetup() {
    LOG.info("\n\n**********\n\nWelcome to Apache StreamPipes!\n\n**********\n\n");
    LOG.info("We will perform the initial setup, grab some coffee and cross your fingers ;-)...");

    BackendConfig.INSTANCE.updateSetupStatus(true);
    LOG.info("Auto-setup will start in 10 seconds to make sure extensions services are running...");
    try {
      TimeUnit.SECONDS.sleep(10);
      LOG.info("Starting installation procedure");
      new AutoInstallation().startAutoInstallation();
      BackendConfig.INSTANCE.updateSetupStatus(false);
    } catch (InterruptedException e) {
      LOG.error("Ooops, something went wrong during the installation", e);
    }
  }

  private void schedulePipelineStart(Pipeline pipeline, boolean restartOnReboot) {
    executorService.schedule(() -> {
      startPipeline(pipeline, restartOnReboot);
    }, WAIT_TIME_AFTER_FAILURE_IN_SECONDS, TimeUnit.SECONDS);
  }

  @PreDestroy
  public void onExit() {
    LOG.info("Shutting down StreamPipes...");
    LOG.info("Flagging currently running pipelines for restart...");
    List<Pipeline> pipelinesToStop = getAllPipelines()
        .stream()
        .filter(Pipeline::isRunning)
        .collect(Collectors.toList());

    LOG.info("Found {} running pipelines which will be stopped...", pipelinesToStop.size());

    pipelinesToStop.forEach(pipeline -> {
      pipeline.setRestartOnSystemReboot(true);
      StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updatePipeline(pipeline);
    });

    LOG.info("Gracefully stopping all running pipelines...");
    List<PipelineOperationStatus> status = Operations.stopAllPipelines(true);
    status.forEach(s -> {
      if (s.isSuccess()) {
        LOG.info("Pipeline {} successfully stopped", s.getPipelineName());
      } else {
        LOG.error("Pipeline {} could not be stopped", s.getPipelineName());
      }
    });

    deregisterService(serviceId());

    LOG.info("Thanks for using Apache StreamPipes - see you next time!");
  }

  private void startAllPreviouslyStoppedPipelines() {
    LOG.info("Checking for orphaned pipelines...");
    List<Pipeline> orphanedPipelines = getAllPipelines()
        .stream()
        .filter(Pipeline::isRunning)
        .collect(Collectors.toList());

    LOG.info("Found {} orphaned pipelines", orphanedPipelines.size());

    orphanedPipelines.forEach(pipeline -> {
      LOG.info("Restoring orphaned pipeline {}", pipeline.getName());
      startPipeline(pipeline, false);
    });

    LOG.info("Checking for gracefully shut down pipelines to be restarted...");

    List<Pipeline> pipelinesToRestart = getAllPipelines()
        .stream()
        .filter(p -> !(p.isRunning()))
        .filter(Pipeline::isRestartOnSystemReboot)
        .collect(Collectors.toList());

    LOG.info("Found {} pipelines that we are attempting to restart...", pipelinesToRestart.size());

    pipelinesToRestart.forEach(pipeline -> {
      startPipeline(pipeline, false);
    });

    LOG.info("No more pipelines to restore...");
  }

  private void startPipeline(Pipeline pipeline, boolean restartOnReboot) {
    PipelineOperationStatus status = Operations.startPipeline(pipeline);
    if (status.isSuccess()) {
      LOG.info("Pipeline {} successfully restarted", status.getPipelineName());
      Pipeline storedPipeline = getPipelineStorage().getPipeline(pipeline.getPipelineId());
      storedPipeline.setRestartOnSystemReboot(restartOnReboot);
      getPipelineStorage().updatePipeline(storedPipeline);
    } else {
      storeFailedRestartAttempt(pipeline);
      int failedAttemptCount = failedPipelines.get(pipeline.getPipelineId());
      if (failedAttemptCount <= MAX_PIPELINE_START_RETRIES) {
        LOG.error("Pipeline {} could not be restarted - I'll try again in {} seconds ({}/{} failed attempts)",
            pipeline.getName(),
            WAIT_TIME_AFTER_FAILURE_IN_SECONDS,
            failedAttemptCount,
            MAX_PIPELINE_START_RETRIES);

        schedulePipelineStart(pipeline, restartOnReboot);
      } else {
        LOG.error("Pipeline {} could not be restarted - are all pipeline element containers running?",
            status.getPipelineName());
      }
    }
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

  private List<Pipeline> getAllPipelines() {
    return getPipelineStorage()
        .getAllPipelines();
  }

  private IPipelineStorage getPipelineStorage() {
    return StorageDispatcher
        .INSTANCE
        .getNoSqlStore()
        .getPipelineStorageAPI();
  }


  @Override
  protected List<SpServiceTag> getServiceTags() {
    return Arrays.asList(
        DefaultSpServiceTags.CORE,
        DefaultSpServiceTags.CONNECT_MASTER,
        DefaultSpServiceTags.STREAMPIPES_CLIENT
    );
  }

  @Override
  protected String getHealthCheckPath() {
    return "/streampipes-backend/api/svchealth/" + AUTO_GENERATED_SERVICE_ID;
  }
}
