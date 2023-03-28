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


import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.random.TokenGenerator;
import org.apache.streampipes.config.backend.model.EmailConfig;
import org.apache.streampipes.config.backend.model.GeneralConfig;
import org.apache.streampipes.config.backend.model.LocalAuthConfig;
import org.apache.streampipes.model.config.MessagingSettings;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

import java.io.File;

public enum BackendConfig {
  INSTANCE;

  private final char[] possibleCharacters =
      ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?")
          .toCharArray();
  private SpConfig config;

  BackendConfig() {
    config = SpServiceDiscovery.getSpConfig("backend");

    config.register(BackendConfigKeys.SERVICE_NAME, "Backend", "Backend Configuration");

    config.register(BackendConfigKeys.BACKEND_HOST, "backend", "Hostname for backend");
    config.register(BackendConfigKeys.BACKEND_PORT, 8030, "Port for backend");

    config.register(BackendConfigKeys.JMS_HOST, "activemq", "Hostname for backend service for active mq");
    config.register(BackendConfigKeys.JMS_PORT, 61616, "Port for backend service for active mq");
    config.register(BackendConfigKeys.MQTT_HOST, "activemq", "Hostname of mqtt service");
    config.register(BackendConfigKeys.MQTT_PORT, 1883, "Port of mqtt service");
    config.register(BackendConfigKeys.NATS_HOST, "nats", "Hostname of Nats");
    config.register(BackendConfigKeys.NATS_PORT, 4222, "Port of Nats");
    config.register(BackendConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(BackendConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");
    config.register(BackendConfigKeys.ZOOKEEPER_HOST, "zookeeper", "Hostname for backend service for zookeeper");
    config.register(BackendConfigKeys.ZOOKEEPER_PORT, 2181, "Port for backend service for zookeeper");
    config.register(BackendConfigKeys.IS_CONFIGURED, false,
        "Boolean that indicates whether streampipes is " + "already configured or not");
    config.register(BackendConfigKeys.IS_SETUP_RUNNING, false,
        "Boolean that indicates whether the initial setup " + "is currently running");
    config.register(BackendConfigKeys.ASSETS_DIR, makeAssetLocation(),
        "The directory where " + "pipeline element assets are stored.");
    config.register(BackendConfigKeys.FILES_DIR, makeFileLocation(),
        "The directory where " + "pipeline element files are stored.");
    config.registerObject(BackendConfigKeys.MESSAGING_SETTINGS, DefaultMessagingSettings.make(),
        "Default Messaging Settings");

    config.registerObject(BackendConfigKeys.LOCAL_AUTH_CONFIG, LocalAuthConfig.fromDefaults(getJwtSecret()),
        "Local authentication settings");
    config.registerObject(BackendConfigKeys.EMAIL_CONFIG, EmailConfig.fromDefaults(), "Email settings");
    config.registerObject(BackendConfigKeys.GENERAL_CONFIG, new GeneralConfig(), "General settings");
  }

  private String makeAssetLocation() {
    return makeStreamPipesHomeLocation()
        + "assets";
  }

  private String makeFileLocation() {
    return makeStreamPipesHomeLocation()
        + "files";
  }

  private String makeStreamPipesHomeLocation() {
    var userDefinedAssetDir = getEnvironment().getCoreAssetBaseDir();
    var assetDirAppendix = getSpAssetDirAppendix();
    if (userDefinedAssetDir.exists()) {
      return userDefinedAssetDir.getValue() + assetDirAppendix;
    } else {
      return System.getProperty("user.home") + assetDirAppendix;
    }
  }

  private String getSpAssetDirAppendix() {
    return File.separator + ".streampipes" + File.separator;
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

  public String getZookeeperHost() {
    return config.getString(BackendConfigKeys.ZOOKEEPER_HOST);
  }

  public int getZookeeperPort() {
    return config.getInteger(BackendConfigKeys.ZOOKEEPER_PORT);
  }

  public MessagingSettings getMessagingSettings() {
    return config.getObject(BackendConfigKeys.MESSAGING_SETTINGS, MessagingSettings.class,
        new MessagingSettings());
  }

  public boolean isConfigured() {
    return config.getBoolean(BackendConfigKeys.IS_CONFIGURED);
  }

  public void setKafkaHost(String s) {
    config.setString(BackendConfigKeys.KAFKA_HOST, s);
  }

  public void setMessagingSettings(MessagingSettings settings) {
    config.setObject(BackendConfigKeys.MESSAGING_SETTINGS, settings);
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

  public void updateEmailConfig(EmailConfig emailConfig) {
    config.setObject(BackendConfigKeys.EMAIL_CONFIG, emailConfig);
  }

  public GeneralConfig getGeneralConfig() {
    return config.getObject(BackendConfigKeys.GENERAL_CONFIG, GeneralConfig.class, new GeneralConfig());
  }

  public void updateGeneralConfig(GeneralConfig generalConfig) {
    config.setObject(BackendConfigKeys.GENERAL_CONFIG, generalConfig);
  }

  public void updateLocalAuthConfig(LocalAuthConfig authConfig) {
    config.setObject(BackendConfigKeys.LOCAL_AUTH_CONFIG, authConfig);
  }

  public void updateSetupStatus(boolean status) {
    config.setBoolean(BackendConfigKeys.IS_SETUP_RUNNING, status);
  }

  private String getJwtSecret() {
    var env = getEnvironment();
    return env.getJwtSecret().getValueOrResolve(this::makeDefaultJwtSecret);
  }

  private String makeDefaultJwtSecret() {
    return TokenGenerator.generateNewToken();
  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }

}
