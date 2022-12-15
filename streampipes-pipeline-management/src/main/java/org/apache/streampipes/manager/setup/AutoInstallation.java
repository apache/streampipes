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

import org.apache.streampipes.commons.constants.CustomEnvs;
import org.apache.streampipes.commons.constants.DefaultEnvValues;
import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.model.client.setup.InitialSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoInstallation {

  private static final Logger LOG = LoggerFactory.getLogger(AutoInstallation.class);

  public void startAutoInstallation() {
    InitialSettings settings = collectInitialSettings();

    List<InstallationStep> steps = InstallationConfiguration.getInstallationSteps(settings);
    AtomicInteger errorCount = new AtomicInteger();

    steps.forEach(step -> {
      step.install();
      errorCount.addAndGet(step.getErrorCount());
    });
    if (errorCount.get() > 0) {
      LOG.error("{} errors occurred during the setup process", errorCount);
    } else {
      BackendConfig.INSTANCE.setIsConfigured(true);
      LOG.info("Initial setup completed successfully - you can now open the login page in the browser.");
    }
    LOG.info("\n\n**********\n\nAuto-Setup finished\n\n**********\n\n");
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
    if (Envs.SP_SETUP_INSTALL_PIPELINE_ELEMENTS.exists()) {
      return Envs.SP_SETUP_INSTALL_PIPELINE_ELEMENTS.getValueAsBoolean();
    } else {
      return DefaultEnvValues.INSTALL_PIPELINE_ELEMENTS;
    }
  }

  private String findServiceAccountSecret() {
    return getStringOrDefault(
        Envs.SP_INITIAL_SERVICE_USER_SECRET.getEnvVariableName(),
        DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT
    );
  }

  private String findServiceAccountName() {
    return getStringOrDefault(
        Envs.SP_INITIAL_SERVICE_USER.getEnvVariableName(),
        DefaultEnvValues.INITIAL_CLIENT_USER_DEFAULT
    );
  }

  private String findAdminUser() {
    return getStringOrDefault(
        Envs.SP_INITIAL_ADMIN_EMAIL.getEnvVariableName(),
        DefaultEnvValues.INITIAL_ADMIN_EMAIL_DEFAULT
    );
  }

  private String findAdminPassword() {
    return getStringOrDefault(
        Envs.SP_INITIAL_ADMIN_PASSWORD.getEnvVariableName(),
        DefaultEnvValues.INITIAL_ADMIN_PW_DEFAULT
    );
  }

  private String getStringOrDefault(String envVariable, String defaultValue) {
    boolean exists = exists(envVariable);
    if (exists) {
      LOG.info("Using provided environment variable {}", envVariable);
      return getString(envVariable);
    } else {
      LOG.info("Environment variable {} not found, using default value {}", envVariable, defaultValue);
      return defaultValue;
    }
  }

  private boolean exists(String envVariable) {
    return CustomEnvs.exists(envVariable);
  }

  private String getString(String envVariable) {
    return CustomEnvs.getEnv(envVariable);
  }
}
