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

package org.apache.streampipes.config.backend;


import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.random.TokenGenerator;
import org.apache.streampipes.model.configuration.DefaultMessagingSettings;
import org.apache.streampipes.model.configuration.EmailConfig;
import org.apache.streampipes.model.configuration.GeneralConfig;
import org.apache.streampipes.model.configuration.LocalAuthConfig;
import org.apache.streampipes.model.configuration.MessagingSettings;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

public enum BackendConfig {
  INSTANCE;

  private final SpConfig config;

  BackendConfig() {
    config = SpServiceDiscovery.getSpConfig("backend");
    config.register(BackendConfigKeys.IS_CONFIGURED, false,
        "Boolean that indicates whether streampipes is " + "already configured or not");
    config.register(BackendConfigKeys.IS_SETUP_RUNNING, false,
        "Boolean that indicates whether the initial setup " + "is currently running");
  }

  public String getJmsHost() {
    return config.getString(BackendConfigKeys.JMS_HOST);
  }

  public int getJmsPort() {
    return config.getInteger(BackendConfigKeys.JMS_PORT);
  }

  public String getMqttHost() {
    return config.getString(BackendConfigKeys.MQTT_HOST);
  }

  public int getMqttPort() {
    return config.getInteger(BackendConfigKeys.MQTT_PORT);
  }

  public String getNatsHost() {
    return config.getString(BackendConfigKeys.NATS_HOST);
  }

  public int getNatsPort() {
    return config.getInteger(BackendConfigKeys.NATS_PORT);
  }

  public String getKafkaHost() {
    return config.getString(BackendConfigKeys.KAFKA_HOST);
  }

  public int getKafkaPort() {
    return config.getInteger(BackendConfigKeys.KAFKA_PORT);
  }

  public String getPulsarUrl() {
    return config.getString(BackendConfigKeys.PULSAR_URL);
  }

  public String getZookeeperHost() {
    return config.getString(BackendConfigKeys.ZOOKEEPER_HOST);
  }

  public int getZookeeperPort() {
    return config.getInteger(BackendConfigKeys.ZOOKEEPER_PORT);
  }

  public MessagingSettings getMessagingSettings() {
    return config.getObject(BackendConfigKeys.MESSAGING_SETTINGS, MessagingSettings.class,
        new DefaultMessagingSettings().make());
  }

  public boolean isConfigured() {
    return config.getBoolean(BackendConfigKeys.IS_CONFIGURED);
  }

  public void setIsConfigured(boolean b) {
    config.setBoolean(BackendConfigKeys.IS_CONFIGURED, b);
  }

  public String getAssetDir() {
    return config.getString(BackendConfigKeys.ASSETS_DIR);
  }

  public String getFilesDir() {
    return config.getString(BackendConfigKeys.FILES_DIR);
  }

  public LocalAuthConfig getLocalAuthConfig() {
    return config.getObject(BackendConfigKeys.LOCAL_AUTH_CONFIG, LocalAuthConfig.class,
        LocalAuthConfig.fromDefaults(getJwtSecret()));
  }

  public EmailConfig getEmailConfig() {
    return config.getObject(BackendConfigKeys.EMAIL_CONFIG, EmailConfig.class, EmailConfig.fromDefaults());
  }

  public GeneralConfig getGeneralConfig() {
    return config.getObject(BackendConfigKeys.GENERAL_CONFIG, GeneralConfig.class, new GeneralConfig());
  }

  private String getJwtSecret() {
    var env = Environments.getEnvironment();
    return env.getJwtSecret().getValueOrResolve(this::makeDefaultJwtSecret);
  }

  private String makeDefaultJwtSecret() {
    return TokenGenerator.generateNewToken();
  }

  public void updateSetupStatus(boolean status) {
    config.setBoolean(BackendConfigKeys.IS_SETUP_RUNNING, status);
  }
}
