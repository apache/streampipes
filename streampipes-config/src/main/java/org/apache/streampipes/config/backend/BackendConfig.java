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


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.random.TokenGenerator;
import org.apache.streampipes.config.backend.model.EmailConfig;
import org.apache.streampipes.config.backend.model.GeneralConfig;
import org.apache.streampipes.config.backend.model.LocalAuthConfig;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

import java.io.File;
import java.security.SecureRandom;

public enum BackendConfig {
  INSTANCE;

  private final char[] possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?").toCharArray();
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
    config.register(BackendConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(BackendConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");
    config.register(BackendConfigKeys.ZOOKEEPER_HOST, "zookeeper", "Hostname for backend service for zookeeper");
    config.register(BackendConfigKeys.ZOOKEEPER_PORT, 2181, "Port for backend service for zookeeper");
    config.register(BackendConfigKeys.ELASTICSEARCH_HOST, "elasticsearch", "Hostname for elasticsearch service");
    config.register(BackendConfigKeys.ELASTICSEARCH_PORT, 9200, "Port for elasticsearch service");
    config.register(BackendConfigKeys.ELASTICSEARCH_PROTOCOL, "http", "Protocol the elasticsearch service");
    config.register(BackendConfigKeys.IS_CONFIGURED, false, "Boolean that indicates whether streampipes is " +
            "already configured or not");
    config.register(BackendConfigKeys.IS_SETUP_RUNNING, false, "Boolean that indicates whether the initial setup " +
            "is currently running");
    config.register(BackendConfigKeys.ASSETS_DIR, makeAssetLocation(), "The directory where " +
            "pipeline element assets are stored.");
    config.register(BackendConfigKeys.FILES_DIR, makeFileLocation(), "The directory where " +
            "pipeline element files are stored.");
    config.register(BackendConfigKeys.DATA_LAKE_HOST, "elasticsearch", "The host of the data base used for the data lake");
    config.register(BackendConfigKeys.DATA_LAKE_PORT, 9200, "The port of the data base used for the data lake");

    config.register(BackendConfigKeys.INFLUX_HOST, "influxdb", "The host of the influx data base");
    config.register(BackendConfigKeys.INFLUX_PORT, 8086, "The hist of the influx data base");
    config.register(BackendConfigKeys.INFLUX_DATA_BASE, "sp", "The influx data base name");
    config.registerObject(BackendConfigKeys.MESSAGING_SETTINGS, MessagingSettings.fromDefault(),
            "Default Messaging Settings");

    config.registerObject(BackendConfigKeys.LOCAL_AUTH_CONFIG, LocalAuthConfig.fromDefaults(getJwtSecret()), "Local authentication settings");
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
    return System.getProperty("user.home")
            + File.separator
            + ".streampipes"
            + File.separator;
  }

  private String randomKey() {
    return RandomStringUtils.random( 10, 0, possibleCharacters.length - 1,
            false, false, possibleCharacters, new SecureRandom());
  }

  public String getBackendHost() {
    return config.getString(BackendConfigKeys.BACKEND_HOST);
  }

  public int getBackendPort() {
    return config.getInteger(BackendConfigKeys.BACKEND_PORT);
  }

  public String getBackendUrl() {
    return "http://" + getBackendHost() + ":" + getBackendPort();
  }

  public String getBackendApiUrl() {
    return getBackendUrl() + "/streampipes-backend/";
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

  public String getKafkaHost() {
    return config.getString(BackendConfigKeys.KAFKA_HOST);
  }

  public int getKafkaPort() {
    return config.getInteger(BackendConfigKeys.KAFKA_PORT);
  }

  public String getKafkaUrl() {
    return getKafkaHost() + ":" + getKafkaPort();
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

  public String getElasticsearchHost() {
    return config.getString(BackendConfigKeys.ELASTICSEARCH_HOST);
  }

  public int getElasticsearchPort() {
    return config.getInteger(BackendConfigKeys.ELASTICSEARCH_PORT);
  }

  public String getElasticsearchProtocol() {
    return config.getString(BackendConfigKeys.ELASTICSEARCH_PROTOCOL);
  }

  public String getKafkaRestHost() {
    return config.getString(BackendConfigKeys.KAFKA_REST_HOST);
  }

  public Integer getKafkaRestPort() {
    return config.getInteger(BackendConfigKeys.KAFKA_REST_PORT);
  }

  public String getKafkaRestUrl() {
    return "http://" + getKafkaRestHost() + ":" + getKafkaRestPort();
  }

  public String getAssetDir() {
    return config.getString(BackendConfigKeys.ASSETS_DIR);
  }

  public String getFilesDir() {
    return config.getString(BackendConfigKeys.FILES_DIR);
  }

  public String getDatalakeHost() {
    return config.getString(BackendConfigKeys.DATA_LAKE_HOST);
  }

  public int getDatalakePort() {
    return config.getInteger(BackendConfigKeys.DATA_LAKE_PORT);
  }

  public String getDataLakeUrl() {
    return getDatalakeHost() + ":" + getDatalakePort();
  }

  public String getInfluxHost() {
    return config.getString(BackendConfigKeys.INFLUX_HOST);
  }

  public int getInfluxPort() {
    return config.getInteger(BackendConfigKeys.INFLUX_PORT);
  }

  public String getInfluxUrl() {
    return "http://" + getInfluxHost() + ":" + getInfluxPort();
  }

  public String getInfluxDatabaseName() {
    return config.getString(BackendConfigKeys.INFLUX_DATA_BASE);
  }

  public LocalAuthConfig getLocalAuthConfig() {
    return config.getObject(BackendConfigKeys.LOCAL_AUTH_CONFIG, LocalAuthConfig.class, LocalAuthConfig.fromDefaults(getJwtSecret()));
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

  public boolean isSetupRunning() {
    return config.getBoolean(BackendConfigKeys.IS_SETUP_RUNNING);
  }

  public void updateSetupStatus(boolean status) {
    config.setBoolean(BackendConfigKeys.IS_SETUP_RUNNING, status);
  }

  private String getJwtSecret() {
    if (Envs.SP_JWT_SECRET.exists()) {
      return Envs.SP_JWT_SECRET.getValue();
    } else {
      return makeDefaultJwtSecret();
    }
  }

  private String makeDefaultJwtSecret() {
    return TokenGenerator.generateNewToken();
  }

}
