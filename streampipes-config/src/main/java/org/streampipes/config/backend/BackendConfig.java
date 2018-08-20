/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.config.backend;


import org.streampipes.config.SpConfig;

public enum BackendConfig {
  INSTANCE;

  private SpConfig config;

  BackendConfig() {
    config = SpConfig.getSpConfig("backend");

    config.register(BackendConfigKeys.SERVICE_NAME, "Backend", "Backend Configuration");

    config.register(BackendConfigKeys.BACKEND_HOST, "backend", "Hostname for backend");
    config.register(BackendConfigKeys.BACKEND_PORT, 8082, "Port for backend");

    config.register(BackendConfigKeys.JMS_HOST, "activemq", "Hostname for backend service for active mq");
    config.register(BackendConfigKeys.JMS_PORT, 61616, "Port for backend service for active mq");
    config.register(BackendConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(BackendConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");
    config.register(BackendConfigKeys.ZOOKEEPER_HOST, "zookeeper", "Hostname for backend service for zookeeper");
    config.register(BackendConfigKeys.ZOOKEEPER_PORT, 2181, "Port for backend service for zookeeper");
    config.register(BackendConfigKeys.ELASTICSEARCH_HOST, "elasticsearch", "Hostname for elasticsearch service");
    config.register(BackendConfigKeys.ELASTICSEARCH_PORT, 9200, "Port for elasticsearch service");
    config.register(BackendConfigKeys.ELASTICSEARCH_PROTOCOL, "http", "Protocol the elasticsearch service");
    config.register(BackendConfigKeys.IS_CONFIGURED, false, "Boolean that indicates whether streampipes is " +
            "already configured or not");
    config.register(BackendConfigKeys.KAFKA_REST_HOST, "kafka-rest", "The hostname of the kafka-rest module");
    config.register(BackendConfigKeys.KAFKA_REST_PORT, 8073, "The port of the kafka-rest module");
    config.register(BackendConfigKeys.KAFKA_REST_HOST, "kafka-rest", "The hostname of the kafka-rest module");


  }

  public String getBackendHost() {
    return config.getString(BackendConfigKeys.BACKEND_HOST);
  }

  public int getBackendPort() {
    return config.getInteger(BackendConfigKeys.BACKEND_PORT);
  }

  public String getJmsHost() {
    return config.getString(BackendConfigKeys.JMS_HOST);
  }

  public int getJmsPort() {
    return config.getInteger(BackendConfigKeys.JMS_PORT);
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

  public boolean isConfigured() {
    return config.getBoolean(BackendConfigKeys.IS_CONFIGURED);
  }

  public void setKafkaHost(String s) {
    config.setString(BackendConfigKeys.KAFKA_HOST, s);
  }

  public void setZookeeperHost(String s) {
    config.setString(BackendConfigKeys.ZOOKEEPER_HOST, s);
  }

  public void setJmsHost(String s) {
    config.setString(BackendConfigKeys.JMS_HOST, s);
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

  public String getElasticsearchURL() {
    return getElasticsearchProtocol()+ "://" + getElasticsearchHost() + ":" + getElasticsearchPort();
  }

  public String getKafkaRestHost() {
    return config.getString(BackendConfigKeys.KAFKA_REST_HOST);
  }

  public Integer getKafkaRestPort() {
    return config.getInteger(BackendConfigKeys.KAFKA_REST_PORT);
  }

  public String getKafkaRestUrl() {
    return "http://" +getKafkaRestHost() +":" +getKafkaRestPort();
  }





}
