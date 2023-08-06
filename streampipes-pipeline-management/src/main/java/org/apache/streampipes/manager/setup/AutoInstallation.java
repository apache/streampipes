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
package org.apache.streampipes.manager.setup;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.environment.variable.StringEnvironmentVariable;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.client.setup.InitialSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoInstallation implements BackgroundTaskNotifier {

  private static final Logger LOG = LoggerFactory.getLogger(AutoInstallation.class);

  private final Environment env;
  private final AtomicInteger errorCount = new AtomicInteger();
  private int numberOfBackgroundSteps = 0;
  private int executedBackgroundSteps = 0;

  public AutoInstallation() {
    this.env = Environments.getEnvironment();
  }

  public void startAutoInstallation() {
    InitialSettings settings = collectInitialSettings();

    List<InstallationStep> steps = InstallationConfiguration.getInstallationSteps(settings);
    List<Runnable> backgroundSteps = InstallationConfiguration.getBackgroundInstallationSteps(settings, this);
    numberOfBackgroundSteps = backgroundSteps.size();

    steps.forEach(step -> {
      step.install();
      errorCount.addAndGet(step.getErrorCount());
    });

    initBackgroundJobs(backgroundSteps);
  }

  private void initBackgroundJobs(List<Runnable> backgroundSteps) {
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    backgroundSteps.forEach(executorService::execute);
    executorService.shutdown();
  }

  private InitialSettings collectInitialSettings() {
    InitialSettings settings = new InitialSettings();
    settings.setInstallPipelineElements(autoInstallPipelineElements());
    settings.setAdminEmail(findAdminUser());
    settings.setAdminPassword(findAdminPassword());
    settings.setInitialServiceAccountName(findServiceAccountName());
    settings.setInitialServiceAccountSecret(findServiceAccountSecret());

    return settings;
  }

  private boolean autoInstallPipelineElements() {
    return env.getSetupInstallPipelineElements().getValueOrDefault();
  }

  private String findServiceAccountSecret() {
    return env.getInitialServiceUserSecret().getValueOrDefault();
  }

  private String findServiceAccountName() {
    return env.getInitialServiceUser().getValueOrDefault();
  }

  private String findAdminUser() {
    return getStringOrDefault(
        env.getInitialAdminEmail()
    );
  }

  private String findAdminPassword() {
    return getStringOrDefault(
        env.getInitialAdminPassword()
    );
  }

  private String getStringOrDefault(StringEnvironmentVariable variable) {
    String name = variable.getEnvVariableName();
    if (variable.exists()) {
      LOG.info("Using provided environment variable {}", name);
      return variable.getValue();
    } else {
      LOG.info("Environment variable {} not found, using default value {}", name, variable.getDefault());
      return variable.getDefault();
    }
  }

  @Override
  public void notifyFinished(int ec) {
    errorCount.addAndGet(ec);
    executedBackgroundSteps++;

    if (executedBackgroundSteps == numberOfBackgroundSteps) {
      if (errorCount.get() > 0) {
        LOG.error("{} errors occurred during the setup process", errorCount);
      } else {
        BackendConfig.INSTANCE.setIsConfigured(true);
        LOG.info("Initial setup completed successfully - you can now open the login page in the browser.");
      }
      LOG.info("\n\n**********\n\nAuto-Setup finished\n\n**********\n\n");
    }
  }
}
