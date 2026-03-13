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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.connect.transformer.api.TransformationEngine;
import org.apache.streampipes.connect.transformer.api.TransformationEngines;
import org.apache.streampipes.connect.transformer.groovy.GroovyScriptEngine;
import org.apache.streampipes.connect.transformer.js.GraalJsScriptEngine;
import org.apache.streampipes.health.monitoring.ExtensionHealthCheck;
import org.apache.streampipes.health.monitoring.ResourceProvider;
import org.apache.streampipes.health.monitoring.ServiceHealthCheck;
import org.apache.streampipes.loadbalance.LoadManager;
import org.apache.streampipes.loadbalance.service.ExtensionsServiceReportExecutor;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.function.FunctionManager;
import org.apache.streampipes.manager.health.CoreInitialInstallationProgress;
import org.apache.streampipes.manager.health.CoreServiceStatusManager;
import org.apache.streampipes.manager.pipeline.ExtensionsServiceLogExecutor;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.manager.setup.AutoInstallation;
import org.apache.streampipes.manager.setup.StreamPipesEnvChecker;
import org.apache.streampipes.manager.setup.tasks.ApplyDefaultRolesAndPrivilegesTask;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.apache.streampipes.messaging.mqtt.SpMqttProtocolFactory;
import org.apache.streampipes.messaging.nats.SpNatsProtocolFactory;
import org.apache.streampipes.messaging.pulsar.SpPulsarProtocolFactory;
import org.apache.streampipes.model.configuration.SpCoreConfigurationStatus;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.service.base.BaseNetworkingConfig;
import org.apache.streampipes.service.base.StreamPipesPrometheusConfig;
import org.apache.streampipes.service.base.StreamPipesServiceBase;
import org.apache.streampipes.service.core.migrations.AvailableMigrations;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.service.core.migrations.MigrationsHandler;
import org.apache.streampipes.service.core.storage.StorageApiConfiguration;
import org.apache.streampipes.storage.api.function.IFunctionStateStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.couchdb.impl.user.UserStorage;
import org.apache.streampipes.storage.couchdb.utils.CouchDbViewGenerator;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@Import({OpenApiConfiguration.class, StreamPipesPasswordEncoder.class,
    StreamPipesPrometheusConfig.class, WebSecurityConfig.class, WelcomePageController.class,
    StorageApiConfiguration.class, ExtensionServiceRequestConfiguration.class})
@ComponentScan({"org.apache.streampipes.rest.*", "org.apache.streampipes.service.core.oauth2",
    "org.apache.streampipes.service.core.scheduler"})
public class StreamPipesCoreApplication extends StreamPipesServiceBase {

  private static final Logger LOG =
      LoggerFactory.getLogger(StreamPipesCoreApplication.class.getCanonicalName());

  private final ISpCoreConfigurationStorage coreConfigStorage =
      StorageDispatcher.INSTANCE.getNoSqlStore().getSpCoreConfigurationStorage();

  private final CoreServiceStatusManager coreStatusManager =
      new CoreServiceStatusManager(coreConfigStorage);

  @Autowired
  private IFunctionStateStorage functionStateStorage;

  @Autowired
  private ExtensionServiceRequestManager extensionServiceRequestManager;

  @Autowired
  private WorkerRestClient workerRestClient;

  private final IExtensionsServiceStorage extensionsServiceStorage =
      StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage();

  public static void main(String[] args) {
    StreamPipesCoreApplication application = new StreamPipesCoreApplication();
    application.initialize(() -> List.of(
            new SpNatsProtocolFactory(),
            new SpKafkaProtocolFactory(),
            new SpMqttProtocolFactory(),
            new SpPulsarProtocolFactory()),
        List.of(
            GroovyScriptEngine::new,
            GraalJsScriptEngine::new
        )
    );
  }

  public void initialize(SupportedProtocols supportedProtocols,
                         List<Supplier<TransformationEngine>> transformationEngines) {
    try {
      registerProtocols(supportedProtocols);
      registerTransformationEngines(transformationEngines);
      BaseNetworkingConfig networkingConfig = BaseNetworkingConfig.defaultResolution(8030);
      startStreamPipesService(StreamPipesCoreApplication.class, networkingConfig);
    } catch (UnknownHostException e) {
      LOG.error("Could not auto-resolve host address - please manually provide the hostname"
          + " using the `SP_HOST` environment variable");
    }
  }

  protected void registerTransformationEngines(List<Supplier<TransformationEngine>> transformationEngines) {
    transformationEngines.forEach(TransformationEngines.INSTANCE::registerEngine);
  }

  protected void registerProtocols(SupportedProtocols protocols) {
    protocols.getSupportedProtocols().forEach(SpProtocolManager.INSTANCE::register);
  }

