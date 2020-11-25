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

package org.apache.streampipes.connect.config;


import org.apache.streampipes.config.SpConfig;

public enum ConnectWorkerConfig {
  INSTANCE;


  private SpConfig config;

  ConnectWorkerConfig() {
    String name = "connect-worker-main";
    config = SpConfig.getSpConfig("connect-worker-main");

    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");

    config.register(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT, 8098, "The port of the connect container");
    config.register(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST, name, "The hostname of the connect container");

    config.register(ConfigKeys.BACKEND_HOST, "backend", "The host of the backend to register the worker");
    config.register(ConfigKeys.BACKEND_PORT, 8030, "The port of the backend to register the worker");

  }

  public String getConnectContainerWorkerUrl() {
    return "http://" + config.getString(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST) + ":" + config.getInteger(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT) + "/";
  }

  public String getBackendUrl() {
    return "http://" + config.getString(ConfigKeys.BACKEND_HOST) + ":" + config.getInteger(ConfigKeys.BACKEND_PORT) + "/streampipes-backend";
  }

  public String getKafkaHost() {
    return config.getString(ConfigKeys.KAFKA_HOST);
  }

  public int getKafkaPort() {
    return config.getInteger(ConfigKeys.KAFKA_PORT);
  }

  public String getKafkaUrl() {
    return getKafkaHost() + ":" + getKafkaPort();
  }

  public void setKafkaHost(String s) {
    config.setString(ConfigKeys.KAFKA_HOST, s);
  }


  public String getConnectContainerWorkerHost() {
    return config.getString(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST);
  }

  public Integer getConnectContainerWorkerPort() {
    return config.getInteger(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT);
  }

}