  @PostConstruct
  public void init() {
    var executorService = Executors.newSingleThreadScheduledExecutor();
    var logCheckExecutorService = Executors.newSingleThreadScheduledExecutor();

    new StreamPipesEnvChecker().updateEnvironmentVariables();
    new CouchDbViewGenerator().createGenericDatabaseIfNotExists();
    var env = Environments.getEnvironment();

    ExtensionsServiceReportExecutor.setServiceReportFetcher(serviceRegistration -> {
      var target = ExtensionServiceRequestTargets.serviceLoad(serviceRegistration);
      var response = extensionServiceRequestManager.requestServiceLoad(target);

      if (!response.isSuccess()) {
        throw new IOException("Could not fetch load report from endpoint " + serviceRegistration.getServiceUrl()
            + " (status " + response.statusCode() + ")");
      }

      return response.responseBody();
    });

    if (env.getLoadManagerEnable().getValueOrDefault()) {
      LoadManager.initialize();
    }
    if (!isConfigured()) {
      CoreInitialInstallationProgress.INSTANCE.triggerInitiallyInstallingMode();
      doInitialSetup(env.getInitialWaitTimeBeforeInstallationInMillis().getValueOrDefault());
    } else {
      // Check needs to be present since core configuration is part of migration
      if (coreConfigStorage.exists()) {
        coreStatusManager.updateCoreStatus(SpCoreConfigurationStatus.MIGRATING);
      }
      new MigrationsHandler().performMigrations(getMigrations());
    }

    new ApplyDefaultRolesAndPrivilegesTask().execute();
    coreStatusManager.updateCoreStatus(SpCoreConfigurationStatus.READY);

    executorService.schedule(new PostStartupTask(
            getPipelineStorage(),
            extensionServiceRequestManager,
            workerRestClient),
        env.getInitialHealthCheckDelayInMillis().getValueOrDefault(),
        TimeUnit.MILLISECONDS);

    scheduleHealthChecks(env.getHealthCheckIntervalInMillis().getValueOrDefault(), List
        .of(new ServiceHealthCheck(
                extensionsServiceStorage,
                extensionServiceRequestManager),
            new ExtensionHealthCheck(
                new ResourceProvider(
                    StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI(),
                    StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage(),
                    new AdapterMasterManagement(
                        StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage(),
                        new SpResourceManager().manageAdapters(),
                        new SpResourceManager().manageDataStreams(),
                        AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
                        workerRestClient,
                        extensionsServiceStorage,
                        extensionServiceRequestManager
                    )),
                StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage(),
                extensionServiceRequestManager
            )));

    var logFetchInterval = env.getLogFetchIntervalInMillis().getValueOrDefault();
    LOG.info("Extensions logs will be fetched every {} milliseconds", logFetchInterval);
    logCheckExecutorService.scheduleAtFixedRate(new ExtensionsServiceLogExecutor(extensionServiceRequestManager),
        logFetchInterval, logFetchInterval,
        TimeUnit.MILLISECONDS);
  }

  private void scheduleHealthChecks(int healthCheckIntervalInMillis, List<Runnable> checks) {
    var healthCheckExecutorService = Executors.newSingleThreadScheduledExecutor();
    checks.forEach(check -> {
      LOG.info("Health check {} configured to run every {} {}", check.getClass().getCanonicalName(),
          healthCheckIntervalInMillis, TimeUnit.MILLISECONDS);
      healthCheckExecutorService.scheduleAtFixedRate(check, healthCheckIntervalInMillis,
          healthCheckIntervalInMillis,
          TimeUnit.MILLISECONDS);
    });
  }

  protected List<Migration> getMigrations() {
    return new AvailableMigrations().getAvailableMigrations();
  }

  private boolean isConfigured() {
    return new UserStorage().existsDatabase();
  }

  private void doInitialSetup(int initialSleepBeforeInstallation) {
    LOG.info("\n\n**********\n\nWelcome to Apache StreamPipes!\n\n**********\n\n");
    LOG.info("We will perform the initial setup, grab some coffee and cross your fingers ;-)...");
    LOG.info("Auto-setup will start in {} milliseconds to make sure all services are running...",
        initialSleepBeforeInstallation);
    try {
      TimeUnit.MILLISECONDS.sleep(initialSleepBeforeInstallation);
      LOG.info("Starting installation procedure");
      new AutoInstallation(extensionServiceRequestManager).startAutoInstallation();
    } catch (InterruptedException e) {
      LOG.error("Ooops, something went wrong during the installation", e);
    }
  }

  @PreDestroy
  public void onExit() {
    LOG.info("Shutting down StreamPipes...");
    LOG.info("Flagging currently running pipelines for restart...");
    List<Pipeline> pipelinesToStop =
        getAllPipelines().stream().filter(Pipeline::isRunning).toList();

    LOG.info("Found {} running pipelines which will be stopped...", pipelinesToStop.size());

    pipelinesToStop.forEach(pipeline -> {
      pipeline.setRestartOnSystemReboot(true);
      StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updateElement(pipeline);
    });

    LOG.info("Gracefully stopping all running pipelines...");
    List<PipelineOperationStatus> status = PipelineManager.stopAllPipelines(true, extensionServiceRequestManager);
    status.forEach(s -> {
      if (s.isSuccess()) {
        LOG.info("Pipeline {} successfully stopped", s.getPipelineName());
      } else {
        LOG.error("Pipeline {} could not be stopped", s.getPipelineName());
      }
    });

    new FunctionManager(extensionServiceRequestManager).stopAllFunctionsAndPersistState(functionStateStorage);

    LOG.info("Thanks for using Apache StreamPipes - see you next time!");
  }

  private List<Pipeline> getAllPipelines() {
    return getPipelineStorage().findAll();
  }

  private IPipelineStorage getPipelineStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();
  }

  @Override
  protected String getHealthCheckPath() {
    return "/streampipes-backend/api/svchealth/" + AUTO_GENERATED_SERVICE_ID;
  }
}
